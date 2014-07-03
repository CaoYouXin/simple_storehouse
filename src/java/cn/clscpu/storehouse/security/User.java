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
public class User implements JsonEntity {
    
    private int id;
    private String name;//Unique
	private String pwd;
	private int roleId;

	public User(int id, String name, String pwd, int roleId) {
		this.id = id;
		this.name = name;
		this.pwd = pwd;
		this.roleId = roleId;
	}

	public User(String name, String pwd, int roleId) {
		this.name = name;
		this.pwd = pwd;
		this.roleId = roleId;
	}

	public User setId(int id) {
		this.id = id;
		return this;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getPwd() {
		return pwd;
	}

	public int getRoleId() {
		return roleId;
	}

	@Override
	public Json getJson() {
		Json json = new Json(Type.object);
		json.addKeyValue("id", new Json(Type.string, "" + id));
		json.addKeyValue("name", new Json(Type.string, name));
		json.addKeyValue("role_name", new Json(Type.string, RoleManager.getRoleNameById(roleId)));
		return json;
	}

	boolean verify(String pwd) {
		return this.pwd.equals(pwd);
	}
    
}
