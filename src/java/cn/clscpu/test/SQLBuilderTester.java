/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cn.clscpu.test;

import cn.clscpu.storehouse.util.db.DBConfig;
import cn.clscpu.storehouse.util.db.sql.SQLBuilder;

import java.util.Arrays;
import java.util.Date;

/**
 *
 * @author CPU
 */
public class SQLBuilderTester {
    
    public static void main(String args[]) {
        System.out.println(Integer.MAX_VALUE);
        System.out.println(new Date().getTime());
        System.out.println(Long.MAX_VALUE);
        System.out.println(new SQLBuilder("test", "devtest").insert("id", "data"));
        System.out.println(new SQLBuilder(DBConfig.SCHEMA, DBConfig.GOODS)
                        .insertWithUpdate("`code`=?,`name`=?,`color`=?", "id", "code", "name", "color"));
		
		System.out.println(Arrays.toString(parseLabels("SElect `a`.`b`, `b`,  `d` as `df` From")));
    }
    
	private static String[] parseLabels(String sql) {
//		if (isShowTables(sql)) {
//			return new String[]{"Tables_in_test"};
//		}
		String upperSql = sql.toUpperCase();
		String select = "SELECT";
		sql = sql.substring(upperSql.indexOf(select) + select.length());
		upperSql = sql.toUpperCase();
		String from = "FROM";
		sql = sql.substring(0, upperSql.indexOf(from));
		String[] labels = sql.split(",");
		for (int i = 0; i < labels.length; i++) {
			String label = labels[i];
			if (label.contains("`")) {
				label = label.replaceAll("`", "");
			}
			if (label.toLowerCase().contains("as")) {
				labels[i] = label.substring(label.toLowerCase().indexOf("as")+2).trim();
			} else if (label.contains(".")) {
				labels[i] = label.substring(label.indexOf(".")+1).trim();
			} else {
				labels[i] = label.trim();
			}
		}
		return labels;
	}
	
}
