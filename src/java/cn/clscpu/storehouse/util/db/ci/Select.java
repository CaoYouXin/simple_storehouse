/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cn.clscpu.storehouse.util.db.ci;

import cn.clscpu.storehouse.util.db.MyResultSet;

/**
 *
 * @author CPU
 */
public interface Select extends DML {
    
    MyResultSet select(String sql);
    
}
