package com.kking.admin.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("注册")
public class UserRegister {

    @ApiModelProperty("用户名")
    private String username;
    @ApiModelProperty("密码")
    private String password;
    @ApiModelProperty("密码重复")
    private String password2;
    @ApiModelProperty("邮箱")
    private String email;

    @ApiModelProperty("电话号码")
    private String mobile;

    @ApiModelProperty("地址")
    private String address;

}
