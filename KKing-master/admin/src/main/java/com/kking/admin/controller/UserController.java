package com.kking.admin.controller;

import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.exceptions.ClientException;
import com.kking.admin.dto.JsonResult;
import com.kking.admin.dto.UserRegister;
import com.kking.admin.shiro.MyRedisSessionDAO;
import com.kking.admin.util.PassUtils;
import com.kking.common.annotation.WriteAuditLog;
import com.kking.dao.entity.*;
import com.kking.dao.service.SendSmsService;
import com.kking.dao.service.TSysMenuService;
import com.kking.dao.service.TSysRoleService;
import com.kking.dao.service.TSysUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.ObjectUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController extends BaseController {
    Logger log = LoggerFactory.getLogger(getClass());
    @Autowired
    TSysUserService userService;
    @Autowired
    TSysMenuService menuService;
    @Autowired
    TSysRoleService roleService;

    @Autowired
    MyRedisSessionDAO myRedisSessionDAO;

    @Autowired
    SendSmsService sendSmsService;

    @WriteAuditLog(operatingType = 1, business = 2)
    @RequestMapping("/info")
    public JsonResult getUserInfo() {
        JsonResult ret = JsonResult.success();
        TSysUser user = (TSysUser) SecurityUtils.getSubject().getPrincipal();
        TSysMenu menuParam = new TSysMenu();
        menuParam.setUserId(user.getId());
        menuParam.setType("MENU");
        ret.put("menus", menuService.getUserMenu(menuParam));
        JSONObject userJson = (JSONObject) JSONObject.toJSON(user);
        userJson.remove("password");
        userJson.remove("salt");
        ret.put("user", userJson);

        List<TSysRole> roleList = roleService.getUserRoleInfo(user.getId(), TSysPerm.PERM_TYPE.MENU);
        ret.put("roles", roleList);
        return ret;
    }

    @WriteAuditLog(operatingType = 1, business = 1)
    @RequestMapping("/login")
    public JsonResult login(@RequestBody TSysUser user) {
        Subject subject = SecurityUtils.getSubject();
        try {
            UsernamePasswordToken token = new UsernamePasswordToken(user.getUserName(), user.getPassword());
            subject.login(token);
        } catch (Exception e) {
            log.error("", e);
            return JsonResult.error();
        }
        TSysUser sysUser = userService.selectOneByProperty("user_name", user.getUserName());
        UserToken userToken = new UserToken();
        userToken.setToken(String.valueOf(subject.getSession().getId()));
        userToken.setUserName(user.getUserName());
        userToken.setUserId(sysUser.getId());
        myRedisSessionDAO.setToken(userToken);
        JsonResult retJson = JsonResult.success();
        retJson.put("token", subject.getSession().getId());
        return retJson;
    }

    @WriteAuditLog(operatingType = 1, business = 1)
    @PostMapping("/register")
    public JsonResult register(@RequestBody UserRegister userRegister) {
        try {
            if (userRegister != null) {
                TSysUser sysUser = new TSysUser();
                sysUser.setUserName(userRegister.getEmail());
                sysUser.setPassword(PassUtils.enCode(userRegister.getPassword(), userRegister.getPassword()));
                TSysUser haveUser = userService.selectOneByProperty("user_name", userRegister.getEmail());
                if (haveUser == null) {
                    boolean result = userService.register(sysUser);
                    if (result) {
                        log.info("账户={}注册成功", userRegister.getUsername());
                    }
                } else {
                    return JsonResult.error(500, "用户名已存在");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info("账户={}注册失败", userRegister.getUsername());
            return JsonResult.error(500, "注册失败");
        }
        return JsonResult.success();
    }

    @RequestMapping("/sendSms")
    public JsonResult sendSms() {
        try {
            sendSmsService.sendSms();
        } catch (ClientException e) {
            e.printStackTrace();
        }
        return  JsonResult.success();

    }




}
