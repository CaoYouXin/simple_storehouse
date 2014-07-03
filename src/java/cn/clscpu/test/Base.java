/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cn.clscpu.test;

import cn.clscpu.storehouse.ServiceBase;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author CPU
 */
public class Base {
    
    private static final Class<?>[] paramsTypes = {
        Map.class
    };
    
    private static boolean isSameParamsTypes(Class<?>[] pTypes) {
        for (int i = 0; i < pTypes.length; i++) {
            Class<?> class1 = pTypes[i];
            Class<?> class2 = paramsTypes[i];
            if (!class1.equals(class2)) {
                return false;
            }
        }
        return true;
    }
    
    public final String handleMessage(String message, Map<String, String> params) {
        Method[] methods = this.getClass().getDeclaredMethods();
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            if (message.equals(method.getName()) && isSameParamsTypes(method.getParameterTypes())) {
                try {
                    return (String) method.invoke(this, params);
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                    Logger.getLogger(ServiceBase.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;
            }
        }
        return null;
    }
     
}
