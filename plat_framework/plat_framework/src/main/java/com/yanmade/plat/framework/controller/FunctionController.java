package com.yanmade.plat.framework.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yanmade.plat.framework.dao.FunctionMapper;
import com.yanmade.plat.framework.entity.ApiResponse;
import com.yanmade.plat.framework.entity.SmFunction;
import com.yanmade.plat.framework.util.ApiResponseUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = "功能接口")
@RestController
@RequestMapping("functions")
public class FunctionController {

    @Autowired
    private FunctionMapper mapper;

    @ApiOperation("获取功能列表")
    @GetMapping
    public ApiResponse<List<SmFunction>> getFunctions() {
        List<SmFunction> functions = mapper.getFunctions();
        return ApiResponseUtil.success(functions);
    }

}
