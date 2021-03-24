package com.yanmade.plat.framework.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yanmade.plat.framework.dao.UserMapper;
import com.yanmade.plat.framework.entity.ApiResponse;
import com.yanmade.plat.framework.entity.SmRole;
import com.yanmade.plat.framework.entity.SmUser;
import com.yanmade.plat.framework.enums.ErrMsgEnum;
import com.yanmade.plat.framework.service.UserService;
import com.yanmade.plat.framework.util.ApiResponseUtil;
import com.yanmade.plat.framework.util.RemoteUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Api(tags = "用户接口")
@RestController
@RequestMapping("/users")
public class UserController {
    
    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    RemoteUtil remoteUtil;

    @ApiOperation("查询用户")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "角色编号", name = "roleId", required = false, dataType = "int")
    })
    @GetMapping
    public ApiResponse<List<SmUser>> getUsers(SmUser user, Integer roleId) {
        List<SmUser> list = userService.getUsers(user, roleId);
        return ApiResponseUtil.success(list);
    }

    @ApiOperation("新增用户")
    @PostMapping
    public ApiResponse<SmUser> registerUser(
            @ApiParam(value = "用户对象", required = true) @RequestBody @Valid SmUser user) {
        boolean result = userService.insert(user);
        if (!result) {
            return ApiResponseUtil.failure(HttpStatus.BAD_REQUEST, null, ErrMsgEnum.NOT_INSERT);
        }

        return ApiResponseUtil.success(user);
    }

    @ApiOperation("用户修改")
    @PutMapping
    public ApiResponse<SmUser> updateUser(@ApiParam(value = "用户对象", required = true) @RequestBody @Valid SmUser user) {
        boolean result = userService.update(user);
        if (!result) {
            return ApiResponseUtil.failure(HttpStatus.BAD_REQUEST, null, ErrMsgEnum.NOT_UPDATE);
        }

        return ApiResponseUtil.success(user);
    }

    @ApiOperation("修改密码")
    @PutMapping("/{id}/password")
    public ApiResponse<SmUser> modifyPassword(HttpServletRequest request,
            @ApiParam(value = "用户编号", required = true) @PathVariable int id,
            @ApiParam(value = "旧密码", required = true) @RequestParam String oldPassword,
            @ApiParam(value = "新密码", required = true) @RequestParam String newPassword) {
        boolean result = userService.modifyPassword(request, id, oldPassword, newPassword);
        if (!result) {
            return ApiResponseUtil.failure(HttpStatus.BAD_REQUEST, null, ErrMsgEnum.NOT_UPDATE);
        }

        return ApiResponseUtil.success(null);
    }

    @ApiOperation("删除用户")
    @DeleteMapping("/{id}")
    public ApiResponse<Object> deleteUser(@ApiParam(value = "用户编号", required = true) @PathVariable int id) {
        boolean result = userService.delete(id);
        if (!result) {
            return ApiResponseUtil.failure(HttpStatus.BAD_REQUEST, null, ErrMsgEnum.NOT_FONUD);
        }

        return ApiResponseUtil.success(null);
    }
    
    @ApiOperation("批量删除用户")
    @DeleteMapping("/batDelUser")
    public ApiResponse<Object> batDelUser(@ApiParam(value = "用户编号", required = true) @RequestBody Map<String, Object> input) {
    	ArrayList<Integer> list = (ArrayList<Integer>) input.get("userid");
    	input.put("userid", list);
    	boolean result = userService.batDelete(input);
    	if (!result) {
    		return ApiResponseUtil.failure(HttpStatus.BAD_REQUEST, null, ErrMsgEnum.NOT_FONUD);
    	}
    	
    	return ApiResponseUtil.success(null);
    }

    @ApiOperation("获取指定用户权限")
    @GetMapping("/{id}/authority")
    public ApiResponse<Map<String, Object>> getFunctionsByUserId(
            @ApiParam(value = "用户编号", required = true) @PathVariable int id) {
        Map<String, Object> map = userService.getFunctionsByUserId(id);
        return ApiResponseUtil.success(map);
    }

    @ApiOperation("获取当前用户权限")
    @GetMapping("/authority")
    public ApiResponse<Map<String, Object>> getFunctionsByUserId() {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        SmUser user = userMapper.getUserByName(username);
        if (user == null) {
            return ApiResponseUtil.failure(HttpStatus.INTERNAL_SERVER_ERROR, null, ErrMsgEnum.FAILURE);
        }
        Map<String, Object> map = userService.getFunctionsByUserId(user.getId());
        return ApiResponseUtil.success(map);
    }

    @GetMapping("/{id}/roles")
    public ApiResponse<List<SmRole>> getRolesByUserId(@ApiParam(value = "用户编号", required = true) int userId) {
        List<SmRole> list = userService.getRolesByUserId(userId);
        return ApiResponseUtil.success(list);
    }
    
    @ApiOperation("获取根据角色获取角色下的用户")
    @GetMapping("/roles")
    public ApiResponse<Object> getUserByRole(@ApiParam(value = "角色id", required = true) int roleId) {
    	Map<String, Object> map = userMapper.getUserByRole(roleId);
    	return ApiResponseUtil.success(map);
    }

    @ApiOperation("从OA系统同步用户")
    @PostMapping("/users")
    public ApiResponse<List<SmUser>> synchronousUsers() {
        List<SmUser> users = remoteUtil.getUserList();
        boolean result = userService.insertUsers(users);
        if (!result) {
            return ApiResponseUtil.success(null);
        }

        return ApiResponseUtil.success(users);
    }

}
