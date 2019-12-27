package com.zhm.service;


import com.google.common.collect.Lists;
import com.zhm.dao.SysDeptDao;
import com.zhm.dao.SysMenuDao;
import com.zhm.dto.DeptDto;
import com.zhm.entity.SysDept;
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

import java.util.*;

/**
 * Created by 赵红明 on 2019/6/3.
 */
@Service
public class SysDeptService {

    private static Logger logger= LoggerFactory.getLogger(SysDeptService.class);

    @Autowired
    private SysDeptDao sysDeptDao;

    /**
     * 分页查询基础
     *
     * @param specification
     * @return
     */
    public Page<SysDept> findAll(Specification<SysDept> specification, Pageable pageable) {
        return sysDeptDao.findAll(specification, pageable);
    }
    public List<SysDept> findAll(Specification<SysDept> specification, Sort sort) {
        return sysDeptDao.findAll(specification,sort);
    }
    public SysDept findOne(Integer id) {
        return sysDeptDao.findOne(id);
    }

    /**
     * 全量查询
     * @param specification
     * @return
     */
    public List<SysDept> findAllData(Specification<SysDept> specification) {
        return sysDeptDao.findAll(specification);
    }


    /**
     * 删除数据(物理删除)
     * @param id
     */
    @Transactional
    public void delete(Integer id){
        sysDeptDao.delete(id);
    }

    /**
     * 新增
     * @param sysMenu
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public SysDept add(SysDept sysMenu)throws Exception {
        LoginUser loginUser= AuthorityUtils.getCurrentUser();
        if(loginUser!=null){
            sysMenu.setCreateUser(loginUser.getUserName());
        }
        sysMenu= sysDeptDao.save(sysMenu);
        return sysMenu;
    }

    /**
     * 保存
     * @param sysMenu
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void save(SysDept sysMenu)throws Exception{
        LoginUser loginUser= AuthorityUtils.getCurrentUser();
        if(loginUser!=null){
            sysMenu.setUpdateUser(loginUser.getUserName());
        }
            sysMenu.setUpdateTime(new Date());
        sysDeptDao.save(sysMenu);
    }
    /**
     * 修改行邮税号状态
     * @param id
     */
    @Transactional(rollbackFor = Exception.class)
    public Result updateStatus(Integer id, Integer status)throws Exception {
        try{
            String userCode="10011";
            SysDept sysDept=sysDeptDao.findOne(id);
            sysDept.setUpdateTime(new Date());
            sysDept.setUpdateUser(userCode);
            if(sysDept!=null){
                sysDept.setStatus(status);
                sysDeptDao.save(sysDept);
                return Result.ok("修改部门状态成功",sysDept);
            }else{
                return Result.failure("没有查询到部门");
            }
        }catch (Exception e){
            e.printStackTrace();
            logger.error("修改部门状态失败：",e.getCause());
            throw new RuntimeException(e.getMessage());
        }

    }

    /**
     * 根据主键来查询
     * @param id
     * @return
     */
    public SysDept findById(Integer id) {
        return sysDeptDao.findOne(id);
    }


    public List<DeptDto> getDeptList(Sort sort){
        Collection<SearchFilter> filters = Lists.newArrayList();
        SearchFilter.addFilter(filters, SearchFilter.build("deleteFlag", Operator.EQ, 0));
        SearchFilter.addFilter(filters, SearchFilter.build("status",Operator.EQ,0));
        Specification<SysDept> specification = DynamicSpecifications.bySearchFilter(filters);
        List<SysDept> list = sysDeptDao.findAll(specification,sort);
        //封装Map
        Map<Integer,DeptDto> map=new HashMap<Integer,DeptDto>();
        List<DeptDto> parentlist=new ArrayList<DeptDto>();
        try{
            if(list!=null&&list.size()>0){
                for(SysDept pm:list){
                    if(pm.getLevel()==0){
                        //根路径
                        List<DeptDto> plist=new ArrayList<DeptDto>();
                        DeptDto pmmodel=new DeptDto();
                        pmmodel.setId(pm.getDeptId());
                        pmmodel.setName(pm.getName());
                        pmmodel.setLevel(pm.getLevel());
                        pmmodel.setParentName("无");
                        pmmodel.setChildren(plist);
                        map.put(pm.getDeptId(),pmmodel);
                        parentlist.add(pmmodel);
                    }else{
                        //查询其父亲节点
                        DeptDto pmodel=map.get(pm.getParentId());
                        if(pmodel!=null){
                            //存到父亲里面

                            List<DeptDto> pmmlist=pmodel.getChildren();
                            if(pmmlist==null){
                                pmmlist=new ArrayList<>();
                            }
                            DeptDto productModel=new DeptDto();
                            productModel.setName(pm.getName());
                            productModel.setLevel(pm.getLevel());
                            productModel.setId(pm.getDeptId());
                            productModel.setParentName(pmodel.getName());
                            pmmlist.add(productModel);
                            pmodel.setChildren(pmmlist);
                            //存到map中
                            map.put(pm.getDeptId(),productModel);
                        }
                    }
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return parentlist;
    }



}
