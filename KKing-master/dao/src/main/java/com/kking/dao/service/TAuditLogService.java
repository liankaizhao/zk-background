package com.kking.dao.service;

import com.kking.dao.entity.TAuditLog;
import java.util.List;

public interface TAuditLogService {
    public TAuditLog selectById(Integer id);
    public List<TAuditLog> selectList(TAuditLog tAuditLog);
    public TAuditLog selectOneByProperty(String key, Object value);
    public List<TAuditLog> selectListByProperty(String key, Object value);
    public int insert(TAuditLog tAuditLog);
    public int deleteById(TAuditLog tAuditLog);
    public int update(TAuditLog tAuditLog);
}
