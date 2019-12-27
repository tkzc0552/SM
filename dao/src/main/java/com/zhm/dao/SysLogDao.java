package com.zhm.dao;

import com.zhm.entity.SysLog;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
/**
 * 系统日志
 * 
 * @author zhao.hongming
 * @email 1183483051@qq.com
 * @date 2019-09-11 09:52:06
 */
public interface SysLogDao extends PagingAndSortingRepository<SysLog,Integer>,JpaSpecificationExecutor<SysLog> {
	
}
