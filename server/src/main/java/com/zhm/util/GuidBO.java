package com.zhm.util;

import java.util.Date;

/**
 * Created by 赵红明 on 2019/10/23.
 */
public class GuidBO {
    /**
     * 生成id的时间戳
     */
    private Date lockTime;

    /**
     * 机器id
     */
    private Long workId;

    /**
     * 机器ip地址
     */
    private String workIpAddr;

    /**
     * 生成的序列号
     */
    private Long sequence;

    public Date getLockTime() {
        return lockTime;
    }

    public void setLockTime(Date lockTime) {
        this.lockTime = lockTime;
    }

    public Long getWorkId() {
        return workId;
    }

    public void setWorkId(Long workId) {
        this.workId = workId;
    }

    public String getWorkIpAddr() {
        return workIpAddr;
    }

    public void setWorkIpAddr(String workIpAddr) {
        this.workIpAddr = workIpAddr;
    }

    public Long getSequence() {
        return sequence;
    }

    public void setSequence(Long sequence) {
        this.sequence = sequence;
    }
}
