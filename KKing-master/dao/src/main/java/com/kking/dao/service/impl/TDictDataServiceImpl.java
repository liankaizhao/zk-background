package com.kking.dao.service.impl;

import com.kking.dao.entity.TDictData;
import com.kking.dao.mapper.TDictDataMapper;
import com.kking.dao.service.TDictDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TDictDataServiceImpl implements TDictDataService {
    @Autowired
    TDictDataMapper tDictDataMapper;
    @Override
    public TDictData selectById(Integer id) {
        return tDictDataMapper.selectById(id);
    }

    @Override
    public List<TDictData> selectList(TDictData tDictData) {
        return tDictDataMapper.selectList(tDictData);
    }

    @Override
    public TDictData selectOneByProperty(String key, Object value) {
        return tDictDataMapper.selectOneByProperty(key,value);
    }

    @Override
    public List<TDictData> selectListByProperty(String key, Object value) {
        return tDictDataMapper.selectListByProperty(key,value);
    }

    @Override
    public int insert(TDictData tDictData) {
        return tDictDataMapper.insert(tDictData);
    }

    @Override
    public int deleteById(TDictData tDictData) {
        return tDictDataMapper.deleteById(tDictData);
    }

    @Override
    public int update(TDictData tDictData) {
        return tDictDataMapper.update(tDictData);
    }

}
