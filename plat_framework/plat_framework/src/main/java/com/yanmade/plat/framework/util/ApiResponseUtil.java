package com.yanmade.plat.framework.util;

import com.yanmade.plat.framework.entity.ApiResponse;
import com.yanmade.plat.framework.enums.ErrMsgEnum;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

public final class ApiResponseUtil implements Serializable {

	private static final long serialVersionUID = 1L;

	public static <T> ApiResponse<T> result(HttpStatus status, T data, String errCode, String errMsg,
			String exception) {
		return new ApiResponse<>(status.value(), data, errCode, errMsg, exception, 0);
	}

	public static <T> ApiResponse<T> result(HttpStatus status, T data, String errCode, String errMsg,
											String exception, int count) {
		return new ApiResponse<>(status.value(), data, errCode, errMsg, exception, count);
	}

	public static <T> ApiResponse<T> result(HttpStatus status, T data, ErrMsgEnum enums, String exception) {
		if (enums == null) {
			return result(status, data, "", "", exception);
		}
		return result(status, data, enums.getErrCode(), enums.getErrMsg(), exception);
	}

	public static <T> ApiResponse<T> success(T data) {
		return result(HttpStatus.OK, data, "", "", "");
	}

	public static <T> ApiResponse<T> success(T data, int count) {
		return result(HttpStatus.OK, data, "", "", "", count);
	}

	public static <T> ApiResponse<T> failure(HttpStatus status, T data, String errCode, String errMsg) {
		return result(status, data, errCode, errMsg, "");
	}

	public static <T> ApiResponse<T> failure(HttpStatus status, T data, ErrMsgEnum errEnum) {
		return result(status, data, errEnum.getErrCode(), errEnum.getErrMsg(), "");
	}

	public static <T> ApiResponse<T> exception(HttpStatus status, T data, String exception) {
		return result(status, data, "", "", exception);
	}
}
