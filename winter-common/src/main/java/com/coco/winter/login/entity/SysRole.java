package com.coco.winter.login.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

/**
 * @ClassName SysRole
 * @Description 角色信息
 * @Author like
 * @Data 2018/11/10 14:00
 * @Version 1.0
 **/

@Entity
@Data
public class SysRole {

    /**
     * 角色编号
     */
    @Id
    @GeneratedValue
    private Integer id;
    /**
     * 角色标识程序中判断使用,如"admin",这个是唯一的
     */
    private String role;
    /**
     * 角色描述,UI界面显示使用
     */
    private String description;
    /**
     * 是否可用,如果不可用将不会添加给用户
     */
    private Boolean available = Boolean.FALSE;

    /**
     * 角色 -- 权限关系：多对多关系
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "SysRolePermission", joinColumns = {@JoinColumn(name = "roleId")}, inverseJoinColumns = {@JoinColumn(name = "permissionId")})
    private List<SysPermission> permissions;

    /**
     * 用户--角色关系定义 角色:用户 --> 1:n
     */
    @ManyToMany
    @JoinTable(name = "SysUserRole", joinColumns = {@JoinColumn(name = "roleId")}, inverseJoinColumns = {@JoinColumn(name = "uid")})
    private List<UserInfo> userInfos;
}
