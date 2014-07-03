/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

Ext.define('SSH.customer.CustomerWindow', {
    extend: 'Ext.ux.desktop.Module',

    requires: [
        'Ext.tab.Panel',
		'Ext.panel.Panel',
		'Ext.data.ArrayStore',
        'Ext.util.Format',
        'Ext.grid.Panel',
        'Ext.grid.RowNumberer',
		'SSH.customer.CustomerAdd'
    ],

    id:'customer-win',

    init : function(){
        this.launcher = {
            text: '客户管理',
            iconCls:'icon-grid'
        };
    },

    createWindow : function(){
		var me = this;
        var desktop = me.app.getDesktop();
        var win = desktop.getWindow(me.id);
        if(!win){
			var grid = me.getGrid();
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
                items: [grid]
            });
        }
        return win;
    },
	
	getGrid : function() {
		var grid = Ext.create('Ext.grid.Panel', {
			border: false,
			xtype: 'grid',
			store: new Ext.data.JsonStore({
				autoLoad: true,
				proxy: {
					type: 'ajax',
					url: '/ssh/MessageDispatcher?target=customer&message=getAll&username='+SSH.User.getUserName()+'&pwd='+SSH.User.getPassword(),
					reader: {
						type: 'json',
						root: 'msg',
						idProperty: 'id'
					}
				},
				fields: [
				   { name: 'id' },
				   { name: 'company' },
				   { name: 'name' },
				   { name: 'telephone' },
				   { name: 'mobile' }
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
					text: "公司名",
					flex: 1,
					sortable: true,
					dataIndex: 'company'
				},
				{
					text: "联系人",
					width: 120,
					sortable: true,
					dataIndex: 'name'
				},
				{
					text: "联系电话",
					width: 150,
					sortable: true,
					dataIndex: 'telephone'
				},
				{
					text: "手机",
					width: 150,
					sortable: true,
					dataIndex: 'mobile'
				}
			],
			tbar:[{
				text:'添加客户',
				tooltip:'增加一家客户',
				iconCls:'add',
				handler: function() {
					SSH.customer.CustomerAdd.show(grid);
				}
			}, '-', {
				text:'刷新',
				tooltip:'刷新表格',
				iconCls:'connect',
				handler: function() {
					grid.getStore().reload();
				}
			}]
		});
		return grid;
	}
	
});
