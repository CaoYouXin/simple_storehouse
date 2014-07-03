/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cn.clscpu.storehouse.idgen;

import java.sql.SQLException;

/**
 *
 * @author CPU
 */
public interface UseIdEX {

	boolean useId(long id) throws SQLException;
	
}
