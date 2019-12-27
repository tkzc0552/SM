package com.zhm.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zhm.ToolUtil.IPUtil.IPUtils;
import com.zhm.dao.KeyValueDao;
import com.zhm.entity.KeyValue;
import com.zhm.util.WorkIdData;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 赵红明 on 2019/10/23.
 */
@Service
public class WorkIdService {
    private static final String KEY = "GUID_WORK_ID";

    /**
     * 默认最近使用的workId是0
     */
    private static final Long DEFAULT_LAST_WORK_ID = 0L;

    @Autowired
    private KeyValueDao keyValueDao;

    @Transactional
    public Long generateWorkId() {
        String ipAddr = IPUtils.getIpAddr();
        KeyValue keyValuePOExample = new KeyValue();
        List<KeyValue> keyValuePOList = keyValueDao.findByBizType("DEFAULT");

        if (CollectionUtils.isEmpty(keyValuePOList)) {
            // 数据库表里没数据，直接插入
            WorkIdData workIdData = new WorkIdData();
            Map<String, Long> workIdsMap = new HashMap<>(1);
            workIdsMap.put(ipAddr, DEFAULT_LAST_WORK_ID);
            workIdData.setWorkIdsMap(workIdsMap);
            workIdData.setLastWorkId(DEFAULT_LAST_WORK_ID);

            KeyValue insertPO = new KeyValue();
            insertPO.setDataKey(KEY);
            insertPO.setBizType("DEFAULT");
            insertPO.setCreateTime(new Date());
            insertPO.setDataValue(JSON.toJSONString(workIdData));
            keyValueDao.save(insertPO);
            return DEFAULT_LAST_WORK_ID;
        }

        // 数据库表里有数据，先取出来，然后看看有没有保存当前IP地址
        KeyValue keyValuePO = keyValuePOList.get(0);
        WorkIdData workIdData =  JSON.parseObject(keyValuePO.getDataValue(), WorkIdData.class);
        Map<String, Long> workIdsMap = workIdData.getWorkIdsMap();

        if (workIdsMap != null && workIdsMap.containsKey(ipAddr)) {
            // 已经保存了当前ip，直接返回对应的workId
            return workIdsMap.get(ipAddr);
        }

        // 没有保存当前ip，把ip插进去
        if (workIdsMap == null) {
            workIdsMap = new HashMap<>();
        }
        long newLastId = workIdData.getLastWorkId() + 1;
        workIdData.setLastWorkId(newLastId);
        workIdsMap.put(ipAddr, newLastId);
        keyValuePO.setDbUpdateTime(new Date());
        keyValuePO.setDataValue(JSONObject.toJSONString(workIdData));
        keyValueDao.save(keyValuePO);
        return newLastId;
    }
}
