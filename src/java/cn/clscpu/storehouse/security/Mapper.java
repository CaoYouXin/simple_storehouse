/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cn.clscpu.storehouse.security;

import java.util.List;

/**
 *
 * @author CPU
 */
public interface Mapper {
	
	boolean map(List<Integer> oldResourceIds);
	
}
