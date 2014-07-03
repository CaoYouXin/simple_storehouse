/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.clscpu.storehouse.customer;

import cn.clscpu.storehouse.entity.JsonEntity;
import cn.clscpu.storehouse.util.json.Json;

/**
 *
 * @author CPU
 */
public class Customer implements JsonEntity {

	private int id;
	private String company;
	private String name;
	private String telephone;
	private String mobile;

	public Customer(int id, String company, String name, String telephone, String mobile) {
		this.id = id;
		this.company = company;
		this.name = name;
		this.telephone = telephone;
		this.mobile = mobile;
	}

	public Customer(String company, String name, String telephone, String mobile) {
		this.company = company;
		this.name = name;
		this.telephone = telephone;
		this.mobile = mobile;
	}

	public Customer setId(int id) {
		this.id = id;
		return this;
	}

	public int getId() {
		return id;
	}

	public String getCompany() {
		return company;
	}

	public String getName() {
		return name;
	}

	public String getTelephone() {
		return telephone;
	}

	public String getMobile() {
		return mobile;
	}

	@Override
	public Json getJson() {
		Json json = new Json(Json.Type.object);
		json.addKeyValue("id", new Json(Json.Type.string, "" + id));
		json.addKeyValue("company", new Json(Json.Type.string, company));
		json.addKeyValue("name", new Json(Json.Type.string, name));
		json.addKeyValue("telephone", new Json(Json.Type.string, telephone));
		json.addKeyValue("mobile", new Json(Json.Type.string, mobile));
		return json;
	}

	@Override
	public String toString() {
		if (-1 == id) {
			return "无此客户";
		} else {
			return company + ", " + name + ", " + telephone + ", " + mobile;
		}
	}

	static Customer getDefault() {
		return new Customer(-1, null, null, null, null);
	}
	
}
