/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

Ext.define('SSH.storage.StorageLook', {
	requires: [
		'Ext.form.*',
		'Ext.window.Window',
		'Ext.data.*',
		'Ext.grid.plugin.RowEditing'
	],
	singleton: true,
	show: function(record) {
		var me = this;
		
		var grid = Ext.create('Ext.grid.Panel', {
			store: new Ext.data.JsonStore({
				autoLoad: true,
				proxy: {
					type: 'ajax',
					url: '/ssh/MessageDispatcher?target=storage&message=getBillById&id='+record.get('id')+'&date='+record.get('created')+'&username=' + SSH.User.getUserName() + '&pwd=' + SSH.User.getPassword(),
					reader: {
						type: 'json',
						root: 'msg'
					}
				},
				fields: [
					{name: 'code'},
					{name: 'name'},
					{name: 'color'},
					{name: 'num0'},
					{name: 'num1'},
					{name: 'num2'},
					{name: 'num3'},
					{name: 'num4'},
					{name: 'num5'},
					{name: 'num6'},
					{name: 'num7'},
					{name: 'sum'},
					{name: 'remark'}
				]
			}),
			columns: [
				{
					header: '商品编号',
					dataIndex: 'code',
					width: 70
				}, {
					header: '商品名称',
					dataIndex: 'name',
					width: 70
				}, {
					header: '商品颜色',
					dataIndex: 'color',
					width: 70
				}, {
					xtype: 'numbercolumn',
					header: 'S号',
					dataIndex: 'num0',
					width: 40
				}, {
					xtype: 'numbercolumn',
					header: 'M号',
					dataIndex: 'num1',
					width: 40
				}, {
					xtype: 'numbercolumn',
					header: 'L号',
					dataIndex: 'num2',
					width: 40
				}, {
					xtype: 'numbercolumn',
					header: 'XL号',
					dataIndex: 'num3',
					width: 40
				}, {
					xtype: 'numbercolumn',
					header: '2XL号',
					dataIndex: 'num4',
					width: 40
				}, {
					xtype: 'numbercolumn',
					header: '3XL号',
					dataIndex: 'num5',
					width: 40
				}, {
					xtype: 'numbercolumn',
					header: '4XL号',
					dataIndex: 'num6',
					width: 40
				}, {
					xtype: 'numbercolumn',
					header: '5XL号',
					dataIndex: 'num7',
					width: 40
				}, {
					xtype: 'numbercolumn',
					header: '合计',
					dataIndex: 'sum',
					width: 60
				}, {
					header: '备注',
					dataIndex: 'remark',
					flex: 1
				}
			]
		});

		var info = Ext.create('Ext.panel.Panel', {
			dock: 'top',
			plain: true,
			border: 0,
			bodyPadding: 5,
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
					value: record.get('code'),
					fieldLabel: '单据代码',
					disabled: true
				}, {
					xtype: 'textfield',
					value: record.get('customer'),
					fieldLabel: '客户',
					disabled: true
				}, {
					xtype: 'textfield',
					value: record.get('recorder'),
					fieldLabel: '记录人',
					disabled: true
				}, {
					xtype: 'textfield',
					value: record.get('checker'),
					fieldLabel: '审核人',
					disabled: true
				}, {
					xtype: 'textfield',
					value: record.get('remark'),
					fieldLabel: '备注',
					disabled: true
				}, {
					xtype: 'textfield',
					value: record.get('created'),
					fieldLabel: '创建时间',
					disabled: true
				}, {
					xtype: 'textfield',
					value: record.get('isIn'),
					fieldLabel: '类型',
					disabled: true
				}
			]
		});

		var win = Ext.create('Ext.window.Window', {
			title: '查看单据',
			modal: true,
			closable: false,
			collapsible: true,
			animCollapse: true,
			maximizable: true,
			width: 700,
			height: 500,
			layout: 'fit',
			items: grid,
			dockedItems: [
				info,
				{
					xtype: 'toolbar',
					dock: 'bottom',
					ui: 'footer',
					layout: {
						pack: 'center'
					},
					items: [
						{
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
