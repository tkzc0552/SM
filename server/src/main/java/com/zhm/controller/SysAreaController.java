package com.zhm.controller;

import com.zhm.dto.AddressDto;
import com.zhm.dto.AreaDto;
import com.zhm.entity.SysArea;
import com.zhm.entity.SysMenu;
import com.zhm.essential.domain.PageableFactory;
import com.zhm.essential.jpa.search.DynamicSpecifications;
import com.zhm.essential.jpa.search.Operator;
import com.zhm.essential.jpa.search.SearchFilter;
import com.zhm.essential.web.servlet.Servlets;
import com.zhm.service.SysAreaService;
import com.zhm.util.Result;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.SortDefault;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.util.*;

/**
 * Created by 赵红明 on 2019/6/26.
 */
@RestController
@RequestMapping(value = "/api/sysArea")
public class SysAreaController {
    private static Logger logger= LoggerFactory.getLogger(SysAreaController.class);

    @Autowired
    private SysAreaService sysAreaService;



    /**
     *
     *  分页查询行邮税号
     * @param
     * @return
     */
    @RequestMapping(name = "分页查询菜单信息", value = {""}, method = RequestMethod.GET)
    public Page<SysArea> pageForList(WebRequest request, Pageable pageable) {
        Map<String, Object> searchParams = Servlets.getParametersStartingWith(request, "search_");
        Collection<SearchFilter> filters = SearchFilter.parse(searchParams);
        SearchFilter.addFilter(filters, SearchFilter.build("deleteFlag", Operator.EQ, 0));
        Specification<SysArea> specification = DynamicSpecifications.bySearchFilter(filters);
        pageable = PageableFactory.create(pageable, "createTime", Sort.Direction.DESC);
        Page<SysArea> page = sysAreaService.findAll(specification, pageable);
        for(SysArea product : page) {
            if(product.getLevel()!=1){
                SysArea sysArea=sysAreaService.findOne(product.getParentId());
                product.setParentName(sysArea.getName());
            }else{
                product.setParentName("中国");
            }
        }
        return page;
    }

    /**
     *
     *  不分页查询原行邮税号
     * @param pageable
     * @return
     */
    @RequestMapping(name = "不分页查询菜單", value = {"/queryList"}, method = RequestMethod.GET)
    public Result queryForList(WebRequest request, Pageable pageable) {
        Result result=new Result();
        try{
            Map<String, Object> searchParams = Servlets.getParametersStartingWith(request, "search_");
            Collection<SearchFilter> filters = SearchFilter.parse(searchParams);
            SearchFilter.addFilter(filters, SearchFilter.build("deleteFlag", Operator.EQ, 0));
            SearchFilter.addFilter(filters, SearchFilter.build("status", Operator.EQ, 0));
            Specification<SysArea> specification = DynamicSpecifications.bySearchFilter(filters);
            List<SysArea> plist = sysAreaService.findAllData(specification);
            Collections.sort(plist, new Comparator<SysArea>() {
                public int compare(SysArea o1, SysArea o2) {
                    //按照CityModel的city_code字段进行降序排列
                    if (o1.getCreateTime().getTime() < o2.getCreateTime().getTime()) {
                        return 1;
                    }
                    if (o1.getCreateTime().getTime() == o2.getCreateTime().getTime()) {
                        return 0;
                    }
                    return -1;
                }
            });
            return Result.ok("查询成功！",plist);
        }catch (Exception e){
            return Result.failure("查询菜單异常");
        }

    }
    @ApiOperation(value="根据菜单名称和菜单编号查询菜单信息")
    @RequestMapping(name = "据菜单名称和菜单编号查询菜单信息", value = {"/query"}, method = RequestMethod.GET)
    public List<AreaDto> queryMenu(@SortDefault(value = "level", direction = Sort.Direction.ASC) Sort sort) {
        return sysAreaService.getAreaList(sort);
    }
    /**
     * 新增行邮税号
     *
     * @param sysArea
     * @return
     */
    @RequestMapping(name = "新增菜单", value = {""}, method = RequestMethod.POST)
    public Result add(@RequestBody SysArea sysArea) throws Exception {
        try{
            sysArea=sysAreaService.add(sysArea);
            return  Result.ok("新增菜单成功",sysArea);
        }catch (Exception e){
            e.printStackTrace();
            logger.error("新增菜单失败：{}",e);
            return Result.failure("新增菜单失败："+e.getMessage());
        }


    }
    @RequestMapping(name = "修改菜单", value = {""}, method = RequestMethod.PUT)
    public Result update(@RequestBody SysArea sysArea) throws Exception {
        try{
            sysAreaService.save(sysArea);
            return  Result.ok("修改成功",null);
        }catch (Exception e){
            e.printStackTrace();
            logger.error("系统异常，{}",e);
            return Result.failure("修改菜单失败："+e.getMessage());
        }


    }

    /**
     * 修改状态
     *
     * @param id
     * @return
     */
    @RequestMapping(name = "修改状态", value = {"/{id}/{status}"}, method = RequestMethod.PUT)
    public Result updatStatus(@PathVariable("id") Integer id,@PathVariable("status") Integer status) {
        try {
            return sysAreaService.updateStatus(id,status);
        }catch (Exception e){
            logger.error("修改状态异常",e);
            return Result.failure("删除修改状态异常"+e.getMessage());
        }
    }


    /**
     * 省市县三级联动
     */
    @ApiOperation(value="根据菜单名称和菜单编号查询菜单信息")
    @RequestMapping(name = "据菜单名称和菜单编号查询菜单信息", value = {"/openAddress"}, method = RequestMethod.GET)
    public List<AddressDto> findAddress(@SortDefault(value = "level", direction = Sort.Direction.ASC) Sort sort){
        return sysAreaService.getAddress();
    }



}
