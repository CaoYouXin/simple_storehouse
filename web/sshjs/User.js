/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

Ext.define('SSH.User', {
	singleton: true,
	setUser: function(username, password, roleId) {
		this.username = username;
		this.password = password;
		this.roleId = roleId;
		this.record = this.check = this.storage = false;
	},
	getRoleId: function() {
		return this.roleId;
	},
	getUserName: function() {
		return this.username;
	},
	getPassword: function() {
		return this.password;
	},
	canRecord: function(can) {
		if (!can)
			return this.record;
		this.record = can;
	},
	canCheck: function(can) {
		if (!can)
			return this.check;
		this.check = can;
	},
	canStorage: function(can) {
		if (!can)
			return this.storage;
		this.storage = can;
	}
});
