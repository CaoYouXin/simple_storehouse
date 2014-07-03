/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

Ext.define('SSH.admin.UserAdd', {
	requires: [
		'Ext.form.*',
		'Ext.window.Window',
		'Ext.data.*'
	],
	singleton: true,
	show: function(grid) {
		var me = this;
		
		var form = Ext.create('Ext.form.Panel', {
			plain: true,
			border: 0,
			bodyPadding: 5,
			url: '/ssh/MessageDispatcher?target=security&message=addUser&username='+SSH.User.getUserName()+'&pwd='+SSH.User.getPassword(),
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
					fieldLabel: '用户名',
					name: 'name',
					minLength: 1,
					minLengthText: '至少1个字符',
					allowBlank: false,
					blankText: '此列不允许为空'
				}, {
					xtype: 'textfield',
					fieldLabel: '密码',
					name: 'plainpwd',
					submitValue: false,
					inputType: 'password',
					minLength: 5,
					minLengthText: '至少5个字符',
					allowBlank: false,
					blankText: '此列不允许为空'
				}, {
					xtype: 'combobox',
					fieldLabel: '职务',
					store: new Ext.data.JsonStore({
						autoLoad: true,
						proxy: {
							type: 'ajax',
							url: '/ssh/MessageDispatcher?target=security&message=getAllRoles&username='+SSH.User.getUserName()+'&pwd='+SSH.User.getPassword(),
							reader: {
								type: 'json',
								root: 'msg',
								idProperty: 'id'
							}
						},
						fields: [
						   { name: 'id' },
						   { name: 'name' }
						],
						sorters: [{
							property: 'name',
							direction: 'ASC'
						}]
					}),
					editable: false,
					queryMode: 'local',
					displayField: 'name',
					valueField: 'id',
					name: 'role_id',
					allowBlank: false,
					blankText: '此列不允许为空'
				}
			]
		});

		var win = Ext.create('Ext.window.Window', {
			title: '创建用户',
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
							text: '创建',
							handler: function() {
								var fields = form.getForm().getFields();
								var plianPwd = fields.get(1).getValue();
								var md5pwd = hex_md5(plianPwd);
								form.submit({
									params: {
										npwd: md5pwd
									},
									success: function(form, action) {
										if (-1 === action.result.msg) {
											Ext.Msg.alert('错误', '服务器操作失败');
										} else {
											grid.getStore().reload();
											win.destroy();
											Ext.example.msg('操作成功', '添加新用户');
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
							text: '清空',
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
	}
	
});
