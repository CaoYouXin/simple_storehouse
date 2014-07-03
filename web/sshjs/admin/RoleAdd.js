/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

Ext.define('SSH.admin.RoleAdd', {
	requires: [
		'Ext.form.*',
		'Ext.window.Window',
		'Ext.data.*'
	],
	singleton: true,
	show: function(grid) {
		var me = this;
		
		if (!me.form) {
			me.form = Ext.create('Ext.form.Panel', {
				plain: true,
				border: 0,
				bodyPadding: 5,
				url: '/ssh/MessageDispatcher?target=security&message=addRole&username='+SSH.User.getUserName()+'&pwd='+SSH.User.getPassword(),
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
						minLength: 1,
						minLengthText: '至少1个字符',
						allowBlank: false,
						blankText: '此列不允许为空'
					}, me.getResources()
				]
			});
		}

		if (!me.win) {
			me.win = Ext.create('Ext.window.Window', {
				title: '添加职务',
				modal: true,
				closable: false,
				collapsible: true,
				animCollapse: true,
				maximizable: true,
				width: 400,
				height: 200,
				layout: 'fit',
				items: me.form,
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
								text: '添加',
								handler: function() {
									me.form.submit({
										success: function(form, action) {
											if (-1 === action.result.msg) {
												Ext.Msg.alert('错误', '服务器操作失败');
											} else {
												grid.getStore().reload();
												me.win.hide();
												Ext.example.msg('操作成功', '添加新职务');
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
									me.form.getForm().reset();
								}
							}, {
								minWidth: 80,
								text: '返回',
								handler: function() {
									me.win.hide();
								}
							}
						]
					}
				]
			});
		}
		me.win.show();
	},
	
	getResources: function () {
		var resources = [];
		SSH.Cache.each('resource', function(id, name) {
			resources.push({
				boxLabel: name,
				name: 'resource_id',
				inputValue: id
			});
		});
		return {
			xtype: 'checkboxgroup',
			fieldLabel: '可访问功能',
			// Arrange checkboxes into two columns, distributed vertically
			columns: 2,
			vertical: true,
			items: resources,
			allowBlank: false,
			blankText: '至少选择一项'
		};
	}
});
