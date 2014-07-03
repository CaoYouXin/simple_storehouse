/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cn.clscpu.test;

import cn.clscpu.storehouse.cache.Cache;
import cn.clscpu.storehouse.cache.Synchronizer;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author CPU
 */
public final class CacheTester {
    
    public static void main(String args[]) {
        Cache<String, Long> cache = new Cache<>(new Synchronizer<String, Long>() {

            @Override
            public boolean put(String key, Long value) {
                System.out.println(String.format("Put [%s=-%d]", key, value));
                return true;
            }

            @Override
            public boolean remove(String key, Long value) {
                System.out.println(String.format("Remove [%s=-%d]", key, value));
                return false;
            }

            @Override
            public void init(Map<String, Long> data) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

        });
                Random random = new Random();
        new Thread(() -> {
            while(true) {
                cache.put("" + random.nextInt(10), random.nextLong());
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    Logger.getLogger(CacheTester.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }).start();
        new Thread(() -> {
            while(true) {
                cache.remove("" + random.nextInt(10));
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    Logger.getLogger(CacheTester.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }).start();
        new Thread(() -> {
            while(true) {
                int nextInt = random.nextInt(10);
                System.out.println(nextInt + "=" + cache.get("" + nextInt));
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(CacheTester.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }).start();
    }
    
}
