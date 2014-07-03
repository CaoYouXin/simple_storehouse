/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cn.clscpu.storehouse.security;

import cn.clscpu.storehouse.entity.JsonEntity;
import cn.clscpu.storehouse.util.json.Json;

/**
 *
 * @author CPU
 */
public class Role implements JsonEntity {
    
    private int id;
    private String name;//Unique

	public Role(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public Role(String name) {
		this.name = name;
	}

	public Role setId(int id) {
		this.id = id;
		return this;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	@Override
	public Json getJson() {
		Json json = new Json(Json.Type.object);
		json.addKeyValue("id", new Json(Json.Type.string, "" + id));
		json.addKeyValue("name", new Json(Json.Type.string, name));
		return json;
	}

}
