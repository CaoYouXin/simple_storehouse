/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.clscpu.storehouse;

import cn.clscpu.storehouse.customer.CustomerManager;
import cn.clscpu.storehouse.goodsmanage.GoodsManager;
import cn.clscpu.storehouse.init.Initiator;
import cn.clscpu.storehouse.security.SecurityFacade;
import cn.clscpu.storehouse.storage.StorageManager;
import cn.clscpu.test.DBTester;
import cn.clscpu.test.TestCatalog;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author CPU
 */
public final class ServiceMapper {

	private final static Map<ServiceName, ServiceBase> services = new HashMap<>();
	private final static List<ServiceName> unAnnotatoinedServices = new ArrayList<>();

	static {
		services.put(ServiceName.init, new Initiator());
		services.put(ServiceName.security, new SecurityFacade());
		services.put(ServiceName.gm, new GoodsManager());
		services.put(ServiceName.customer, new CustomerManager());
		services.put(ServiceName.storage, new StorageManager());
		services.put(ServiceName.dbtest, new DBTester());
		services.put(ServiceName.testcatalog, new TestCatalog());
		
		unAnnotatoinedServices.add(ServiceName.init);
		unAnnotatoinedServices.add(ServiceName.dbtest);
		unAnnotatoinedServices.add(ServiceName.testcatalog);
	}

	static ServiceBase parseService(String target) {
		ServiceBase service = services.get(ServiceName.valueOf(target));
		if (service == null) {
			throw new RuntimeException("target[" + target + "] not found.");
		}
		return service;
	}
	
	public static List<String> getResources() {
		List<String> rets = new ArrayList<>();
		services.entrySet().stream().forEach((entry) -> {
			if (!unAnnotatoinedServices.contains(entry.getKey())) {
				rets.addAll(entry.getValue().getResources());
			}
		});
		return rets;
	}

}
