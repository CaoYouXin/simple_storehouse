/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

Ext.define('SSH.goods.GoodsAdd', {
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
			url: '/ssh/MessageDispatcher?target=gm&message=addGoodsAndReturnId&username='+SSH.User.getUserName()+'&pwd='+SSH.User.getPassword(),
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
					fieldLabel: '商品代码',
					name: 'code',
					minLength: 1,
					minLengthText: '至少1个字符',
					allowBlank: false,
					blankText: '此列不允许为空'
				}, {
					xtype: 'textfield',
					fieldLabel: '商品名称',
					name: 'name',
					minLength: 1,
					minLengthText: '至少1个字符',
					allowBlank: false,
					blankText: '此列不允许为空'
				}, {
					xtype: 'textfield',
					fieldLabel: '颜色',
					name: 'color',
					minLength: 1,
					minLengthText: '至少1个字符',
					allowBlank: false,
					blankText: '此列不允许为空'
				}
			]
		});

		var win = Ext.create('Ext.window.Window', {
			title: '创建商品',
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
								form.submit({
									success: function(form, action) {
										if (-1 === action.result.msg) {
											Ext.Msg.alert('错误', '服务器操作失败');
										} else {
											grid.getStore().reload();
											win.destroy();
											Ext.example.msg('操作成功', '添加新商品');
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
