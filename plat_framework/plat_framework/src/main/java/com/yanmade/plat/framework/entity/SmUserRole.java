package com.yanmade.plat.framework.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("用户角色关系类")
public class SmUserRole {

    @ApiModelProperty(value = "用户id", required = true)
    private int userId;

    @ApiModelProperty(value = "角色id", required = true)
    private int roleId;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

}
