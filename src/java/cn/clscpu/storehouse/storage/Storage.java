/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cn.clscpu.storehouse.storage;

import cn.clscpu.storehouse.entity.JsonEntity;
import cn.clscpu.storehouse.goodsmanage.Goods;
import cn.clscpu.storehouse.goodsmanage.GoodsManager;
import static cn.clscpu.storehouse.util.DateUtil.formatDate;
import cn.clscpu.storehouse.util.json.Json;
import cn.clscpu.storehouse.util.json.Json.Type;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 *
 * @author CPU
 */
public class Storage implements JsonEntity {

	private Date date;
	private int goodsId;
	private List<Integer> nums;

	public Storage(Date date, int goodsId, List<Integer> nums) {
		this.date = date;
		this.goodsId = goodsId;
		this.nums = nums;
	}
	
	@Override
	public Json getJson() {
		Json json = new Json(Type.object);
		json.addKeyValue("date", new Json(Type.string, formatDate(date)));
		Goods goods = GoodsManager.getById(goodsId);
		json.addKeyValue("code", new Json(Type.string, goods.getCode()));
		json.addKeyValue("name", new Json(Type.string, goods.getName()));
		json.addKeyValue("color", new Json(Type.string, goods.getColor()));
		
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
	
	public static void main(String args[]) {
		List<Integer> list = Arrays.asList(1, 2, 4, 5, 6);
		System.out.println(new Storage(null, 0, null).sum(list));
	}
	
}
