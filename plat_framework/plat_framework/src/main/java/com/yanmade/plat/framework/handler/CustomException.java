package com.yanmade.plat.framework.handler;

import com.yanmade.plat.framework.enums.ErrMsgEnum;

public class CustomException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String errCode;

    private final String errMsg;

    public CustomException(ErrMsgEnum enums) {
        this.errCode = enums.getErrCode();
        this.errMsg = enums.getErrMsg();
    }

    public String getErrCode() {
        return errCode;
    }

    public String getErrMsg() {
        return errMsg;
    }

}
