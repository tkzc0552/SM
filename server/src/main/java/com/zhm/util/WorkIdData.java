package com.zhm.util;

import java.util.Map;

/**
 * Created by 赵红明 on 2019/10/23.
 */
public class WorkIdData {

    private Map<String, Long> workIdsMap;
    private Long lastWorkId;

    public Map<String, Long> getWorkIdsMap() {
        return workIdsMap;
    }

    public void setWorkIdsMap(Map<String, Long> workIdsMap) {
        this.workIdsMap = workIdsMap;
    }

    public Long getLastWorkId() {
        return lastWorkId;
    }

    public void setLastWorkId(Long lastWorkId) {
        this.lastWorkId = lastWorkId;
    }
}
