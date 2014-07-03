/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

Ext.define('SSH.admin.AdminWindow', {
    extend: 'Ext.ux.desktop.Module',

    requires: [
        'Ext.tab.Panel',
		'Ext.panel.Panel',
		'Ext.data.ArrayStore',
        'Ext.util.Format',
        'Ext.grid.Panel',
        'Ext.grid.RowNumberer',
		'SSH.admin.RoleAdd',
		'SSH.admin.UserAdd',
		'SSH.admin.RoleMod',
		'SSH.admin.UserMod'
    ],

    id:'admin-win',

    init : function(){
        this.launcher = {
            text: '系统管理',
            iconCls:'tabs'
        };
    },

    createWindow : function(){
		var me = this;
        var desktop = me.app.getDesktop();
        var win = desktop.getWindow(me.id);
        if(!win){
			var roleGrid = me.getRoleGrid();
			var userGrid = me.getUserGrid();
			var pwdResetForm = me.getPwdResetForm();
            win = desktop.createWindow({
                id: me.id,
                title: me.launcher.text,
                width:740,
                height:480,
                iconCls: me.launcher.iconCls,
                animCollapse:false,
                border:false,
                constrainHeader:true,

                layout: 'fit',
                items: [
					{
                        xtype: 'tabpanel',
                        activeTab:0,
                        bodyStyle: 'padding: 5px;',

                        items: [
							roleGrid,
							userGrid,
							pwdResetForm
						]
                    }
                ]
            });
        }
        return win;
    },
	
	getRoleGrid : function() {
		var roleGrid = Ext.create('Ext.grid.Panel', {
			title: '职务',
			border: false,
			xtype: 'grid',
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
				]
			}),
			columns: [
				{
					text: 'ID',
					width: 70,
					sortable: true,
					dataIndex: 'id'
				},
				{
					text: "职务",
					flex: 1,
					sortable: true,
					dataIndex: 'name'
				}
			],
			tbar:[{
				text:'添加职务',
				tooltip:'增加一种职务',
				iconCls:'add',
				handler: function() {
					SSH.admin.RoleAdd.show(roleGrid);
				}
			}, '-', {
				itemId:'mod',
				text:'修改权限',
				tooltip:'修改职务权限',
				iconCls:'option',
				handler: function() {
					SSH.admin.RoleMod.show(roleGrid);
				},
				disabled: true
			}, '-', {
				text:'刷新',
				tooltip:'刷新表格',
				iconCls:'connect',
				handler: function() {
					roleGrid.getStore().reload();
				}
			}],
			listeners: {
				'selectionchange': function(view, records) {
					if (Ext.Number.from(records[0].get('id')) <= 1) {
						roleGrid.down('#mod').setDisabled(true);
					} else {
						roleGrid.down('#mod').setDisabled(!records.length);
					}
				}
			}
		});
		return roleGrid;
	},
	
	getUserGrid : function() {
		var userGrid = Ext.create('Ext.grid.Panel', {
			title: '用户',
			border: false,
			xtype: 'grid',
			store: new Ext.data.JsonStore({
				autoLoad: true,
				proxy: {
					type: 'ajax',
					url: '/ssh/MessageDispatcher?target=security&message=getAllUsers&username='+SSH.User.getUserName()+'&pwd='+SSH.User.getPassword(),
					reader: {
						type: 'json',
						root: 'msg',
						idProperty: 'id'
					}
				},
				fields: [
				   { name: 'id' },
				   { name: 'role_name' },
				   { name: 'name' }
				]
			}),
			columns: [
				{
					text: 'ID',
					width: 70,
					sortable: true,
					dataIndex: 'id'
				},
				{
					text: "用户名",
					flex: 1,
					sortable: true,
					dataIndex: 'name'
				},
				{
					text: "职务",
					width: 200,
					sortable: true,
					dataIndex: 'role_name'
				}
			],
			tbar:[{
				text:'创建用户',
				tooltip:'创建一个用户',
				iconCls:'add',
				handler: function() {
					SSH.admin.UserAdd.show(userGrid);
				}
			}, '-', {
				itemId:'mod',
				text:'修改用户职务',
				tooltip:'修改用户职务',
				iconCls:'option',
				handler: function() {
					SSH.admin.UserMod.show(userGrid);
				},
				disabled: true
			}, '-', {
				text:'刷新',
				tooltip:'刷新表格',
				iconCls:'connect',
				handler: function() {
					userGrid.getStore().reload();
				}
			}],
			listeners: {
				'selectionchange': function(view, records) {
					if (Ext.Number.from(records[0].get('id')) <= 2) {
						userGrid.down('#mod').setDisabled(true);
					} else {
						userGrid.down('#mod').setDisabled(!records.length);
					}
				}
			}
		});
		return userGrid;
	},
	
	getPwdResetForm: function() {
		var pwdResetForm = Ext.create('Ext.form.Panel', {
			title: '重置密码',
			plain: true,
			border: 0,
			bodyPadding: 5,
			url: '/ssh/MessageDispatcher?target=security&message=changeUserPwd&username='+SSH.User.getUserName()+'&pwd='+SSH.User.getPassword(),
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
					xtype: 'combobox',
					fieldLabel: '用户名',
					store: new Ext.data.JsonStore({
						autoLoad: true,
						proxy: {
							type: 'ajax',
							url: '/ssh/MessageDispatcher?target=security&message=getAllUsers&username='+SSH.User.getUserName()+'&pwd='+SSH.User.getPassword(),
							reader: {
								type: 'json',
								root: 'msg',
								idProperty: 'name'
							}
						},
						fields: [
						   { name: 'name' }
						]
					}),
					queryMode: 'local',
					displayField: 'name',
					valueField: 'name',
					name: 'name',
					allowBlank: false,
					blankText: '此列不允许为空',
					validator: function(value) {
						if ('test' !== value) {
							return true;
						} else {
							return '"test"用户不能修改密码，系统保留哦';
						}
					}
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
				}
			],
			dockedItems: [{
				xtype: 'toolbar',
				dock: 'bottom',
				ui: 'footer',
				layout: {
					pack: 'center'
				},
				items: [{
						minWidth: 80,
						text: '修改',
						handler: function() {
							var fields = pwdResetForm.getForm().getFields();
							var plianPwd = fields.get(1).getValue();
							var md5pwd = hex_md5(plianPwd);
							pwdResetForm.submit({
								params: {
									npwd: md5pwd
								},
								success: function(form, action) {
									if (action.result.msg) {
										Ext.example.msg('成功', '密码已重置！');
									} else {
										Ext.example.msg('错误', '旧密码不对哦！');
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
							pwdResetForm.getForm().reset();
						}
					}]
			}]
		});
		return pwdResetForm;
	}
	
});
