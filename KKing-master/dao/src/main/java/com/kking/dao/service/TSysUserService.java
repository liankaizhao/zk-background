package com.kking.dao.service;

import com.kking.dao.entity.TSysUser;
import com.kking.dao.entity.UserToken;

import java.util.List;

public interface TSysUserService {
     TSysUser selectById(Integer id);
     List<TSysUser> selectList(TSysUser tSysUser);
     TSysUser selectOneByProperty(String key, Object value);
     List<TSysUser> selectListByProperty(String key, Object value);
     int insert(TSysUser tSysUser);
     int deleteById(TSysUser tSysUser);
     int update(TSysUser tSysUser);

    boolean isUserHasPermForRole(TSysUser user, Integer roleId);

     boolean register(TSysUser tSysUser);







}
