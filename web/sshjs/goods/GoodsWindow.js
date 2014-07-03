/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

Ext.define('SSH.goods.GoodsWindow', {
    extend: 'Ext.ux.desktop.Module',

    requires: [
        'Ext.tab.Panel',
		'Ext.panel.Panel',
		'Ext.data.ArrayStore',
        'Ext.util.Format',
        'Ext.grid.Panel',
        'Ext.grid.RowNumberer',
		'SSH.goods.GoodsAdd'
    ],

    id:'goods-win',

    init : function(){
        this.launcher = {
            text: '商品管理',
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
					url: '/ssh/MessageDispatcher?target=gm&message=getAll&username='+SSH.User.getUserName()+'&pwd='+SSH.User.getPassword(),
					reader: {
						type: 'json',
						root: 'msg',
						idProperty: 'id'
					}
				},
				fields: [
				   { name: 'id' },
				   { name: 'code' },
				   { name: 'name' },
				   { name: 'color' }
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
					text: '商品代码',
					width: 200,
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
					width: 120,
					sortable: true,
					dataIndex: 'color'
				}
			],
			tbar:[{
				text:'添加商品',
				tooltip:'增加一种商品',
				iconCls:'add',
				handler: function() {
					SSH.goods.GoodsAdd.show(grid);
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
