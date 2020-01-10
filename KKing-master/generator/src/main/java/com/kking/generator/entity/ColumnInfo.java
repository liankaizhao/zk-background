package com.kking.generator.entity;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ColumnInfo {
    private String columnName;
    private String camelName;
    private String xCamelName;
    private String dataType;
    private String javaType;
}
