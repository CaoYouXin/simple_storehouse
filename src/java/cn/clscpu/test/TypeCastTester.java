/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cn.clscpu.test;

/**
 *
 * @author CPU
 */
public class TypeCastTester {
    
    public static void main(String args[]) {
        int i = 100;
        Object obj = i;
        if (obj instanceof Integer) {
            int j = (int) obj;
            System.out.println(j);
        }
        Integer k = 200;
        Object object = k;
        if (obj instanceof Integer) {
            int j = (int) object;
            System.out.println(j);
        }
    }
    
}
