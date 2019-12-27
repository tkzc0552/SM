package com.zhm.controller;

import com.zhm.annotation.SysLogEntity;
import com.zhm.dto.DeptDto;
import com.zhm.dto.MenuDto;
import com.zhm.entity.ParamDetail;
import com.zhm.entity.SysDept;
import com.zhm.entity.SysRole;
import com.zhm.entity.SysUser;
import com.zhm.essential.domain.PageableFactory;
import com.zhm.essential.jpa.search.DynamicSpecifications;
import com.zhm.essential.jpa.search.Operator;
import com.zhm.essential.jpa.search.SearchFilter;
import com.zhm.essential.web.servlet.Servlets;
import com.zhm.service.SysDeptService;
import com.zhm.service.SysMenuService;
import com.zhm.service.SysRoleService;
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
@RequestMapping(value = "/api/sysRole")
public class SysRoleController {
    private static Logger logger= LoggerFactory.getLogger(SysRoleController.class);

    @Autowired
    private SysMenuService sysMenuService;
    @Autowired
    private SysRoleService sysRoleService;
    @Autowired
    private SysDeptService sysDeptService;


    @RequestMapping(name = "分页查询实例", value = {""}, method = RequestMethod.GET)
    public Page<SysRole> queryParamsInfo(WebRequest request, Pageable pageable) {
        //过滤掉一些参数
        Map<String, Object> searchParams = Servlets.getParametersStartingWith(request, "search_");
        Collection<SearchFilter> filters = SearchFilter.parse(searchParams);
        //默认值
        SearchFilter.addFilter(filters, SearchFilter.build("deleteFlag", Operator.EQ, 0));
        Specification<SysRole> specification = DynamicSpecifications.bySearchFilter(filters);
        //排序
        pageable = PageableFactory.create(pageable, "createTime", Sort.Direction.DESC);
        return sysRoleService.findAll(specification, pageable);
    }

    /**
     * 新增
     *
     * @param sysRole
     * @return
     */
    @SysLogEntity("新增/修改角色")
    @RequestMapping(name = "新增/修改实例", value = {""}, method = RequestMethod.POST)
    public Result add(@RequestBody SysRole sysRole) throws Exception {
        try{
            sysRole=sysRoleService.add(sysRole);
            return  Result.ok("新增成功",sysRole);
        }catch (Exception e){
            logger.error("用户新增失败",e);
            return Result.failure("新增失败："+e.getMessage());
        }
    }
    @CrossOrigin(origins = "*")
    @ApiOperation(value="查询角色列表")
    @RequestMapping(name = "查询角色列表", value = {"/list/{name}"}, method = RequestMethod.GET)
    public List<SysRole> list(@PathVariable String name, WebRequest request) {
        Map<String, Object> searchParams = Servlets.getParametersStartingWith(request, "search_");
        Collection<SearchFilter> filters = SearchFilter.parse(searchParams);
        SearchFilter.addFilter(filters, SearchFilter.build("roleName", Operator.LIKE, name));
        SearchFilter.addFilter(filters, SearchFilter.build("status",  Operator.EQ, 0));
        SearchFilter.addFilter(filters, SearchFilter.build("deleteFlag", Operator.EQ, 0));
        Specification<SysRole> specification = DynamicSpecifications.bySearchFilter(filters);
        List<SysRole> list = sysRoleService.findAllData(specification);
        return list;
    }
    @RequestMapping(name = "查询角色列表", value = {"/checkMenu/{roleId}"}, method = RequestMethod.GET)
    public Result roleCheckMenu(@PathVariable Integer roleId, @SortDefault(value = "menuLevel", direction = Sort.Direction.ASC) Sort sort,@SortDefault(value = "level", direction = Sort.Direction.ASC) Sort deptsort){
       List<MenuDto> menuDtos=sysMenuService.getMenuList(sort);
       //查询选中的菜单
        List<Integer> checkMenus=sysRoleService.roleCheckMenu(roleId);
        List<DeptDto> deptDtos=sysDeptService.getDeptList(deptsort);
        List<Integer> checkDept=sysRoleService.roleCheckDept(roleId);
        Map<String,Object> map=new HashMap<>();
        map.put("menuList",menuDtos);
        map.put("checks",checkMenus);
        map.put("deptList",deptDtos);
        map.put("checksDept",checkDept);
        return Result.ok("成功",map);
    }

    @CrossOrigin(origins = "*")
    @ApiOperation(value="查询角色列表")
    @RequestMapping(name = "查询角色列表", value = {"/listAll"}, method = RequestMethod.GET)
    public List<SysRole> listAll(WebRequest request) {
        Map<String, Object> searchParams = Servlets.getParametersStartingWith(request, "search_");
        Collection<SearchFilter> filters = SearchFilter.parse(searchParams);
        SearchFilter.addFilter(filters, SearchFilter.build("status",  Operator.EQ, 0));
        SearchFilter.addFilter(filters, SearchFilter.build("deleteFlag", Operator.EQ, 0));
        Specification<SysRole> specification = DynamicSpecifications.bySearchFilter(filters);
        List<SysRole> list = sysRoleService.findAllData(specification);
        return list;
    }
}
