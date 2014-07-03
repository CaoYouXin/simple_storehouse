/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cn.clscpu.test;

import cn.clscpu.storehouse.init.InitTable;
import cn.clscpu.storehouse.util.db.sql.ColumnInfo;
import cn.clscpu.storehouse.util.db.sql.ColumnInfo.Type;
import cn.clscpu.storehouse.util.db.sql.Partition;
import cn.clscpu.storehouse.util.db.sql.Partitioning;
import cn.clscpu.storehouse.util.db.sql.Partitioning.PartitionBy;

/**
 *
 * @author CPU
 */
public class InitTableTester {
    
    public static void main(String args[]) {
        System.out.println(new InitTable("test", "luck")
            .addColumn(new ColumnInfo("id", Type.integer))
            .addColumn(new ColumnInfo("created", Type.datetime))
            .setPartitioning(new Partitioning(PartitionBy.range, "TO_DAYS(created)", 1), 
                    (int idx) -> new Partition("luck"+idx, "735964"))
        );
    }
    
}
