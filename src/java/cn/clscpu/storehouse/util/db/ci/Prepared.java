/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cn.clscpu.storehouse.util.db.ci;

import java.util.List;

/**
 *
 * @author CPU
 */
public interface Prepared {
    
    List<Object[]> getParams();
    
}
