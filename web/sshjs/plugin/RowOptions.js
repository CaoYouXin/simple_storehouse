/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

Ext.define('SSH.plugin.RowOptions', {
	extend: 'Ext.AbstractPlugin',
	alias: 'plugin.options',
	mixins: {
		observable: 'Ext.util.Observable'
	},
	constructor: function(config) {
		var me = this;

		me.addEvents(
				
				);
		
		me.callParent(arguments);
		me.mixins.observable.constructor.call(me);
	},
	init: function(grid) {
		var me = this;
		me.grid = grid;
		me.mon(grid, "selectionchange", me.onSelectionChange, me);
	},
	onSelectionChange: function(view, records) {
		alert(view.getCount());
		alert(records.length);
	}
});
