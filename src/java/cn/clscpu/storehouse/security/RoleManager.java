/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cn.clscpu.storehouse.security;

import cn.clscpu.storehouse.cache.Cache;
import cn.clscpu.storehouse.cache.Synchronizer;
import cn.clscpu.storehouse.idgen.IDGenerator;
import cn.clscpu.storehouse.util.Suc;
import cn.clscpu.storehouse.util.db.DB;
import cn.clscpu.storehouse.util.db.DBConfig;
import cn.clscpu.storehouse.util.db.MyResultSet;
import cn.clscpu.storehouse.util.db.sql.SQLBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author CPU
 */
public final class RoleManager {
	
	private static final Cache<Integer, Role> cache = new Cache<>(new Synchronizer<Integer, Role>() {

		@Override
		public boolean put(Integer key, Role value) {
			return DB.instance().preparedExecute(new SQLBuilder(DBConfig.SCHEMA, DBConfig.ROLE)
					.insertWithUpdate("`name`=?", "id", "name"), 1, value.getId(), value.getName(), value.getName());
		}

		@Override
		public boolean remove(Integer key, Role value) {
			throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
		}

		@Override
		public void init(Map<Integer, Role> data) {
			MyResultSet rs = DB.instance().simpleQuery(new SQLBuilder(DBConfig.SCHEMA, DBConfig.ROLE)
					.select(null, "id", "name"));
			while (rs.next()) {
				data.put(rs.getInt("id"), new Role(rs.getInt("id"), rs.getString("name")));
			}
		}
	});
	
	public static List<Role> getAll() {
		return new ArrayList<>(cache.getValues());
	}
	
	public static int addAndReturnId(String name, AddRole fn) {
		Suc suc = new Suc();
		Role role = new Role(name);
		IDGenerator.next(DBConfig.ROLE, (long id) -> {
			suc.val(fn.add(role.setId((int) id)));
			return suc.val();
		});
		if (suc.val()) {
			cache.putButNotSync(role.getId(), role);
			return role.getId();
		}
		return -1;
	}
	
	public static boolean changeName(Role role, ChangeRole fn) {
		if (fn.change()) {
			cache.putButNotSync(role.getId(), role);
			return true;
		}
		return false;
	}

	static String getRoleNameById(int roleId) {
		Role get = cache.get(roleId);
		return null == get ? "无此职务" : get.getName();
	}
	
}
