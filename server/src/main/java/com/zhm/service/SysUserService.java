package com.zhm.service;


import com.google.j2objc.annotations.AutoreleasePool;
import com.zhm.dao.RandomCodeDao;
import com.zhm.dao.SysUserDao;
import com.zhm.dao.SysUserRoleDao;
import com.zhm.dto.CheckUserDto;
import com.zhm.dto.MenuDto;
import com.zhm.dto.UpdatePassword;
import com.zhm.entity.*;
import com.zhm.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * Created by 赵红明 on 2019/6/3.
 */
@Service
public class SysUserService {

    private static Logger logger= LoggerFactory.getLogger(SysUserService.class);

    @Autowired
    private SysUserDao sysUserDao;

    @Autowired
    private RandomCodeDao randomCodeDao;

    @Autowired
    private SysUserRoleDao sysUserRoleDao;

    static final MD5Util md5Util=new MD5Util();

    @Autowired
    private ParamUtil paramUtil;
    /**
     * 分页查询基础
     *
     * @param specification
     * @return
     */
    public Page<SysUser> findAll(Specification<SysUser> specification, Pageable pageable) {
        return sysUserDao.findAll(specification, pageable);
    }
    public List<SysUser> findAll(Specification<SysUser> specification, Sort sort) {
        return sysUserDao.findAll(specification,sort);
    }
    public SysUser findOne(Integer id) {
        return sysUserDao.findOne(id);
    }

    /**
     * 全量查询
     * @param specification
     * @return
     */
    public List<SysUser> findAllData(Specification<SysUser> specification) {
        return sysUserDao.findAll(specification);
    }


    /**
     * 删除数据(物理删除)
     * @param id
     */
    @Transactional
    public void delete(Integer id){
        sysUserDao.delete(id);
    }

    /**
     * 新增
     * @param sysUser
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public SysUser add(SysUser sysUser)throws Exception {
        LoginUser loginUser= AuthorityUtils.getCurrentUser();
        if(loginUser!=null){
            sysUser.setCreateUser(loginUser.getUserName());
        }
        sysUser= sysUserDao.save(sysUser);
        addUserRole(sysUser);
        return sysUser;
    }

    /**
     * 保存
     * @param sysUser
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void save(SysUser sysUser)throws Exception{
        LoginUser loginUser= AuthorityUtils.getCurrentUser();
        if(loginUser!=null){
            sysUser.setUpdateUser(loginUser.getUserName());
            //逻辑删除用户和角色关系
            sysUserRoleDao.updateUserRole(sysUser.getUserId());
        }

        addUserRole(sysUser);
        sysUserDao.save(sysUser);
    }
    @Transactional(rollbackFor = Exception.class)
    public void savePassword(SysUser sysUser){
        sysUserDao.save(sysUser);
    }


    public boolean findUserAndPassword(String userName,String passWord){
       List<SysUser> sysUsers=sysUserDao.findByUserNameAndPassWordAndStatusAndDeleteFlag(userName,passWord,0,0);
       if(sysUsers!=null&&sysUsers.size()>0){
           return true;
       }else{
           return false;
       }
    }

    public SysUser findUser(String userName, String passWord){
        List<SysUser> sysUsers=sysUserDao.findByUserNameAndPassWordAndStatusAndDeleteFlag(userName,passWord,0,0);
        if(sysUsers!=null&&sysUsers.size()>0){
            return sysUsers.get(0);
        }else{
            return null;
        }
    }


    public Result checkRandomCode(CheckUserDto checkUserDto){
        //1:校验用户是否存在
        List<SysUser>sysUsers= sysUserDao.findByUserNameAndDeleteFlag(checkUserDto.getUserName(),0);
        SysUser sysUser=null;
        if(sysUsers!=null&&sysUsers.size()>0){
            sysUser=sysUsers.get(0);
            logger.info("用户存在");
        }else{
            return Result.failure("登陆的用户："+checkUserDto.getUserName()+"不存在");
        }
        //2:看Random是否存在，并且是否有效期内。

        List<RandomCode> randomCodes=randomCodeDao.findByUserIdAndRandomCodeAndDeleteFlag(sysUser.getUserId(),checkUserDto.getCode(),0);

        Collections.sort(randomCodes, new Comparator<RandomCode>() {
            public int compare(RandomCode o1, RandomCode o2) {
                //按照RandomCode的createTime字段进行降序排列
                if (o1.getCreateTime().getTime() < o2.getCreateTime().getTime()) {
                    return 1;
                }
                if (o1.getCreateTime().getTime() == o2.getCreateTime().getTime()) {
                    return 0;
                }
                return -1;
            }
        });
        if(randomCodes!=null&&randomCodes.size()>0){
            RandomCode randomCode=randomCodes.get(0);
            long nTime=(new Date()).getTime();
            long cTime= randomCode.getCreateTime().getTime();
            if(nTime-cTime<randomCode.getPastTime()){
                logger.info("验证码有效");
            }else{
                //查询出来的所有验证码失效
                for(RandomCode rdCode:randomCodes){
                    rdCode.setUpdateUser(checkUserDto.getUserName());
                    rdCode.setUpdateTime(new Date());
                    rdCode.setDeleteFlag(0);
                    randomCodeDao.save(rdCode);
                }
                return Result.failure("验证码："+checkUserDto.getCode()+"已失效请重新发送！");
            }
        }else{
            return Result.failure("验证码："+checkUserDto.getCode()+"不存在！");
        }
        return Result.ok("成功",null);
    }

    public Result updatePassword(UpdatePassword updatePassword){
        try{
            List<SysUser>sysUsers= sysUserDao.findByUserNameAndDeleteFlag(updatePassword.getUserName(),0);
            SysUser sysUser=null;
            if(sysUsers!=null&&sysUsers.size()>0){
                sysUser=sysUsers.get(0);
                logger.info("用户存在");
            }else{
                return Result.failure("登陆的用户："+updatePassword.getUserName()+"不存在");
            }
            //修改密码，新密码MD5加密
            String passWord=md5Util.StringInMd5(updatePassword.getNewpassword());
            sysUser.setPassWord(passWord);
            sysUser.setUpdateUser(sysUser.getUserName());
            sysUser.setUpdateTime(new Date());
            sysUserDao.save(sysUser);
            return Result.ok("修改密码成功",null);
        }catch (Exception e){
            logger.error("修改密码失败{}",e.getMessage());
            return Result.failure("修改密码失败：原因"+e.getCause());
        }
    }

    /**
     * 添加用户和角色关系
     * @param sysUser
     */
    public void addUserRole(SysUser sysUser){
        if(sysUser.getRoleIds()!=null&&sysUser.getRoleIds().size()>0){
            for(Integer roleId:sysUser.getRoleIds()){
                SysUserRole sysUserRole=new SysUserRole();
                sysUserRole.setUserId(sysUser.getUserId());
                sysUserRole.setRoleId(roleId);
                sysUserRoleDao.save(sysUserRole);
            }

        }
    }

    /**
     * 根据userId查询角色Id
     * @return
     */
    public Result queryUserRoles(Integer userId){
        List<Integer> users=sysUserRoleDao.queryChecKUserRole(userId);
        return Result.ok("查询用户和角色关系成功",users);
    }

    /**
     * view界面的菜单
     * @param userId
     * @return
     */
    public Result queryUserMenuForView(Integer userId){
        //根据用户id查询角色信息，根据角色信息查询菜单信息
        List<SysMenu> sysMenus=sysUserDao.queryForUserMenuForView(userId);
        List<MenuDto> parentlist=this.combianMenuDto(sysMenus);
        return Result.ok("menuInfo",parentlist);
    }

    /**
     * 各种系统中的菜单
     * @param userId
     * @return
     */
    public Result queryUserInfo(Integer userId,String menuCode){
        //根据用户id查询角色信息，根据角色信息查询菜单信息
        List<SysMenu> sysMenus=sysUserDao.queryForUserMenu(userId,menuCode);
        List<MenuInfo> parentlist=this.queryMenuInfo(sysMenus);
        return Result.ok("menuInfo",parentlist);
    }

    public List<MenuInfo> queryMenuInfo(List<SysMenu> sysMenus){
        Map<Integer,MenuInfo> map=new HashMap<Integer,MenuInfo>();
        List<MenuInfo> parentlist=new ArrayList<MenuInfo>();
        try{
            if(sysMenus!=null&&sysMenus.size()>0){
                for(SysMenu pm:sysMenus){
                    if(pm.getMenuLevel()==1){
                        //根路径
                        List<MenuInfo> plist=new ArrayList<MenuInfo>();
                        MenuInfo pmmodel=new MenuInfo();
                        pmmodel.setMenuCname(pm.getMenuName());
                        pmmodel.setMenuEname(pm.getMenuEname());
                        pmmodel.setMenuCode(pm.getMenuCode());
                        if(pm.getMenuType()==0){
                            pmmodel.setLeafFlag(0);
                        }
                        pmmodel.setMenuLevel(pm.getMenuLevel());
                        pmmodel.setMenuLink(pm.getMenuUrl());
                        pmmodel.setChildren(plist);
                        map.put(pm.getId(),pmmodel);
                        parentlist.add(pmmodel);
                    }else{
                        //查询其父亲节点
                        MenuInfo pmodel=map.get(pm.getParentId());
                        if(pmodel!=null){
                            if(pm.getMenuType()==2){
                                List<ButtonInfo> buttonInfos=pmodel.getOpts();
                                if(buttonInfos!=null&&buttonInfos.size()>0){
                                    ButtonInfo buttonInfo=new ButtonInfo();
                                    buttonInfo.setOptCname(pm.getMenuName());
                                    buttonInfo.setOptEname(pm.getMenuEname());
                                    buttonInfo.setOptCode(pm.getMenuCode());
                                    buttonInfos.add(buttonInfo);
                                    pmodel.setOpts(buttonInfos);
                                }else{
                                    buttonInfos=new ArrayList<>();
                                    ButtonInfo buttonInfo=new ButtonInfo();
                                    buttonInfo.setOptCname(pm.getMenuName());
                                    buttonInfo.setOptEname(pm.getMenuEname());
                                    buttonInfo.setOptCode(pm.getMenuCode());
                                    buttonInfos.add(buttonInfo);
                                    pmodel.setOpts(buttonInfos);
                                }

                            }else{
                                //存到父亲里面
                                List<MenuInfo> pmmlist=pmodel.getChildren();
                                if(pmmlist==null){
                                    pmmlist=new ArrayList<>();
                                }
                                MenuInfo productModel=new MenuInfo();
                                productModel.setMenuCname(pm.getMenuName());
                                productModel.setMenuEname(pm.getMenuEname());
                                productModel.setMenuCode(pm.getMenuCode());
                                productModel.setMenuLevel(pm.getMenuLevel());
                                if(pm.getMenuType()==0){
                                    productModel.setLeafFlag(0);
                                } else if(pm.getMenuType()==1){
                                    productModel.setLeafFlag(0);
                                }else{
                                    productModel.setLeafFlag(1);
                                }
                                productModel.setMenuLink(pm.getMenuUrl());
                                pmmlist.add(productModel);
                                pmodel.setChildren(pmmlist);
                                //存到map中
                                map.put(pm.getId(),productModel);
                            }

                        }

                    }
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return parentlist;
    }

    /**
     * 组合menuList
     * @param sysMenus
     * @return
     */
    public List<MenuDto> combianMenuDto(List<SysMenu> sysMenus){
        //封装Map
        Map<Integer,MenuDto> map=new HashMap<Integer,MenuDto>();
        List<MenuDto> parentlist=new ArrayList<MenuDto>();
        try{
            if(sysMenus!=null&&sysMenus.size()>0){
                for(SysMenu pm:sysMenus){
                    if(pm.getMenuLevel()==1){
                        //根路径
                        List<MenuDto> plist=new ArrayList<MenuDto>();
                        MenuDto pmmodel=new MenuDto();
                        pmmodel.setId(pm.getId());
                        pmmodel.setMenuName(pm.getMenuName());
                        pmmodel.setMenuEname(pm.getMenuEname());
                        pmmodel.setMenuCode(pm.getMenuCode());
                        pmmodel.setMenuType(pm.getMenuType());
                        pmmodel.setMenuLevel(pm.getMenuLevel());
                        pmmodel.setMenuUrl(pm.getMenuUrl());
                        pmmodel.setParentName("无");
                        pmmodel.setChildren(plist);
                        map.put(pm.getId(),pmmodel);
                        parentlist.add(pmmodel);
                    }else{
                        //查询其父亲节点
                        MenuDto pmodel=map.get(pm.getParentId());
                        if(pmodel!=null){
                            //存到父亲里面

                            List<MenuDto> pmmlist=pmodel.getChildren();
                            if(pmmlist==null){
                                pmmlist=new ArrayList<>();
                            }
                            MenuDto productModel=new MenuDto();
                            productModel.setMenuName(pm.getMenuName());
                            productModel.setMenuEname(pm.getMenuEname());
                            productModel.setMenuCode(pm.getMenuCode());
                            productModel.setMenuLevel(pm.getMenuLevel());
                            productModel.setId(pm.getId());
                            productModel.setMenuType(pm.getMenuType());
                            productModel.setMenuUrl(pm.getMenuUrl());
                            productModel.setParentName(pmodel.getMenuName());
                            pmmlist.add(productModel);
                            pmodel.setChildren(pmmlist);
                            //存到map中
                            map.put(pm.getId(),productModel);
                        }
                    }
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return parentlist;
    }

    public SysUser findUserForCookie(String userName,String passWord){
        List<SysUser> sysUsers=sysUserDao.findByUserNameAndPassWordAndStatusAndDeleteFlag(userName,passWord,0,0);
        if(sysUsers!=null&&sysUsers.size()>0){
            return sysUsers.get(0);
        }else{
            return null;
        }
    }
    public Result uploadCompress(MultipartFile multipartFile) {
        Result result = new Result();
        try {
            String fileName = multipartFile.getOriginalFilename();
            List<ParamDetail> paramDetails= ParamUtil.getParamDetailList("loadParam");
            String localUrl="";
            String exitUrl="";
            if(paramDetails!=null&&paramDetails.size()>0){
                for(ParamDetail paramDetail:paramDetails){
                    if(paramDetail.getParamDetailCode().equals("localUrl")){
                        localUrl=paramDetail.getParamDetailCname();
                    }else if(paramDetail.getParamDetailCode().equals("exitUrl")){
                        exitUrl=paramDetail.getParamDetailCname();
                    }
                }
            }else{
                return Result.failure("请配置系统参数");
            }
            String pname = (new Date()).getTime()+ fileName;
            String filePath = localUrl + pname;
            File desFile = new File(filePath);
            if (!desFile.getParentFile().exists()) {
                desFile.mkdirs();
            }
            try {
                multipartFile.transferTo(desFile);
            } catch (IllegalStateException | IOException e) {
                e.printStackTrace();
            }
            String endInfo = localUrl + "pdf/" + pname;
            System.out.println("endInfo=" + endInfo);
            String compressPath = exitUrl + pname;
            if (compressPath != null && compressPath != "") {
                result.setCode("00000");
                result.setMessage("图片上传成功");
                result.setData(compressPath);
            } else {
                result.setCode("99999");
                result.setMessage("上传失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getCause()+","+e.getMessage());
            result.setCode("99999");
            result.setMessage("上传失败");
            logger.error("上传失败", e);
        }
        return result;
    }


    public List<Map<String,String>> registerNum(){
        return sysUserDao.findRegister();
    }

}
