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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author CPU
 */
public final class ResourceCache {
	
	private static final Cache<Integer, Resource> cache = new Cache<>(new Synchronizer<Integer, Resource>() {

		@Override
		public boolean put(Integer key, Resource value) {
			throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
		}

		@Override
		public boolean remove(Integer key, Resource value) {
			throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
		}

		@Override
		public void init(Map<Integer, Resource> data) {
			MyResultSet rs = DB.instance().simpleQuery(new SQLBuilder(DBConfig.SCHEMA, DBConfig.RESOURCE).select(null, "id", "name"));
			while (rs.next()) {
				int rid = rs.getInt("id");
				String name = rs.getString("name");
				data.put(rid, new Resource(rid, name));
			}
		}
	});
	
	public static String getResourceNameById(int id) {
		return cache.get(id).getName();
	}
	
	public static List<Resource> getAll() {
		return new ArrayList<>(cache.getValues());
	}
	
}
