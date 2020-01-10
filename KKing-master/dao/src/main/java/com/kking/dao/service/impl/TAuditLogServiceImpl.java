package com.kking.dao.service.impl;

import com.kking.dao.entity.TAuditLog;
import com.kking.dao.mapper.TAuditLogMapper;
import com.kking.dao.service.TAuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TAuditLogServiceImpl implements TAuditLogService {
    @Autowired
    TAuditLogMapper tAuditLogMapper;
    @Override
    public TAuditLog selectById(Integer id) {
        return tAuditLogMapper.selectById(id);
    }

    @Override
    public List<TAuditLog> selectList(TAuditLog tAuditLog) {
        return tAuditLogMapper.selectList(tAuditLog);
    }

    @Override
    public TAuditLog selectOneByProperty(String key, Object value) {
        return tAuditLogMapper.selectOneByProperty(key,value);
    }

    @Override
    public List<TAuditLog> selectListByProperty(String key, Object value) {
        return tAuditLogMapper.selectListByProperty(key,value);
    }

    @Override
    public int insert(TAuditLog tAuditLog) {
        return tAuditLogMapper.insert(tAuditLog);
    }

    @Override
    public int deleteById(TAuditLog tAuditLog) {
        return tAuditLogMapper.deleteById(tAuditLog);
    }

    @Override
    public int update(TAuditLog tAuditLog) {
        return tAuditLogMapper.update(tAuditLog);
    }

}
