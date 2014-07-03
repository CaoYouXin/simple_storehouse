/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.clscpu.test;

import cn.clscpu.storehouse.storage.Bill;
import cn.clscpu.storehouse.storage.GoodsClause;
import cn.clscpu.storehouse.util.json.Json;
import cn.clscpu.storehouse.util.json.Json.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author CPU
 */
public class JsonTester {

	public static void main(String args[]) {
		Json root = new Json(Type.object);
//        root.setObject();

		Json array1 = new Json(Type.array);
//        array1.setArray();

		Json obj1 = new Json(Type.object);
//        obj1.setObject();

		Json obj2 = new Json(Type.string);
//        obj2.setString();
		obj2.setString("2月26日翡翠特卖");

		obj1.addKeyValue("title", obj2);

		Json obj3 = new Json(Type.string);
//        obj3.setString();
		obj3.setString("2013/1/15 14:04:54");

		obj1.addKeyValue("date", obj3);

		Json array2 = new Json(Type.array);
//        array2.setArray();

		for (int i = 0; i < 3; i++) {
			Json obj = new Json(Type.object);
//            obj.setObject();

			Json obj6 = new Json(Type.string);
//            obj6.setString();
			obj6.setString(i % 2 == 0 ? "True" : "False");

			obj.addKeyValue("ispicture", obj6);

			Json obj5 = new Json(Type.string);
//            obj5.setString();
			obj5.setString("fuck" + i);

			obj.addKeyValue("content", obj5);

			array2.addJson(obj);
		}

		obj1.addKeyValue("val", array2);

		array1.addJson(obj1);

		root.addKeyValue("job", array1);
		String toJson = root.toJson();

		System.out.println(toJson);
		System.out.println(new Json(toJson).toJson());

		Map<String, String[]> params = new HashMap<>();
		Json json = new Json("{\"a\":\"va\",\"b\":[\"vb1\",\"vb2\"]}");
		json.each((int idx, String key, Json value) -> {
			params.put(key, value.strList().toArray(new String[0]));
		});
		System.out.print("--->");
		for (Map.Entry<String, String[]> entry : params.entrySet()) {
			String key = entry.getKey();
			String[] strings = entry.getValue();
			System.out.print(key + "=" + Arrays.toString(strings) + ";");
		}
		System.out.println();

		Bill bill = new Bill("XXOO", 1, 2, "oh fuck", true)
				.addGoods(new GoodsClause(1, "Oh Fuck", Arrays.asList(1, 2, 3, 7, 4, 5)));
		Json goodses = new Json(Type.array);
		bill.getGoodses().forEach((clause) -> {
			goodses.addJson(clause.getJson());
		});
		goodses.each((int i, String k1, Json clause) -> {
			List<Integer> nums = new ArrayList<>();
			clause.get("nums").each((int j, String k2, Json num) -> {
				nums.add(Integer.parseInt(num.string()));
			});
			bill.addGoods(new GoodsClause(Integer.parseInt(clause.get("goodsid").string()), clause.get("remark").string(), nums));
		});
		System.out.println(bill.getJson().toJson());
	}

}
