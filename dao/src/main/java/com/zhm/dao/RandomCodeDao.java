package com.zhm.dao;

import com.zhm.entity.RandomCode;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by 赵红明 on 2019/6/3.
 */
public interface RandomCodeDao extends PagingAndSortingRepository<RandomCode,Integer>,JpaSpecificationExecutor<RandomCode> {
    List<RandomCode> findByRandomCodeAndDeleteFlag(String randomCode, Integer deleteFlag);
    List<RandomCode> findByUserIdAndRandomCodeAndDeleteFlag(Integer userId, String randomCode, Integer deleteFlag);
    List<RandomCode> findByDeleteFlag(Integer deleteFlag);
}
