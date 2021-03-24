package com.yanmade.plat.framework.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yanmade.plat.framework.entity.SmDepartment;
import com.yanmade.plat.framework.entity.SmUser;

@Configuration
public class RemoteUtil {
    private static final String USERS_URL = "http://oa.yanmade.com/ExtraInterface/getUserInfoList.action?staStatus=1";
    private static final String DEPARTMENTS_URL = "http://oa.yanmade.com/ExtraInterface/getDeptList.action";

    @Autowired
    private RestTemplate restTemplate;

    public List<SmUser> getUserList() {
        List<SmUser> userList = new ArrayList<>();
        JSONObject result = restTemplate.getForObject(USERS_URL, JSONObject.class);
        JSONArray array = result.getJSONArray("list");

        for (int i = 0; i < array.size(); i++) {
            JSONObject object = array.getJSONObject(i);

            SmUser user = new SmUser();
            user.setId(object.getIntValue("staId"));
            user.setUsername(object.getString("staffCode"));
            user.setRealName(object.getString("staffName"));
            user.setPhone(object.getString("mobile"));
            user.setDeptId(object.getIntValue("deptId"));
            user.setDeptName(object.getString("deptName"));
            user.setCreateTime(object.getSqlDate("st_date"));

            userList.add(user);
        }

        return userList;
    }

    public List<SmDepartment> getDepartmentsList() {
        List<SmDepartment> departmentList = new ArrayList<>();
        JSONObject result = restTemplate.getForObject(DEPARTMENTS_URL, JSONObject.class);
        JSONArray array = result.getJSONArray("list");

        for (int i = 0; i < array.size(); i++) {
            JSONObject object = array.getJSONObject(i);

            SmDepartment department = new SmDepartment();
            department.setId(object.getIntValue("id"));
            department.setName(object.getString("name"));
            department.setDescription(object.getString("remark"));
            department.setPid(object.getIntValue("upper_id"));

            departmentList.add(department);
        }

        return departmentList;
    }

}
