package com.kking.dao.mapper;

import com.kking.dao.entity.TSysUserRole;
import java.util.List;

public interface TSysUserRoleMapper {
    public TSysUserRole selectById(Integer id);
    public List<TSysUserRole> selectList(TSysUserRole tSysUserRole);
    public TSysUserRole selectOneByProperty(String key, Object value);
    public List<TSysUserRole> selectListByProperty(String key, Object value);
    public int insert(TSysUserRole tSysUserRole);
    public int deleteById(TSysUserRole tSysUserRole);
    public int update(TSysUserRole tSysUserRole);
}
