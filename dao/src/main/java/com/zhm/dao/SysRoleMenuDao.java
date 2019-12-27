package com.zhm.dao;

import com.zhm.entity.SysDept;
import com.zhm.entity.SysRoleMenu;
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
public interface SysRoleMenuDao extends PagingAndSortingRepository<SysRoleMenu,Integer>,JpaSpecificationExecutor<SysRoleMenu> {
    @Query(value = "select srm.menu_id from sys_role_menu srm left join sys_menu sm on srm.menu_id=sm.id left join sys_role sr on sr.id=srm.role_id WHERE srm.delete_flag=0 and sm.delete_flag=0 and sr.delete_flag=0 and sr.`status`=0 and sm.`status`=0 and srm.role_id=:roleId", nativeQuery = true)
    List<Integer> queryChecKMenu(@Param("roleId") Integer roleId);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "update sys_role_menu set delete_flag =1 where role_id = :roleId", nativeQuery = true)
    void updateRoleMenu( @Param("roleId") Integer roleId);

}
