package com.zhm.service;


import com.zhm.dao.*;
import com.zhm.dto.CheckUserDto;
import com.zhm.dto.UpdatePassword;
import com.zhm.entity.*;
import com.zhm.util.AuthorityUtils;
import com.zhm.util.LoginUser;
import com.zhm.util.MD5Util;
import com.zhm.util.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by 赵红明 on 2019/6/3.
 */
@Service
public class SysRoleService {

    private static Logger logger= LoggerFactory.getLogger(SysRoleService.class);

    @Autowired
    private SysRoleDao sysRoleDao;
    @Autowired
    private SysRoleMenuDao sysRoleMenuDao;
    @Autowired
    private SysRoleDeptDao sysRoleDeptDao;


    /**
     * 分页查询基础
     *
     * @param specification
     * @return
     */
    public Page<SysRole> findAll(Specification<SysRole> specification, Pageable pageable) {
        return sysRoleDao.findAll(specification, pageable);
    }
    public List<SysRole> findAll(Specification<SysRole> specification, Sort sort) {
        return sysRoleDao.findAll(specification,sort);
    }
    public SysRole findOne(Integer id) {
        return sysRoleDao.findOne(id);
    }

    /**
     * 全量查询
     * @param specification
     * @return
     */
    public List<SysRole> findAllData(Specification<SysRole> specification) {
        return sysRoleDao.findAll(specification);
    }


    /**
     * 删除数据(物理删除)
     * @param id
     */
    @Transactional
    public void delete(Integer id){
        sysRoleDao.delete(id);
    }

    /**
     * 新增
     * @param sysRole
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public SysRole add(SysRole sysRole)throws Exception {
        //修改
        if(sysRole.getId()!=null&& sysRole.getId()!=0){
            //逻辑删除菜单与角色关系
            sysRoleMenuDao.updateRoleMenu(sysRole.getId());
            //逻辑删除角色和部门关系
            sysRoleDeptDao.updateRoleDept(sysRole.getId());
            //新增菜单与角色关系
            addMenuList(sysRole);
            //新增角色和部门关系
            addDeptList(sysRole);
            LoginUser loginUser= AuthorityUtils.getCurrentUser();
            if(loginUser!=null){
                sysRole.setUpdateUser(loginUser.getUserName());
            }
            sysRole= sysRoleDao.save(sysRole);
        }else{//新增
            String maxRoleCode=sysRoleDao.findMaxRoleCode();
            String roleCode="00"+(Integer.parseInt(maxRoleCode)+1);
            sysRole.setRoleCode(roleCode);
            LoginUser loginUser= AuthorityUtils.getCurrentUser();
            if(loginUser!=null){
                sysRole.setCreateUser(loginUser.getUserName());
            }
            sysRole= sysRoleDao.save(sysRole);
            //添加菜单和角色关系信息
            addMenuList(sysRole);
            //新增角色和部门关系
            addDeptList(sysRole);
        }

        return sysRole;
    }

    /**
     * 保存
     * @param sysUser
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void save(SysRole sysUser)throws Exception{
        sysUser.setUpdateTime(new Date());
        sysRoleDao.save(sysUser);
    }

    public List<Integer> roleCheckMenu(Integer roleId){
        //查询已选中的菜单
       return sysRoleMenuDao.queryChecKMenu(roleId);
    }

    public List<Integer> roleCheckDept(Integer roleId){
        //查询已选中的菜单
        return sysRoleDeptDao.queryChecKRoleDept(roleId);
    }

    public void addMenuList(SysRole sysRole){
        if(sysRole.getMenuList()!=null&&sysRole.getMenuList().size()>0){
            for(Integer menuId:sysRole.getMenuList()){
                SysRoleMenu sysRoleMenu=new SysRoleMenu();
                sysRoleMenu.setRoleId(sysRole.getId());
                sysRoleMenu.setMenuId(menuId);
                sysRoleMenuDao.save(sysRoleMenu);
            }
        }
    }

    public void addDeptList(SysRole sysRole){
        if(sysRole.getDataList()!=null&&sysRole.getDataList().size()>0){
            for(Integer deptId:sysRole.getDataList()){
                SysRoleDept sysRoleDept=new SysRoleDept();
                sysRoleDept.setRoleId(sysRole.getId());
                sysRoleDept.setDeptId(deptId);
                sysRoleDeptDao.save(sysRoleDept);
            }
        }
    }
}
