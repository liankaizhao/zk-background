package com.kking.dao.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.kking.dao.entity.TSysRole;
import com.kking.dao.entity.TSysUser;
import com.kking.dao.entity.TSysUserRole;
import com.kking.dao.mapper.TSysUserMapper;
import com.kking.dao.mapper.TSysUserRoleMapper;
import com.kking.dao.service.TSysUserService;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class TSysUserServiceImpl implements TSysUserService {

    @Autowired
    TSysUserMapper tSysUserMapper;

    @Autowired
    TSysUserRoleMapper tSysUserRoleMapper;

    // 默认注册角色
    private static final Integer ROLE_ID = 4;
    // 用户状态 0--有效 1--无效
    private static final Integer USER_STATE = 0;



    @Override
    public TSysUser selectById(Integer id) {
        return tSysUserMapper.selectById(id);
    }

    @Override
    public List<TSysUser> selectList(TSysUser tSysUser) {
        return tSysUserMapper.selectList(tSysUser);
    }

    @Override
    public TSysUser selectOneByProperty(String key, Object value) {
        return tSysUserMapper.selectOneByProperty(key, value);
    }

    @Override
    public List<TSysUser> selectListByProperty(String key, Object value) {
        return tSysUserMapper.selectListByProperty(key, value);
    }

    @Override
    public int insert(TSysUser tSysUser) {
        return tSysUserMapper.insert(tSysUser);
    }

    @Override
    public int deleteById(TSysUser tSysUser) {
        return tSysUserMapper.deleteById(tSysUser);
    }

    @Override
    public int update(TSysUser tSysUser) {
        return tSysUserMapper.update(tSysUser);
    }

    @Override
    public boolean isUserHasPermForRole(TSysUser user, Integer roleId) {
        if (user.isAdmin()) {
            return true;
        }
        for (TSysRole role : user.getRoleList()) {
            if (TSysRole.PERM_TYPE.ALL.equals(role.getDeptPermType())) {
                return true;
            }
        }
        return tSysUserMapper.checkUserRoleEditPermission(user.getId(), roleId) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean register(TSysUser tSysUser) {
        try {
            tSysUser.setState(USER_STATE);
            tSysUser.setCreateTime(new Date());
            tSysUser.setUpdateTime(new Date());
            int count = tSysUserMapper.insert(tSysUser);
            if (count != 0) {
                TSysUser haveUser = tSysUserMapper.selectOneByProperty("user_name", tSysUser.getUserName());
                TSysUserRole tSysUserRole = new TSysUserRole();
                tSysUserRole.setUserId(haveUser.getId());
                tSysUserRole.setRoleId(ROLE_ID);
                tSysUserRole.setCreateTime(new Date());
                int roleCount = tSysUserRoleMapper.insert(tSysUserRole);
                if (roleCount != 0) {
                    return true;
                }
            }
        } catch (Exception e) {

            log.error("用户{}注册失败", JSONObject.toJSON(tSysUser));
            return false;
        }
        return true;
    }
}
