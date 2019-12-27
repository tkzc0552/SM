package com.zhm.dao;

import com.zhm.entity.Params;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by 赵红明 on 2019/6/3.
 */
public interface ParamsDao extends PagingAndSortingRepository<Params,Integer>,JpaSpecificationExecutor<Params> {
  @Override
  @Transactional
  @Modifying(clearAutomatically = true)
  @Query(value = "update params set delete_flag = 1 where param_id = :id", nativeQuery = true)
  void delete(@Param("id") Integer id);

  @Query(value = "select * from params where param_id = :id and delete_flag = :deleteFlag", nativeQuery = true)
  Params findOne(@Param("id") Integer id, @Param("deleteFlag") Integer deleteFlag);
}
