/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cn.clscpu.test;

import cn.clscpu.storehouse.util.Bool;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.catalina.tribes.util.Arrays;

/**
 *
 * @author CPU
 */
public class Tester {
    
    public static void main(String args[]) {
        Service service = new Service();
        Map<String, String> params = new HashMap<>();
        params.put("key", "fuck");
        System.out.println(service.handleMessage("getId", params));
		System.out.println(String.format("%%%s%%", "fuck"));
		
		List<Object> paramsList = new ArrayList<>();
		paramsList.add("fucker1");
		paramsList.add("fukcer2");
		print(paramsList);
		print(paramsList.toArray());
		
		System.out.println(Bool.False.toString());
		
		System.out.println("fuck".contains(""));
	}
    
	public static void print(Object... params) {
		System.out.println(Arrays.toString(params));
	}
	
    public static void printClassArray(Class<?>[] classArray) {
        for (int i = 0; i < classArray.length; i++) {
            Class<?> class1 = classArray[i];
            System.out.print(class1.getName() + "//");
        }
        System.out.println();
    }
    
}
