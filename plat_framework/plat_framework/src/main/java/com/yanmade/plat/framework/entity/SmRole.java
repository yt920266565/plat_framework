package com.yanmade.plat.framework.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("角色")
public class SmRole implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "编号", required = false)
    private int id;

    @NotBlank(message = "INVALID_PARAMETER")
    @ApiModelProperty(value = "名称", required = true)
    private String name;

    @ApiModelProperty(value = "描述", required = true)
    private String description;

    @ApiModelProperty(value = "创建时间", required = true)
    private Date createTime;

    private List<SmFunction> functions;

    private List<SmDepartment> departments;

    public SmRole() {
        super();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public List<SmFunction> getFunctions() {
        return functions;
    }

    public void setFunctions(List<SmFunction> functions) {
        this.functions = functions;
    }

    public List<SmDepartment> getDepartments() {
        return departments;
    }

    public void setDepartments(List<SmDepartment> departments) {
        this.departments = departments;
    }

}
