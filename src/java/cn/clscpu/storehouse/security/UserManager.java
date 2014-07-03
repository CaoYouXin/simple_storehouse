/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cn.clscpu.storehouse.security;

import cn.clscpu.storehouse.cache.Cache;
import cn.clscpu.storehouse.cache.Synchronizer;
import cn.clscpu.storehouse.idgen.IDGenerator;
import cn.clscpu.storehouse.util.db.DB;
import cn.clscpu.storehouse.util.db.DBConfig;
import cn.clscpu.storehouse.util.db.MyResultSet;
import cn.clscpu.storehouse.util.db.sql.SQLBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author CPU
 */
public final class UserManager {
	
	private static final Map<Integer, String> id_name = new ConcurrentHashMap<>();
	private static final Cache<String, User> cache = new Cache<>(new Synchronizer<String, User>() {

		@Override
		public boolean put(String key, User value) {
			return DB.instance().preparedExecute(new SQLBuilder(DBConfig.SCHEMA, DBConfig.USER)
					.insertWithUpdate("`name`=?, `pwd`=?, `role_id`=?", "id", "name", "pwd", "role_id"), 1,
					value.getId(), value.getName(), value.getPwd(), value.getRoleId(),
					value.getName(), value.getPwd(), value.getRoleId());
		}

		@Override
		public boolean remove(String key, User value) {
			throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
		}

		@Override
		public void init(Map<String, User> data) {
			MyResultSet rs = DB.instance().simpleQuery(new SQLBuilder(DBConfig.SCHEMA, DBConfig.USER)
					.select(null, "id", "name", "pwd", "role_id"));
			while (rs.next()) {
				data.put(rs.getString("name"), new User(rs.getInt("id"), rs.getString("name"), rs.getString("pwd"),
						rs.getInt("role_id")));
				id_name.put(rs.getInt("id"), rs.getString("name"));
			}
		}
	});
	
	public static List<User> getAll() {
		return new ArrayList<>(cache.getValues());
	}
	
	public static boolean change(String name, String newPwd) {
		User user = cache.get(name);
		if (null == user) {
			return false;
		}
		return cache.put(name, new User(user.getId(), name, newPwd, user.getRoleId()));
	}
	
	public static boolean change(String name, int roleId) {
		User user = cache.get(name);
		return cache.put(name, new User(user.getId(), name, user.getPwd(), roleId));
	}
	
	public static int add(String name, String newPwd, int role_id) {
		User cUser = cache.get(name);
		if (null != cUser) {
			return -1;
		}
		User user = new User(name, newPwd, role_id);
		IDGenerator.next(DBConfig.USER, (id) -> {
			boolean put = cache.put(name, user.setId((int) id));
			if (put) {
				id_name.put((int) id, name);
			} else {
				user.setId(-1);
			}
			return put;
		});
		return user.getId();
	}
	
	public static int getRole(String name, String pwd) {
		User get = cache.get(name);
		if (null == get) {
			return -1;
		}
		return get.verify(pwd) ? get.getRoleId() : -1;
	}
	
	public static int getRole(String name) {
		User get = cache.get(name);
		if (null == get) {
			return -1;
		}
		return get.getRoleId();
	}
	
	public static int getId(String name) {
		User get = cache.get(name);
		if (null == get) {
			return -1;
		}
		return get.getId();
	}

	public static String getName(int userId) {
		if (0 == userId) 
			return "未审核";
		String get = id_name.get(userId);
		return null == get ? "无此用户" : get;
	}
	
}
