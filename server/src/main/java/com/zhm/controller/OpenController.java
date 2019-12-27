package com.zhm.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zhm.ToolUtil.HttpUtil.OkHttpUtils;
import com.zhm.ToolUtil.IPUtil.IPUtils;
import com.zhm.annotation.TokenFilter;
import com.zhm.dto.AddressDto;
import com.zhm.entity.SysArea;
import com.zhm.service.OpenService;
import com.zhm.service.SysAreaService;
import com.zhm.service.SysUserService;
import com.zhm.util.*;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import okhttp3.*;
import okhttp3.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.web.SortDefault;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 该处的接口都是对外提供的
 * Created by 赵红明 on 2019/8/5.
 */
@RestController
@RequestMapping(value = "/api/interface")
public class OpenController {
    private static Logger logger= LoggerFactory.getLogger(OpenController.class);

    @Autowired
    private Environment environment;

    @Autowired
    private StringRedisTemplate rt=new StringRedisTemplate();

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private OpenService openService;



    @Autowired
    private SysAreaService sysAreaService;
    @CrossOrigin(origins = "*")
    @ApiModelProperty(value = "获取菜单接口(view页面使用)")
    @RequestMapping(value = "/menu",method= RequestMethod.GET)
    public Result menu(Integer userId,HttpServletRequest request,HttpServletResponse response)throws Exception{
        if(userId!=null){
            return  sysUserService.queryUserMenuForView(userId);
        }else{
            //获取cookie信息
            String cookieName=environment.getProperty("systemmodel.authority.default.cookie_name");
            Cookie cookie = WebUtils.getCookie(request, cookieName);
            if(cookie!=null){
                String loginToken = cookie.getValue();
                logger.info("loginToken"+loginToken);
                String redisName=environment.getProperty("systemmodel.user.info")+loginToken;
                Map<String, Object> userInfo = this.getUserInfo(redisName);
                userId=Integer.parseInt(userInfo.get("userId").toString());
                return  sysUserService.queryUserMenuForView(userId);
            }else{
                return Result.failure("cookie失效请重新登录");
            }
        }

    }
    @CrossOrigin(origins = "*")
    @ApiModelProperty(value = "获取菜单接口(其他系统使用)")
    @RequestMapping(value = "/permission",method= RequestMethod.GET)
    public Result queryMenu(String menuCode,Integer userId,HttpServletRequest request){
        if(menuCode!=null&&!menuCode.trim().equals("")){
            logger.info("查询菜单："+menuCode+"的菜单列表");
        }else{
            menuCode=environment.getProperty("systemModel.authority.menu_code");

        }
        //获取cookie信息
        if(userId!=null){
            return sysUserService.queryUserInfo(userId,menuCode);
        }else{
            String cookieName=environment.getProperty("systemmodel.authority.default.cookie_name");
            Cookie cookie = WebUtils.getCookie(request, cookieName);
            if(cookie!=null){
                logger.info("系统："+menuCode+"开始获取菜单");
                String loginToken = cookie.getValue();
                logger.info("loginToken"+loginToken);
                String redisName=environment.getProperty("systemmodel.user.info")+loginToken;
                Map<String, Object> userInfo = this.getUserInfo(redisName);
                userId=Integer.parseInt(userInfo.get("userId").toString());
                return sysUserService.queryUserInfo(userId,menuCode);
            }else{
                return Result.failure("cookie失效请重新登！");
            }
        }

    }

    @CrossOrigin(origins = "*")
    @ApiModelProperty(value = "退出登陸")
    @RequestMapping(value = "/exitLogin",method= RequestMethod.GET)
    public Result exitLogin(HttpServletRequest request, HttpServletResponse response){
        String host = request.getHeader("host");// 域名+端口，例如sdisp.seedeer.com:6080
        String domain = StringUtils.getSecondLevelDomain(host);
        //获取cookie信息
        String cookieName=environment.getProperty("systemmodel.authority.default.cookie_name");
        Cookie cookie = WebUtils.getCookie(request, cookieName);
        if(cookie!=null){
            //刪除redis
            String loginToken = cookie.getValue();
            logger.info("loginToken"+loginToken);
            String redisName=environment.getProperty("systemmodel.user.info")+loginToken;
            rt.delete(redisName);
            //清空cookie
            cookie.setDomain(domain);
            cookie.setMaxAge(0);
            cookie.setValue(null);
            cookie.setPath("/");
            response.addCookie(cookie);
            return Result.ok("退出登陸",null);
        }else{
            //登录页面
            return Result.failure("cookie失效请重新登录");
        }
    }
    /**
     * 省市县三级联动
     */
    @TokenFilter
    @CrossOrigin(origins = "*")
    @ApiOperation(value="根据菜单名称和菜单编号查询菜单信息")
    @RequestMapping(name = "据菜单名称和菜单编号查询菜单信息", value = {"/openAddress"}, method = RequestMethod.GET)
    public List<AddressDto> findAddress(@SortDefault(value = "level", direction = Sort.Direction.ASC) Sort sort){
        return sysAreaService.getAddress();
    }

    public Map<String,Object> getUserInfo(String loginToken){
        String userInfoJson = rt.opsForValue().get(loginToken);
        logger.debug("redis.userInfoJson : " + userInfoJson);
        if(userInfoJson!=null){
            Map<String,Object> map= JsonUtil.fromJson(userInfoJson, new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {});
            if(map!=null){
                return map;
            }
        }
        return null;
    }
    @CrossOrigin(origins = "*")
    @ApiOperation(value="查询用户信息在行头显示")
    @RequestMapping(name = "查询用户信息在行头显示", value = {"/userInfo"}, method = RequestMethod.GET)
    public Result userInfo(HttpServletRequest request){
        String cookieName=environment.getProperty("systemmodel.authority.default.cookie_name");
        Cookie cookie = WebUtils.getCookie(request, cookieName);
        if(cookie!=null){
            String loginToken = cookie.getValue();
            logger.info("loginToken"+loginToken);
            String redisName=environment.getProperty("systemmodel.user.info")+loginToken;
            Map<String, Object> userInfo = this.getUserInfo(redisName);
            return Result.ok("用户信息查询成功！",userInfo);
        }else{
            return Result.failure("cookie失效请重新登！");
        }
    }

    @CrossOrigin(origins = "*")
    @ApiOperation(value="获取token")
    @RequestMapping(name = "获取token", value = {"/getToken"}, method = RequestMethod.GET)
    public Result token(String username,String password){
       String token=JwtUtil.sign(username,password);
       return Result.ok("获取token成功",token);
    }


    @CrossOrigin(origins = "*")
    @ApiOperation(value="今日天气")
    @RequestMapping(name = "今日天气", value = {"/todayWeather"}, method = RequestMethod.GET)
    public Result todayWeather(HttpServletRequest request)throws Exception{
        //根据ip查询当前地址
        String ip= IPUtils.getIpAddr(request);
        //根据ip查询当前地址
        /*String addressUrl="http://ip.taobao.com/service/getIpInfo.php?ip=";
        logger.info("当前机器的ip地址是："+ip);
        if(ip!=null){
            addressUrl+=ip;
        }else{
            addressUrl= "http://ip.taobao.com/service/getIpInfo.php?ip=180.169.29.210";
        }
        Response addressResponse=OkHttpUtils.get(addressUrl);
        ResponseBody addressResponseBody=addressResponse.body();
        if(addressResponse.isSuccessful()&&addressResponseBody!=null){
            String addressInfo=addressResponseBody.string();
            logger.info("当前地址信息是："+addressInfo);
        }*/
        //根据IP地址查询IP所在位置
        String address=IPUtils.getAddress(ip);
        logger.info("ip地址====>"+address);
        String cityId=null;
        if(address!=null&&!address.contains("内网IP")){
            String cityName=address.substring(9,12);
            List<SysArea> sysAreas=sysAreaService.getAreas(cityName);
            if(sysAreas!=null&&sysAreas.size()>0){
                cityId=sysAreas.get(0).getCityId();
            }else{
                cityId="101021300";
            }
        }else{
            cityId="101021300";
        }

        Map<String,Object> weatherMap=new HashMap<>();
        //查询天气1
        String url="http://www.weather.com.cn/data/cityinfo/"+cityId+".html";
        Response response=OkHttpUtils.get(url);
        ResponseBody responseBody=response.body();
        if(response.isSuccessful()&&responseBody!=null){
            String info=responseBody.string();
            JSONObject jsonObject=JSONObject.parseObject(info);
            JSONObject weatherinfo=JSONObject.parseObject(jsonObject.get("weatherinfo").toString());
            String imgUrl="http://www.weather.com.cn/m2/i/icon_weather/29x20/";
            //当前城市
            weatherMap.put("city",weatherinfo.get("city"));
            //天气图片
            weatherMap.put("pic1",imgUrl+weatherinfo.get("img1"));
            weatherMap.put("pic2",imgUrl+weatherinfo.get("img2"));
            //天气范围
            weatherMap.put("tempRegion",weatherinfo.get("temp1")+"--"+weatherinfo.get("temp2"));
            //当前天气
            weatherMap.put("weather",weatherinfo.get("weather"));
        }else{
            return Result.failure("今日天气失败");
        }
        //查询天气2
        String url1="http://www.weather.com.cn/data/sk/101010100.html";
        Response response1=OkHttpUtils.get(url1);
        ResponseBody responseBody1=response1.body();
        if(response1.isSuccessful()&&responseBody1!=null){
            String info=responseBody1.string();
            JSONObject jsonObject1=JSONObject.parseObject(info);
            JSONObject weatherinfo1=JSONObject.parseObject(jsonObject1.get("weatherinfo").toString());
            //当前温度
            weatherMap.put("temp",weatherinfo1.get("temp"));
            //风向
            weatherMap.put("WD",weatherinfo1.get("WD"));
            //风级
            weatherMap.put("WS",weatherinfo1.get("WS"));
            //相对湿度SD
            weatherMap.put("SD",weatherinfo1.get("SD"));
            return Result.ok("查询天气成功",weatherMap);
        }else{
            return Result.failure("今日天气失败");
        }
    }

    @CrossOrigin(origins = "*")
    @ApiOperation(value="IP地址")
    @RequestMapping(name = "IP地址", value = {"/getCountry"}, method = RequestMethod.GET)
    public Result getCountry(HttpServletRequest request)throws Exception{
       //查询IP地址：
        String ipAddress=IPUtils.getIpAddr(request);
        if(ipAddress!=null){
            //根据IP地址查询IP所在位置
            String address=IPUtils.getAddress(ipAddress);
            System.out.println("address====>"+address);
            String countryName=address.substring(0,2);
            String provinceName=address.substring(5,8);
            String cityName=address.substring(9,12);
            logger.info("国家="+countryName+",省="+provinceName+",市="+cityName);
            return Result.ok("地址已经查到！",address);
        }else{
            return Result.failure("IP不存在！");
        }
    }
    @CrossOrigin(origins = "*")
    @ApiOperation(value="分布式全局id生成规则")
    @RequestMapping(name = "分布式全局id生成规则", value = {"/openId"}, method = RequestMethod.GET)
    public Object error(){
        long openId=openService.generateId();
        GuidBO guidBO=openService.parseGUID(openId);
        logger.info("openId====>"+openId+",,returnO====>"+JSON.toJSONString(guidBO));
        return  openService.generateId();
    }

}
