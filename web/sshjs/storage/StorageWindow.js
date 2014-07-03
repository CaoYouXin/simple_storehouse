/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

Ext.define('SSH.storage.StorageWindow', {
	extend: 'Ext.ux.desktop.Module',
	requires: [
		'Ext.tab.Panel',
		'Ext.panel.Panel',
		'Ext.data.ArrayStore',
		'Ext.util.Format',
		'Ext.grid.Panel',
		'Ext.grid.RowNumberer',
		'SSH.User',
		'SSH.storage.StorageAdd',
		'SSH.storage.StorageLook'
	],
	id: 'storage-win',
	init: function() {
		this.launcher = {
			text: '仓库管理',
			iconCls: 'tabs'
		};
	},
	createWindow: function() {
		var me = this;
		var desktop = me.app.getDesktop();
		var win = desktop.getWindow(me.id);
		if (!win) {
			var recordGrid = me.getRecordGrid();
			var checkGrid = me.getCheckGrid();
			var storageGrid = me.getStorageGrid();
			var items = [recordGrid];
			if (SSH.User.canCheck()) {
				items.push(checkGrid);
			}
			if (SSH.User.canStorage()) {
				items.push(storageGrid);
			}
			win = desktop.createWindow({
				id: me.id,
				title: me.launcher.text,
				width: 1024,
				height: 540,
				iconCls: me.launcher.iconCls,
				animCollapse: false,
				border: false,
				constrainHeader: true,
				layout: 'fit',
				items: [
					{
						xtype: 'tabpanel',
						activeTab: 0,
						bodyStyle: 'padding: 5px;',
						items: items
					}
				]
			});
		}
		return win;
	},
	getRecordGrid: function() {
		var me = this;
		var recordStore = new Ext.data.JsonStore({
			autoLoad: false,
			proxy: {
				type: 'ajax',
				url: '/ssh/MessageDispatcher?target=storage&message=queryBills&username=' + SSH.User.getUserName() + '&pwd=' + SSH.User.getPassword(),
				reader: {
					type: 'json',
					root: 'msg',
					idProperty: 'id'
				}
			},
			fields: [
				{name: 'id'},
				{name: 'code'},
				{name: 'customer'},
				{name: 'recorder'},
				{name: 'checker'},
				{name: 'remark'},
				{name: 'created'},
				{name: 'isIn'}
			]
		});
		var tbar = [];
		if (SSH.User.canRecord()) {
			tbar.push({
				text: '创建单据',
				tooltip: '增加一条单据',
				iconCls: 'add',
				handler: function() {
					SSH.storage.StorageAdd.show(recordGrid);
				}
			});
			tbar.push('-');
		}
		tbar.push({
			text: '查看明细',
			tooltip: '查看明细',
			iconCls: 'option',
			handler: function() {
				SSH.storage.StorageLook.show(recordGrid.getSelectionModel().getSelection()[0]);
			},
			disabled: true,
			itemId: 'look'
		});
		var recordGrid = Ext.create('Ext.grid.Panel', {
			title: '出入库记录',
			loadMask: true,
			border: false,
			xtype: 'grid',
			store: recordStore,
			columns: [
				{
					text: 'ID',
					width: 50,
					sortable: true,
					dataIndex: 'id'
				},
				{
					text: "单据代码",
					width: 100,
					sortable: true,
					dataIndex: 'code'
				},
				{
					text: "客户",
					width: 150,
					sortable: true,
					dataIndex: 'customer'
				},
				{
					text: "记录人",
					width: 100,
					sortable: true,
					dataIndex: 'recorder'
				},
				{
					text: "审核人",
					width: 100,
					sortable: true,
					dataIndex: 'checker'
				},
				{
					text: "备注",
					flex: 1,
					sortable: true,
					dataIndex: 'remark'
				},
				{
					text: "时间",
					width: 120,
					sortable: true,
					dataIndex: 'created'
				},
				{
					text: "类型",
					width: 60,
					sortable: true,
					dataIndex: 'isIn'
				}
			],
			tbar: tbar,
			bbar: [
				me.getBBar(recordStore, true)
			],
			listeners: {
				'selectionchange': function(view, records) {
					recordGrid.down('#look').setDisabled(!records.length);
				}
			}
		});
		return recordGrid;
	},
	getBBar: function(store, hasChecker) {
		var users = new Ext.data.JsonStore({
			autoLoad: true,
			proxy: {
				type: 'ajax',
				url: '/ssh/MessageDispatcher?target=security&message=getAllUsers&username=' + SSH.User.getUserName() + '&pwd=' + SSH.User.getPassword(),
				reader: {
					type: 'json',
					root: 'msg',
					idProperty: 'id'
				}
			},
			fields: [
				{name: 'id'},
				{name: 'name'}
			],
			sorters: [{
					property: 'name',
					direction: 'ASC'
				}]
		});
		var customers = new Ext.data.JsonStore({
			autoLoad: true,
			proxy: {
				type: 'ajax',
				url: '/ssh/MessageDispatcher?target=customer&message=getAllSimpleDesc&username=' + SSH.User.getUserName() + '&pwd=' + SSH.User.getPassword(),
				reader: {
					type: 'json',
					root: 'msg',
					idProperty: 'id'
				}
			},
			fields: [
				{name: 'id'},
				{name: 'desc'}
			],
			sorters: [{
					property: 'desc',
					direction: 'ASC'
				}]
		});
		var items = [
			{
				xtype: 'datefield',
				format: 'Y年m月d日',
				anchor: '100%',
				fieldLabel: '开始日期',
				itemId: 'from',
				maxValue: new Date(),
				value: new Date(),
				allowBlank: false,
				blankText: '为了系统相应更快，就指定时间吧'
			}, {
				xtype: 'datefield',
				format: 'Y年m月d日',
				anchor: '100%',
				fieldLabel: '截至日期',
				itemId: 'to',
				maxValue: new Date(),
				value: new Date(),
				allowBlank: false,
				blankText: '为了系统相应更快，就指定时间吧'
			}, {
				xtype: 'textfield',
				itemId: 'code',
				fieldLabel: '单据代码'
			}, {
				xtype: 'textfield',
				itemId: 'remark',
				fieldLabel: '备注'
			}, {
				xtype: 'combobox',
				editable: false,
				fieldLabel: '客户',
				store: customers,
				queryMode: 'local',
				displayField: 'desc',
				valueField: 'id',
				itemId: 'customer'
			}, {
				xtype: 'combobox',
				editable: false,
				fieldLabel: '记录人',
				store: users,
				queryMode: 'local',
				displayField: 'name',
				valueField: 'id',
				itemId: 'recorder'
			}, {
				xtype: 'combobox',
				editable: false,
				fieldLabel: '类型',
				store: new Ext.data.JsonStore({
					data: [
						{typestr: 'True', type: '入库'},
						{typestr: 'False', type: '出库'},
						{typestr: 'Null', type: '全部'}
					],
					fields: [
						{name: 'typestr'},
						{name: 'type'}
					]
				}),
				queryMode: 'local',
				displayField: 'type',
				valueField: 'typestr',
				itemId: 'isIn'
			}
		];
		if (hasChecker)
			items.push({
				xtype: 'combobox',
				editable: false,
				fieldLabel: '审核人',
				store: users,
				queryMode: 'local',
				displayField: 'name',
				valueField: 'id',
				itemId: 'checker'
			});
		var bbar = Ext.create('Ext.panel.Panel', {
			items: items,
			layout: {
				type: 'table',
				columns: 3
			},
			rbar: [{
					text: '查询',
					tooltip: '按条件查询',
					iconCls: 'connect',
					handler: function() {
						var from = bbar.down('#from').getValue();
						var to = bbar.down('#to').getValue();
						if (!from || !to) {
							Ext.Msg.alert('错误', '为了系统相应更快，就指定时间吧');
							return;
						}
						if (from > to) {
							Ext.Msg.alert('错误', '开始时间不能晚于截至时间');
							return;
						}
						store.load({
							params: {
								from: Ext.Date.format(from, 'Y-m-d'),
								to: Ext.Date.format(to, 'Y-m-d'),
								code: bbar.down('#code').getValue(),
								customer: bbar.down('#customer').getValue() || -1,
								recorder: bbar.down('#recorder').getValue() || -1,
								checker: (bbar.down('#checker') && (bbar.down('#checker').getValue() || -1)) || 0,
								remark: bbar.down('#remark').getValue(),
								isIn: bbar.down('#isIn').getValue() || 'Null'
							}
						});
					}
				}]
		});
		return bbar;
	},
	getCheckGrid: function() {
		var me = this;
		var checkerStore = new Ext.data.JsonStore({
			autoLoad: false,
			proxy: {
				type: 'ajax',
				url: '/ssh/MessageDispatcher?target=storage&message=queryBills&username=' + SSH.User.getUserName() + '&pwd=' + SSH.User.getPassword(),
				reader: {
					type: 'json',
					root: 'msg',
					idProperty: 'id'
				}
			},
			fields: [
				{name: 'id'},
				{name: 'code'},
				{name: 'customer'},
				{name: 'recorder'},
				{name: 'checker'},
				{name: 'remark'},
				{name: 'created'},
				{name: 'isIn'}
			]
		});
		var checkerGrid = Ext.create('Ext.grid.Panel', {
			title: '审核',
			loadMask: true,
			border: false,
			xtype: 'grid',
			store: checkerStore,
			columns: [
				{
					text: 'ID',
					width: 50,
					sortable: true,
					dataIndex: 'id'
				},
				{
					text: "单据代码",
					width: 100,
					sortable: true,
					dataIndex: 'code'
				},
				{
					text: "客户",
					width: 150,
					sortable: true,
					dataIndex: 'customer'
				},
				{
					text: "记录人",
					width: 100,
					sortable: true,
					dataIndex: 'recorder'
				},
				{
					text: "审核人",
					width: 100,
					sortable: true,
					dataIndex: 'checker'
				},
				{
					text: "备注",
					flex: 1,
					sortable: true,
					dataIndex: 'remark'
				},
				{
					text: "时间",
					width: 120,
					sortable: true,
					dataIndex: 'created'
				},
				{
					text: "类型",
					width: 60,
					sortable: true,
					dataIndex: 'isIn'
				}
			],
			tbar: [
				{
					text: '审核',
					tooltip: '审核一条单据',
					iconCls: 'option',
					handler: function() {
						var record = checkerGrid.getSelectionModel().getSelection()[0];
						Ext.Ajax.request({
							url: '/ssh/MessageDispatcher?target=storage&message=check&id=' + record.get('id') + '&username=' + SSH.User.getUserName() + '&pwd=' + SSH.User.getPassword(),
							success: function(response) {
								var obj = Ext.decode(response.responseText);
								console.dir(obj);
								if (obj['msg']) {
									Ext.example.msg('操作成功', '已审核');
								} else {
									Ext.Msg.alert('错误', '重新查询看看，是不是已经审核了');
								}
							},
							failure: function(response) {
								console.log('server-side failure with status code ' + response.status);
								alert('出错啦！错误码是' + response.status);
							}
						});
					},
					disabled: true,
					itemId: 'check'
				}, '-', {
					text: '查看明细',
					tooltip: '查看明细',
					iconCls: 'option',
					handler: function() {
						SSH.storage.StorageLook.show(checkerGrid.getSelectionModel().getSelection()[0]);
					},
					disabled: true,
					itemId: 'look'
				}
			],
			bbar: [
				me.getBBar(checkerStore, false)
			],
			listeners: {
				'selectionchange': function(view, records) {
					checkerGrid.down('#check').setDisabled(!records.length);
					checkerGrid.down('#look').setDisabled(!records.length);
				}
			}
		});
		return checkerGrid;
	},
	getStorageGrid: function() {
		var me = this;
		var storageStore = new Ext.data.JsonStore({
			autoLoad: false,
			proxy: {
				type: 'ajax',
				url: '/ssh/MessageDispatcher?target=storage&message=queryStorage&username=' + SSH.User.getUserName() + '&pwd=' + SSH.User.getPassword(),
				reader: {
					type: 'json',
					root: 'msg',
				}
			},
			fields: [
				{name: 'date'},
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
				{name: 'sum'}
			]
		});
		var storageGrid = Ext.create('Ext.grid.Panel', {
			title: '库存',
			loadMask: true,
			border: false,
			xtype: 'grid',
			store: storageStore,
			columns: [
				{
					text: '日期',
					width: 100,
					sortable: true,
					dataIndex: 'date'
				},
				{
					text: "商品代码",
					width: 100,
					sortable: true,
					dataIndex: 'code'
				},
				{
					text: "商品名称",
					flex: 1,
					sortable: true,
					dataIndex: 'name'
				},
				{
					text: "颜色",
					width: 100,
					sortable: true,
					dataIndex: 'color'
				},
				{
					text: "S号",
					width: 60,
					sortable: true,
					dataIndex: 'num0'
				},
				{
					text: "M号",
					width: 60,
					sortable: true,
					dataIndex: 'num1'
				},
				{
					text: "L号",
					width: 60,
					sortable: true,
					dataIndex: 'num2'
				},
				{
					text: "XL号",
					width: 60,
					sortable: true,
					dataIndex: 'num3'
				},
				{
					text: "2XL号",
					width: 60,
					sortable: true,
					dataIndex: 'num4'
				},
				{
					text: "3XL号",
					width: 60,
					sortable: true,
					dataIndex: 'num5'
				},
				{
					text: "4XL号",
					width: 60,
					sortable: true,
					dataIndex: 'num6'
				},
				{
					text: "5XL号",
					width: 60,
					sortable: true,
					dataIndex: 'num7'
				},
				{
					text: "合计",
					width: 80,
					sortable: true,
					dataIndex: 'sum'
				}
			],
			tbar: [
				me.getTBar(storageStore)
			]
		});
		return storageGrid;
	},
	getTBar: function(store) {
		var items = [
			{
				xtype: 'combobox',
				fieldLabel: '商品代码',
				store: new Ext.data.JsonStore({
					autoLoad: true,
					proxy: {
						type: 'ajax',
						url: '/ssh/MessageDispatcher?target=gm&message=getAllCode&username=' + SSH.User.getUserName() + '&pwd=' + SSH.User.getPassword(),
						reader: {
							type: 'json',
							root: 'msg',
							idProperty: 'code'
						}
					},
					fields: [
						{name: 'code'}
					],
					sorters: [{
							property: 'code',
							direction: 'ASC'
						}]
				}),
				queryMode: 'local',
				displayField: 'code',
				valueField: 'code',
				itemId: 'code'
			}, {
				xtype: 'combobox',
				fieldLabel: '商品名称',
				store: new Ext.data.JsonStore({
					autoLoad: true,
					proxy: {
						type: 'ajax',
						url: '/ssh/MessageDispatcher?target=gm&message=getAllName&username=' + SSH.User.getUserName() + '&pwd=' + SSH.User.getPassword(),
						reader: {
							type: 'json',
							root: 'msg',
							idProperty: 'name'
						}
					},
					fields: [
						{name: 'name'}
					],
					sorters: [{
							property: 'name',
							direction: 'ASC'
						}]
				}),
				queryMode: 'local',
				displayField: 'name',
				valueField: 'name',
				itemId: 'name'
			}, {
				xtype: 'combobox',
				fieldLabel: '颜色',
				store: new Ext.data.JsonStore({
					autoLoad: true,
					proxy: {
						type: 'ajax',
						url: '/ssh/MessageDispatcher?target=gm&message=getAllColor&username=' + SSH.User.getUserName() + '&pwd=' + SSH.User.getPassword(),
						reader: {
							type: 'json',
							root: 'msg',
							idProperty: 'color'
						}
					},
					fields: [
						{name: 'color'}
					],
					sorters: [{
							property: 'color',
							direction: 'ASC'
						}]
				}),
				queryMode: 'local',
				displayField: 'color',
				valueField: 'color',
				itemId: 'color'
			}, {
				xtype: 'datefield',
				format: 'Y年m月d日',
				anchor: '100%',
				fieldLabel: '日期',
				itemId: 'date',
				maxValue: new Date(),
				value: new Date(),
				allowBlank: false,
				blankText: '必须指定日期'
			}
		];
		var tbar = Ext.create('Ext.panel.Panel', {
			items: items,
			layout: {
				type: 'table',
				columns: 3
			},
			rbar: [{
					text: '查询',
					tooltip: '按条件查询',
					iconCls: 'connect',
					handler: function() {
						var date = tbar.down('#date').getValue();
						if (!date) {
							Ext.Msg.alert('错误', '必须指定时间');
							return;
						}
						store.load({
							params: {
								date: Ext.Date.format(date, 'Y-m-d'),
								code: tbar.down('#code').getValue() || '',
								name: tbar.down('#name').getValue() || '',
								color: tbar.down('#color').getValue() || ''
							}
						});
					}
				}]
		});
		return tbar;
	}

});
