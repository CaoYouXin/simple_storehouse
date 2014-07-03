/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cn.clscpu.storehouse.security;

import cn.clscpu.storehouse.ServiceBase;
import cn.clscpu.storehouse.util.Bool;
import static cn.clscpu.storehouse.util.StringUtil.valueOf;
import cn.clscpu.storehouse.util.json.Json;
import cn.clscpu.storehouse.util.json.Json.Type;
import cn.clscpu.test.TestConfig;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author CPU
 */
public final class SecurityFacade extends ServiceBase {

	public static final String COMMON = "通用";
	
//	@ResourceId("查询所有用户")
	@ResourceId(COMMON)
	public String getAllUsers(Map<String, String[]> params) {
		List<User> all = UserManager.getAll();
		Json json = new Json(Type.array);
		all.stream().forEach((user) -> {
			json.addJson(user.getJson());
		});
		return json.toJson();
	}
	
//	@ResourceId("添加用户")
	public String addUser(Map<String, String[]> params) {
		return "" + UserManager.add(params.get("name")[0], params.get("npwd")[0], Integer.parseInt(params.get("role_id")[0]));
	}
	
//	@ResourceId("修改用户角色")
	public String changeUserRole(Map<String, String[]> params) {
		return Bool.valuseOf(UserManager.change(params.get("name")[0], Integer.parseInt(params.get("role_id")[0]))).boolStrVal();
	}
	
//	@ResourceId("查询所有角色")
	public String getAllRoles(Map<String, String[]> params) {
		List<Role> all = RoleManager.getAll();
		Json json = new Json(Type.array);
		all.stream().forEach((role) -> {
			json.addJson(role.getJson());
		});
		return json.toJson();
	}
	
//	@ResourceId("添加角色")
	public String addRole(Map<String, String[]> params) {
		List<Integer> resourceIds = parseResourceIds(params);
		return "" + SecurityDao.addRole(params.get("role_name")[0], resourceIds);
	}
	
//	@ResourceId("修改角色")
	public String changeRole(Map<String, String[]> params) {
		int roleId = Integer.parseInt(params.get("role_id")[0]);
		if (roleId <= 1) {
			return Bool.False.toString();
		}
		List<Integer> resourceIds = parseResourceIds(params);
		return Bool.valuseOf(SecurityDao.changeRole(new Role(roleId, params.get("role_name")[0]), resourceIds)).boolStrVal();
	}

	private List<Integer> parseResourceIds(Map<String, String[]> params) throws NumberFormatException {
		List<Integer> resourceIds = new ArrayList<>();
		for (String resourceId : params.get("resource_id")) {
			resourceIds.add(Integer.parseInt(resourceId));
		}
		return resourceIds;
	}
	
//	@ResourceId("查询全部资源")
	@ResourceId(SecurityFacade.COMMON)
	public String getAllResources(Map<String, String[]> params) {
		List<Resource> all = ResourceCache.getAll();
		Json json = new Json(Type.array);
		all.stream().forEach((resource) -> {
			json.addJson(resource.getJson());
		});
		return json.toJson();
	}
	
//	@ResourceId("查询所有角色资源链接情况")
	@ResourceId(SecurityFacade.COMMON)
	public String getResourceMapByRoleId(Map<String, String[]> params) {
		List<Integer> rids = RoleResourceMapper.map(Integer.parseInt(params.get("role_id")[0]));
		Json json = new Json(Type.array);
		rids.stream().forEach((rid) -> {
			json.addJson(new Json(Type.string, rid.toString()));
		});
		return json.toJson();
	}
	
	public static boolean canMessaging(String userName, String pwd, String resoureName) {
		int roleId = UserManager.getRole(userName, pwd);
		if (-1 == roleId) {
			return false;
		} else if (SecurityFacade.COMMON.equals(resoureName)) {
			return true;
		}else if (TestConfig.FOR_TEST.equals(resoureName)) {
			return 0 == roleId;
		} else if (1 == roleId) {
			return null == resoureName || SecurityFacade.COMMON.equals(resoureName);
		} else {
			return RoleResourceMapper.map(roleId).stream().anyMatch((resourceId) ->
					(ResourceCache.getResourceNameById(resourceId).equals(resoureName)));
		}
	}
	
//	@ResourceId("修改用户密码")
	public String changeUserPwd(Map<String, String[]> params) {
		return Bool.valuseOf(UserManager.change(params.get("name")[0], params.get("npwd")[0])).boolStrVal();
	}
	
	@ResourceId(COMMON)
	public String changePwdOneself(Map<String, String[]> params) {
		return Bool.valuseOf(UserManager.change(params.get("username")[0], params.get("npwd")[0])).boolStrVal();
	}
	
	@ResourceId(COMMON)
	public String login(Map<String, String[]> params) {
		return valueOf(UserManager.getRole(params.get("username")[0]));
	}
	
}
