package com.zhm.dao;

/**
 * Created by 赵红明 on 2019/7/31.
 */

import com.zhm.entity.SysGenerator;
import com.zhm.entity.SysMenu;
import com.zhm.entity.SysUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

public interface SysUserDao extends PagingAndSortingRepository<SysUser,Integer>,JpaSpecificationExecutor<SysUser> {
    List<SysUser> findByUserNameAndPassWordAndStatusAndDeleteFlag(String userName, String passWord,Integer status,Integer deleteFlag);
    List<SysUser> findByUserNameAndDeleteFlag(String userName,Integer deleteFlag);
     List<SysMenu> queryForUserMenu(Integer userId,String menuCode);
     List<SysMenu> queryForUserMenuForView(Integer userId);
    @Query(value = "select DATE_FORMAT(create_time,'%Y-%m') months,count(user_id) count from sys_user group by months", nativeQuery = true)
    List<Map<String, String>> findRegister();
    /**
     * 下面是代码生成器部分
     * @param params
     * @param pageable
     * @return
     */
    Page<SysGenerator> queryList(Map<String, Object[]> params, Pageable pageable);
    @Query(value = "select table_name tableName, engine, table_comment tableComment, create_time createTime from information_schema.tables where table_schema = (select database()) and table_name = :tableName", nativeQuery = true)
    Map<String, String> queryTable(@Param("tableName") String tableName);
    @Query(value = "select column_name columnName, data_type dataType, column_comment columnComment, column_key columnKey, extra from information_schema.columns where table_name =:tableName and table_schema = (select database()) order by ordinal_position", nativeQuery = true)
    List<Map<String, String>> queryColumns(@Param("tableName") String tableName);


}
