package com.zhm.controller;

import com.google.common.collect.Lists;
import com.zhm.annotation.SysLogEntity;
import com.zhm.dto.DeptDto;
import com.zhm.dto.MenuDto;
import com.zhm.entity.SysDept;
import com.zhm.entity.SysMenu;
import com.zhm.essential.domain.PageableFactory;
import com.zhm.essential.jpa.search.DynamicSpecifications;
import com.zhm.essential.jpa.search.Operator;
import com.zhm.essential.jpa.search.SearchFilter;
import com.zhm.essential.web.servlet.Servlets;
import com.zhm.service.SysDeptService;
import com.zhm.service.SysMenuService;
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
@RequestMapping(value = "/api/sysDept")
public class SysDeptController {
    private static Logger logger= LoggerFactory.getLogger(SysDeptController.class);

    @Autowired
    private SysDeptService sysDeptService;

    /**
     *
     *  分页查询行邮税号
     * @param
     * @return
     */
    @RequestMapping(name = "分页查询部門信息", value = {""}, method = RequestMethod.GET)
    public Page<SysDept> pageForList(WebRequest request, Pageable pageable) {
        Map<String, Object> searchParams = Servlets.getParametersStartingWith(request, "search_");
        Collection<SearchFilter> filters = SearchFilter.parse(searchParams);
        SearchFilter.addFilter(filters, SearchFilter.build("deleteFlag", Operator.EQ, 0));
        Specification<SysDept> specification = DynamicSpecifications.bySearchFilter(filters);
        pageable = PageableFactory.create(pageable, "createTime", Sort.Direction.DESC);
        Page<SysDept> page = sysDeptService.findAll(specification, pageable);
        for(SysDept product : page) {
            if(product.getLevel()!=0){
                SysDept sysDept=sysDeptService.findById(product.getParentId());
                product.setParentName(sysDept.getName());
            }else{
                product.setParentName("无");
            }
        }
        return page;
    }
    @RequestMapping(name = "商品分类列表", value = {"/query"}, method = RequestMethod.GET)
    public List<DeptDto> query(WebRequest request, @SortDefault(value = "level", direction = Sort.Direction.ASC) Sort sort) {

        return sysDeptService.getDeptList(sort);
    }

    /**
     *
     *  不分页查询原行邮税号
     * @param pageable
     * @return
     */
    @RequestMapping(name = "不分页查询部门", value = {"/queryList"}, method = RequestMethod.GET)
    public Result queryForList(WebRequest request, Pageable pageable) {
        Result result=new Result();
        try{

            Map<String, Object> searchParams = Servlets.getParametersStartingWith(request, "search_");
            Collection<SearchFilter> filters = SearchFilter.parse(searchParams);
            SearchFilter.addFilter(filters, SearchFilter.build("deleteFlag", Operator.EQ, 0));
            Specification<SysDept> specification = DynamicSpecifications.bySearchFilter(filters);
            List<SysDept> plist = sysDeptService.findAllData(specification);
            Collections.sort(plist, new Comparator<SysDept>() {
                public int compare(SysDept o1, SysDept o2) {
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

    /**
     * 新增行邮税号
     *
     * @param sysDept
     * @return
     */
    @SysLogEntity("新增部门")
    @RequestMapping(name = "新增部门", value = {""}, method = RequestMethod.POST)
    public Result add(@RequestBody SysDept sysDept) throws Exception {
        try{
            //查询父亲节点信息
            SysDept parentMenu=sysDeptService.findById(sysDept.getParentId());
            //
            sysDept=sysDeptService.add(sysDept);
            return  Result.ok("新增部门成功",sysDept);
        }catch (Exception e){
            return Result.failure("新增部门失败："+e.getMessage());
        }


    }
    @SysLogEntity("修改部门")
    @RequestMapping(name = "修改部门", value = {""}, method = RequestMethod.PUT)
    public Result update(@RequestBody SysDept sysDept) throws Exception {
        try{
            SysDept pp=sysDeptService.findById(sysDept.getDeptId());
            if(pp!=null){
                sysDeptService.save(sysDept);
                return Result.ok("修改部门失败，原因是没有查到行邮税号信息",null);
            }else{
                return  Result.failure("修改部门失败，原因是没有查到行邮税号信息");
            }

        }catch (Exception e){
            return Result.failure("修改部门失败："+e.getMessage());
        }


    }

    /**
     * 修改行邮税号状态
     *
     * @param id
     * @return
     */
    @SysLogEntity("修改部门状态")
    @RequestMapping(name = "修改部门状态", value = {"/{id}/{status}"}, method = RequestMethod.PUT)
    public Result updatStatus(@PathVariable("id") Integer id,@PathVariable("status") Integer status) {
        try {
            return sysDeptService.updateStatus(id,status);
        }catch (Exception e){
            logger.error("修改菜单状态异常",e);
            return Result.failure("删除修改菜单状态异常"+e.getMessage());
        }
    }

    /*@CrossOrigin(origins = "*")
    @ApiOperation(value="根据菜单名称和菜单编号查询菜单信息")
    @RequestMapping(name = "据菜单名称和菜单编号查询菜单信息", value = {"/queryMenu"}, method = RequestMethod.GET)
    public List<SysDept> queryMenu(String menuCode,String menuName) {
        if(menuCode==null){
            menuCode="";
        }
        if(menuName==null){
            menuName="";
        }
        return sysDeptService.findByList(menuName,menuCode);
    }*/

    @CrossOrigin(origins = "*")
    @ApiOperation(value="查询部门列表")
    @RequestMapping(name = "查询部门列表", value = {"/list/{name}"}, method = RequestMethod.GET)
    public List<SysDept> list(@PathVariable String name, WebRequest request) {
        Map<String, Object> searchParams = Servlets.getParametersStartingWith(request, "search_");
        Collection<SearchFilter> filters = SearchFilter.parse(searchParams);
        SearchFilter.addFilter(filters, SearchFilter.build("status", Operator.EQ, 0));
        SearchFilter.addFilter(filters, SearchFilter.build("name", Operator.LIKE, name));

        SearchFilter.addFilter(filters, SearchFilter.build("deleteFlag", Operator.EQ, 0));
        Specification<SysDept> specification = DynamicSpecifications.bySearchFilter(filters);
        List<SysDept> list = sysDeptService.findAllData(specification);
        for(SysDept product : list) {
            if(product.getLevel()!=0){
                SysDept sysDept=sysDeptService.findById(product.getParentId());
                product.setParentName(sysDept.getName());
            }else{
                product.setParentName("无");
            }
        }
        return list;
    }

    @ApiOperation(value="查询部门列表")
    @RequestMapping(name = "查询部门列表", value = {"/listName/{id}"}, method = RequestMethod.GET)
    public String listName(@PathVariable Integer id, WebRequest request) {
        String name="";
        SysDept sysDept=sysDeptService.findById(id);
        if(sysDept!=null){
            name=sysDept.getName();
        }
        return name;
    }
    @CrossOrigin(origins = "*")
    @ApiOperation(value="查询部门列表")
    @RequestMapping(name = "查询部门列表", value = {"/listAll"}, method = RequestMethod.GET)
    public List<SysDept> listAll(String name, WebRequest request) {
        Map<String, Object> searchParams = Servlets.getParametersStartingWith(request, "search_");
        Collection<SearchFilter> filters = SearchFilter.parse(searchParams);
        SearchFilter.addFilter(filters, SearchFilter.build("status", Operator.EQ, 0));
        if(name!=null&&!name.equals("")){
            SearchFilter.addFilter(filters, SearchFilter.build("name", Operator.LIKE, name));
        }

        SearchFilter.addFilter(filters, SearchFilter.build("deleteFlag", Operator.EQ, 0));
        Specification<SysDept> specification = DynamicSpecifications.bySearchFilter(filters);
        List<SysDept> list = sysDeptService.findAllData(specification);
        for(SysDept product : list) {
            if(product.getLevel()!=0){
                SysDept sysDept=sysDeptService.findById(product.getParentId());
                product.setParentName(sysDept.getName());
            }else{
                product.setParentName("无");
            }
        }
        return list;
    }
}
