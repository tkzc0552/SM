package com.zhm.controller;

import com.zhm.annotation.SysLogEntity;
import com.zhm.dto.CheckUserDto;
import com.zhm.dto.SendEmailDto;
import com.zhm.dto.UpdatePassword;
import com.zhm.dto.UpdateUserDto;
import com.zhm.entity.SysUser;
import com.zhm.service.CommonService;
import com.zhm.service.SysUserService;
import com.zhm.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.WebUtils;
import sun.rmi.runtime.Log;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by 赵红明 on 2019/6/27.
 */
@RestController
@RequestMapping(value = "/api/login")
public class LoginController {


    private static Logger logger= LoggerFactory.getLogger(LoginController.class);

   @Autowired
   private  Environment environment;

    static final MD5Util md5Util=new MD5Util();



    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private StringRedisTemplate rt=new StringRedisTemplate();

    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private CommonService commonService;

    /**
     * 用户登录校验，首先校验用户在数据库中存不存在，如果存在，
     * 1：存在cookie里， 用户的token，以及cookie失效时间
     * 2：存在Redis里    根据用户token查询客户信息。
     * 3：存在session   用户的token,存储到期时间。
     * @param loginParam
     * @param request
     * @param response
     * @return
     */
    @SysLogEntity("登录")
    @RequestMapping(value = "",method= RequestMethod.POST)
    public Result login(@RequestBody Map<String,Object> loginParam, HttpServletRequest request, HttpServletResponse response){
        logger.info("进入登录校验阶段");
        String userName=(String)loginParam.get("username");
        String passWord=(String)loginParam.get("password");
        String parameter=(String)loginParam.get("vrifyCode");

        String captchaId = (String) request.getSession().getAttribute("vrifyCode");
       // String parameter = request.getParameter("vrifyCode");
        logger.info("Session  vrifyCode "+captchaId+" form vrifyCode "+parameter);
        if (!captchaId.equals(parameter)) {
           return Result.failure("错误的验证码");
        }
        //校验是否存在
        if(sysUserService.findUserAndPassword(userName,passWord)){
            SysUser sysUser=sysUserService.findUserForCookie(userName,passWord);
            //获取cookie信息
            String cookieName=environment.getProperty("systemmodel.authority.default.cookie_name");
            Cookie cookie = WebUtils.getCookie(request, cookieName);
            if(cookie!=null){
                String loginToken = cookie.getValue();
                logger.info("loginToken"+loginToken);
                //获取redis中的用户信息
                String userInfo=rt.opsForValue().get(loginToken);
                //如果Redis为空，则向redis里添加数值
                if(userInfo==null){
                    this.saveCookieAndRedis(request,response,sysUser,cookieName);
                }
            }else{
                logger.info("cookie not found");
                this.saveCookieAndRedis(request,response,sysUser,cookieName);
            }
            return Result.ok("成功",sysUser);
        }else{
            return Result.failure("用戶名或密码错误,或者被禁用！");
        }
    }

    public void saveCookieAndRedis(HttpServletRequest request,HttpServletResponse response,SysUser sysUser,String cookieName){

        String host = request.getHeader("host");// 域名+端口，例如sdisp.seedeer.com:6080
        String domain = StringUtils.getSecondLevelDomain(host);

        long timestamp = System.currentTimeMillis();// 时间戳

        String userInfo = sysUser.getUserName() + timestamp;// 用户信息 userCode + timestamp
        String cookieInfo = md5Util.StringInMd5(userInfo);// 用户信息密文，也是缓存key
        //设置失效时间
        String cookieExpireTimeStr = (String) request.getAttribute("loginCookieExpireTime");// 失效时间，单位为秒

        Integer cookieExpireTime=0;
        if(cookieExpireTimeStr!=null){
            cookieExpireTime = Integer.valueOf(cookieExpireTimeStr);
        }else{
            cookieExpireTime=2*60*60;//2个小时后才失效
        }

        /**
         * 登录信息存储到Redis中
         */
        // 1: 计算失效时间，单位毫秒
        long expiresTime = timestamp + cookieExpireTime * 1000;
        //2:redis中用户名称
        String redisName = (String) request.getAttribute("redisName");
        if(redisName==null){
            redisName=environment.getProperty("systemmodel.user.info")+cookieInfo;
        }
        //3:设置失效时间
        Date date = new Date(expiresTime);
        String img="";
        if(sysUser.getAvatarImg()!=null&&!sysUser.getAvatarImg().equals("")){
            img=sysUser.getAvatarImg();
        }else{
            img="https://img-blog.csdnimg.cn/20190829103618321.jpg";
        }
        LoginCache loginCache=new LoginCache(sysUser.getUserId(),sysUser.getUserName(),"", SDF.format(date),img);
        //4：用戶登录信息存储在Redis里
        String jsonInfo= JsonUtil.toJson(loginCache);
        rt.opsForValue().set(redisName,jsonInfo);
        //5:在redis中设置失效时间
        rt.expire(redisName,cookieExpireTime , TimeUnit.SECONDS);//设置过期时间
        /**
         * 存在session里的数据用户名，
         */
        LoginUser loginUser=new LoginUser();
        loginUser.setUserId(sysUser.getUserId());
        loginUser.setUserName(sysUser.getUserName());
        AuthorityUtils.setCurrentUser(loginUser);
        /**
         * 用户登录信息token（也就是cookieInfo）存在Cookie里
         *
         */
        Cookie loginCookie = new Cookie(cookieName, cookieInfo);
        //1:跨域共享cookie的方法：设置cookie.setDomain(".jszx.com")
        logger.info("cookie共享的域名："+domain);
        loginCookie.setDomain(domain);
        //2:设置路径
        loginCookie.setPath("/");
        // cookie失效时间，单位为秒
        loginCookie.setMaxAge(cookieExpireTime);
        response.addCookie(loginCookie);
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        logger.info("用户："+sysUser.getUserName()+"在"+sdf.format(new Date())+"登录系统");
    }

    @SysLogEntity("注册")
    @RequestMapping(value = "/register",method = RequestMethod.POST)
    public Result register(@RequestBody Map<String,Object> loginParam, HttpServletRequest request, HttpServletResponse response){
        try{
            String parameter=(String)loginParam.get("vrifyCode");
            String captchaId = (String) request.getSession().getAttribute("vrifyCode");
            // String parameter = request.getParameter("vrifyCode");
            logger.info("Session  vrifyCode "+captchaId+" form vrifyCode "+parameter);
            if (!captchaId.equals(parameter)) {
                return Result.failure("错误的验证码");
            }
            String userName=(String)loginParam.get("userName");
            String passWord=(String)loginParam.get("passWord");
            logger.info("md5=====>"+md5Util.StringInMd5(passWord));
            String email=(String)loginParam.get("email");
            String mobile=(String)loginParam.get("mobile");
            SysUser sysUser=new SysUser();
            sysUser.setUserName(userName);
            sysUser.setPassWord(passWord);
            sysUser.setEmail(email);
            sysUser.setMobile(mobile);
            sysUser.setStatus(0);
            sysUserService.save(sysUser);
            return Result.ok("用户注册成功！",null);
        }catch (Exception e){
            logger.error("用户注册异常，{}",e);
            return Result.ok("用户注册失败！",null);
        }

    }


    /**
     * 修改密码
     * @param updateUserDto
     * @return
     */
    @SysLogEntity("修改密码")
    @RequestMapping(value = "/update",method = RequestMethod.POST)
    public Result updatePassword(@RequestBody UpdateUserDto updateUserDto)throws Exception{
        String oldPassword=md5Util.StringInMd5(updateUserDto.getPassWord());
        boolean flag=sysUserService.findUserAndPassword(updateUserDto.getUserName(),oldPassword);
        SysUser sysUser=sysUserService.findUser(updateUserDto.getUserName(),oldPassword);
        if(flag){
            if(updateUserDto.getConpassWord().equals(updateUserDto.getRepassWord())){
               String passWord=md5Util.StringInMd5(updateUserDto.getRepassWord());
                sysUser.setPassWord(passWord);
                sysUserService.savePassword(sysUser);
                return Result.ok("修密码成功！",null);
            }else{
                return Result.failure("输入的密码不相同！");
            }
        }else{
            return Result.failure("输入的原密码错误！");
        }
    }

    /**
     * 发送邮件
     * @param sendEmailDto
     * @return
     */
    @RequestMapping(value = "/sendEmail",method= RequestMethod.POST)
    public Result sendEmail(@RequestBody SendEmailDto sendEmailDto){
        return commonService.sendSimpleMail(sendEmailDto.getToEmail(),sendEmailDto.getUserName(),"找回MyModel账号密码");
    }

    /**
     * 校验是否有验证码，如果成功登陆到填写新密码的页面
     * @return
     */
    @RequestMapping(value = "/searchPassword",method= RequestMethod.POST)
    public Result searchPassword(@RequestBody CheckUserDto checkUserDto){
        return sysUserService.checkRandomCode(checkUserDto);
    }

    /**
     * 修改密码
     * @param updatePassword
     * @return
     */
    @RequestMapping(value = "/changePassword",method= RequestMethod.POST)
    public Result changePassword(@RequestBody UpdatePassword updatePassword){
        return sysUserService.updatePassword(updatePassword);
    }
}
