package com.zhm.service;


import com.google.common.collect.Lists;
import com.zhm.dao.SysMenuDao;

import com.zhm.dto.MenuDto;
import com.zhm.entity.SysMenu;

import com.zhm.essential.jpa.search.DynamicSpecifications;
import com.zhm.essential.jpa.search.Operator;
import com.zhm.essential.jpa.search.SearchFilter;
import com.zhm.util.AuthorityUtils;
import com.zhm.util.LoginUser;
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
import org.springframework.web.bind.annotation.RequestBody;

import java.util.*;

/**
 * Created by 赵红明 on 2019/6/3.
 */
@Service
public class SysMenuService {

    private static Logger logger= LoggerFactory.getLogger(SysMenuService.class);

    @Autowired
    private SysMenuDao sysMenuDao;

    /**
     * 分页查询基础
     *
     * @param specification
     * @return
     */
    public Page<SysMenu> findAll(Specification<SysMenu> specification, Pageable pageable) {
        return sysMenuDao.findAll(specification, pageable);
    }
    public List<SysMenu> findAll(Specification<SysMenu> specification, Sort sort) {
        return sysMenuDao.findAll(specification,sort);
    }
    public SysMenu findOne(Integer id) {
        return sysMenuDao.findOne(id);
    }

    /**
     * 全量查询
     * @param specification
     * @return
     */
    public List<SysMenu> findAllData(Specification<SysMenu> specification) {
        return sysMenuDao.findAll(specification);
    }


    /**
     * 删除数据(物理删除)
     * @param id
     */
    @Transactional
    public void delete(Integer id){
        sysMenuDao.delete(id);
    }

    /**
     * 新增
     * @param sysMenu
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public SysMenu add(@RequestBody  SysMenu sysMenu)throws Exception {
        LoginUser loginUser= AuthorityUtils.getCurrentUser();
        if(loginUser!=null){
            sysMenu.setCreateUser(loginUser.getUserName());
        }
        sysMenu= sysMenuDao.save(sysMenu);
        return sysMenu;
    }

    /**
     * 保存
     * @param sysMenu
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void save(SysMenu sysMenu)throws Exception{
            LoginUser loginUser= AuthorityUtils.getCurrentUser();
            if(loginUser!=null){
                sysMenu.setUpdateUser(loginUser.getUserName());
            }
            sysMenu.setUpdateTime(new Date());
            sysMenuDao.save(sysMenu);
    }
    /**
     * 修改行邮税号状态
     * @param id
     */
    @Transactional(rollbackFor = Exception.class)
    public Result updateStatus(Integer id, Integer status)throws Exception {
        try{

            SysMenu productProtaxCategory=sysMenuDao.findOne(id);
            productProtaxCategory.setUpdateTime(new Date());
            LoginUser loginUser= AuthorityUtils.getCurrentUser();
            if(loginUser!=null){
                productProtaxCategory.setUpdateUser(loginUser.getUserName());
            }
            productProtaxCategory.setStatus(status);
            if(productProtaxCategory!=null){
                sysMenuDao.save(productProtaxCategory);
                return Result.ok("修改行邮税号状态成功",productProtaxCategory);
            }else{
                return Result.failure("没有查询到行邮税号");
            }
        }catch (Exception e){
            e.printStackTrace();
            logger.error("修改行邮税号状态失败：",e.getCause());
            throw new RuntimeException(e.getMessage());
        }

    }

    /**
     * 根据主键来查询
     * @param reasonId
     * @return
     */
    public SysMenu findById(Integer reasonId) {
        return sysMenuDao.findOne(reasonId);
    }


    public List<SysMenu> findByMenuCode(String menuCode){
        return sysMenuDao.findByMenuCodeAndDeleteFlag(menuCode,0);
    }
    public List<SysMenu> findByList(String menuName,String menuCode){
        return sysMenuDao.findByMenuCodeAndMenuName(menuCode,menuName);
    }

    public List<MenuDto> getMenuList(Sort sort){
        Collection<SearchFilter> filters = Lists.newArrayList();
        SearchFilter.addFilter(filters, SearchFilter.build("deleteFlag", Operator.EQ, 0));
        SearchFilter.addFilter(filters, SearchFilter.build("status",Operator.EQ,0));
        Specification<SysMenu> specification = DynamicSpecifications.bySearchFilter(filters);
        List<SysMenu> list = sysMenuDao.findAll(specification,sort);
        //封装Map
        Map<Integer,MenuDto> map=new HashMap<Integer,MenuDto>();
        List<MenuDto> parentlist=new ArrayList<MenuDto>();
        try{
            if(list!=null&&list.size()>0){
                for(SysMenu pm:list){
                    if(pm.getMenuLevel()==0){
                        //根路径
                        List<MenuDto> plist=new ArrayList<MenuDto>();
                        MenuDto pmmodel=new MenuDto();
                        pmmodel.setId(pm.getId());
                        pmmodel.setMenuName(pm.getMenuName());
                        pmmodel.setMenuCode(pm.getMenuCode());
                        pmmodel.setMenuLevel(pm.getMenuLevel());
                        pmmodel.setParentName("无");
                        pmmodel.setChildren(plist);
                        map.put(pm.getId(),pmmodel);
                        parentlist.add(pmmodel);
                    }else{
                        //查询其父亲节点
                        MenuDto pmodel=map.get(pm.getParentId());
                        if(pmodel!=null){
                            //存到父亲里面

                            List<MenuDto> pmmlist=pmodel.getChildren();
                            if(pmmlist==null){
                                pmmlist=new ArrayList<>();
                            }
                            MenuDto productModel=new MenuDto();
                            productModel.setMenuName(pm.getMenuName());
                            productModel.setMenuCode(pm.getMenuCode());
                            productModel.setMenuLevel(pm.getMenuLevel());
                            productModel.setId(pm.getId());
                            productModel.setParentName(pmodel.getMenuName());
                            pmmlist.add(productModel);
                            pmodel.setChildren(pmmlist);
                            //存到map中
                            map.put(pm.getId(),productModel);
                        }
                    }
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return parentlist;
    }

    public String getMaxMenuCode(Integer menuLevel,Integer parentId){
        return sysMenuDao.findMaxMenuCode(menuLevel,parentId);
    }

}
