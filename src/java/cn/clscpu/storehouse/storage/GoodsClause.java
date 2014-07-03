/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.clscpu.storehouse.storage;

import cn.clscpu.storehouse.entity.JsonEntity;
import cn.clscpu.storehouse.goodsmanage.Goods;
import cn.clscpu.storehouse.goodsmanage.GoodsManager;
import cn.clscpu.storehouse.util.StringUtil;
import cn.clscpu.storehouse.util.json.Json;
import cn.clscpu.storehouse.util.json.Json.Type;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author CPU
 */
public class GoodsClause implements JsonEntity {

	private int goodsid;
	private List<Integer> nums;
	private String remark;

	public GoodsClause(int goodsid, String remark, List<Integer> nums) {
		this.goodsid = goodsid;
		this.nums = nums;
		this.remark = remark;
	}

	public int getGoodsid() {
		return goodsid;
	}

	public List<Integer> getNums() {
		return Collections.unmodifiableList(nums);
	}

	public String getRemark() {
		return remark;
	}

	@Override
	public Json getJson() {
		final Json json = new Json(Type.object);
		Goods goods = GoodsManager.getById(goodsid);
		json.addKeyValue("code", new Json(Type.string, goods.getCode()));
		json.addKeyValue("name", new Json(Type.string, goods.getName()));
		json.addKeyValue("color", new Json(Type.string, goods.getColor()));
		
		json.addKeyValue("remark", new Json(Type.string, remark));
		
		json.addKeyValue("num0", new Json(Type.string, "" + nums.get(0)));
		json.addKeyValue("num1", new Json(Type.string, "" + nums.get(1)));
		json.addKeyValue("num2", new Json(Type.string, "" + nums.get(2)));
		json.addKeyValue("num3", new Json(Type.string, "" + nums.get(3)));
		json.addKeyValue("num4", new Json(Type.string, "" + nums.get(4)));
		json.addKeyValue("num5", new Json(Type.string, "" + nums.get(5)));
		json.addKeyValue("num6", new Json(Type.string, "" + nums.get(6)));
		json.addKeyValue("num7", new Json(Type.string, "" + nums.get(7)));
		
		json.addKeyValue("sum", new Json(Type.string, "" + sum(nums)));
		return json;
	}

	private int sum(List<Integer> nums) {
		int sum = 0;
		sum = nums.stream().map((num) -> num).reduce(sum, Integer::sum);
		return sum;
	}

	public String getStrNums() {
		return StringUtil.join(nums, SEPRATOR);
	}

	@Override
	public String toString() {
		return "GoodsClause{" + "goodsid=" + goodsid + ", nums=" + nums + ", remark=" + remark + '}';
	}
	
	static final String SEPRATOR = ";";

}
