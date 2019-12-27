package com.zhm.dao;

import com.zhm.entity.KeyValue;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by 赵红明 on 2019/6/3.
 */
public interface KeyValueDao extends PagingAndSortingRepository<KeyValue,Integer>,JpaSpecificationExecutor<KeyValue> {
    List<KeyValue> findByBizType(String bizType);
}
