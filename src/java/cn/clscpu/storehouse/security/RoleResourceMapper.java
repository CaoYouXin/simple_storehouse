/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cn.clscpu.storehouse.security;

import cn.clscpu.storehouse.cache.Cache;
import cn.clscpu.storehouse.cache.Synchronizer;
import cn.clscpu.storehouse.util.db.DB;
import cn.clscpu.storehouse.util.db.DBConfig;
import cn.clscpu.storehouse.util.db.MyResultSet;
import cn.clscpu.storehouse.util.db.sql.SQLBuilder;
import cn.clscpu.storehouse.util.json.Json;
import cn.clscpu.storehouse.util.json.Json.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 *
 * @author CPU
 */
public final class RoleResourceMapper {
	
	private static final Cache<Integer, List<Integer>> cache = new Cache<>(new Synchronizer<Integer, List<Integer>>() {

		@Override
		public boolean put(Integer key, List<Integer> value) {
			return true;
		}

		@Override
		public boolean remove(Integer key, List<Integer> value) {
			throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
		}

		@Override
		public void init(Map<Integer, List<Integer>> data) {
			MyResultSet rs = DB.instance().simpleQuery(new SQLBuilder(DBConfig.SCHEMA, DBConfig.ROLE_RESOURCE)
					.select(null, "role_id", "resource_id"));
			while (rs.next()) {
				int roleId = rs.getInt("role_id");
				List<Integer> resourceIds = data.get(roleId);
				if (null == resourceIds) {
					resourceIds = new ArrayList<>();
					data.put(roleId, resourceIds);
				}
				resourceIds.add(rs.getInt("resource_id"));
			}
		}
	});
	
	public static Json getAll() {
		Json json = new Json(Type.array);
		cache.getEntries().stream().forEach((entry) -> {
			Json object = new Json(Type.object);
			object.addKeyValue("role_id", new Json(Type.string, entry.getKey().toString()));
			Json array = new Json(Type.array);
			object.addKeyValue("resource_ids", array);
			entry.getValue().stream().forEach((rid) -> {
				array.addJson(new Json(Type.string, "" + rid));
			});
			json.addJson(object);
		});
		return json;
	}
	
	public static boolean mapper(int roleId, List<Integer> resourceIds, Mapper fn) {
		List<Integer> oldResourceIds = cache.get(roleId);
		if (null == oldResourceIds) {
			oldResourceIds = new ArrayList<>();
		}
		if (fn.map(oldResourceIds)) {
			cache.putButNotSync(roleId, resourceIds);
			return true;
		}
		return false;
	}
	
	public static List<Integer> map(int roleId) {
		List<Integer> get = cache.get(roleId);
		return null == get ? Arrays.asList(new Integer[0]) : get;
	}
	
}
