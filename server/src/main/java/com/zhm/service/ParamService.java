package com.zhm.service;


import com.zhm.dao.ParamDetailDao;
import com.zhm.dao.ParamsDao;
import com.zhm.entity.ParamDetail;
import com.zhm.entity.Params;
import com.zhm.util.Constants;
import com.zhm.util.JsonUtil;
import com.zhm.util.ParamUtil;
import com.zhm.util.PropertiesUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 赵红明 on 2019/6/3.
 */
@Service
public class ParamService {
    @Autowired
    private ParamsDao paramsDao;

    @Autowired
    private ParamDetailDao paramDetailDao;

    @Autowired
    private StringRedisTemplate rt;

    /**
     * 分页查询基础总参数列表
     *
     * @param specification
     * @return
     */
    public Page<Params> findAll(Specification<Params> specification, Pageable pageable) {
        return paramsDao.findAll(specification, pageable);
    }
    public List<Params> findAll() {
        return paramsDao.findAll(ParamUtil.getSpecification(null));
    }
    public Params findOne(Integer id) {
        return paramsDao.findOne(id);
    }

    /**
     * 分页查询基础子参数列表
     *
     * @param specification
     * @return
     */
    public Page<ParamDetail> findDetailAll(Specification<ParamDetail> specification, Pageable pageable) {
        return paramDetailDao.findAll(specification, pageable);
    }


    @Transactional
    public void save(Params params) {
        paramsDao.save(params);
        rt.opsForHash().put(PropertiesUtil.getRedisPrefixKey() + Constants.PARENT_PARAMS, params.getParamCode(), JsonUtil.toJson(params));

    }

    @Transactional
    public void saveD(ParamDetail paramDetail) {
        paramDetailDao.save(paramDetail);
        rt.opsForHash().put(PropertiesUtil.getRedisPrefixKey() + Constants.SON_PARAMS + paramDetail.getParamCode(), paramDetail.getParamDetailCode(), JsonUtil.toJson(paramDetail));

    }
    public Boolean checkCode(String parentCode, String sonCode, Boolean isParent) {
        long count = 0L;
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("paramCode", parentCode);
        if (isParent) {
            count = paramsDao.count(ParamUtil.getSpecification(map));
        } else {
            map.put("paramDetailCode", sonCode);
            count = paramDetailDao.count(ParamUtil.getSpecification(map));
        }
        return count == 0L;
    }

    @Transactional
    public void updateParams(Params params) {
      /*  Params beforeParams = paramsDao.findOne(params.getParamId(), 0);
        String beforeCode = beforeParams.getParamCode();
        if (!beforeParams.equals(params)) {
            paramsDao.save(params);
            String nowCode = params.getParamCode();
            if (!StringUtils.equals(beforeCode, nowCode)) {
                paramDetailDao.updateDeatails(params.getParamCode(), beforeCode);
                List<ParamDetail> list = getDetailsByCode(nowCode);
                if (CollectionUtils.isNotEmpty(list)) {
                    Map<String, String> map = new HashMap<String, String>(list.size());
                    ParamDetail paramDetail;
                    for (int i = 0; i < list.size(); i++) {
                        paramDetail = list.get(i);
                        map.put(paramDetail.getParamDetailCode(), JsonUtil.toJson(paramDetail));
                    }
                }
            }
        }*/
        Params beforeParams = paramsDao.findOne(params.getParamId(), 0);
        String beforeCode = beforeParams.getParamCode();
        if (!beforeParams.equals(params)) {
            paramsDao.save(params);
            String nowCode = params.getParamCode();
            if (!StringUtils.equals(beforeCode, nowCode)) {
                paramDetailDao.updateDeatails(params.getParamCode(), beforeCode);
                rt.delete(PropertiesUtil.getRedisPrefixKey() + Constants.SON_PARAMS + beforeCode);
                rt.opsForHash().put(PropertiesUtil.getRedisPrefixKey() + Constants.PARENT_PARAMS, nowCode, JsonUtil.toJson(params));
                List<ParamDetail> list = getDetailsByCode(nowCode);
                if (CollectionUtils.isNotEmpty(list)) {
                    Map<String, String> map = new HashMap<String, String>(list.size());
                    ParamDetail paramDetail;
                    for (int i = 0; i < list.size(); i++) {
                        paramDetail = list.get(i);
                        map.put(paramDetail.getParamDetailCode(), JsonUtil.toJson(paramDetail));
                    }
                    rt.opsForHash().putAll(PropertiesUtil.getRedisPrefixKey() + Constants.SON_PARAMS + nowCode, map);
                }
            } else {
                rt.opsForHash().put(PropertiesUtil.getRedisPrefixKey() + Constants.PARENT_PARAMS, nowCode, JsonUtil.toJson(params));
            }
        }
    }
    private List<ParamDetail> getDetailsByCode(String nowCode) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("paramCode", nowCode);
        return paramDetailDao.findAll(ParamUtil.getSpecification(map));
    }

    @Transactional
    public void delParams(Integer paramId) {
        Params beforeParams = paramsDao.findOne(paramId, 0);
        String beforeCode = beforeParams.getParamCode();
        paramsDao.delete(paramId);
        paramDetailDao.deleteDetails(paramId);
        rt.opsForHash().delete(PropertiesUtil.getRedisPrefixKey() + Constants.PARENT_PARAMS, beforeCode);
        rt.delete(PropertiesUtil.getRedisPrefixKey() + Constants.SON_PARAMS + beforeCode);
    }

    @Transactional
    public void delParamsDetail(Integer paramDetailId) {
        ParamDetail paramDetail = paramDetailDao.findOne(paramDetailId, 0);
        paramDetailDao.delete(paramDetailId);
        rt.opsForHash().delete(PropertiesUtil.getRedisPrefixKey() + Constants.SON_PARAMS + paramDetail.getParamCode(), paramDetail.getParamDetailCode());
    }
    @Transactional
    public void upateDetail(ParamDetail paramDetail, boolean isRefresh) {
        String code = paramDetail.getParamDetailCode();
        ParamDetail beforeRecord = paramDetailDao.findOne(paramDetail.getParamDetailId());
        String beforeCode = beforeRecord.getParamDetailCode();
        paramDetailDao.save(paramDetail);
        String redisKey = PropertiesUtil.getRedisPrefixKey() + Constants.SON_PARAMS + paramDetail.getParamCode();
        if (!StringUtils.equals(beforeCode, code)) {
            rt.opsForHash().delete(redisKey, beforeCode);
        }
        rt.opsForHash().put(redisKey, code, JsonUtil.toJson(paramDetail));
    }


    public ParamDetail findParamDetail(String paramCode, String paramDetailCode){
        return paramDetailDao.findByParamCodeAndParamDetailCodeAndDeleteFlag(paramCode,paramDetailCode,0);
    }
    public List<ParamDetail> findParamDetails(String paramCode){
        return paramDetailDao.findByParamCodeAndDeleteFlag(paramCode,0);
    }

    public List<ParamDetail> findAllDetails() {
        Sort.Order[] orders = new Sort.Order[3];
        orders[0] = new Sort.Order(Sort.Direction.ASC, "paramCode");
        orders[1] = new Sort.Order(Sort.Direction.ASC, "sortNum");
        orders[2] = new Sort.Order(Sort.Direction.DESC, "updateTime");
        return paramDetailDao.findAll(ParamUtil.getSpecification(null), new Sort(orders));
    }

}
