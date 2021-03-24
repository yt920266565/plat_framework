package com.yanmade.plat.framework.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Result对象", description = "公共返回对象Result")
public class ApiResponse<T> {

    @ApiModelProperty(value = "返回码")
    private int status;

    @ApiModelProperty(value = "对象")
    private T data;

    @ApiModelProperty(value = "错误编码")
    private String errCode;

    @ApiModelProperty(value = "错误信息")
    private String errMsg;

    @ApiModelProperty(value = "异常信息")
    private String exception;

    @ApiModelProperty(value = "查询结果条数")
    private int count;

    public ApiResponse(int status, T data, String errCode, String errMsg, String exception) {
        super();
        this.status = status;
        this.data = data;
        this.errCode = errCode;
        this.errMsg = errMsg;
        this.exception = exception;
        this.count = 0;
    }

    public ApiResponse(int status, T data, String errCode, String errMsg, String exception, int count) {
        this.status = status;
        this.data = data;
        this.errCode = errCode;
        this.errMsg = errMsg;
        this.exception = exception;
        this.count = count;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public String getErrCode() {
        return errCode;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
