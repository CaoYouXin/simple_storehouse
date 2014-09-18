/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cn.clscpu.test;

import cn.clscpu.storehouse.ServiceBase;
import cn.clscpu.storehouse.util.DateUtil;
import cn.clscpu.storehouse.util.db.DB;
import cn.clscpu.storehouse.util.db.MyResultSet;
import static cn.clscpu.test.TestConfig.PASS;
import static cn.clscpu.test.TestConfig.WRONG;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author CPU
 */
public final class DBTester extends ServiceBase {
    
    public static void main(String args[]) {
        String sql = "SELECT `id`, name FROM `test`.`devtest`";
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
                int preIndex = label.indexOf("`") + 1;
                labels[i] = label.substring(preIndex, label.indexOf("`", preIndex)).trim();
            } else {
                labels[i] = label.trim();
            }
        }
        System.out.println(Arrays.toString(labels));
    }
    
    public String init(Map<String, String[]> params) {
        try {
            DB.instance();
            return PASS;
        } catch(Exception ex) {
            Logger.getLogger(DBTester.class.getName()).log(Level.SEVERE, null, ex);
            return WRONG;
        }
    }
    
    public String select(Map<String, String[]> params) {
        try {
            int ret = testSelect();
            if (ret == -1) {
                return WRONG;
            }
            return PASS + ret;
        } catch(Exception ex) {
            Logger.getLogger(DBTester.class.getName()).log(Level.SEVERE, null, ex);
            return WRONG;
        }
    }
    
    public String select2(Map<String, String[]> params) {
        MyResultSet rs = DB.instance().simpleQuery("SELECT `id`, `created` FROM `test`.`luck`");
        while (rs.next()) {
            return DateUtil.formatDateTime(rs.getDate("created"));
        }
        return WRONG;
    }
    
    public String insert2(Map<String, String[]> params) {
        final Date date = new Date();
        boolean suc = DB.instance().preparedExecute("INSERT INTO `test`.`luck`(`id`, `created`) Values(?, ?) "
                + "ON DUPLICATE KEY UPDATE `created` = ?", 1, 2, date, date);
        if (suc) {
            return PASS;
        }
        return WRONG;
    }
    
    public final String batch(Map<String, String[]> params) {
        try {
            boolean suc = testBatch();
            if (!suc) {
                return WRONG;
            }
            return PASS + suc;
        } catch(Exception ex) {
            Logger.getLogger(DBTester.class.getName()).log(Level.SEVERE, null, ex);
            return WRONG;
        }
    }

    private int testSelect() {
        MyResultSet rs = DB.instance().simpleQuery("SELECT `id` FROM `test`.`devtest`");
        while (rs.next()) {
            return rs.getInt("id");
        }
        return -1;
    }
    
    private boolean testBatch() {
        int size = 5;
        List<Object[]> params = new ArrayList<>();
        for (int i = 1; i <= size; i++) {
            params.add(new Object[]{ (i * 200), i });
        }
        boolean suc = DB.instance().batchExecute("INSERT INTO `test`.`devtest`(`id`, `data`) Values(?, ?) "
                + "ON DUPLICATE KEY UPDATE `data` = `data` + 1", size, params);
        return suc;
    }

}
