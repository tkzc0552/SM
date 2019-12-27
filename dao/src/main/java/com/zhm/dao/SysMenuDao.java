package com.zhm.dao;

import com.zhm.entity.RandomCode;
import com.zhm.entity.SysMenu;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by 赵红明 on 2019/6/3.
 */
public interface SysMenuDao extends PagingAndSortingRepository<SysMenu,Integer>,JpaSpecificationExecutor<SysMenu> {
    List<SysMenu> findByMenuCodeAndDeleteFlag(String menuCode,Integer deleteFlag);
    List<SysMenu> findByMenuCodeAndMenuName(String menuCode,String menuName);
    @Query(value = "select max(menu_code) from sys_menu where menu_level=:menuLevel and parent_id=:parentId and delete_flag=0",nativeQuery = true)
    String findMaxMenuCode(@Param("menuLevel") Integer menuLevel,@Param("parentId") Integer parentId);
}
