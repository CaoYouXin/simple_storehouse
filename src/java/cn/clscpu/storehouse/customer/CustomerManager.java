/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.clscpu.storehouse.customer;

import cn.clscpu.storehouse.ServiceBase;
import cn.clscpu.storehouse.cache.Cache;
import cn.clscpu.storehouse.cache.Synchronizer;
import cn.clscpu.storehouse.idgen.IDGenerator;
import cn.clscpu.storehouse.security.ResourceId;
import cn.clscpu.storehouse.security.SecurityFacade;
import cn.clscpu.storehouse.util.Suc;
import cn.clscpu.storehouse.util.db.DB;
import cn.clscpu.storehouse.util.db.DBConfig;
import cn.clscpu.storehouse.util.db.MyResultSet;
import cn.clscpu.storehouse.util.db.sql.SQLBuilder;
import cn.clscpu.storehouse.util.json.Json;
import cn.clscpu.storehouse.util.json.Json.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 *
 * @author CPU
 */
public final class CustomerManager extends ServiceBase {
	
	public static final String ADD = "添加客户";

	private static final Cache<Integer, Customer> cache = new Cache<>(new Synchronizer<Integer, Customer>() {
		@Override
		public boolean put(Integer key, Customer value) {
			return DB.instance().preparedExecute(new SQLBuilder(DBConfig.SCHEMA, DBConfig.CUSTOMER)
					.insert("id", "company", "name", "telephone", "mobile"), 1, value.getId(), value.getCompany(),
					value.getName(), value.getTelephone(), value.getMobile());
		}

		@Override
		public boolean remove(Integer key, Customer value) {
			throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
		}

		@Override
		public void init(Map<Integer, Customer> data) {
			MyResultSet rs = DB.instance().simpleQuery(new SQLBuilder(DBConfig.SCHEMA, DBConfig.CUSTOMER)
					.select(null, "id", "company", "name", "telephone", "mobile"));
			while (rs.next()) {
				data.put(rs.getInt("id"), new Customer(rs.getInt("id"), rs.getString("company"), 
						rs.getString("name"), rs.getString("telephone"), rs.getString("mobile")));
			}
		}
	});
	
	private static Collection<Customer> getAll() {
		return cache.getValues();
	}

	private static int addCustomerAndReturnId(Customer customer) {
		Suc suc = new Suc();
		IDGenerator.next(DBConfig.CUSTOMER, (long id) -> {
			suc.val(cache.put((int) id, customer.setId((int) id)));
			return suc.val();
		});
		return suc.val() ? customer.getId() : -1;
	}

	public static Customer getCustomerById(int customerId) {
		Customer get = cache.get(customerId);
		return null == get ? Customer.getDefault() : get;
	}
	
	@ResourceId(SecurityFacade.COMMON)
	public final String getAll(Map<String, String[]> params) {
		List<Customer> all = new ArrayList<>(getAll());
		Collections.sort(all, (Customer o1, Customer o2) -> o1.getId() - o2.getId());
		Json json = new Json(Json.Type.array);
		all.stream().forEach((Customer customer) -> {
			json.addJson(customer.getJson());
		});
		return json.toJson();
	}
	
	@ResourceId(SecurityFacade.COMMON)
	public final String getAllSimpleDesc(Map<String, String[]> params) {
		List<Customer> all = new ArrayList<>(getAll());
		Collections.sort(all, (Customer o1, Customer o2) -> o1.getId() - o2.getId());
		Json json = new Json(Json.Type.array);
		all.stream().forEach((Customer customer) -> {
			Json o = new Json(Type.object);
			o.addKeyValue("id", new Json(Type.string, "" + customer.getId()));
			o.addKeyValue("desc", new Json(Type.string, customer.toString()));
			json.addJson(o);
		});
		return json.toJson();
	}

	@ResourceId(ADD)
	public final String addCustomerAndReturnId(Map<String, String[]> params) {
		return "" + addCustomerAndReturnId(new Customer(params.get("company")[0],
				params.get("name")[0], params.get("telephone")[0], params.get("mobile")[0]));
	}

}
