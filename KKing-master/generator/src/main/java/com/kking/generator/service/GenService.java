package com.kking.generator.service;

import java.util.List;

public interface GenService {
    /**
     * 自动生成代码及mapper接口
     * @param tableName
     * @param packageName
     * @return
     */
    public byte[] generateCode(String tableName,String packageName);
    public byte[] generateCode(List<String> tableName,String packageName);
}
