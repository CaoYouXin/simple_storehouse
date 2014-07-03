/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

Ext.define('SSH.storage.StorageAdd', {
	requires: [
		'Ext.form.*',
		'Ext.window.Window',
		'Ext.data.*',
		'Ext.grid.plugin.RowEditing'
	],
	singleton: true,
	show: function() {
		var me = this;
		
		var form = Ext.create('Ext.form.Panel', {
			dock: 'top',
			plain: true,
			border: 0,
			bodyPadding: 5,
			url: '/ssh/MessageDispatcher?target=storage&message=store&username='+SSH.User.getUserName()+'&pwd='+SSH.User.getPassword(),
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
					fieldLabel: '单据代码',
					name: 'code',
					minLength: 1,
					minLengthText: '至少1个字符',
					allowBlank: false,
					blankText: '此列不允许为空'
				}, {
					xtype: 'combobox',
					fieldLabel: '客户',
					store: new Ext.data.JsonStore({
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
					}),
					editable: false,
					queryMode: 'local',
					displayField: 'desc',
					valueField: 'id',
					allowBlank: false,
					blankText: '此列不允许为空',
					name: 'customer'
				}, {
					xtype: 'textfield',
					fieldLabel: '备注',
					name: 'remark'
				}, {
					xtype: 'combobox',
					fieldLabel: '类型',
					store: new Ext.data.JsonStore({
						data: [
							{typestr: 'True', type: '入库'},
							{typestr: 'False', type: '出库'}
						],
						fields: [
							{name: 'typestr'},
							{name: 'type'}
						]
					}),
					editable: false,
					queryMode: 'local',
					displayField: 'type',
					valueField: 'typestr',
					allowBlank: false,
					blankText: '此列不允许为空',
					name: 'isIn'
				}
			]
		});

		// create the Data Store
		var store = Ext.create('Ext.data.Store', {
			proxy: {
				type: 'memory'
			},
			// reader configs
			fields: [
			   'goodsinfo',
			   {name: 'num0', type: 'int'},
			   {name: 'num1', type: 'int'},
			   {name: 'num2', type: 'int'},
			   {name: 'num3', type: 'int'},
			   {name: 'num4', type: 'int'},
			   {name: 'num5', type: 'int'},
			   {name: 'num6', type: 'int'},
			   {name: 'num7', type: 'int'},
			   {name: 'sum', type: 'int'},
			   'remark'
			]
		});
		
		var rowEditing = Ext.create('Ext.grid.plugin.RowEditing', {
			clicksToMoveEditor: 1,
			autoCancel: false,
			saveBtnText  : '更新',
			cancelBtnText: '取消',
			errorsText: '错误',
			dirtyText: '你需要提交更新'
		});

		var grid = Ext.create('Ext.grid.Panel', {
			store: store,
			columns: [
				{
					header: '商品ID',
					dataIndex: 'goodsinfo',
					width: 90,
					tooltip: '双击查看详情',
					editor: {
						xtype: 'combobox',
						store: new Ext.data.JsonStore({
							autoLoad: true,
							proxy: {
								type: 'ajax',
								url: '/ssh/MessageDispatcher?target=gm&message=getAllDesc&username=' + SSH.User.getUserName() + '&pwd=' + SSH.User.getPassword(),
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
						}),
						editable: false,
						queryMode: 'local',
						displayField: 'desc',
						valueField: 'id',
						allowBlank: false,
						blankText: '此列不允许为空'
					}
				}, {
					xtype: 'numbercolumn',
					header: 'S号',
					dataIndex: 'num0',
					width: 40,
					editor: {
						xtype: 'numberfield',
						allowBlank: false,
						minValue: 0,
						maxValue: 150000
					}
				}, {
					xtype: 'numbercolumn',
					header: 'M号',
					dataIndex: 'num1',
					width: 40,
					editor: {
						xtype: 'numberfield',
						allowBlank: false,
						minValue: 0,
						maxValue: 150000
					}
				}, {
					xtype: 'numbercolumn',
					header: 'L号',
					dataIndex: 'num2',
					width: 40,
					editor: {
						xtype: 'numberfield',
						allowBlank: false,
						minValue: 0,
						maxValue: 150000
					}
				}, {
					xtype: 'numbercolumn',
					header: 'XL号',
					dataIndex: 'num3',
					width: 40,
					editor: {
						xtype: 'numberfield',
						allowBlank: false,
						minValue: 0,
						maxValue: 150000
					}
				}, {
					xtype: 'numbercolumn',
					header: '2XL号',
					dataIndex: 'num4',
					width: 40,
					editor: {
						xtype: 'numberfield',
						allowBlank: false,
						minValue: 0,
						maxValue: 150000
					}
				}, {
					xtype: 'numbercolumn',
					header: '3XL号',
					dataIndex: 'num5',
					width: 40,
					editor: {
						xtype: 'numberfield',
						allowBlank: false,
						minValue: 0,
						maxValue: 150000
					}
				}, {
					xtype: 'numbercolumn',
					header: '4XL号',
					dataIndex: 'num6',
					width: 40,
					editor: {
						xtype: 'numberfield',
						allowBlank: false,
						minValue: 0,
						maxValue: 150000
					}
				}, {
					xtype: 'numbercolumn',
					header: '5XL号',
					dataIndex: 'num7',
					width: 40,
					editor: {
						xtype: 'numberfield',
						allowBlank: false,
						minValue: 0,
						maxValue: 150000
					}
				}, {
					xtype: 'numbercolumn',
					header: '合计',
					dataIndex: 'sum',
					width: 60
				}, {
					header: '备注',
					dataIndex: 'remark',
					flex: 1,
					editor: {
						// defaults to textfield if no xtype is supplied
						allowBlank: true
					}
				}
			],
			tbar: [{
					text: '添加明细',
					iconCls: 'add',
					handler: function() {
						rowEditing.cancelEdit();

						// Create a model instance
						var r = {company:'', num0:0, num1:0, num2:0, num3:0, num4:0, num5:0, num6:0, num7:0, remark:''};

						store.insert(0, r);
						rowEditing.startEdit(0, 0);
					}
				}, {
					itemId: 'remove',
					text: '删除明细',
					iconCls: 'remove',
					handler: function() {
						var sm = grid.getSelectionModel();
						rowEditing.cancelEdit();
						store.remove(sm.getSelection());
						if (store.getCount() > 0) {
							sm.select(0);
						}
					},
					disabled: true
				}, {
					itemId: 'sum',
					text: '合计',
					iconCls: 'option',
					handler: function() {
						store.each(function(record) {
							record.set('sum', record.get('num0') + record.get('num1') + record.get('num2')
									+ record.get('num5') + record.get('num4') + record.get('num3') 
									+ record.get('num6') + record.get('num7'));
						});
					}
				}
			],
			plugins: [rowEditing],
			listeners: {
				'selectionchange': function(view, records) {
					grid.down('#remove').setDisabled(!records.length);
				}
			}
		});

		var win = Ext.create('Ext.window.Window', {
			title: '创建单据',
			modal: true,
			closable: false,
			collapsible: true,
			animCollapse: true,
			maximizable: true,
			width: 600,
			height: 500,
			layout: 'fit',
			items: grid,
			dockedItems: [
				form,
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
							text: '创建',
							handler: function() {
								var goodses = [];
								store.each(function(record) {
									var goodsinfo = {};
									goodsinfo['goodsid'] = record.get('goodsinfo');
									goodsinfo['remark'] = record.get('remark');
									var nums = goodsinfo['nums'] = [];
									nums.push('' + record.get('num0'));
									nums.push('' + record.get('num1'));
									nums.push('' + record.get('num2'));
									nums.push('' + record.get('num3'));
									nums.push('' + record.get('num4'));
									nums.push('' + record.get('num5'));
									nums.push('' + record.get('num6'));
									nums.push('' + record.get('num7'));
									goodses.push(goodsinfo);
								});
								if (0 == goodses.length) {
									Ext.Msg.alert('错误', '必须有明细');
									return;
								}
								var json = Ext.JSON.encode(goodses);
								form.submit({
									params: {
										goodses: json
									},
									success: function(form, action) {
										if (-1 === action.result.msg) {
											Ext.Msg.alert('错误', '服务器操作失败');
										} else {
											win.destroy();
											Ext.example.msg('操作成功', '添加新记录');
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
