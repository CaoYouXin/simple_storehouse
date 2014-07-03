/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

Ext.define('SSH.Login', {
	requires: [
		'MyDesktop.App',
		'Ext.form.*',
		'Ext.window.Window',
		'Ext.data.*'
	],
	singleton: true,
	myDesktopApp: null,
	form:null,
	win:null,
	show: function() {
		var me = this;
		if (me.myDesktopApp)
			me.myDesktopApp.hide();
		
		if (!me.form) {
			me.form = Ext.create('Ext.form.Panel', {
				plain: true,
				border: 0,
				bodyPadding: 5,
				url: '/ssh/MessageDispatcher',
				fieldDefaults: {
					labelWidth: 55,
					anchor: '100%'
				},
				layout: {
					type: 'vbox',
					align: 'stretch'  // Child items are stretched to full width
				},
				items: [{
						xtype: 'textfield',
						fieldLabel: '用户名',
						name: 'username'
					}, {
						xtype: 'textfield',
						fieldLabel: '密码',
						name: 'plainpwd',
						submitValue: false,
						inputType: 'password'
					}]
			});
		} else {
			me.form.getForm().getFields().get(1).reset();
		}

		if (!me.win) {
			me.win = Ext.create('Ext.window.Window', {
				title: '请登录',
				closable: false,
				collapsible: true,
				animCollapse: true,
				maximizable: true,
				width: 300,
				height: 150,
				layout: 'fit',
				items: me.form,
				dockedItems: [{
						xtype: 'toolbar',
						dock: 'bottom',
						ui: 'footer',
						layout: {
							pack: 'center'
						},
						items: [{
								minWidth: 80,
								text: '登录',
								handler: function() {
									var fields = me.form.getForm().getFields();
									var plianPwd = fields.get(1).getValue();
									var md5pwd = hex_md5(plianPwd);
									me.form.submit({
										params: {
											target: 'security',
											message: 'login',
											pwd: md5pwd
										},
										success: function(form, action) {
	//										Ext.Msg.alert('Success', action.result.msg);
											if ('0' === action.result.msg) {
												return;
											}
											me.win.hide();
											SSH.User.setUser(fields.get(0).getValue(), md5pwd, action.result.msg);
											SSH.Cache.init();
											if (!me.myDesktopApp) {
												me.myDesktopApp = new MyDesktop.App();
												me.myDesktopApp.setLoginWin(me);
											} else {
												me.myDesktopApp.reConfig();
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
							}]
					}]
			});
		}
		me.win.show();
	}
});
