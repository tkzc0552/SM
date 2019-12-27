package com.zhm.dao;

import com.zhm.entity.SysArea;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by 赵红明 on 2019/6/3.
 */
public interface SysAreaDao extends PagingAndSortingRepository<SysArea,Integer>,JpaSpecificationExecutor<SysArea> {
    List<SysArea> findByNameAndDeleteFlag(String name, Integer deleteFlag);
}
