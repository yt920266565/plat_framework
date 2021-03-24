package com.yanmade.plat.framework.entity;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("权限信息")
public class SmFunction implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "权限id", required = true)
    private int id;

    @ApiModelProperty(value = "权限名", required = true)
    private String name;

    @ApiModelProperty(value = "权限描述", required = true)
    private String description;

    @ApiModelProperty(value = "RESTFUL接口URL", required = true)
    private String url;

    @ApiModelProperty(value = "GET/POST/PUT/DELETE/ALL", required = true)
    private String method;

    @ApiModelProperty(value = "父节点id", required = true)
    private int pid;

    public SmFunction() {
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

}
