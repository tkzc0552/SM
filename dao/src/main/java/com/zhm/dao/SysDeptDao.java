package com.zhm.dao;

import com.zhm.entity.SysDept;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by 赵红明 on 2019/6/3.
 */
public interface SysDeptDao extends PagingAndSortingRepository<SysDept,Integer>,JpaSpecificationExecutor<SysDept> {
}
