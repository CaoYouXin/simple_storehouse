/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cn.clscpu.test;

import java.util.Map;

/**
 *
 * @author CPU
 */
public class Service extends Base {
    
    public final String getId(Map<String, String> params) {
        return "" + getId(params.get("key"));
    }
    
    private final int getId(String key) {
        return 1;
    }
    
}
