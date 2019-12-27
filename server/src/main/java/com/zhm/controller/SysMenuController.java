package com.zhm.controller;

import com.google.common.collect.Lists;
import com.zhm.annotation.SysLogEntity;
import com.zhm.dto.DeptDto;
import com.zhm.dto.MenuDto;
import com.zhm.entity.ParamDetail;
import com.zhm.entity.SysMenu;
import com.zhm.entity.SysUser;
import com.zhm.essential.domain.PageableFactory;
import com.zhm.essential.jpa.search.DynamicSpecifications;
import com.zhm.essential.jpa.search.Operator;
import com.zhm.essential.jpa.search.SearchFilter;
import com.zhm.essential.web.servlet.Servlets;
import com.zhm.service.SysDeptService;
import com.zhm.service.SysMenuService;
import com.zhm.service.SysUserService;
import com.zhm.util.MD5Util;
import com.zhm.util.ParamUtil;
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
@RequestMapping(value = "/api/sysMenu")
public class SysMenuController {
    private static Logger logger= LoggerFactory.getLogger(SysMenuController.class);

    @Autowired
    private SysMenuService sysMenuService;

    @Autowired
    private SysDeptService sysDeptService;

    /**
     *
     *  分页查询行邮税号
     * @param
     * @return
     */
    @RequestMapping(name = "分页查询菜单信息", value = {""}, method = RequestMethod.GET)
    public Page<SysMenu> pageForList(WebRequest request, Pageable pageable) {
        Map<String, Object> searchParams = Servlets.getParametersStartingWith(request, "search_");
        Collection<SearchFilter> filters = SearchFilter.parse(searchParams);
        SearchFilter.addFilter(filters, SearchFilter.build("deleteFlag", Operator.EQ, 0));

        Specification<SysMenu> specification = DynamicSpecifications.bySearchFilter(filters);
        pageable = PageableFactory.create(pageable, "createTime", Sort.Direction.DESC);
        Page<SysMenu> page = sysMenuService.findAll(specification, pageable);
        for(SysMenu product : page) {
            if(product.getMenuLevel()!=0){
                SysMenu productProtaxCategory=sysMenuService.findById(product.getParentId());
                product.setParentName(productProtaxCategory.getMenuName());
            }else{
                product.setParentName("无");
            }
        }
        return page;
    }
    @RequestMapping(name = "菜单", value = {"/query"}, method = RequestMethod.GET)
    public List<MenuDto> query(@SortDefault(value = "menuLevel", direction = Sort.Direction.ASC) Sort sort) {
        return sysMenuService.getMenuList(sort);
    }
    @RequestMapping(name = "菜单与数据权限", value = {"/queryData"}, method = RequestMethod.GET)
    public Result queryData(@SortDefault(value = "menuLevel", direction = Sort.Direction.ASC) Sort menusort, @SortDefault(value = "level", direction = Sort.Direction.ASC) Sort deptsort) {
        List<MenuDto> menus=sysMenuService.getMenuList(menusort);
        List<DeptDto> depts=sysDeptService.getDeptList(deptsort);
        Map<String,Object> map=new HashMap<>();
        map.put("menus",menus);
        map.put("depts",depts);
        return Result.ok("查询成功",map);
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
            Specification<SysMenu> specification = DynamicSpecifications.bySearchFilter(filters);
            List<SysMenu> plist = sysMenuService.findAllData(specification);
            Collections.sort(plist, new Comparator<SysMenu>() {
                public int compare(SysMenu o1, SysMenu o2) {
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
     * @param sysMenu
     * @return
     */
    @SysLogEntity("新增菜单")
    @RequestMapping(name = "新增菜单", value = {""}, method = RequestMethod.POST)
    public Result add(@RequestBody SysMenu sysMenu) throws Exception {
        try{
            //查询出同层的最大MenuCode
            String maxMenuCode=sysMenuService.getMaxMenuCode(sysMenu.getMenuLevel(),sysMenu.getParentId());
            if(maxMenuCode!=null){
                String endCode=maxMenuCode.substring(maxMenuCode.length()-3,maxMenuCode.length());
                //最大MenuCode+1
                Integer menuInt=Integer.parseInt(endCode)+1;
                String menuCode="";
                if(menuInt<10){
                    menuCode=maxMenuCode.substring(0,maxMenuCode.length()-1)+String.valueOf(menuInt);
                }else if(menuInt>=10&&menuInt<100){
                    menuCode=maxMenuCode.substring(0,maxMenuCode.length()-2)+String.valueOf(menuInt);
                }else if(menuInt>=100){
                    menuCode=maxMenuCode.substring(0,maxMenuCode.length()-3)+String.valueOf(menuInt);
                }
                sysMenu.setMenuCode(menuCode);
            }else{
              SysMenu parentMenu=sysMenuService.findById(sysMenu.getParentId());
              String menuCode=parentMenu.getMenuCode();
              sysMenu.setMenuCode(menuCode+"001");
            }
            sysMenu.setStatus(0);
            sysMenu=sysMenuService.add(sysMenu);
            return  Result.ok("新增菜单成功",sysMenu);
        }catch (Exception e){
            e.printStackTrace();
            logger.error("新增菜单失败：{}",e);
            return Result.failure("新增菜单失败："+e.getMessage());
        }


    }
    @SysLogEntity("修改菜单")
    @RequestMapping(name = "修改菜单", value = {""}, method = RequestMethod.PUT)
    public Result update(@RequestBody SysMenu sysMenu) throws Exception {
        try{
            SysMenu pp=sysMenuService.findById(sysMenu.getId());
            pp.setMenuName(sysMenu.getMenuName());
            pp.setMenuCode(sysMenu.getMenuCode());
            pp.setMenuEname(sysMenu.getMenuEname());
            pp.setMenuUrl(sysMenu.getMenuUrl());
            pp.setMenuOrder(sysMenu.getMenuOrder());
            pp=sysMenuService.add(pp);
            return  Result.ok("修改菜单成功",pp);
        }catch (Exception e){
            return Result.failure("修改菜单失败："+e.getMessage());
        }


    }

    /**
     * 修改行邮税号状态
     *
     * @param id
     * @return
     */
    @SysLogEntity("修改菜单状态")
    @RequestMapping(name = "修改菜单状态", value = {"/{id}/{status}"}, method = RequestMethod.PUT)
    public Result updatStatus(@PathVariable("id") Integer id,@PathVariable("status") Integer status) {
        try {
            return sysMenuService.updateStatus(id,status);
        }catch (Exception e){
            logger.error("修改菜单状态异常",e);
            return Result.failure("删除修改菜单状态异常"+e.getMessage());
        }
    }

    @CrossOrigin(origins = "*")
    @ApiOperation(value="根据菜单名称和菜单编号查询菜单信息")
    @RequestMapping(name = "据菜单名称和菜单编号查询菜单信息", value = {"/queryMenu"}, method = RequestMethod.GET)
    public List<SysMenu> queryMenu(String menuCode,String menuName) {
        if(menuCode==null){
            menuCode="";
        }
        if(menuName==null){
            menuName="";
        }
        return sysMenuService.findByList(menuName,menuCode);
    }

    @CrossOrigin(origins = "*")
    @ApiOperation(value="查询菜单列表")
    @RequestMapping(name = "查询菜单列表", value = {"/list"}, method = RequestMethod.GET)
    public List<SysMenu> list(WebRequest request) {
        Map<String, Object> searchParams = Servlets.getParametersStartingWith(request, "search_");
        Collection<SearchFilter> filters = SearchFilter.parse(searchParams);
        SearchFilter.addFilter(filters, SearchFilter.build("status", Operator.EQ, 0));
        SearchFilter.addFilter(filters, SearchFilter.build("deleteFlag", Operator.EQ, 0));
        Specification<SysMenu> specification = DynamicSpecifications.bySearchFilter(filters);
        List<SysMenu> list = sysMenuService.findAllData(specification);
        return list;
    }



}
