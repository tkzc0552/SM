package com.zhm.controller;

import com.zhm.annotation.SysLogEntity;
import com.zhm.entity.SysAnnounce;
import com.zhm.essential.domain.PageableFactory;
import com.zhm.essential.jpa.search.DynamicSpecifications;
import com.zhm.essential.jpa.search.Operator;
import com.zhm.essential.jpa.search.SearchFilter;
import com.zhm.essential.web.servlet.Servlets;
import com.zhm.service.SysAnnounceService;
import com.zhm.service.SysDeptService;
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
@RequestMapping(value = "/api/sysAnnounce")
public class SysAnnounceController {
    private static Logger logger= LoggerFactory.getLogger(SysAnnounceController.class);

    @Autowired
    private SysAnnounceService sysAnnounceService;

    /**
     *
     *  分页查询公告
     * @param
     * @return
     */
    @RequestMapping(name = "分页查询公告信息", value = {""}, method = RequestMethod.GET)
    public Page<SysAnnounce> pageForList(WebRequest request, Pageable pageable) {
        Map<String, Object> searchParams = Servlets.getParametersStartingWith(request, "search_");
        Collection<SearchFilter> filters = SearchFilter.parse(searchParams);
        SearchFilter.addFilter(filters, SearchFilter.build("deleteFlag", Operator.EQ, 0));
        Specification<SysAnnounce> specification = DynamicSpecifications.bySearchFilter(filters);
        pageable = PageableFactory.create(pageable, "createTime", Sort.Direction.DESC);
        Page<SysAnnounce> page = sysAnnounceService.findAll(specification, pageable);
        return page;
    }


    /**
     *
     *  不分页查询公告
     * @param pageable
     * @return
     */
    @RequestMapping(name = "不分页查询公告", value = {"/queryList"}, method = RequestMethod.GET)
    public Result queryForList(WebRequest request, Pageable pageable) {
        Result result=new Result();
        try{
            Map<String, Object> searchParams = Servlets.getParametersStartingWith(request, "search_");
            Collection<SearchFilter> filters = SearchFilter.parse(searchParams);
            SearchFilter.addFilter(filters, SearchFilter.build("deleteFlag", Operator.EQ, 0));
            Specification<SysAnnounce> specification = DynamicSpecifications.bySearchFilter(filters);
            List<SysAnnounce> plist = sysAnnounceService.findAllData(specification);
            Collections.sort(plist, new Comparator<SysAnnounce>() {
                public int compare(SysAnnounce o1, SysAnnounce o2) {
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
            return Result.failure("查询公告异常");
        }

    }

    /**
     * 新增公告
     *
     * @param sysDept
     * @return
     */
    @SysLogEntity("新增公告")
    @RequestMapping(name = "新增公告", value = {""}, method = RequestMethod.POST)
    public Result add(@RequestBody SysAnnounce sysDept) throws Exception {
        try{
            sysDept=sysAnnounceService.add(sysDept);
            return  Result.ok("新增公告成功",sysDept);
        }catch (Exception e){
            return Result.failure("新增公告失败："+e.getMessage());
        }


    }
    @SysLogEntity("修改公告")
    @RequestMapping(name = "修改公告", value = {""}, method = RequestMethod.PUT)
    public Result update(@RequestBody SysAnnounce sysDept) throws Exception {
        try{
            SysAnnounce pp=sysAnnounceService.findById(sysDept.getId());
            if(pp!=null){
                sysAnnounceService.save(sysDept);
                return Result.ok("修改公告失败，原因是没有查到公告信息",null);
            }else{
                return  Result.failure("修改公告失败，原因是没有查到公告信息");
            }

        }catch (Exception e){
            return Result.failure("修改公告失败："+e.getMessage());
        }


    }

    /**
     * 修改行邮税号状态
     *
     * @param id
     * @return
     */
    @SysLogEntity("修改公告状态")
    @RequestMapping(name = "修改公告状态", value = {"/{id}/{status}"}, method = RequestMethod.PUT)
    public Result updatStatus(@PathVariable("id") Integer id,@PathVariable("status") Integer status) {
        try {
            return sysAnnounceService.updateStatus(id,status);
        }catch (Exception e){
            logger.error("修改公告状态异常",e);
            return Result.failure("删除修改公告状态异常"+e.getMessage());
        }
    }


}
