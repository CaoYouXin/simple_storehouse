/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cn.clscpu.test;

import cn.clscpu.storehouse.ServiceMapper;
import cn.clscpu.storehouse.customer.CustomerManager;
import cn.clscpu.storehouse.goodsmanage.GoodsManager;
import cn.clscpu.storehouse.init.InitRoleResourceData;
import cn.clscpu.storehouse.storage.StorageManager;
import cn.clscpu.storehouse.util.db.DBConfig;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author CPU
 */
public class InitRoleResourceDataTester {
	
	public static void main(String args[]) {
		Map<String, Integer> datasource = new HashMap<>();
		List<String> resources = ServiceMapper.getResources();
		List<Object[]> data = new ArrayList<>();
		for (int i = 0; i < resources.size(); i++) {
			String rName = resources.get(i);
			data.add(new Object[]{ i, rName });
			datasource.put(rName, i);
		}
		System.out.println(datasource);
		new InitRoleResourceData(DBConfig.SCHEMA, DBConfig.ROLE_RESOURCE, datasource, "role_id", "resource_id")
				.addData(2, StorageManager.STORE, StorageManager.QUERY_STORE, StorageManager.CHECK)
				.addData(3, StorageManager.STORE, StorageManager.QUERY_STORE)
				.addData(4, StorageManager.QUERY_STORE)
				.addData(5, StorageManager.QUERY_STORE, CustomerManager.ADD, GoodsManager.ADD)
				.addData(6, StorageManager.QUERY_STORE, CustomerManager.ADD, GoodsManager.ADD)
				.init();
	}
	
}
