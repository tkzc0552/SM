package com.zhm.aspect;

import com.alibaba.fastjson.JSONObject;
import com.zhm.ToolUtil.IPUtil.IPUtils;
import com.zhm.annotation.SysLogEntity;
import com.zhm.entity.SysLog;
import com.zhm.service.SysLogService;
import com.zhm.util.AuthorityUtils;
import com.zhm.util.HttpContextUtils;
import com.zhm.util.LoginUser;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * 系統日志，切面处理类  切面类
 * Created by 赵红明 on 2019/9/11.
 */
@Aspect
@Component
public class SysLogAspect {

    @Autowired
    private SysLogService sysLogService;
    //定义切点
    @Pointcut("@annotation(com.zhm.annotation.SysLogEntity)")
    public void logPointCut(){

    }
    // 属于环绕增强，能控制切点执行前，执行后，，用这个注解后，程序抛异常，会影响@AfterThrowing这个注解
    @Around("logPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long beginTime = System.currentTimeMillis();
        //执行方法
        Object result = point.proceed();
        //执行时长(毫秒)
        long time = System.currentTimeMillis() - beginTime;

        //保存日志
        saveSysLog(point, Integer.parseInt(time+""));

        return result;
    }

    private void saveSysLog(ProceedingJoinPoint joinPoint, int time)throws Exception {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        SysLog sysLog = new SysLog();
        SysLogEntity syslog = method.getAnnotation(SysLogEntity.class);
        if(syslog != null){
            //注解上的描述
            sysLog.setOperation(syslog.value());
        }

        //请求的方法名
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = signature.getName();
        sysLog.setMethod(className + "." + methodName + "()");

        //请求的参数
        Object[] args = joinPoint.getArgs();
        try{
            String params = JSONObject.toJSONString(args[0]);
            sysLog.setParams(params);
        }catch (Exception e){
            e.printStackTrace();
        }

        //获取request
        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
        //设置IP地址
        sysLog.setIp(IPUtils.getIpAddr(request));

        //用户名
        LoginUser loginUser= AuthorityUtils.getCurrentUser();
        if(loginUser!=null){
            sysLog.setUsername(loginUser.getUserName());
        }else{
            sysLog.setUsername("admim");
        }


        sysLog.setTime(time);
        //保存系统日志
        sysLogService.add(sysLog);
    }
}
