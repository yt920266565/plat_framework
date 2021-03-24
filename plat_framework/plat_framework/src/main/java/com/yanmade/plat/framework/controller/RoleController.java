package com.yanmade.plat.framework.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yanmade.plat.framework.entity.ApiResponse;
import com.yanmade.plat.framework.entity.SmRole;
import com.yanmade.plat.framework.entity.SmUser;
import com.yanmade.plat.framework.enums.ErrMsgEnum;
import com.yanmade.plat.framework.service.RoleService;
import com.yanmade.plat.framework.util.ApiResponseUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@Api(tags = "角色接口")
@RequestMapping("/roles")
@Validated
public class RoleController {

    @Autowired
    private RoleService service;

    @ApiOperation("获取所有角色")
    @GetMapping
    public ApiResponse<List<SmRole>> getRoles(@RequestParam(defaultValue = "0") int roleId) {
        List<SmRole> list = service.getRoles(roleId);
        return ApiResponseUtil.success(list);
    }
    
    @ApiOperation("获取角色list")
    @GetMapping("/list")
    public Map<String, Object> getRoleList(@RequestParam Map<String, Object> input) {
        List<HashMap<String, Object>> list = service.getRoleList(input);
        int count = service.getRoleCnt();
        Map<String, Object> map = new HashMap<>();
        map.put("data", list);
        map.put("code",0);
        map.put("count", count);
        return map;
    }

    // @ApiOperation("获取角色信息")
    // @GetMapping("/{id}")
    // public ApiResponse<SmRole> getRole(@ApiParam(value = "角色编号", required = true)
    // @PathVariable int id) {
    // SmRole role = service.getRole(id);
    // if (role == null) {
    // return ApiResponseUtil.failure(HttpStatus.NOT_FOUND, role,
    // ErrMsgEnum.NOT_FONUD);
    // }
    //
    // return ApiResponseUtil.success(HttpStatus.OK, role);
    // }

    @ApiOperation("添加角色")
    @PostMapping
    public ApiResponse<SmRole> insert(@ApiParam(value = "角色对象", required = true) @Valid @RequestBody SmRole role) {
        boolean result = service.insert(role);
        if (!result) {
            return ApiResponseUtil.failure(HttpStatus.BAD_REQUEST, null, ErrMsgEnum.NOT_UPDATE);
        }

        return ApiResponseUtil.success(role);
    }

    @ApiOperation("更新角色")
    @PutMapping()
    public ApiResponse<SmRole> update(@ApiParam(value = "角色对象", required = true) @Valid @RequestBody SmRole role) {
        boolean result = service.update(role);
        if (!result) {
            return ApiResponseUtil.failure(HttpStatus.BAD_REQUEST, null, ErrMsgEnum.NOT_UPDATE);
        }

        return ApiResponseUtil.success(role);
    }
    
    
    @ApiOperation("删除角色")
    @DeleteMapping("/{id}")
    public ApiResponse<Object> delete(@ApiParam(value = "角色编号", required = true) @PathVariable int id) {
        boolean result = service.delete(id);
        if (!result) {
            return ApiResponseUtil.failure(HttpStatus.BAD_REQUEST, null, ErrMsgEnum.NOT_FONUD);
        }

        return ApiResponseUtil.success(null);
    }

    @ApiOperation("角色批量关联用户")
    @PostMapping("/{roleId}/users")
    public ApiResponse<Integer> relationUsers(@RequestBody List<SmUser> users, @PathVariable int roleId) {
        boolean result = service.relationUsers(users, roleId);

        if (!result) {
            return ApiResponseUtil.failure(HttpStatus.BAD_REQUEST, roleId, ErrMsgEnum.NOT_FONUD);
        }

        return ApiResponseUtil.success(0);
    }

}
