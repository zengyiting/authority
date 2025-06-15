package com.code.permissionservice.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 
 * @TableName roles
 */
@TableName(value ="roles")
public class Roles {
    /**
     * 角色id：1超管，2普通用户，3管理员
     */
    @TableId(value = "role_id")
    private Integer roleId;

    /**
     * super_admin/user/admin
     */
    private String roleCode;

    /**
     * 角色id：1超管，2普通用户，3管理员
     */
    public Integer getRoleId() {
        return roleId;
    }

    /**
     * 角色id：1超管，2普通用户，3管理员
     */
    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    /**
     * super_admin/user/admin
     */
    public String getRoleCode() {
        return roleCode;
    }

    /**
     * super_admin/user/admin
     */
    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        Roles other = (Roles) that;
        return (this.getRoleId() == null ? other.getRoleId() == null : this.getRoleId().equals(other.getRoleId()))
            && (this.getRoleCode() == null ? other.getRoleCode() == null : this.getRoleCode().equals(other.getRoleCode()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getRoleId() == null) ? 0 : getRoleId().hashCode());
        result = prime * result + ((getRoleCode() == null) ? 0 : getRoleCode().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", roleId=").append(roleId);
        sb.append(", roleCode=").append(roleCode);
        sb.append("]");
        return sb.toString();
    }
}