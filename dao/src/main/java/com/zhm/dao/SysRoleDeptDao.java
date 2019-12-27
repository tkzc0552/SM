package com.zhm.dao;

import com.zhm.entity.SysRoleDept;
import com.zhm.entity.SysUserRole;
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
public interface SysRoleDeptDao extends PagingAndSortingRepository<SysRoleDept,Integer>,JpaSpecificationExecutor<SysRoleDept> {
    @Query(value = "select dept_id from sys_role_dept where delete_flag=0 and role_id=:roleId", nativeQuery = true)
    List<Integer> queryChecKRoleDept(@Param("roleId") Integer roleId);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "update sys_role_dept set delete_flag =1 where role_id = :roleId", nativeQuery = true)
    void updateRoleDept(@Param("roleId") Integer roleId);

}
