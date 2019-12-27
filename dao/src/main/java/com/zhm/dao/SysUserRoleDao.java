package com.zhm.dao;

import com.zhm.entity.SysRoleMenu;
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
public interface SysUserRoleDao extends PagingAndSortingRepository<SysUserRole,Integer>,JpaSpecificationExecutor<SysUserRole> {
    @Query(value = "select role_id from sys_user_role where delete_flag=0 and user_id=:userId", nativeQuery = true)
    List<Integer> queryChecKUserRole(@Param("userId") Integer userId);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "update sys_user_role set delete_flag =1 where user_id = :userId", nativeQuery = true)
    void updateUserRole(@Param("userId") Integer userId);

}
