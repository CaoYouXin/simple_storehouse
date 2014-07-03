/*!
 * Ext JS Library 4.0
 * Copyright(c) 2006-2011 Sencha Inc.
 * licensing@sencha.com
 * http://www.sencha.com/license
 */

Ext.define('MyDesktop.App', {
	extend: 'Ext.ux.desktop.App',
	requires: [
		'Ext.window.MessageBox',
		'Ext.ux.desktop.ShortcutModel',
		'MyDesktop.VideoWindow',
		'MyDesktop.Settings',
		'SSH.admin.AdminWindow',
		'SSH.ChangePassword',
		'SSH.goods.GoodsWindow',
		'SSH.customer.CustomerWindow',
		'SSH.storage.StorageWindow'
	],
	loginWin: null,
	init: function() {
		// custom logic before getXYZ methods get called...

		this.callParent();

		// now ready...
		Ext.example.msg('你好', '{0}，欢迎你！', SSH.User.getUserName());
	},
	setLoginWin: function(loginWin) {
		this.loginWin = loginWin;
	},
	reConfig: function() {
		var me = this;
		Ext.MessageBox.show({
		   title: '请等待',
		   msg: '正在更新桌面.',
		   progressText: '更新中...',
		   width:300,
		   progress:true,
		   closable:false
		});
		
		var step = 0, total = 4;
		
		me.desktop.taskbar.startMenu.setTitle(SSH.User.getUserName());
		Ext.MessageBox.updateProgress((++step)/total, '已完成'+Math.round(100*(step/total))+'%');
		
		me.desktop.closeAllWindows();
		Ext.MessageBox.updateProgress((++step)/total, '已完成'+Math.round(100*(step/total))+'%');
		
		me.desktop.reloadShortcuts(me.getShotcutsModel());
		Ext.MessageBox.updateProgress((++step)/total, '已完成'+Math.round(100*(step/total))+'%');
		
		setTimeout(function(){
			step++;
			if (step === total) {
				Ext.MessageBox.hide();
				me.desktop.show();
				Ext.example.msg('完成', '桌面已更新');
			}
		}, 1000);
	},
	getModules: function() {
		return [
			new MyDesktop.VideoWindow(),
			new SSH.admin.AdminWindow(),
			new SSH.ChangePassword(),
			new SSH.goods.GoodsWindow(),
			new SSH.customer.CustomerWindow(),
			new SSH.storage.StorageWindow()
		];
	},
	getShotcutsModel: function() {
		var models = [];
		this.getShortcutsData().forEach(function(ele) {
			models.push(Ext.create('Ext.ux.desktop.ShortcutModel', {
				name: ele['name'],
				iconCls: ele['iconCls'],
				module: ele['module']
			}));
		});
		return models;
	},
	getShortcutsData: function() {
		var roleId = SSH.User.getRoleId(),
				shortcutsData = [],
				resourceName;
		if ('1' === roleId)
			shortcutsData.push({name: '系统管理', iconCls: 'accordion-shortcut', module: 'admin-win'});
		Ext.Ajax.request({
			async: false,
			url: '/ssh/MessageDispatcher?target=security&message=getResourceMapByRoleId&role_id='+roleId+'&username='+SSH.User.getUserName()+'&pwd='+SSH.User.getPassword(),
			success: function(response) {
				var obj = Ext.decode(response.responseText);
				console.dir(obj);
				obj['msg'].forEach(function(ele){
					resourceName = SSH.Cache.get('resource', ele);
					if ('添加商品' === resourceName) 
						shortcutsData.push({name: '商品管理', iconCls: 'grid-shortcut', module: 'goods-win'});
					else if ('添加客户' === resourceName) 
						shortcutsData.push({name: '客户管理', iconCls: 'grid-shortcut', module: 'customer-win'});
					else if ('添加出入库记录' === resourceName)
						SSH.User.canRecord(true);
					else if ('审核出入库记录' === resourceName)
						SSH.User.canCheck(true);
					else if ('查询库存情况' === resourceName)
						SSH.User.canStorage(true);
				});
				if (SSH.User.canRecord() || SSH.User.canCheck() || SSH.User.canStorage())
					shortcutsData.push({name: '仓库管理', iconCls: 'grid-shortcut', module: 'storage-win'});
			},
			failure: function(response) {
				console.log('server-side failure with status code ' + response.status);
				alert('出错啦！错误码是' + response.status);
			}
		});
		return shortcutsData;
	},
	getDesktopConfig: function() {
		var me = this, ret = me.callParent();

		return Ext.apply(ret, {
			//cls: 'ux-desktop-black',

			contextMenuItems: [
				{text: '设置', handler: me.onSettings, scope: me}
			],
			shortcuts: Ext.create('Ext.data.Store', {
				model: 'Ext.ux.desktop.ShortcutModel',
				data: me.getShortcutsData()
			}),
			wallpaper: 'wallpapers/Wood-Sencha.jpg',
			wallpaperStretch: true
		});
	},
	// config for the start menu
	getStartConfig: function() {
		var me = this, ret = me.callParent();

		return Ext.apply(ret, {
			title: SSH.User.getUserName(),
			iconCls: 'user',
			height: 300,
			toolConfig: {
				width: 100,
				items: [
					{
						text: '设置',
						iconCls: 'settings',
						handler: me.onSettings,
						scope: me
					},
					'-',
					{
						text: '注销',
						iconCls: 'logout',
						handler: me.onLogout,
						scope: me
					}
				]
			}
		});
	},
	getTaskbarConfig: function() {
		var ret = this.callParent();

		return Ext.apply(ret, {
			quickStart: [
//				{name: 'Accordion Window', iconCls: 'accordion', module: 'acc-win'},
//				{name: 'Grid Window', iconCls: 'icon-grid', module: 'grid-win'}
			],
			trayItems: [
				{xtype: 'trayclock', flex: 1}
			]
		});
	},
	onLogout: function() {
		var me = this;
		Ext.Msg.confirm('退出登录', '确定要退出么?', function(buttonId) {
			if ('yes' === buttonId)
				me.loginWin.show();
		});
	},
	onSettings: function() {
		var dlg = new MyDesktop.Settings({
			desktop: this.desktop
		});
		dlg.show();
	}
});
