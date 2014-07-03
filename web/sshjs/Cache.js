/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

Ext.define('SSH.Cache', {
	singleton: true,
	data: new Ext.util.HashMap(),
	put: function(table, key, value) {
		var table_cache = this.data.get(table);
		if (!table_cache) {
			table_cache = new Ext.util.HashMap();
			this.data.add(table, table_cache);
		}
		table_cache.add(key, value);
	},
	get: function(table, key) {
		var table_cache = this.data.get(table);
		if (!table_cache)
			return null;
		return table_cache.get(key);
	},
	each: function(table, callback) {
		var table_cache = this.data.get(table);
		if (!table_cache)
			return;
		table_cache.each(function(key, value){
			callback(key, value);
		});
	},
	init: function() {
		var table_cache = this.data.get('resource');
		if (!table_cache) {
			table_cache = new Ext.util.HashMap();
			this.data.add('resource', table_cache);
			Ext.Ajax.request({
				url: '/ssh/MessageDispatcher?target=security&message=getAllResources&username='+SSH.User.getUserName()+'&pwd='+SSH.User.getPassword(),
				success: function(response) {
					var obj = Ext.decode(response.responseText);
					console.dir(obj);
					obj['msg'].forEach(function(ele){
						if ('通用' !== ele['name']) {
							table_cache.add(ele['id'], ele['name']);
						}
					});
				},
				failure: function(response) {
					console.log('server-side failure with status code ' + response.status);
					alert('出错啦！错误码是' + response.status);
				}
			});
		}
	}
});
