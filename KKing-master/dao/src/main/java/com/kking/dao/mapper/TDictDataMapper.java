package com.kking.dao.mapper;

import com.kking.dao.entity.TDictData;
import java.util.List;

public interface TDictDataMapper {
    public TDictData selectById(Integer id);
    public List<TDictData> selectList(TDictData tDictData);
    public TDictData selectOneByProperty(String key, Object value);
    public List<TDictData> selectListByProperty(String key, Object value);
    public int insert(TDictData tDictData);
    public int deleteById(TDictData tDictData);
    public int update(TDictData tDictData);
}
