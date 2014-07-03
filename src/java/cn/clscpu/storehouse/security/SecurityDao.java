/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cn.clscpu.storehouse.security;

import cn.clscpu.storehouse.util.db.DB;
import cn.clscpu.storehouse.util.db.DBConfig;
import cn.clscpu.storehouse.util.db.sql.SQLBuilder;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author CPU
 */
public class SecurityDao {
	
	static int addRole(String roleName, List<Integer> resourceIds) {
		return RoleManager.addAndReturnId(roleName, (role) -> {
			return RoleResourceMapper.mapper(role.getId(), resourceIds, (oldResourceIds) -> {
				Collections.sort(resourceIds, (Integer i1, Integer i2) -> (i1 - i2));
				Collections.sort(oldResourceIds, (Integer i1, Integer i2) -> (i1 - i2));
				List<Integer> toInserts = new ArrayList<>();
				List<Integer> toDeletes = new ArrayList<>();
				parseInsertsAndDeletes(resourceIds, oldResourceIds, toInserts, toDeletes);
				return DB.instance().transaction((conn) -> {
					DB.instance().preparedExecute(conn, new SQLBuilder(DBConfig.SCHEMA, DBConfig.ROLE).insert("id", "name"),
							1, role.getId(), role.getName());
					updateResourceIds(conn, toInserts, role, toDeletes);
				});
			});
		});
	}

	static boolean changeRole(Role role, List<Integer> resourceIds) {
		return RoleManager.changeName(role, () -> {
			return RoleResourceMapper.mapper(role.getId(), resourceIds, (oldResourceIds) -> {
				Collections.sort(resourceIds, (Integer i1, Integer i2) -> (i1 - i2));
				Collections.sort(oldResourceIds, (Integer i1, Integer i2) -> (i1 - i2));
				List<Integer> toInserts = new ArrayList<>();
				List<Integer> toDeletes = new ArrayList<>();
				parseInsertsAndDeletes(resourceIds, oldResourceIds, toInserts, toDeletes);
				return DB.instance().transaction((conn) -> {
					DB.instance().preparedExecute(conn, new SQLBuilder(DBConfig.SCHEMA, DBConfig.ROLE)
							.update("`id`=?", "name"), 1, role.getName(), role.getId());
					updateResourceIds(conn, toInserts, role, toDeletes);
				});
			});
		});
	}

	private static void updateResourceIds(Connection conn, List<Integer> toInserts, Role role, List<Integer> toDeletes) throws SQLException {
		DB.instance().batchExecute(conn, new SQLBuilder(DBConfig.SCHEMA, DBConfig.ROLE_RESOURCE)
				.insert("role_id", "resource_id"), toInserts.size(), (int rowId) -> {
					return new Object[]{ role.getId(), toInserts.get(rowId) };
				});
		DB.instance().batchExecute(conn, new SQLBuilder(DBConfig.SCHEMA, DBConfig.ROLE_RESOURCE)
				.delete("`role_id` = ? AND `resource_id` = ?"), toDeletes.size(), (int rowId) -> {
					return new Object[]{ role.getId(), toDeletes.get(rowId) };
				});
	}

	private static void parseInsertsAndDeletes(List<Integer> resourceIds, List<Integer> oldResourceIds, List<Integer> toInserts, List<Integer> toDeletes) {
		int i = 0, j = 0, size1 = resourceIds.size(), size2 = oldResourceIds.size();
		while (i < size1 && j < size2) {
			if (resourceIds.get(i) < oldResourceIds.get(j)) {
				toInserts.add(resourceIds.get(i));
				i++;
			} else if (resourceIds.get(i) > oldResourceIds.get(j)) {
				toDeletes.add(oldResourceIds.get(j));
				j++;
			} else {
				i++;
				j++;
			}
		}
		while (i < size1) {
			toInserts.add(resourceIds.get(i++));
		}
		while (j < size2) {
			toInserts.add(oldResourceIds.get(j++));
		}
	}
	
}
