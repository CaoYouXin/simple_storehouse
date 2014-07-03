/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cn.clscpu.storehouse.security;

import cn.clscpu.storehouse.entity.JsonEntity;
import cn.clscpu.storehouse.util.json.Json;
import cn.clscpu.storehouse.util.json.Json.Type;

/**
 *
 * @author CPU
 */
public class Resource implements JsonEntity {
    
    private int id;
    private String name;//Unique

	public Resource(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public Resource(int id) {
		this(id, null);
	}

	public int getId() {
		return id;
	}

	public String getName() {
		if (null == name) {
			return "";
		}
		return name;
	}

	@Override
	public Json getJson() {
		Json json = new Json(Type.object);
		json.addKeyValue("id", new Json(Type.string, "" + id));
		json.addKeyValue("name", new Json(Type.string, name));
		return json;
	}
    
}
