/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.clscpu.storehouse.goodsmanage;

import cn.clscpu.storehouse.entity.JsonEntity;
import cn.clscpu.storehouse.util.json.Json;
import cn.clscpu.storehouse.util.json.Json.Type;

/**
 *
 * @author CPU
 */
public class Goods implements JsonEntity {

	static Goods getDefault() {
		return new Goods("不存在", "不存在", "不存在");
	}

	private int id;
	private String code;
	private String name;
	private String color;

	Goods(int id, String code, String name, String color) {
		this.id = id;
		this.code = code;
		this.name = name;
		this.color = color;
	}

	Goods(String code, String name, String color) {
		this.code = code;
		this.name = name;
		this.color = color;
	}

	public Goods setId(int id) {
		this.id = id;
		return this;
	}

	public int getId() {
		return id;
	}

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	public String getColor() {
		return color;
	}

	@Override
	public Json getJson() {
		Json json = new Json(Type.object);
		json.addKeyValue("id", new Json(Type.string, "" + id));
		json.addKeyValue("code", new Json(Type.string, code));
		json.addKeyValue("name", new Json(Type.string, name));
		json.addKeyValue("color", new Json(Type.string, color));
		return json;
	}

}
