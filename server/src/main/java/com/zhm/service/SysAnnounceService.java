package com.zhm.service;


import com.zhm.dao.SysAnnounceDao;
import com.zhm.entity.SysAnnounce;
import com.zhm.util.AuthorityUtils;
import com.zhm.util.LoginUser;
import com.zhm.util.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Created by 赵红明 on 2019/6/3.
 */
@Service
public class SysAnnounceService {

    private static Logger logger= LoggerFactory.getLogger(SysAnnounceService.class);

    @Autowired
    private SysAnnounceDao sysAnnounceDao;

    /**
     * 分页查询基础
     *
     * @param specification
     * @return
     */
    public Page<SysAnnounce> findAll(Specification<SysAnnounce> specification, Pageable pageable) {
        return sysAnnounceDao.findAll(specification, pageable);
    }
    public List<SysAnnounce> findAll(Specification<SysAnnounce> specification, Sort sort) {
        return sysAnnounceDao.findAll(specification,sort);
    }
    public SysAnnounce findOne(Integer id) {
        return sysAnnounceDao.findOne(id);
    }

    /**
     * 全量查询
     * @param specification
     * @return
     */
    public List<SysAnnounce> findAllData(Specification<SysAnnounce> specification) {
        return sysAnnounceDao.findAll(specification);
    }


    /**
     * 删除数据(物理删除)
     * @param id
     */
    @Transactional
    public void delete(Integer id){
        sysAnnounceDao.delete(id);
    }

    /**
     * 新增
     * @param sysMenu
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public SysAnnounce add(SysAnnounce sysMenu)throws Exception {
        LoginUser loginUser= AuthorityUtils.getCurrentUser();
        if(loginUser!=null){
            sysMenu.setCreateUser(loginUser.getUserName());
        }
        sysMenu= sysAnnounceDao.save(sysMenu);
        return sysMenu;
    }

    /**
     * 保存
     * @param sysMenu
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void save(SysAnnounce sysMenu)throws Exception{
            sysMenu.setUpdateTime(new Date());
        sysAnnounceDao.save(sysMenu);
    }
    /**
     * 修改公告状态
     * @param id
     */
    @Transactional(rollbackFor = Exception.class)
    public Result updateStatus(Integer id, Integer status)throws Exception {
        try{
            String userCode="10011";
            SysAnnounce sysDept=sysAnnounceDao.findOne(id);
            sysDept.setUpdateTime(new Date());
            sysDept.setUpdateUser(userCode);
            if(sysDept!=null){
                sysDept.setStatus(status);
                sysAnnounceDao.save(sysDept);
                return Result.ok("修改公告状态成功",sysDept);
            }else{
                return Result.failure("没有查询到公告");
            }
        }catch (Exception e){
            e.printStackTrace();
            logger.error("修改公告状态失败：",e.getCause());
            throw new RuntimeException(e.getMessage());
        }

    }

    /**
     * 根据主键来查询
     * @param id
     * @return
     */
    public SysAnnounce findById(Integer id) {
        return sysAnnounceDao.findOne(id);
    }




}
