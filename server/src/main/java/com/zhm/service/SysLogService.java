package com.zhm.service;

import com.zhm.entity.SysLog;
import com.zhm.dao.SysLogDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Date;
/**
 * 系统日志
 *
 * @author zhao.hongming
 * @email 1183483051@qq.com
 * @date 2019-09-11 09:52:06
 */
@Service
public class SysLogService {

    private static Logger logger= LoggerFactory.getLogger(SysLogService.class);

    @Autowired
    private SysLogDao  sysLogDao;

    public Page<SysLog> findAll(Specification<SysLog> specification, Pageable pageable) {
        return sysLogDao.findAll(specification, pageable);
    }
    public List<SysLog> findAll(Specification<SysLog> specification) {
        return sysLogDao.findAll(specification);
    }

    public SysLog findOne(Integer id) {
        return sysLogDao.findOne(id);
    }
    /**
         * 删除数据(物理删除)
         * @param id
         */
    @Transactional
    public void delete(Integer id){
            sysLogDao.delete(id);
    }

    /**
     * 新增
     * @param sysLog
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public SysLog add(SysLog sysLog)throws Exception {
            sysLog=sysLogDao.save(sysLog);
            return sysLog;
    }

    /**
    * 保存
    * @param sysLog
    * @return
    */
    @Transactional(rollbackFor = Exception.class)
    public void save(SysLog sysLog)throws Exception{
            sysLog.setUpdateTime(new Date());
            sysLogDao.save(sysLog);
    }
}

