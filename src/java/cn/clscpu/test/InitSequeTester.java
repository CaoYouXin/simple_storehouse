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
public final class InitSequeTester {
    
    private int i, j = 1;
    private String str = "tester", str2 = "tester";
    private Tester t = new Tester();

    public InitSequeTester() {
        i = 2;
        str = "oh fuck";
        init();
    }
    
    private void init() {
        System.out.println(String.format("%d %d %s %s %s", i, j, str, str2, t.text));
    }
    
    private static class Tester {
        String text = "fuck";
        Tester() {
            System.out.println("Init Tester.");
        }
    }
    
    public static void main(String args[]) {
        new InitSequeTester();
    }
    
}
