package com.zhm.util;


import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.zhm.dao.ParamDetailDao;
import com.zhm.dao.ParamsDao;
import com.zhm.entity.ParamDetail;
import com.zhm.entity.Params;
import com.zhm.essential.jpa.search.DynamicSpecifications;
import com.zhm.essential.jpa.search.Operator;
import com.zhm.essential.jpa.search.SearchFilter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * 系统参数Util
 */
@Component
public class ParamUtil {
    private static ParamUtil paramUtil;

    @Autowired
    private ParamsDao paramsDao;

    @Autowired
    private ParamDetailDao paramDetailDao;
    @Autowired
    private StringRedisTemplate rt;

    @PostConstruct
    public void init() {
        paramUtil = this;
        paramUtil.paramsDao = this.paramsDao;
        paramUtil.paramDetailDao = this.paramDetailDao;
        paramUtil.rt=this.rt;
    }

    /**
     * 获取所有有效的总参
     * @return
     */
    public static List<Params> getParamsList(){
        /**
         * 使用Redis操作
         */
        String redisKey = PropertiesUtil.getRedisPrefixKey() + Constants.PARENT_PARAMS;
        HashOperations<String, String, String> hashOperations = paramUtil.rt.opsForHash();
        List<Params> paramsList = convertList(hashOperations.values(redisKey), Params.class);
        if (CollectionUtils.isEmpty(paramsList)) {
            paramsList = paramUtil.paramsDao.findAll(getSpecification(null));
            if (CollectionUtils.isNotEmpty(paramsList)) {
                HashMap<String, String> map = new HashMap<>(paramsList.size());
                Params params;
                for (int i = 0; i < paramsList.size(); i++) {
                    params = paramsList.get(i);
                    map.put(params.getParamCode(), JSON.toJSONString(params));
                }
                hashOperations.putAll(redisKey, map);
            }
        }
        return paramsList;

        /**
         * 不適用Redis
         */
       /* List<Params>  paramsList = paramUtil.paramsDao.findAll(getSpecification(null));
        if (CollectionUtils.isNotEmpty(paramsList)) {
            HashMap<String, String> map = new HashMap<>(paramsList.size());
            Params params;
            for (int i = 0; i < paramsList.size(); i++) {
                params = paramsList.get(i);
                map.put(params.getParamCode(), JSON.toJSONString(params));
            }
        }
        return paramsList;*/
    }

    /**
     * 获取总参的编号查询到对应的总参实体
     * @param paramCode
     * @return
     */
    public static Params getParams(String paramCode){
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("paramCode", paramCode);
        Params params = paramUtil.paramsDao.findOne(getSpecification(map));
        return params;
    }

    public static List<ParamDetail> getParamDetails(String paramCode){
        Collection<SearchFilter> filters = Lists.newArrayList();
        SearchFilter.addFilter(filters, SearchFilter.build("paramCode", Operator.EQ, paramCode));

        SearchFilter.addFilter(filters, SearchFilter.build("deleteFlag", Operator.EQ, 0));
        Specification<ParamDetail> specification = DynamicSpecifications.bySearchFilter(filters);
        List<ParamDetail> ilist=paramUtil.paramDetailDao.findAll(specification);
        return ilist;
    }

    /**
     * 获取总参的编号，查询改总参下的所有子参
     * @param paramCode
     * @return
     */
    public static List<ParamDetail> getParamDetailList(String paramCode){
        String redisKey = PropertiesUtil.getRedisPrefixKey() + Constants.SON_PARAMS + paramCode;
        HashOperations<String, String, String> hashOperations = paramUtil.rt.opsForHash();
        List<ParamDetail> list = convertList(hashOperations.values(redisKey), ParamDetail.class);
        if (CollectionUtils.isEmpty(list)) {
            Map<String, Object> map = new HashMap<>();
            map.put("paramCode", paramCode);
            Sort.Order[] orders =  new Sort.Order[2];
            orders[0] = new Sort.Order(Sort.Direction.ASC, "sortNum");
            orders[1] = new Sort.Order(Sort.Direction.DESC, "updateTime");
             list = paramUtil.paramDetailDao.findAll(getSpecification(map), new Sort(orders));
            if (CollectionUtils.isNotEmpty(list)) {
                HashMap<String, String> hashMap = new HashMap(list.size());
                ParamDetail paramDetail;
                for (int i = 0; i < list.size(); i++) {
                    paramDetail = list.get(i);
                    hashMap.put(paramDetail.getParamDetailCode(), JSON.toJSONString(paramDetail));
                }
            }
        }
        return list;
    }

    /**
     * 获取总参的编号和子参编号查询子参
     * @param paramCode
     * @param paramDetailCode
     * @return
     */
    public static ParamDetail getParamDetail(String paramCode, String paramDetailCode){
        String redisKey = PropertiesUtil.getRedisPrefixKey() + Constants.SON_PARAMS + paramCode;
        HashOperations<String, String, String> hashOperations = paramUtil.rt.opsForHash();
        ParamDetail paramDetail = convertObject(hashOperations.get(redisKey, paramDetailCode), ParamDetail.class);
        if (null == paramDetail) {
            Map<String, Object> map = new HashMap<>(2);
            map.put("paramCode", paramCode);
            map.put("paramDetailCode", paramDetailCode);
            paramDetail = paramUtil.paramDetailDao.findOne(getSpecification(map));
        }
        return paramDetail;
    }

    /**
     * @Description 根据系统当地语言环境和参数code获取对应的解释语
     * @Author wangqiang
     * @Date 2018/7/20 13:41
     * @Param [paramCode, orderStatus, locale]
     * @return java.lang.String
     */
    public static String getLanuageNameForParamDetail(String paramCode, String paramDetailCode, Locale locale){

        Map<String, Object> map = new HashMap<>(2);
        map.put("paramCode", paramCode);
        map.put("paramDetailCode", paramDetailCode);
        ParamDetail paramDetail = paramUtil.paramDetailDao.findOne(getSpecification(map));
        //默认中文翻译
        String lanuageName = paramDetail.getParamDetailCname();
        //英文
        if (StringUtils.equals(locale.getLanguage(), Locale.ENGLISH.getLanguage())) {
            lanuageName = paramDetail.getParamDetailEname();
        }
        return lanuageName;
    }

    /**
     * 封装查询参数
     * @param map
     * @param <T>
     * @return
     */
    public static <T> Specification<T> getSpecification(Map<String, Object> map) {
        List<SearchFilter> filters = Lists.newArrayList();
        filters.add(SearchFilter.build("deleteFlag", Operator.EQ, 0));
        if (MapUtils.isNotEmpty(map)) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                filters.add(SearchFilter.build(entry.getKey(), Operator.EQ, entry.getValue()));
            }
        }
        return DynamicSpecifications.bySearchFilter(filters);
    }

    /**
     * list转换成是实体集合
     * @param values
     * @param paramsClass
     * @param <T>
     * @return
     */
    private static <T> List<T> convertList(List<String> values, Class<T> paramsClass) {
        List<T> list = null;
        if (CollectionUtils.isNotEmpty(values)) {
            list = new ArrayList<>(values.size());
            for (String value : values) {
                list.add(JSON.parseObject(value, paramsClass));
            }
        }
        return list;
    }

    /**
     * String 转换成 实体
     * @param value
     * @param paramsClass
     * @param <T>
     * @return
     */
    private static <T> T convertObject(String value, Class<T> paramsClass) {
        return value == null ? null : JSON.parseObject(value, paramsClass);
    }

    public static Map<String, Object> convertMap (String param, Object value) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put(param, value);
        return paramMap;
    }

}
