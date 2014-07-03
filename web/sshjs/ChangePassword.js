/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

Ext.define('SSH.ChangePassword', {
    extend: 'Ext.ux.desktop.Module',

    requires: [
        'Ext.form.*'
    ],

    id:'cpwd-win',

    init : function(){
        this.launcher = {
            text: '修改密码',
            iconCls:'option'
        };
    },

    createWindow : function(){
		var me = this;
		var form = Ext.create('Ext.form.Panel', {
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
					fieldLabel: '旧密码',
					name: 'plainpwd',
					submitValue: false,
					inputType: 'password',
					validator: function(value) {
						var md5Value = hex_md5(value);
						if (md5Value === SSH.User.getPassword()) {
							return true;
						} else {
							return "密码不正确";
						}
					}
				}, {
					xtype: 'textfield',
					fieldLabel: '新密码',
					name: 'plainpwd',
					submitValue: false,
					inputType: 'password',
					minLength: 5,
					minLengthText: '至少5个字符',
					validator: function(value) {
						var md5Value = hex_md5(value);
						if (md5Value === SSH.User.getPassword()) {
							return "与旧密码相同";
						} else {
							return true;
						}
					}
				}, {
					xtype: 'textfield',
					fieldLabel: '确认密码',
					name: 'plainpwd',
					submitValue: false,
					inputType: 'password',
					validator: function(value) {
						var equal_value = form.getForm().getFields().get(1).getValue();
						if (value === equal_value) {
							return true;
						} else {
							return "两次输入不相同";
						}
					}
				}]
		});
		
        var desktop = this.app.getDesktop();
        var win = desktop.getWindow(this.id);
        if(!win){
            win = desktop.createWindow({
                id: this.id,
                title:this.launcher.text,
                width:300,
                height:200,
                iconCls: this.launcher.iconCls,
                animCollapse:false,
                border:false,
                constrainHeader:true,

                layout: 'fit',
                items: form,
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
									var fields = form.getForm().getFields();
									var plianPwd = fields.get(0).getValue();
									var newPlianPwd = fields.get(1).getValue();
									var md5pwd = hex_md5(plianPwd);
									var newMd5pwd = hex_md5(newPlianPwd);
									form.submit({
										params: {
											target: 'security',
											message: 'changePwdOneself',
											username: SSH.User.getUserName(),
											pwd: md5pwd,
											npwd: newMd5pwd
										},
										success: function(form, action) {
											if (action.result.msg) {
												SSH.Login.show();
//												self.location = "/ssh/examples/desktop/desktop.html";
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
									form.getForm().reset();
								}
							}]
					}]
            });
        }
        return win;
    }
});

