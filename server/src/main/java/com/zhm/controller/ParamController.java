package com.zhm.controller;


import com.zhm.annotation.SysLogEntity;
import com.zhm.dto.ParamDetailDTO;
import com.zhm.entity.ParamDetail;
import com.zhm.entity.Params;
import com.zhm.essential.domain.PageableFactory;
import com.zhm.essential.jpa.search.DynamicSpecifications;
import com.zhm.essential.jpa.search.SearchFilter;
import com.zhm.essential.web.servlet.Servlets;
import com.zhm.service.ParamService;
import com.zhm.util.ParamUtil;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.util.*;

/**
 * Created by 赵红明 on 2019/3/20.
 */
@RestController
@RequestMapping(value = {"/api/params"})
public class ParamController {
    private final static Logger logger = LoggerFactory.getLogger(ParamController.class);
    @Autowired
    private ParamService paramsService;

    @RequestMapping(name = "基本参数总参列表页面", value = {"/queryParamsInfo"}, method = RequestMethod.GET)
    public Page<Params> queryParamsInfo(WebRequest request, Pageable pageable) {
        Map<String, Object> searchParams = Servlets.getParametersStartingWith(request, "search_");
        Collection<SearchFilter> filters = SearchFilter.parse(searchParams);
        Specification<Params> specification = DynamicSpecifications.bySearchFilter(filters);
        pageable = PageableFactory.create(pageable, "updateTime", Sort.Direction.DESC);
        return paramsService.findAll(specification, pageable);
    }

    /**
     * 基本子参数列表
     *
     * @param pageable
     * @return
     */
    @RequestMapping(name = "基本参数子参列表页面", value = {"/queryParamsDetailInfo"}, method = RequestMethod.GET)
    public Page<ParamDetail> queryParamsDetailInfo(WebRequest request, Pageable pageable) {
        Map<String, Object> searchParams = Servlets.getParametersStartingWith(request, "search_");
        Collection<SearchFilter> filters = SearchFilter.parse(searchParams);
        Specification<ParamDetail> specification = DynamicSpecifications.bySearchFilter(filters);
        Sort.Order[] orders = new Sort.Order[2];
        orders[0] = new Sort.Order(Sort.Direction.ASC, "sortNum");
        orders[1] = new Sort.Order(Sort.Direction.DESC, "updateTime");
        return paramsService.findDetailAll(specification, PageableFactory.create(pageable, orders));
    }

    /**
     * 保存总参
     *
     * @param
     * @return Boolean
     */
    @SysLogEntity("新增总参")
    @ApiOperation(value = "总参保存")
    @RequestMapping(name = "总参保存", value = {"/insertParamsModel"}, method = RequestMethod.POST)
    public Boolean insertParamsModel(@RequestBody Params params) {
        params.setCreateUser("111");
        params.setCreateTime(new Date());
        paramsService.save(params);
        return true;
    }

    /**
     * 保存子参
     *
     * @param
     * @return Boolean
     */
    @SysLogEntity("新增子参")
    @ApiOperation(value = "子参保存")
    @RequestMapping(name = "子参保存", value = {"/insertParamsDetailModel"}, method = RequestMethod.POST)
    public Boolean insertParamsModel(@RequestBody ParamDetail paramDetail) {

        paramDetail.setCreateUser("11");
        paramDetail.setCreateTime(new Date());
        //paramsService.saveDetail(paramDetail, true);
        paramsService.saveD(paramDetail);
        return true;
    }

    /**
     * 去重code
     *
             * @param parentCode
     * @param sonCode
     * @param isParent
     * @return Boolean
     */
    @ApiOperation(value = "去重code")
    @RequestMapping(name = "去重code", value = {"/checkCode"}, method = RequestMethod.GET)
    public Boolean checkCode(@RequestParam("parentCode") String parentCode, @RequestParam(value = "sonCode", required = false) String sonCode, @RequestParam("isParent") Boolean isParent) {
        return paramsService.checkCode(parentCode, sonCode, isParent);
    }

    /**
     * 修改总参
     *
     * @param
     * @return Boolean
     */
    @SysLogEntity("总参修改")
    @ApiOperation(value = "总参修改")
    @RequestMapping(name = "总参修改", value = {"/updateParamsModel"}, method = RequestMethod.PUT)
    public Boolean updateParamsModel(@RequestBody Params params) {
        paramsService.updateParams(params);
        return true;
    }

    /**
     * 修改子参
     *
     * @param
     * @return Boolean
     */
    @SysLogEntity("子参修改")
    @ApiOperation(value = "子参修改")
    @RequestMapping(name = "子参修改", value = {"/updateParamsDetailModel"}, method = RequestMethod.PUT)
    public Boolean updateParamsDetailModel(@RequestBody ParamDetail paramDetail) {
        paramsService.upateDetail(paramDetail, true);
        return true;
    }



    /**
     * 删除总参
     *
     * @param paramId
     * @return Boolean
     */
    @SysLogEntity("删除总参")
    @ApiOperation(value = "删除总参")
    @RequestMapping(name = "删除总参", value = {"/deleteParamsModel/{paramId}"}, method = RequestMethod.DELETE)
    public Boolean deleteParamsModel(@PathVariable("paramId") Integer paramId) {
        paramsService.delParams(paramId);
        return true;
    }

    /**
     * 删除子参
     *
     * @param paramDetailId
     * @return Boolean
     */
    @SysLogEntity(value = "删除子参")
    @ApiOperation(value = "删除子参")
    @RequestMapping(name = "删除子参", value = {"/deleteParamsDetailModel/{paramDetailId}"}, method = RequestMethod.DELETE)
    public Boolean deleteParamsDetailModel(@PathVariable("paramDetailId") Integer paramDetailId) {
        paramsService.delParamsDetail(paramDetailId);
        return true;
    }

    /**
     * 获取参数列表
     *
     * @param code
     * @return
     */
    @ApiOperation(value = "获取参数列表")
    @RequestMapping(name = "获取参数列表", value = {"/queryParamDetailDetails"}, method = RequestMethod.GET)
    public List<ParamDetailDTO> queryDetails(@RequestParam("code") String code) {
        List<ParamDetailDTO> details = null;
        List<ParamDetail> list = ParamUtil.getParamDetailList(code);
        if (CollectionUtils.isNotEmpty(list)) {
            ParamDetail paramDetail;
            ParamDetailDTO paramDetailDTO;
            details = new ArrayList<>(list.size());

            for (int i = 0; i < list.size(); i++) {
                paramDetail = list.get(i);
                if (null != paramDetail) {
                    paramDetailDTO = new ParamDetailDTO();
                    paramDetailDTO.setCode(paramDetail.getParamDetailCode());
                    paramDetailDTO.setCname(paramDetail.getParamDetailCname());
                    paramDetailDTO.setEname(paramDetail.getParamDetailEname());
                    paramDetailDTO.setComment(paramDetail.getParamDetailNote());
                    details.add(paramDetailDTO);
                }
            }
        }
        return details;
    }

    @ApiOperation(value="查询子参数列表")
    @RequestMapping(name = "查询子参数列表", value = {"/list/{code}"}, method = RequestMethod.GET)
    public List<ParamDetailDTO> list(@PathVariable String code) {
        List<ParamDetailDTO> details = null;
        List<ParamDetail> list=new ArrayList<>();
        if(code!=null&&!code.equals("")){
            ParamDetail paramDetail= ParamUtil.getParamDetail("limitStatus",code);
            list.add(paramDetail);
        }else{
             list = ParamUtil.getParamDetailList("limitStatus");

        }
        if (CollectionUtils.isNotEmpty(list)) {
            ParamDetail paramDetail;
            ParamDetailDTO paramDetailDTO;
            details = new ArrayList<>(list.size());
            for (int i = 0; i < list.size(); i++) {
                paramDetail = list.get(i);
                if (null != paramDetail) {
                    paramDetailDTO = new ParamDetailDTO();
                    paramDetailDTO.setCode(paramDetail.getParamDetailCode());
                    paramDetailDTO.setCname(paramDetail.getParamDetailCname());
                    paramDetailDTO.setEname(paramDetail.getParamDetailEname());
                    paramDetailDTO.setComment(paramDetail.getParamDetailNote());
                    details.add(paramDetailDTO);
                }
            }
        }
        return details;
    }
}
