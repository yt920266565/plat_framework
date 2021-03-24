package com.yanmade.plat.framework.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yanmade.plat.framework.entity.ApiResponse;
import com.yanmade.plat.framework.entity.SmFunction;
import com.yanmade.plat.framework.enums.ErrMsgEnum;
import com.yanmade.plat.framework.service.PermissionService;
import com.yanmade.plat.framework.util.ApiResponseUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@Api(tags = "权限接口")
@RequestMapping("/permissions")
public class PermissionController {

    @Autowired
    PermissionService service;

    @ApiOperation("增加权限")
    @PostMapping
    public ApiResponse<Boolean> insert(SmFunction permission) {
        boolean result = service.insert(permission);
        if (!result) {
            return ApiResponseUtil.failure(HttpStatus.INTERNAL_SERVER_ERROR, result, ErrMsgEnum.NOT_INSERT);
        }

        return ApiResponseUtil.success(result);
    }

    @ApiOperation("获取权限")
    @GetMapping("/{id}")
    public ApiResponse<SmFunction> getSmPermission(
            @ApiParam(value = "权限id", required = true) @PathVariable int permissionId) {
        SmFunction permission = service.getSmPermission(permissionId);
        if (permission == null) {
            return ApiResponseUtil.failure(HttpStatus.INTERNAL_SERVER_ERROR, null, ErrMsgEnum.NOT_FONUD);
        }

        return ApiResponseUtil.success(permission);
    }

    @ApiOperation("获取权限列表")
    @GetMapping
    public ApiResponse<List<SmFunction>> getPermissions(int roleId) {
        List<SmFunction> list = service.getPermissions(roleId);
        return ApiResponseUtil.success(list);
    }

}
