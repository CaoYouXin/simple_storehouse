/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.clscpu.storehouse.storage;

import cn.clscpu.storehouse.customer.CustomerManager;
import cn.clscpu.storehouse.entity.JsonEntity;
import cn.clscpu.storehouse.security.UserManager;
import static cn.clscpu.storehouse.util.DateUtil.formatDateTime;
import cn.clscpu.storehouse.util.json.Json;
import cn.clscpu.storehouse.util.json.Json.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 *
 * @author CPU
 */
public class Bill implements JsonEntity {

	private int id;
	private String code;
	private List<GoodsClause> goodses;
	private int customerId;
	private int recorderId;
	private int checkerId;
	private String remark;
	private Date created;
	private boolean isIn;

	public Bill(String code, int customerId, int recorderId, String remark, boolean isIn) {
		this(-1, code, customerId, recorderId, 0, remark, null, isIn);
	}

	public Bill(int id, String code, int customerId, int recorderId, int checkerId, String remark, Date created, boolean isIn) {
		this.id = id;
		this.code = code;
		this.customerId = customerId;
		this.recorderId = recorderId;
		this.checkerId = checkerId;
		this.remark = remark;
		this.created = created;
		this.isIn = isIn;
	}

	public Bill setId(int id) {
		this.id = id;
		return this;
	}
	
	public int getId() {
		return this.id;
	}
	
	public Bill addGoods(GoodsClause goods) {
		if (null == goodses) {
			goodses = new ArrayList<>();
		}
		goodses.add(goods);
		return this;
	}

	public String getCode() {
		return code;
	}

	public List<GoodsClause> getGoodses() {
		return Collections.unmodifiableList(goodses);
	}

	public String getRemark() {
		return remark;
	}

	public boolean isIn() {
		return isIn;
	}

	public int getCustomerId() {
		return customerId;
	}

	public int getRecorderId() {
		return recorderId;
	}

	public int getCheckerId() {
		return checkerId;
	}

	@Override
	public Json getJson() {
		final Json json = new Json(Type.object);
		json.addKeyValue("id", new Json(Type.string, "" + id));
		json.addKeyValue("code", new Json(Type.string, code));
		json.addKeyValue("customer", new Json(Type.string, CustomerManager.getCustomerById(customerId).toString()));
		json.addKeyValue("recorder", new Json(Type.string, UserManager.getName(recorderId)));
		json.addKeyValue("checker", new Json(Type.string, UserManager.getName(checkerId)));
		json.addKeyValue("remark", new Json(Type.string, remark));
		json.addKeyValue("created", new Json(Type.string, formatDateTime(created)));
		json.addKeyValue("isIn", new Json(Type.string, isIn ? "入库" : "出库"));
		if (null == goodses || 0 == goodses.size()) {
			return json;
		}
		
		final Json array = new Json(Type.array);
		json.addKeyValue("goodses", array);

		goodses.stream().forEach((goodsClause) -> {
			array.addJson(goodsClause.getJson());
		});
		return json;
	}

}
