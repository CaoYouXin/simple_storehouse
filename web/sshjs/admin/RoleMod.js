/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

Ext.define('SSH.admin.RoleMod', {
	requires: [
		'Ext.form.*',
		'Ext.window.Window',
		'Ext.data.*'
	],
	singleton: true,
	show: function(grid) {
		var me = this;
		me.role = grid.getSelectionModel().getSelection()[0];
		
		var form = Ext.create('Ext.form.Panel', {
			plain: true,
			border: 0,
			bodyPadding: 5,
			url: '/ssh/MessageDispatcher?target=security&message=changeRole&role_id='+me.role.get('id')+'&username='+SSH.User.getUserName()+'&pwd='+SSH.User.getPassword(),
			fieldDefaults: {
				labelWidth: 55,
				anchor: '100%'
			},
			layout: {
				type: 'vbox',
				align: 'stretch'  // Child items are stretched to full width
			},
			items: [
				{
					xtype: 'textfield',
					fieldLabel: '职务名',
					name: 'role_name',
					value: me.role.get('name'),
					minLength: 1,
					minLengthText: '至少1个字符',
					allowBlank: false,
					blankText: '此列不允许为空'
				}, me.getResources()
			]
		});

		var win = Ext.create('Ext.window.Window', {
			title: '修改职务权限',
			modal: true,
			closable: false,
			collapsible: true,
			animCollapse: true,
			maximizable: true,
			width: 400,
			height: 200,
			layout: 'fit',
			items: form,
			dockedItems: [{
					xtype: 'toolbar',
					dock: 'bottom',
					ui: 'footer',
					layout: {
						pack: 'center'
					},
					items: [
						{
							minWidth: 80,
							text: '修改',
							handler: function() {
								form.submit({
									success: function(form, action) {
										if (action.result.msg) {
											grid.getStore().reload();
											win.destroy();
											Ext.example.msg('操作成功', '职务权限已更新');
										} else {
											Ext.Msg.alert('错误', '服务器操作失败');
										}
									},
									failure: function(form, action) {
										switch (action.failureType) {
											case Ext.form.action.Action.CLIENT_INVALID:
												Ext.Msg.alert('错误', '客户端验证失败');
												break;
											case Ext.form.action.Action.CONNECT_FAILURE:
												Ext.Msg.alert('错误', '网络故障');
												break;
											case Ext.form.action.Action.SERVER_INVALID:
												Ext.Msg.alert('错误', action.result.msg);
										}
									}
								});
							}
						}, {
							minWidth: 80,
							text: '还原',
							handler: function() {
								form.getForm().reset();
							}
						}, {
							minWidth: 80,
							text: '返回',
							handler: function() {
								win.destroy();
							}
						}
					]
				}
			]
		});
		win.show();
	},
	
	getResources: function () {
		var me = this;
		var resourceIds = [];
		Ext.Ajax.request({
			async: false,
			url: '/ssh/MessageDispatcher?target=security&message=getResourceMapByRoleId&role_id='+me.role.get('id')+'&username='+SSH.User.getUserName()+'&pwd='+SSH.User.getPassword(),
			success: function(response) {
				var obj = Ext.decode(response.responseText);
				console.dir(obj);
				obj['msg'].forEach(function(ele){
					resourceIds.push(ele);
				});
			},
			failure: function(response) {
				console.log('server-side failure with status code ' + response.status);
				alert('出错啦！错误码是' + response.status);
			}
		});
		var resources = [];
		SSH.Cache.each('resource', function(id, name) {
			resources.push({
				boxLabel: name,
				name: 'resource_id',
				inputValue: id,
				checked: resourceIds.some(function(ele){
					return ele === id;
				})
			});
		});
		return {
			id: 'linkedResources',
			xtype: 'checkboxgroup',
			fieldLabel: '可访问功能',
			// Arrange checkboxes into two columns, distributed vertically
			columns: 2,
			vertical: true,
			items: resources,
			allowBlank: false,
			blankText: '此列不允许为空'
		};
	}
});
