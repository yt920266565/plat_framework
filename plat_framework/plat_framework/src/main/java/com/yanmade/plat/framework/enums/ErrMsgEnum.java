package com.yanmade.plat.framework.enums;

public enum ErrMsgEnum {
    SUCCESS("SUCCESS", "成功"),
    FAILURE("FAILURE", "失败"),
    UNKNOWN("UNKNOWN", "未知"),
    NOT_FONUD("NOT_FOUND", "资源不存在"),
    NOT_INSERT("NOT_INSERT", "新增资源失败"),
    NOT_UPDATE("NOT_UPDATE", "修改资源失败"),
    NOT_DELETE("NOT_DELETE", "删除资源失败"),
    USER_NOT_FOUND("USER_NOT_FOUND", "用户不存在"),

    LOGIN_FAILD("LOGIN_FAILD", "用户名或密码错误"),
    UNAUTHORIZED("UNAUTHORIZED", "用户未登录"),
    FORBIDDEN("FORBIDDEN", "用户权限不足"),

    INVALID_PARAMETER("INVALID_PARAMETER", "非法参数"),
    ;
 
    private String errCode;
 
    private String errMsg;
 
    ErrMsgEnum(String errCode, String errMsg) {
        this.errCode = errCode;
        this.errMsg = errMsg;
    }
 
    /**
     * @deprecation:通过code返回枚举
    */
    public static ErrMsgEnum parse(String errCode) {
        ErrMsgEnum[] values = values();
        for (ErrMsgEnum value : values) {
            if (errCode.equals(value.getErrCode())) {
                return value;
            }
        }

        return UNKNOWN;
    }

    public String getErrCode() {
        return errCode;
    }

    public String getErrMsg() {
        return errMsg;
    }

}
