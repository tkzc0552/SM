package com.zhm.dao;

import com.zhm.entity.SysAnnounce;
import com.zhm.entity.SysRole;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by 赵红明 on 2019/6/3.
 */
public interface SysAnnounceDao extends PagingAndSortingRepository<SysAnnounce,Integer>,JpaSpecificationExecutor<SysAnnounce> {
}
