package com.yanmade.plat.framework.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yanmade.plat.framework.dao.FunctionMapper;
import com.yanmade.plat.framework.dao.UserMapper;
import com.yanmade.plat.framework.dao.UserRoleMapper;
import com.yanmade.plat.framework.entity.SmRole;
import com.yanmade.plat.framework.entity.SmUser;
import com.yanmade.plat.framework.enums.ErrMsgEnum;
import com.yanmade.plat.framework.handler.CustomException;
import com.yanmade.plat.framework.util.CommonUtil;
import com.yanmade.plat.framework.util.JwtTokenUtil;
import com.yanmade.plat.framework.util.RedisUtil;

@Service
public class UserServiceImpl implements UserService {

	private static final String USER_ID_KEY = "user_id_key";

	@Autowired
	private UserMapper mapper;

	@Autowired
	private UserRoleMapper userRoleMapper;

	@Autowired
	private RedisUtil redisUtil;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private CommonUtil commonUtil;

	@Autowired
	private CacheService cacheService;

	@Autowired
	private FunctionMapper functionMapper;

	@Override
	public List<SmUser> getUsers(SmUser user, Integer roleId) {
		if (roleId == null) {
			roleId = 0;
		}
		Map<String, Object> map = commonUtil.convertObjectToMap(user);
		map.put("roleId", roleId);
		return mapper.getUsers(map);
	}

	public List<SmUser> getUsers(SmUser user) {
		Map<String, Object> map = commonUtil.convertObjectToMap(user);
		map.put("roleId", 0);
		return mapper.getUsers(map);
	}

	@Override
	public boolean insert(SmUser user) {
		UserServiceImpl userServiceImpl = (UserServiceImpl) AopContext.currentProxy();
		userServiceImpl.insertUser(user);

		int userId = user.getId();
		cacheService.refreshUserIsAdminCache(userId);
		cacheService.refreshUserFunctionsCache(userId);
		cacheService.refreshUserFunctionDepartmentsCache(userId);

		return true;
	}

	@Transactional
	public boolean insertUser(SmUser user) {
		int id = redisUtil.increment(USER_ID_KEY);
		user.setId(id);
		user.setPassword(encodePassword(user.getUsername(), user.getPassword()));
		user.setCreateTime(new Date());
		boolean result = mapper.insert(user);
		if (!result) {
			throw new CustomException(ErrMsgEnum.NOT_INSERT);
		}

		boolean result1 = true;
		if (!user.getRoles().isEmpty()) {
			result1 = userRoleMapper.insert(user);
		}

		if (!result1) {
			throw new CustomException(ErrMsgEnum.NOT_INSERT);
		}

		return true;
	}

	@Override
	public boolean modifyPassword(HttpServletRequest request, int id, String oldPassword, String newPassword) {
		// 查询用户信息
		SmUser user = mapper.getUserById(id);
		if (user == null) {
			return false;
		}

		String username = user.getUsername();
		// 检查参数中旧密码与查出来的密码是否匹配
		if (!matches(username + " " + oldPassword, user.getPassword())) {
			return false;
		}

		// 修改密码
		SmUser smUser = new SmUser();
		smUser.setId(id);
		smUser.setPassword(encodePassword(username, newPassword));
		boolean result = mapper.updateUser(smUser);

		if (result) {
			// 修改密码成功删除token
			String tokenHeader = request.getHeader(JwtTokenUtil.TOKEN_HEADER);
			if (Objects.nonNull(tokenHeader)) {
				String token = tokenHeader.replace(JwtTokenUtil.TOKEN_PREFIX, "");

				redisUtil.delete(username + ":" + token);
			}
		}

		return result;
	}

	@Override
	public boolean delete(int userId) {
		UserServiceImpl currentProxy = (UserServiceImpl) AopContext.currentProxy();
		currentProxy.deleteUser(userId);
		cacheService.deleteCache(userId);
		return true;
	}

	@Transactional
	public boolean deleteUser(int userId) {
		boolean result = userRoleMapper.deleteByUserId(userId);
		if (!result) {
			throw new CustomException(ErrMsgEnum.NOT_DELETE);
		}
		return true;
	}

	@Override
	public Map<String, Object> getFunctionsByUserId(int userId) {
		Map<String, Object> map = new HashMap<>();

		if (cacheService.isAdminCache(userId)) {
			List<String> nameList = functionMapper.getFunctionName();
			map.put("authority", nameList.toArray());
			return map;
		}

		// 先查缓存
		Set<Object> set = cacheService.getUserFunctionsCache(userId);
		if (set != null) {
			Object[] nameArray = set.toArray();
			map.put("authority", nameArray);
		}

		return map;
	}

	// 检查密码
	private boolean matches(String password, String encodedPassword) {
		return passwordEncoder.matches(password, encodedPassword);
	}

	// 密码加密
	private String encodePassword(String username, String password) {
		return passwordEncoder.encode(username + " " + password);
	}

	@Override
	public List<SmRole> getRolesByUserId(int userId) {
		return mapper.getRolesByUserId(userId);
	}

	@Override
	public boolean update(SmUser user) {
		if (user.getId() <= 0) {
			return false;
		}

		UserServiceImpl userServiceImpl = (UserServiceImpl) AopContext.currentProxy();
		userServiceImpl.updateUser(user);

		cacheService.refreshUserIsAdminCache(user.getId());
		cacheService.refreshUserFunctionsCache(user.getId());
		cacheService.refreshUserFunctionDepartmentsCache(user.getId());

		return true;
	}

	@Transactional
	public void updateUser(SmUser user) {
		// 先更新用户基本信息 , 再删除用户角色,再插入用户角色，有一个失败则回滚
		boolean result = mapper.updateUser(user);
		if (!result) {
			throw new CustomException(ErrMsgEnum.NOT_UPDATE);
		}

		userRoleMapper.deleteByUserId(user.getId());

		boolean result2 = userRoleMapper.insert(user);
		if (!result2) {
			throw new CustomException(ErrMsgEnum.NOT_UPDATE);
		}
	}

	@Override
	@Transactional
	public boolean insertUsers(List<SmUser> users) {
		boolean del = mapper.delAllUser();
		if (!del) {
			return false;
		}
		return mapper.insertBatch(users);
	}

	@Override
	public boolean batDelete(Map<String, Object> input) {
		UserServiceImpl currentProxy = (UserServiceImpl) AopContext.currentProxy();
		currentProxy.batDelUser(input);
		cacheService.deleteCache(input);
		return true;
	}

	@Transactional
	public boolean batDelUser(Map<String, Object> input) {
		boolean result = userRoleMapper.batDelete(input);
		if (!result) {
			throw new CustomException(ErrMsgEnum.NOT_DELETE);
		}
		return true;
	}
	
	

}
