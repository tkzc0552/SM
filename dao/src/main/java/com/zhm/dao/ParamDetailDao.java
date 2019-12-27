package com.zhm.dao;

import com.zhm.entity.ParamDetail;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by 赵红明 on 2019/6/3.
 */
public interface ParamDetailDao extends PagingAndSortingRepository<ParamDetail,Integer>,JpaSpecificationExecutor<ParamDetail> {
    ParamDetail findByParamCodeAndParamDetailCodeAndDeleteFlag(String paramCode, String paramDetailCode, Integer deleteFlag);

    List<ParamDetail> findByParamCodeAndDeleteFlag(String paramCode, Integer deleteFlag);
    @Query(value = "select * from param_detail where param_detail_id = :id and delete_flag = :deleteFlag", nativeQuery = true)
    ParamDetail findOne(@Param("id") Integer id, @Param("deleteFlag") Integer deleteFlag);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "update param_detail set param_code = :code1 where param_code = :code2", nativeQuery = true)
    void updateDeatails(@Param("code1") String code1, @Param("code2") String code2);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "update param_detail set delete_flag = 1 where param_code = (select param_code from  params where param_id = :id)", nativeQuery = true)
    void deleteDetails(@Param("id") Integer id);

    @Override
    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "update param_detail set delete_flag = 1 where param_detail_id = :id", nativeQuery = true)
    void delete(@Param("id") Integer id);
}
