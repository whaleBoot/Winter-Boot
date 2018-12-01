package com.coco.winter.utils;

import com.coco.winter.common.entity.Identity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;

/**
 * @ClassName JWTTokenUtil
 * @Description TODO
 * @Author like
 * @Data 2018/11/10 16:59
 * @Version 1.0
 **/

@Data
@Slf4j
public class JWTTokenUtil {

    /**
     * 传递一个载荷   然后通过jjwt 自动生成Token
     *
     * @param poyload
     * @return
     */
    public static String createJWT(String poyload, String authJm) {
        //加密方式HS256 使用jsonwebtoken
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(authJm);
        Key siginingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
        //生成jwt
        JwtBuilder jwtBuilder = Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setHeaderParam("alg", "HS256")
                .setPayload(poyload)//载荷
                .signWith(signatureAlgorithm, siginingKey);//签名
        //生成token并序列化编码成一个URL安全的字符串
        log.info("token生成成功");
        return jwtBuilder.compact();
    }

    /**
     * @param identity
     * @param apiKeySecret
     * @return
     */
    public static String createToken(Identity identity, String apiKeySecret) {
        //JWT采用的签名算法
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        //获取当前时间戳
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        //封装好加密算法与私钥apiKeySecret
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(apiKeySecret);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
        //采用建造者模式定制化token属性
        JwtBuilder builder = Jwts.builder().setId(identity.getId())
                .setIssuedAt(now)
                .setSubject(identity.getId() + "," + identity.getUserName() + "," + identity.getRole())
                .setIssuer(identity.getIssuer())
                .signWith(signatureAlgorithm, signingKey);
        //设置失效时间戳
        long ttlMillis = identity.getDuration();
        if (ttlMillis >= 0) {
            long expMillis = nowMillis + ttlMillis;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);
//	        identity.setDuration(exp.getTime());
        }
        //生成token并序列化编码成一个URL安全的字符串
        log.info("token生成成功");
        return builder.compact();
    }

    /**
     * 解析jwt 并判断是否被修改，如验证通过则返回新的
     *
     * @param jwt
     * @return
     * @throws Exception
     */
    public static Claims parseJWT(String jwt, String authJm) throws Exception {
        if (jwt.split("\\.").length == 3) {
            String sign = jwt.split("\\.")[2];//签名
            System.out.println("签名jwt中：" + sign);
            Claims claims = Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary(authJm))
                    .parseClaimsJws(jwt).getBody();

            String signNew = createJWT(JacksonJsonUtil.obj2json(claims), authJm).split("\\.")[2];
            if (signNew.equals(sign)) {
                log.info("token解析成功，匹配一致，数据没有篡改!");
                return claims;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * @param token
     * @param apiKeySecret
     * @return
     * @throws Exception
     */
    public static Identity parseToken(String token, String apiKeySecret) throws Exception {
        Claims claims = Jwts.parser()
                .setSigningKey(DatatypeConverter.parseBase64Binary(apiKeySecret))
                .parseClaimsJws(token).getBody();

        String[] subjectInfos = claims.getSubject().split(",");
        String id = subjectInfos[0];
        String userName = subjectInfos[1];
        String role = subjectInfos[2];
        // 封装成pojo
        Identity identity = new Identity();
        identity.setId(id);
        identity.setUserName(userName);
        identity.setRole(role);
        identity.setDuration(claims.getExpiration().getTime());
        log.info("已登录的用户，有效token");
        return identity;
    }
}
