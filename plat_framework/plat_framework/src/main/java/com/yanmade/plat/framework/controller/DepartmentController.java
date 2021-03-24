package com.yanmade.plat.framework.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yanmade.plat.framework.dao.DepartmentMapper;
import com.yanmade.plat.framework.dao.UserMapper;
import com.yanmade.plat.framework.entity.ApiResponse;
import com.yanmade.plat.framework.entity.SmDepartment;
import com.yanmade.plat.framework.enums.ErrMsgEnum;
import com.yanmade.plat.framework.util.ApiResponseUtil;
import com.yanmade.plat.framework.util.RemoteUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = "部门接口")
@RestController
@RequestMapping("/departments")
public class DepartmentController {

    @Autowired
    DepartmentMapper mapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    RemoteUtil remoteUtil;

    @ApiOperation("获取所有部门")
    @GetMapping
    public ApiResponse<List<SmDepartment>> getDepartments(int id) {
        List<SmDepartment> departmentList = mapper.getDepartments();
        List<SmDepartment> resultList = new ArrayList<>();

        if (id <= 0) {
            resultList.addAll(departmentList);
            return ApiResponseUtil.success(resultList);
        } else {
            // 遍历部门下所有部门
            getDepartmentsById(id, departmentList, resultList);
        }

        return ApiResponseUtil.success(resultList);
    }
    
    @ApiOperation("获取拥有权限的用户所在部门")
    @GetMapping(value="/getURDepartments")
    public ApiResponse<List<SmDepartment>> getURDepartments() {
    	List<SmDepartment> departmentList = mapper.getURDepartments();
		return ApiResponseUtil.success(departmentList);
    }

    /**
     * 递归获取指定部门下的所有部门
     * 
     * @param 部门id
     * @param departmentList
     * @param resultList
     */
    private void getDepartmentsById(int id, List<SmDepartment> departmentList, List<SmDepartment> resultList) {
        for (Iterator<SmDepartment> iterator = departmentList.iterator(); iterator.hasNext();) {
            SmDepartment smDepartment = iterator.next();
            if (smDepartment.getId() == id) {
                resultList.add(smDepartment);
            } else if (smDepartment.getPid() == id) {
                getDepartmentsById(smDepartment.getId(), departmentList, resultList);
            }
        }
    }

    
    @ApiOperation("获取指定部门下所有员工")
    @GetMapping("/users")
    public /*ApiResponse<List<SmUser>>*/Map<String, Object> getUsers(@RequestParam Map<String, Object> input) {
    	String limitString = "limit";
    	String pageString = "page";
    	String staffString = "staff";
    	if ( input.get(limitString) != null) {
			int page = Integer.parseInt(input.get(pageString).toString());
			int limit = Integer.parseInt(input.get(limitString).toString());
			if (page < 1) {
				input.put(pageString, (page * limit));
			} else {
				input.put(pageString, (page - 1) * limit);
			}
			input.put(limitString, limit);
		}
    	if(input.containsKey(staffString)) {
			if(isCNChar(input.get(staffString).toString())) {
				input.put("realName", input.get(staffString));
			}else {
				input.put("userName", input.get(staffString));
			}
		} 
    	List<HashMap<String, Object>> userList = userMapper.getUsersByDeptId(input);
        int count = userMapper.getUsersByDepCnt(input);
        Map<String, Object> map = new HashMap<>();
        map.put("code",0);
        map.put("data",userList);
        map.put("count",count);
//        List<SmUser> resultList = new ArrayList<>();
//
//        for (Iterator<SmUser> iterator = userList.iterator(); iterator.hasNext();) {
//            SmUser smUser = iterator.next();
//            if (smUser.getDeptId() == id) {
//                resultList.add(smUser);
//            }
//        }

		return /* ApiResponseUtil.success(userList) */map;
    }
    
    
    @ApiOperation("获取指定部门下拥有权限员工")
    @GetMapping("/getURByDeptId")
    public Map<String, Object> getURByDeptId(@RequestParam Map<String, Object> input) {
    	String limitString = "limit";
    	String pageString = "page";
    	String staffString = "staff";
    	if ( input.get(limitString) != null) {
    		int page = Integer.parseInt(input.get(pageString).toString());
    		int limit = Integer.parseInt(input.get(limitString).toString());
    		if (page < 1) {
    			input.put(pageString, (page * limit));
    		} else {
    			input.put(pageString, (page - 1) * limit);
    		}
    		input.put(limitString, limit);
    	}
    	if(input.containsKey(staffString)) {
			if(isCNChar(input.get(staffString).toString())) {
				input.put("realName", input.get(staffString));
			}else {
				input.put("userName", input.get(staffString));
			}
		} 
    	List<HashMap<String, Object>> userList = userMapper.getURByDeptId(input);
    	int count = userMapper.getURByDepCnt(input);
    	Map<String, Object> map = new HashMap<>();
    	map.put("code",0);
    	map.put("data",userList);
    	map.put("count",count);
    	return map;
    }

    @ApiOperation("从OA系统同步部门")
    @PostMapping("/departments")
    public ApiResponse<List<SmDepartment>> synchronousDepartments() {
        List<SmDepartment> departmentList = remoteUtil.getDepartmentsList();
        boolean result = mapper.insertBatch(departmentList);
        if (!result) {
            return ApiResponseUtil.failure(HttpStatus.INTERNAL_SERVER_ERROR, null, ErrMsgEnum.FAILURE);
        }

        return ApiResponseUtil.success(departmentList);
    }
    
  //判断字符串是否为中文
  	public static boolean isCNChar(String s){
          boolean booleanValue = false;
          for(int i=0; i<s.length(); i++){
              char c = s.charAt(i);
              if(c > 128){
                  booleanValue = true;
                  break;
              }
          }
          return booleanValue;
      }

}
