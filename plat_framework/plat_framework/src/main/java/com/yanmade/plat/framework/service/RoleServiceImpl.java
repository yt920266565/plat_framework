package com.yanmade.plat.framework.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yanmade.plat.framework.dao.RoleDepartmentMapper;
import com.yanmade.plat.framework.dao.RoleFunctionMapper;
import com.yanmade.plat.framework.dao.RoleMapper;
import com.yanmade.plat.framework.dao.UserRoleMapper;
import com.yanmade.plat.framework.entity.SmRole;
import com.yanmade.plat.framework.entity.SmUser;
import com.yanmade.plat.framework.enums.ErrMsgEnum;
import com.yanmade.plat.framework.handler.CustomException;
import com.yanmade.plat.framework.util.RedisUtil;

@Service
public class RoleServiceImpl implements RoleService {

    private static final String ROLE_ID_KEY = "role_id_key";
    private static final String PAGE = "page";
	private static final String LIMIT = "limit";

    @Autowired
    private RoleMapper mapper;

    @Autowired
    private RoleFunctionMapper roleFunctionMapper;

    @Autowired
    private RoleDepartmentMapper roleDepartmentMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private CacheService cacheService;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Override
    public SmRole getRole(int id) {
        return mapper.getRole(id);
    }

    @Transactional
    @Override
    public boolean insert(SmRole role) {
        int id = redisUtil.increment(ROLE_ID_KEY);
        role.setId(id);
        role.setCreateTime(new Date());

        boolean result = mapper.insert(role);
        if (!result) {
            throw new CustomException(ErrMsgEnum.NOT_INSERT);
        }

        boolean result1 = roleFunctionMapper.insert(role);
        if (!result1) {
            throw new CustomException(ErrMsgEnum.NOT_INSERT);
        }

        boolean result2 = roleDepartmentMapper.insert(role);
        if (!result2) {
            throw new CustomException(ErrMsgEnum.NOT_INSERT);
        }

        return true;
    }

    @Override
    public boolean update(SmRole role) {
//        if (role.getId() <= 0) {
//            return false;
//        }
        RoleServiceImpl currentProxy = (RoleServiceImpl) AopContext.currentProxy();
        currentProxy.updateRole(role);

        cacheService.refreshRoleUsersCache(role.getId());

        return true;
    }

    @Transactional
    public void updateRole(SmRole role) {
        // 更新角色基本信息
        boolean result = mapper.update(role);
        if (!result) {
            throw new CustomException(ErrMsgEnum.NOT_UPDATE);
        }

        // 删除角色功能关联信息
        roleFunctionMapper.deleteByRoleId(role.getId());

        // 重新插入角色功能关联信息
        boolean result2 = roleFunctionMapper.insert(role);
        if (!result2) {
            throw new CustomException(ErrMsgEnum.NOT_UPDATE);
        }

        // 删除角色部门关联信息
        roleDepartmentMapper.deleteByRoleId(role.getId());

        // 重新插入角色部门关联信息
        boolean result4 = roleDepartmentMapper.insert(role);
        if (!result4) {
            throw new CustomException(ErrMsgEnum.NOT_UPDATE);
        }
    }

    @Override
    public boolean delete(int id) {
        RoleServiceImpl currentProxy = (RoleServiceImpl) AopContext.currentProxy();
        currentProxy.deleteRole(id);

        cacheService.refreshRoleUsersCache(id);
        return true;
    }

    @Transactional
    public void deleteRole(int id) {
        boolean result = mapper.delete(id);
        if (!result) {
            throw new CustomException(ErrMsgEnum.NOT_DELETE);
        }
        //删除sm_user_role里面的数据，没分配角色时没有数据
        mapper.deleteUserRole(id);

        roleFunctionMapper.deleteByRoleId(id);
        roleDepartmentMapper.deleteByRoleId(id);
    }

    @Override
    public List<SmRole> getRoles(int roleId) {
        return mapper.getRoles(roleId);
    }

    @Override
    public boolean relationUsers(List<SmUser> users, int roleId) {
        boolean result = userRoleMapper.insertBatch(users, roleId);
        if (!result) {
            return false;
        }

        for (SmUser user : users) {
            cacheService.refreshUserIsAdminCache(user.getId());
            cacheService.refreshUserFunctionsCache(user.getId());
            cacheService.refreshUserFunctionDepartmentsCache(user.getId());
        }
        return true;
    }

	@Override
	public List<HashMap<String, Object>> getRoleList(Map<String, Object> input) {
		if (input.get(LIMIT) != null) {
			int page = Integer.parseInt(input.get(PAGE).toString());
            int limit = Integer.parseInt(input.get(LIMIT).toString());
			if (page < 1) {
				input.put(PAGE, (page * limit));
			} else {
				input.put(PAGE, (page - 1) * limit);
			}
            input.put(LIMIT, limit);
		}else {
			input.put(LIMIT, 50);
			input.put(PAGE, 0);
		}
		return mapper.getRoleList(input);
	}

	@Override
	public int getRoleCnt() {
		return mapper.getRoleCnt();
	}

}
