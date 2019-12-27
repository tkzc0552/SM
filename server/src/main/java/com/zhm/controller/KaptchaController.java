package com.zhm.controller;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Random;

/**
 * Created by 赵红明 on 2019/7/8.
 */
@RestController
@RequestMapping(value = "/api")
public class KaptchaController {

    private static Logger logger= LoggerFactory.getLogger(KaptchaController.class);

    private static final String CONTENT_TYPE = "image/jpeg; charset=utf-8";

    @Autowired
    DefaultKaptcha defaultKaptcha;

    @Autowired
    private StringRedisTemplate rt=new StringRedisTemplate();

    /**
     * 产生验证码（登录页面使用）
     * @param httpServletRequest
     * @param httpServletResponse
     * @throws Exception
     */
    @RequestMapping("/defaultKaptcha")
    public void defaultKaptcha(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception{
        byte[] captchaChallengeAsJpeg = null;
        ByteArrayOutputStream jpegOutputStream = new ByteArrayOutputStream();
        try {
            //生产验证码字符串并保存到session中
            String createText = defaultKaptcha.createText();
            httpServletRequest.getSession().setAttribute("vrifyCode", createText);
            //使用生产的验证码字符串返回一个BufferedImage对象并转为byte写入到byte数组中
            BufferedImage challenge = defaultKaptcha.createImage(createText);
            ImageIO.write(challenge, "jpg", jpegOutputStream);
        } catch (IllegalArgumentException e) {
            httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        //定义response输出类型为image/jpeg类型，使用response输出流输出图片的byte数组
        captchaChallengeAsJpeg = jpegOutputStream.toByteArray();
        httpServletResponse.setHeader("Cache-Control", "no-store");
        httpServletResponse.setHeader("Pragma", "no-cache");
        httpServletResponse.setDateHeader("Expires", 0);
        httpServletResponse.setContentType("image/jpeg");
        ServletOutputStream responseOutputStream =
                httpServletResponse.getOutputStream();
        responseOutputStream.write(captchaChallengeAsJpeg);
        responseOutputStream.flush();
        responseOutputStream.close();
    }

    /**
     * &
     * 产生验证码
     *
     * @param request
     * @param response
     */
    @RequestMapping("/getIdentifyingCode")
    public void getIdentifyingCode(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType(CONTENT_TYPE);
        try {
            // 在内存中创建图象
            int width = 60, height = 20;
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            // 获取图形上下文
            Graphics g = image.getGraphics();
            // 生成随机类
            Random random = new Random();
            // 设定背景色
            g.setColor(getRandColor(200, 250));
            g.fillRect(0, 0, width, height);
            // 设定字体
            g.setFont(new Font("Times New Roman", Font.PLAIN, 18));
            // 画边框
            //g.setColor(new Color());
            //g.drawRect(0,0,width-1,height-1);
            // 随机产生155条干扰线，使图象中的认证码不易被其它程序探测到
            g.setColor(getRandColor(160, 200));
            int j = 155;
            for (int i = 0; i < j; i++) {
                int x = random.nextInt(width);
                int y = random.nextInt(height);
                int xl = random.nextInt(12);
                int yl = random.nextInt(12);
                g.drawLine(x, y, x + xl, y + yl);
            }

            // 取随机产生的认证码(4位数字)
            String sRand = "";
            int h = 4;
            for (int i = 0; i < h; i++) {
                String rand = String.valueOf(random.nextInt(10));
                sRand += rand;
                // 将认证码显示到图象中
                g.setColor(new Color(20 + random.nextInt(110), 20 + random.nextInt(110), 20 + random.nextInt(110)));
                // 调用函数出来的颜色相同，可能是因为种子太接近，所以只能直接生成
                g.drawString(rand, 13 * i + 6, 16);
            }

            // 将认证码存入redis
            rt.opsForValue().set("verification_code",sRand,60*2);
            // 图象生效
            g.dispose();
            // 输出图象到页面
            ImageIO.write(image, "JPEG", response.getOutputStream());
        }catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
    }

    /**
     * 给定范围获得随机颜色
     *
     * @param fc
     * @param bc
     * @return
     */
    private static Color getRandColor(int fc, int bc) {
        Random random = new Random();
        int i = 255;
        if (fc > i) {
            fc = 255;
        }
        if (bc > i) {
            bc = 255;
        }

        int r = fc + random.nextInt(bc - fc);
        int g = fc + random.nextInt(bc - fc);
        int b = fc + random.nextInt(bc - fc);
        return new Color(r, g, b);
    }
}
