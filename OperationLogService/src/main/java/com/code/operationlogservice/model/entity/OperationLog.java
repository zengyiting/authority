package com.code.operationlogservice.model.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.util.Date;

/**
 * 
 * @TableName operation_logs
 */
@TableName(value ="operation_logs")
public class OperationLog {
    /**
     * 
     */
    @TableId(value = "log_id")
    private Long logId;

    /**
     * 操作用户ID
     */
    private Long userId;

    /**
     * 操作类型,  如update_user
     */
    private String action;

    /**
     * ip地址,支持ipv6
     */
    private String ip;

    /**
     * 操作详情
     */
    private String detail;

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 更新时间
     */
    private Date gmtUpdate;

    /**
     * 
     */
    public Long getLogId() {
        return logId;
    }

    /**
     * 
     */
    public void setLogId(Long logId) {
        this.logId = logId;
    }

    /**
     * 操作用户ID
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * 操作用户ID
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * 操作类型,  如update_user
     */
    public String getAction() {
        return action;
    }

    /**
     * 操作类型,  如update_user
     */
    public void setAction(String action) {
        this.action = action;
    }

    /**
     * ip地址,支持ipv6
     */
    public String getIp() {
        return ip;
    }

    /**
     * ip地址,支持ipv6
     */
    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * 操作详情
     */
    public String getDetail() {
        return detail;
    }

    /**
     * 操作详情
     */
    public void setDetail(String detail) {
        this.detail = detail;
    }

    /**
     * 创建时间
     */
    public Date getGmtCreate() {
        return gmtCreate;
    }

    /**
     * 创建时间
     */
    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    /**
     * 更新时间
     */
    public Date getGmtUpdate() {
        return gmtUpdate;
    }

    /**
     * 更新时间
     */
    public void setGmtUpdate(Date gmtUpdate) {
        this.gmtUpdate = gmtUpdate;
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
        OperationLog other = (OperationLog) that;
        return (this.getLogId() == null ? other.getLogId() == null : this.getLogId().equals(other.getLogId()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getAction() == null ? other.getAction() == null : this.getAction().equals(other.getAction()))
            && (this.getIp() == null ? other.getIp() == null : this.getIp().equals(other.getIp()))
            && (this.getDetail() == null ? other.getDetail() == null : this.getDetail().equals(other.getDetail()))
            && (this.getGmtCreate() == null ? other.getGmtCreate() == null : this.getGmtCreate().equals(other.getGmtCreate()))
            && (this.getGmtUpdate() == null ? other.getGmtUpdate() == null : this.getGmtUpdate().equals(other.getGmtUpdate()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getLogId() == null) ? 0 : getLogId().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getAction() == null) ? 0 : getAction().hashCode());
        result = prime * result + ((getIp() == null) ? 0 : getIp().hashCode());
        result = prime * result + ((getDetail() == null) ? 0 : getDetail().hashCode());
        result = prime * result + ((getGmtCreate() == null) ? 0 : getGmtCreate().hashCode());
        result = prime * result + ((getGmtUpdate() == null) ? 0 : getGmtUpdate().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", logId=").append(logId);
        sb.append(", userId=").append(userId);
        sb.append(", action=").append(action);
        sb.append(", ip=").append(ip);
        sb.append(", detail=").append(detail);
        sb.append(", gmtCreate=").append(gmtCreate);
        sb.append(", gmtUpdate=").append(gmtUpdate);
        sb.append("]");
        return sb.toString();
    }
}