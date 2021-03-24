package com.yanmade.plat.framework.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("角色权限关系类")
public class SmRoleFunction {

    @ApiModelProperty
    private int roleId;

    private int functionId;

    public SmRoleFunction() {
        super();
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public int getFunctionId() {
        return functionId;
    }

    public void setFunctionId(int functionId) {
        this.functionId = functionId;
    }

}
