package com.zhm.controller;

import com.zhm.entity.SysGenerator;
import com.zhm.essential.jpa.search.SearchFilter;
import com.zhm.essential.web.servlet.Servlets;
import com.zhm.service.SysGeneratorService;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * Created by 赵红明 on 2019/6/26.
 */
@RestController
@RequestMapping(value = "/api/sysGenerator")
public class SysGeneratorController {
    private static Logger logger= LoggerFactory.getLogger(SysGeneratorController.class);

    @Autowired
    private SysGeneratorService sysGeneratorService;


    /**
     *
     *  分页查询公告
     * @param
     * @return
     */
    @RequestMapping(name = "分頁查询所有表名", value = {""}, method = RequestMethod.GET)
    public Page<SysGenerator> pageForList(WebRequest request, Pageable pageable) {
        Map<String, Object> searchParam = Servlets.getParametersStartingWith(request, "search_");
        Collection<SearchFilter> filters = SearchFilter.parse(searchParam);
        Page<SysGenerator> page = sysGeneratorService.findAll(filters, pageable);
        return page;
    }


    @RequestMapping(name = "代码生成",value = {"/code/{tableName}"},method = RequestMethod.GET)
    public void code(@PathVariable("tableName") String  tableName, HttpServletResponse response) throws IOException {
        String[] tableNames =tableName.split(",");

        byte[] data = sysGeneratorService.generatorCode(tableNames);

        response.reset();
        response.setHeader("Content-Disposition", "attachment; filename=\"SM.zip\"");
        response.addHeader("Content-Length", "" + data.length);
        response.setContentType("application/octet-stream; charset=UTF-8");

        IOUtils.write(data, response.getOutputStream());
    }


}
