package com.zhm.service;

import com.alibaba.fastjson.JSONObject;
import com.zhm.dao.RandomCodeDao;
import com.zhm.dao.SysUserDao;
import com.zhm.entity.RandomCode;
import com.zhm.entity.SysUser;
import com.zhm.util.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 * redisTemplate.opsForValue();//操作字符串
 redisTemplate.opsForHash();//操作hash
 redisTemplate.opsForList();//操作list
 redisTemplate.opsForSet();//操作set
 redisTemplate.opsForZSet();//操作有序set
 * Created by 赵红明 on 2019/6/18.
 */
@Service
public class CommonService {

    private static Logger logger= LoggerFactory.getLogger(CommonService.class);

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private Environment environment;
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private RandomCodeDao randomCodeDao;

    @Autowired
    private SysUserDao sysUserDao;



    public ListOperations getRedisList(){
       ListOperations redisList= redisTemplate.opsForList();
       return redisList;
    }

    /**
     * 发送邮件
     * @param toEamil
     * @param userName
     * @param subject
     * @return
     */
    public Result sendSimpleMail(String toEamil, String userName, String subject) {
        logger.info("发送邮件主题："+subject);
         List<SysUser> sysUsers=sysUserDao.findByUserNameAndDeleteFlag(userName,0);
         SysUser sysUser=null;
         if(sysUsers!=null&&sysUsers.size()>0){
             sysUser=sysUsers.get(0);
         }else{
             return Result.failure("没有查询到用户"+userName);
         }
         //查询是否依旧有数据

        String fromEmail=environment.getProperty("systemmodel.sendEmail.from.address");
        SimpleMailMessage message = new SimpleMailMessage();

        //生成验证码
        int randomCode=returnRandom();
        message.setFrom(fromEmail);
        message.setTo(toEamil);
        message.setSubject(subject);
        message.setText("用户  "+userName+"   的验证码是："+randomCode +",请在三分钟之内输入。");
        try {
            //发送邮件
            mailSender.send(message);
            //存储到数据库中
            RandomCode random=new RandomCode();
            random.setRandomCode(randomCode+"");
            random.setPastTime(3*60*1000);
            random.setUserId(sysUser.getUserId());
            randomCodeDao.save(random);
            logger.info("简单邮件已经发送。");
            return  Result.ok("发送邮件成功",randomCode);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("发送简单邮件时发生异常！", e);
            return Result.failure("发送邮件失败");
        }
    }

    /**
     * 生成验证码
     * @return
     */
    public int returnRandom(){
        int randomCode=(int)((Math.random()*9+1)*100000);
        List<RandomCode> randomCodes=randomCodeDao.findByRandomCodeAndDeleteFlag(randomCode+"",0);
        if(randomCodes!=null&&randomCodes.size()>0){
            return randomCode;
        }else{
            return randomCode;
        }
    }
    public Result getToken(String info){
        return Result.ok("",null);
    }
}
