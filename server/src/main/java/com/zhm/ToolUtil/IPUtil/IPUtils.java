package com.zhm.ToolUtil.IPUtil;

import org.apache.commons.lang3.StringUtils;
import org.lionsoul.ip2region.DataBlock;
import org.lionsoul.ip2region.DbConfig;
import org.lionsoul.ip2region.DbSearcher;
import org.lionsoul.ip2region.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.lang.reflect.Method;

/**
 * IP地址
 */
public class IPUtils {

	private static Logger logger = LoggerFactory.getLogger(IPUtils.class);

	/**
	 * 获取IP地址
	 * 使用Nginx等反向代理软件， 则不能通过request.getRemoteAddr()获取IP地址
	 * 如果使用了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串IP地址，X-Forwarded-For中第一个非unknown的有效IP字符串，则为真实IP地址
	 */
	public static String getIpAddr() {
		String ip = null;
		try {
			HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
			ip = request.getHeader("x-forwarded-for");
			if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("Proxy-Client-IP");
			}
			if (StringUtils.isEmpty(ip) || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("WL-Proxy-Client-IP");
			}
			if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("HTTP_CLIENT_IP");
			}
			if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("HTTP_X_FORWARDED_FOR");
			}
			if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getRemoteAddr();
			}
		} catch (Exception e) {
			logger.error("IPUtils ERROR ", e);
		}
		// 使用代理，则获取第一个IP地址
		if (StringUtils.isNotEmpty(ip) && ip.length() > 15) {
			if (ip.indexOf(",") > 0) {
				ip = ip.substring(0, ip.indexOf(","));
			}
		}
		return ip;
	}

	/**
	 * 获取IP地址
	 *
	 * 使用Nginx等反向代理软件， 则不能通过request.getRemoteAddr()获取IP地址
	 * 如果使用了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串IP地址，X-Forwarded-For中第一个非unknown的有效IP字符串，则为真实IP地址
	 */
	public static String getIpAddr(HttpServletRequest request) {
		String ip = null;
		try {
			ip = request.getHeader("x-forwarded-for");
			if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("Proxy-Client-IP");
			}
			if (StringUtils.isEmpty(ip) || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("WL-Proxy-Client-IP");
			}
			if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("HTTP_CLIENT_IP");
			}
			if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("HTTP_X_FORWARDED_FOR");
			}
			if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getRemoteAddr();
			}
		} catch (Exception e) {
			logger.error("IPUtils ERROR ", e);
		}
		return ip;
	}

	/**
	 * 根据IP地址查询所在物理位置
	 *
	 * @param ip
	 * @return
	 */
	public static String getAddress(String ip){

		String dbPath = IPUtils.class.getResource("/ip2region.db").getPath();

		File file = new File(dbPath);

		if ( file.exists() == false ) {
			System.out.println("Error: Invalid ip2region.db file");
		}

		//查询算法
		int algorithm = DbSearcher.BTREE_ALGORITHM; //B-tree
		//DbSearcher.BINARY_ALGORITHM //Binary
		//DbSearcher.MEMORY_ALGORITYM //Memory
		try {
			DbConfig config = new DbConfig();
			DbSearcher searcher = new DbSearcher(config, dbPath);

			//define the method
			Method method = null;
			switch ( algorithm )
			{
				case DbSearcher.BTREE_ALGORITHM:
					method = searcher.getClass().getMethod("btreeSearch", String.class);
					break;
				case DbSearcher.BINARY_ALGORITHM:
					method = searcher.getClass().getMethod("binarySearch", String.class);
					break;
				case DbSearcher.MEMORY_ALGORITYM:
					method = searcher.getClass().getMethod("memorySearch", String.class);
					break;
			}

			DataBlock dataBlock = null;
			if ( Util.isIpAddress(ip) == false ) {
				logger.error("Error: Invalid ip address");
			}

			dataBlock  = (DataBlock) method.invoke(searcher, ip);
			String address=dataBlock.getRegion();
			System.out.println("address====>"+address);
			String countryName=address.substring(0,2);
			String provinceName=address.substring(5,8);
			String cityName=address.substring(9,12);
			System.out.println("countryName="+countryName+",provinceName="+provinceName+",cityName="+cityName);
			return dataBlock.getRegion();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
   public static void main(String[] args){
	   getAddress("42.203.192.25");
   }
}
