package com.zhm.dao;

import com.zhm.entity.SysRole;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;


/**
 * Created by 赵红明 on 2019/6/3.
 */
public interface SysRoleDao extends PagingAndSortingRepository<SysRole,Integer>,JpaSpecificationExecutor<SysRole> {
    @Query(value = "select max(role_code) from sys_role where delete_flag=0",nativeQuery = true)
    String findMaxRoleCode();
}
