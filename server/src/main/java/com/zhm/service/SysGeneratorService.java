package com.zhm.service;


import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.zhm.dao.SysUserDao;
import com.zhm.entity.SysGenerator;
import com.zhm.essential.jpa.search.SearchFilter;

import com.zhm.util.GenUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipOutputStream;

/**
 * Created by 赵红明 on 2019/6/3.
 */
@Service
public class SysGeneratorService {

    private static Logger logger= LoggerFactory.getLogger(SysGeneratorService.class);

    @Autowired
    private SysUserDao sysUserDao;

    /**
     * 分页查询基础
     *
     * @param
     * @return
     */
    public Page<SysGenerator> findAll(Collection<SearchFilter> filters, Pageable pageable) {
        Map<String, Object[]> params = Maps.newHashMap();
        for (SearchFilter filter : filters) {
            params.put(filter.fieldName(), filter.values());
        }
        return sysUserDao.queryList(params, pageable);
    }

    public byte[] generatorCode(String[] tableNames) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ZipOutputStream zip = new ZipOutputStream(outputStream);

        for(String tableName : tableNames){
            //查询表信息
            Map<String, String> table = sysUserDao.queryTable(tableName);
            logger.info("table="+ JSONObject.toJSONString(table));
            //查询列信息
            List<Map<String, String>> columns = sysUserDao.queryColumns(tableName);
            logger.info("columns="+ JSONObject.toJSONString(columns));
            //生成代码
            GenUtils.generatorCode(table, columns, zip);
        }
        IOUtils.closeQuietly(zip);
        return outputStream.toByteArray();
    }


}
