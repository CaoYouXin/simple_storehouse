/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.clscpu.storehouse.storage;

import cn.clscpu.storehouse.ServiceBase;
import cn.clscpu.storehouse.security.ResourceId;
import cn.clscpu.storehouse.security.SecurityFacade;
import cn.clscpu.storehouse.security.UserManager;
import cn.clscpu.storehouse.util.Bool;
import static cn.clscpu.storehouse.util.DateUtil.parseDate;
import static cn.clscpu.storehouse.util.DateUtil.parseDateTime;
import cn.clscpu.storehouse.util.json.Json;
import cn.clscpu.storehouse.util.json.Json.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 *
 * @author CPU
 */
public final class StorageManager extends ServiceBase {

	public static final String STORE = "添加出入库记录";
	public static final String CHECK = "审核出入库记录";
	public static final String QUERY_STORE = "查询库存情况";
	
	@ResourceId(STORE)
	public String store(Map<String, String[]> params) {
		Bill bill = new Bill(params.get("code")[0], Integer.parseInt(params.get("customer")[0]),
				UserManager.getId(params.get("username")[0]), params.get("remark")[0],
				Bool.valueOf(params.get("isIn")[0]).val());

		Json goodses = new Json(params.get("goodses")[0]);
		goodses.each((int i, String k1, Json clause) -> {
			List<Integer> nums = new ArrayList<>();
			clause.get("nums").each((int j, String k2, Json num) -> {
				nums.add(Integer.parseInt(num.string()));
			});
			bill.addGoods(new GoodsClause(Integer.parseInt(clause.get("goodsid").string()), clause.get("remark").string(), nums));
		});

		return "" + StorageDao.add(bill);
	}

	@ResourceId(SecurityFacade.COMMON)
	public String queryBills(Map<String, String[]> params) {
		List<Bill> bills = StorageDao.query(parseDate(params.get("from")[0]), parseDate(params.get("to")[0]),
				params.get("code")[0], Integer.parseInt(params.get("customer")[0]),
				Integer.parseInt(params.get("recorder")[0]),
				Integer.parseInt(params.get("checker")[0]), params.get("remark")[0],
				Bool.valueOf(params.get("isIn")[0]));
		Collections.sort(bills, (Bill o1, Bill o2) -> o1.getId() - o2.getId());
		
		Json json = new Json(Type.array);
		for (int i = 0; i < bills.size(); i++) {
			Bill bill = bills.get(i);
			json.addJson(bill.getJson());
		}
		return json.toJson();
	}

	@ResourceId(SecurityFacade.COMMON)
	public String getBillById(Map<String, String[]> params) {
		List<GoodsClause> goodses = StorageDao.getBillById(Integer.parseInt(params.get("id")[0]), parseDateTime(params.get("date")[0]));
			
		Json json = new Json(Type.array);
		for (int i = 0; i < goodses.size(); i++) {
			GoodsClause goodsinfo = goodses.get(i);
			json.addJson(goodsinfo.getJson());
		}
		return json.toJson();
	}

	@ResourceId(CHECK)
	public String check(Map<String, String[]> params) {
		return StorageDao.check(Integer.parseInt(params.get("id")[0]), UserManager.getId(params.get("username")[0])).boolStrVal();
	}

	@ResourceId(QUERY_STORE)
	public String queryStorage(Map<String, String[]> params) {
		List<Storage> query = StorageCache.query(parseDate(params.get("date")[0]), params.get("code")[0],
				params.get("name")[0], params.get("color")[0]);
		Json json = new Json(Type.array);
		query.stream().forEach((Storage storage) -> {
			json.addJson(storage.getJson());
		});
		return json.toJson();
	}

}
