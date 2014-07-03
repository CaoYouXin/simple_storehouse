/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.clscpu.storehouse.init;

import cn.clscpu.storehouse.util.db.sql.Partition;

/**
 *
 * @author CPU
 */
public interface SetPartition {

	Partition set(int idx);

}
