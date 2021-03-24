package com.yanmade.plat.framework.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModelProperty;

public class SmUser implements Serializable{
    
    private static final long serialVersionUID = 1L;
    
    @ApiModelProperty("用户表主键")
    private int id;

    @NotBlank(message = "INVALID_PARAMETER")
    @ApiModelProperty(value = "工号", required = false)
    private String username;
    
    @ApiModelProperty(value = "密码", required = false)
    private String password;

    @NotBlank(message = "INVALID_PARAMETER")
    @ApiModelProperty(value = "真实姓名", required = false)
    private String realName;

    @ApiModelProperty(value = "公司邮箱", required = false)
    private String email;

    @NotBlank(message = "INVALID_PARAMETER")
    @ApiModelProperty(value = "电话", required = false)
    private String phone;

    @ApiModelProperty(value = "部门编号", required = false)
    private int deptId;

    @ApiModelProperty(value = "部门名称", required = false)
    private String deptName;

    @ApiModelProperty(value = "创建时间", required = false)
    private Date createTime;

    private List<SmRole> roles;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getDeptId() {
        return deptId;
    }

    public void setDeptId(int deptId) {
        this.deptId = deptId;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public List<SmRole> getRoles() {
        return roles;
    }

    public void setRoles(List<SmRole> roles) {
        this.roles = roles;
    }

}
