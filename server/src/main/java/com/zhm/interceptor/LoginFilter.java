package com.zhm.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.zhm.util.*;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.util.ResourceUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.WebUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 赵红明 on 2019/6/27.
 *
 * filterName的首字母一定要小写！！！小写！！！小写！！！
 * 如果不小写，会导致配置的多个过滤器拦截url都失效了
 */
@WebFilter(urlPatterns = {"/api/*"}, filterName = "loginFilter")
public class LoginFilter implements Filter {

    private static Logger logger= LoggerFactory.getLogger(LoginFilter.class);
    private final Environment environment;
    private final StringRedisTemplate redisTemplate;

    @Autowired
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    /**
     * 记录那些方法需要token
     */
    private Map<RequestMappingInfo, HandlerMethod> ignoredRequestMappingInfoMap;

    private String HTML_403;

    public LoginFilter(Environment environment,StringRedisTemplate redisTemplate){
        this.environment=environment;
        this.redisTemplate=redisTemplate;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("LoginFilter----init----");
       /* Map<RequestMappingInfo, HandlerMethod> allRequestMappingInfoMap = requestMappingHandlerMapping.getHandlerMethods();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : allRequestMappingInfoMap.entrySet()) {
            HandlerMethod handlerMethod = entry.getValue();
            boolean hasIgnoreFilter = handlerMethod.hasMethodAnnotation(TokenFilter.class);
            if (hasIgnoreFilter) {
                if (ignoredRequestMappingInfoMap == null) {
                    ignoredRequestMappingInfoMap = Maps.newConcurrentMap();
                }
                ignoredRequestMappingInfoMap.put(entry.getKey(), entry.getValue());
            }
        }*/
        logger.info("getFilterName:"+filterConfig.getFilterName());//返回<filter-name>元素的设置值。
        logger.info("getServletContext:"+filterConfig.getServletContext());//返回FilterConfig对象中所包装的ServletContext对象的引用。
        logger.info("getInitParameter:"+filterConfig.getInitParameter("cacheTimeout"));//用于返回在web.xml文件中为Filter所设置的某个名称的初始化的参数值
        logger.info("getInitParameterNames:"+filterConfig.getInitParameterNames());//返回一个Enum

        try {
            HTML_403 = FileUtils.readFileToString(ResourceUtils.getFile("classpath*:403.html"), StandardCharsets.UTF_8);
        } catch (IOException e) {
            logger.warn(e.getMessage());
            HTML_403 = "<!DOCTYPE html><html><head><title>403 Forbidden</title></head><body><div style=\"text-align: center;\"><h1>403 Forbidden</h1></div><hr><div style=\"text-align: center;\">Permission denied</div></body></html>";
        }

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        logger.info("LoginFilter----doFilter----");
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        String servletPath = httpServletRequest.getServletPath();


      /*  HandlerExecutionChain handlerExecutionChain = null;
        try {
            handlerExecutionChain = requestMappingHandlerMapping.getHandler(httpServletRequest);
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
        }*/
        logger.debug("servletPath : " + servletPath);
        //如果是登录页面则不需要拦截
        if(servletPath.contains("/api/login")){
            chain.doFilter(request, response);
            return;
        }
        //验证码不需要拦截
        if(servletPath.contains("/api/defaultKaptcha")){
            chain.doFilter(request, response);
            return;
        }
        //接口类不拦截 但是要校验token是否存在
        if(servletPath.contains("/api/interface")){
            /*boolean isIgnore = ignore(handlerExecutionChain);
            if (!isIgnore) {
                chain.doFilter(request, response);
                return;
            }else{
                String token = httpServletRequest.getHeader("access_token");
                //token不存在
                if (null != token) {
                    //验证token是否正确
                    boolean result = JwtUtil.verify(token);
                    if (!result) {
                        logger.info("jwt token 没有校验失败！");
                        this.sendTokenJson(response,JSONObject.parseObject(JSONObject.toJSONString(Result.failure("jwt token 没有校验失败！"))));
                        return;
                    }
                }else{
                    logger.info("jwt token 不存在！");
                    this.sendTokenJson(response,JSONObject.parseObject(JSONObject.toJSONString(Result.failure("jwt token 不存在！"))));
                    return;
                }
            }*/
            chain.doFilter(request, response);
            return;
        }

        //登录页面
        String loginUrl=environment.getProperty("systemmodel.authority.login_url");
        //cookie
        String cookieName= environment.getProperty("systemmodel.authority.default.cookie_name");

        Cookie cookie = WebUtils.getCookie(httpServletRequest, cookieName);
        if(cookie!=null){
            logger.info("cookie found");
        }else{
            logger.info("cookie not found");
            httpServletResponse.sendRedirect(loginUrl);
            return;
        }

        String loginToken = cookie.getValue();
        logger.debug("loginToken : " + loginToken);
        Map<String, Object> userInfo=new HashMap<String, Object>();
        String redisName=environment.getProperty("systemmodel.user.info")+loginToken;
        try {
            userInfo = this.getUserInfo(redisName);
            LoginUser loginUser=new LoginUser();
            loginUser.setUserId(Integer.parseInt(userInfo.get("userId").toString()));
            loginUser.setUserName(userInfo.get("userName").toString());
            if(userInfo.get("avataUrl")!=null){
                loginUser.setAvataUrl(userInfo.get("avataUrl").toString());
            }else{
                loginUser.setAvataUrl("");
            }
            loginUser.setExpires(userInfo.get("expires").toString());
            AuthorityUtils.setCurrentUser(loginUser);
        } catch (Exception e) {
            logger.info("redisName not exist");
            httpServletResponse.sendRedirect(loginUrl);
            return;
        }
        if (userInfo == null) {
            logger.info("user info not exist");
            httpServletResponse.sendRedirect(loginUrl);
            return;
        }
        chain.doFilter(request, response);
    }


    public Map<String,Object> getUserInfo(String loginToken){
        String userInfoJson = redisTemplate.opsForValue().get(loginToken);
        logger.debug("redis.userInfoJson : " + userInfoJson);
        if(userInfoJson!=null){
            Map<String,Object> map= JsonUtil.fromJson(userInfoJson, new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {});
            if(map!=null){
                return map;
            }
        }
        return null;
    }

    private void TokenErrorHtml(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.TEXT_HTML_VALUE);
        response.getWriter().write(HTML_403);
    }

    private boolean ignore(HandlerExecutionChain handlerExecutionChain) {
        HandlerMethod handler;
        try {
            if(handlerExecutionChain == null) {
                return false;
            }
            handler = (HandlerMethod) handlerExecutionChain.getHandler();
            if (handler == null) {
                return false;
            }
        } catch (Exception ignore) {
            return false;
        }
        if(ignoredRequestMappingInfoMap != null && !ignoredRequestMappingInfoMap.isEmpty()) {
            Collection<HandlerMethod> handlerMethods = ignoredRequestMappingInfoMap.values();
            for (HandlerMethod handlerMethod : handlerMethods) {
                if (handler.getBeanType().equals(handlerMethod.getBeanType()) && handler.getMethod().equals(handlerMethod.getMethod())) {
                    return true;
                }
            }
        }
        return false;
    }
    @Override
    public void destroy() {
        logger.info("LoginFilter----destroy----");
    }

    private void sendTokenJson(ServletResponse response, JSONObject menuJson) throws IOException {
        PrintWriter out = setPrintWriter(response);
        out.print(menuJson);
        out.flush();
        out.close();
    }
    private PrintWriter setPrintWriter(ServletResponse response) throws IOException {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.setCharacterEncoding("UTF-8");
        httpResponse.setContentType("application/json; charset=utf-8");
        httpResponse.setStatus(HttpServletResponse.SC_OK);
        return response.getWriter();
    }
}
