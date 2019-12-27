package com.zhm.service;


import com.google.common.collect.Lists;
import com.zhm.dao.SysAreaDao;
import com.zhm.dto.AddressDto;
import com.zhm.dto.AreaDto;
import com.zhm.dto.MenuDto;
import com.zhm.entity.SysArea;
import com.zhm.essential.jpa.search.DynamicSpecifications;
import com.zhm.essential.jpa.search.Operator;
import com.zhm.essential.jpa.search.SearchFilter;
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
public class SysAreaService {

    private static Logger logger= LoggerFactory.getLogger(SysAreaService.class);

    @Autowired
    private SysAreaDao sysAreaDao;

    /**
     * 分页查询基础
     *
     * @param specification
     * @return
     */
    public Page<SysArea> findAll(Specification<SysArea> specification, Pageable pageable) {
        return sysAreaDao.findAll(specification, pageable);
    }
    public List<SysArea> findAll(Specification<SysArea> specification, Sort sort) {
        return sysAreaDao.findAll(specification,sort);
    }
    public SysArea findOne(Integer id) {
        return sysAreaDao.findOne(id);
    }

    /**
     * 全量查询
     * @param specification
     * @return
     */
    public List<SysArea> findAllData(Specification<SysArea> specification) {
        return sysAreaDao.findAll(specification);
    }


    /**
     * 删除数据(物理删除)
     * @param id
     */
    @Transactional
    public void delete(Integer id){
        sysAreaDao.delete(id);
    }

    /**
     * 新增
     * @param sysMenu
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public SysArea add(@RequestBody  SysArea sysMenu)throws Exception {
        sysMenu= sysAreaDao.save(sysMenu);
        return sysMenu;
    }

    /**
     * 保存
     * @param sysMenu
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void save(SysArea sysMenu)throws Exception{
            sysMenu.setUpdateUser("10011");
            sysMenu.setUpdateTime(new Date());
        sysAreaDao.save(sysMenu);
    }
    /**
     * 修改行邮税号状态
     * @param id
     */
    @Transactional(rollbackFor = Exception.class)
    public Result updateStatus(Integer id, Integer status)throws Exception {
        try{
            String userCode="10011";
            SysArea productProtaxCategory=sysAreaDao.findOne(id);
            productProtaxCategory.setUpdateTime(new Date());
            productProtaxCategory.setUpdateUser(userCode);
            productProtaxCategory.setStatus(status);
            if(productProtaxCategory!=null){
                sysAreaDao.save(productProtaxCategory);
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
    public SysArea findById(Integer reasonId) {
        return sysAreaDao.findOne(reasonId);
    }




    public List<AreaDto> getAreaList(Sort sort){
        Collection<SearchFilter> filters = Lists.newArrayList(SearchFilter.build("deleteFlag", Operator.EQ, 0));
        Specification<SysArea> specification = DynamicSpecifications.bySearchFilter(filters);
        List<SysArea> list = sysAreaDao.findAll(specification,sort);
        //封装Map
        Map<Integer,AreaDto> map=new HashMap<Integer,AreaDto>();
        List<AreaDto> parentlist=new ArrayList<AreaDto>();
        try{
            if(list!=null&&list.size()>0){
                for(SysArea pm:list){
                    if(pm.getLevel()==1){
                        //根路径
                        List<AreaDto> plist=new ArrayList<AreaDto>();
                        AreaDto pmmodel=new AreaDto();
                        pmmodel.setId(pm.getId());
                        pmmodel.setName(pm.getName());
                        pmmodel.setLevel(pm.getLevel());
                        pmmodel.setParentName("中国");
                        pmmodel.setChildren(plist);
                        map.put(pm.getId(),pmmodel);
                        parentlist.add(pmmodel);
                    }else{
                        //查询其父亲节点
                        AreaDto pmodel=map.get(pm.getParentId());
                        if(pmodel!=null){
                            //存到父亲里面

                            List<AreaDto> pmmlist=pmodel.getChildren();
                            if(pmmlist==null){
                                pmmlist=new ArrayList<>();
                            }
                            AreaDto productModel=new AreaDto();
                            productModel.setName(pm.getName());
                            productModel.setLevel(pm.getLevel());
                            productModel.setId(pm.getId());
                            productModel.setParentName(pmodel.getName());
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



    public List<AddressDto> getAddress(){
        Collection<SearchFilter> filters = Lists.newArrayList(SearchFilter.build("deleteFlag", Operator.EQ, 0));
        Specification<SysArea> specification = DynamicSpecifications.bySearchFilter(filters);
        List<SysArea> list = sysAreaDao.findAll(specification);
        //封装Map
        Map<Integer,AddressDto> map=new HashMap<Integer,AddressDto>();
        List<AddressDto> parentlist=new ArrayList<AddressDto>();
        try{
            if(list!=null&&list.size()>0){
                for(SysArea pm:list){
                    if(pm.getLevel()==1){
                        //根路径
                        List<AddressDto> plist=new ArrayList<AddressDto>();
                        AddressDto pmmodel=new AddressDto();
                        pmmodel.setValue(pm.getId());
                        pmmodel.setLabel(pm.getName());
                        pmmodel.setChildren(plist);
                        map.put(pm.getId(),pmmodel);
                        parentlist.add(pmmodel);
                    }else{
                        //查询其父亲节点
                        AddressDto pmodel=map.get(pm.getParentId());
                        if(pmodel!=null){
                            //存到父亲里面

                            List<AddressDto> pmmlist=pmodel.getChildren();
                            if(pmmlist==null){
                                pmmlist=new ArrayList<>();
                            }
                            AddressDto productModel=new AddressDto();
                            productModel.setLabel(pm.getName());
                            productModel.setValue(pm.getId());
                            pmmlist.add(productModel);
                            pmodel.setChildren(pmmlist);
                            //存到map中
                            map.put(pm.getId(),productModel);
                        }
                    }
                }
            }
        }catch (Exception e) {
           logger.error("系统异常，{}",e);
        }
        return parentlist;
    }

    public List<SysArea> getAreas(String name){
        return sysAreaDao.findByNameAndDeleteFlag(name,0);
    }
}
