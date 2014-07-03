/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cn.clscpu.test;

import cn.clscpu.storehouse.ServiceBase;
import cn.clscpu.storehouse.ServiceName;
import cn.clscpu.storehouse.security.ResourceId;
import cn.clscpu.storehouse.util.json.Json;
import cn.clscpu.storehouse.util.json.Json.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author CPU
 */
public class TestCatalog extends ServiceBase {
    
    private static class Message {
        private String message;
        private String description;
        private String params;
        private Json json = null;

        public Message(String message, String description, String params) {
            this.message = message;
            this.description = description;
            this.params = params;
        }
        
        public Json getJson() {
            if (json != null) {
                return json;
            }
            
            json = new Json(Type.object);
            
            json.addKeyValue("msg", new Json(Type.string, message));
            json.addKeyValue("desc", new Json(Type.string, description));
            json.addKeyValue("params", new Json(Type.string, params));
            
            return json;
        }
    }
    
    private static final Map<ServiceName, Message[]> catalogs = new HashMap<>();
    
    static {
        catalogs.put(ServiceName.gm, new Message[]{
            new Message("getAll", "获取全部商品条目", ""),
            new Message("addGoodsAndReturnId", "添加商品", "{\\\"code\\\":\\\"\\\",\\\"name\\\":\\\"\\\",\\\"color\\\":\\\"\\\"}")
        });
        
        catalogs.put(ServiceName.customer, new Message[]{
            new Message("getAll", "查询全部客户", ""),
            new Message("addCustomerAndReturnId", "添加客户", "{\\\"company\\\":\\\"\\\",\\\"name\\\":\\\"\\\",\\\"telephone\\\":\\\"\\\",\\\"mobile\\\":\\\"\\\"}")
        });
        
        catalogs.put(ServiceName.storage, new Message[]{
            new Message("store", "添加出入库记录", "{\\\"code\\\":\\\"\\\",\\\"customer\\\":\\\"\\\",\\\"recorder\\\":\\\"\\\""
					+ ",\\\"remark\\\":\\\"\\\",\\\"isIn\\\":\\\"True,False\\\",\\\"goodses\\\":\\\"[{\\\\\\\"goodsid\\\\\\\":\\\\\\\"\\\\\\\""
					+ ",\\\\\\\"remark\\\\\\\":\\\\\\\"\\\\\\\",\\\\\\\"nums\\\\\\\":[\\\\\\\"\\\\\\\",\\\\\\\"\\\\\\\"]}]\\\"}"),
            new Message("queryBills", "查询出入库记录", "{\\\"from\\\":\\\"\\\",\\\"to\\\":\\\"\\\",\\\"code\\\":\\\"\\\",\\\"customer\\\":\\\"\\\""
					+ ",\\\"recorder\\\":\\\"\\\",\\\"checker\\\":\\\"\\\",\\\"remark\\\":\\\"\\\",\\\"isIn\\\":\\\"True,False\\\"}"),
            new Message("check", "审核出入库记录", "{\\\"id\\\":\\\"\\\",\\\"checker_id\\\":\\\"\\\"}"),
            new Message("queryStorage", "查询库存情况", "{\\\"date\\\":\\\"\\\",\\\"code\\\":\\\"\\\",\\\"name\\\":\\\"\\\",\\\"color\\\":\\\"\\\"}")
        });
        
        catalogs.put(ServiceName.security, new Message[]{
            new Message("getAllUsers", "查询所有用户", ""),
            new Message("addUser", "添加用户", "{\\\"name\\\":\\\"\\\",\\\"npwd\\\":\\\"\\\",\\\"role_id\\\":\\\"\\\"}"),
            new Message("changeUserRole", "修改用户角色", "{\\\"name\\\":\\\"\\\",\\\"role_id\\\":\\\"\\\"}"),
            new Message("getAllRoles", "查询所有角色", ""),
            new Message("addRole", "添加角色", "{\\\"role_name\\\":\\\"\\\",\\\"resource_id\\\":[\\\"\\\",\\\"\\\"]}"),
            new Message("changeRole", "修改角色", "{\\\"role_id\\\":\\\"\\\",\\\"role_name\\\":\\\"\\\",\\\"resource_id\\\":[\\\"\\\",\\\"\\\"]}"),
            new Message("getAllResources", "查询全部资源", ""),
            new Message("getAllRoleResourceMaps", "查询所有角色资源链接情况", ""),
            new Message("changeUserPwd", "修改用户密码", "{\\\"name\\\":\\\"\\\",\\\"npwd\\\":\\\"\\\"}"),
			new Message("changePwdOneself", "修改自己密码", "{\\\"npwd\\\":\\\"\\\"}")
        });
        
        catalogs.put(ServiceName.dbtest, new Message[]{
            new Message("init", "初始化数据库连接", "")
//            new Message("select", "查一张表", ""),
//            new Message("select2", "查一张带时间的表", ""),
//            new Message("insert2", "插入一张带时间的表", ""),
//            new Message("batch", "批处理", "")
        });
    }
    
	@ResourceId(TestConfig.FOR_TEST)
    public String catalog(Map<String, String[]> params) {
        Message[] msgs = null;
        try {
            String selectedTarget = params.get("sTarget")[0];
            msgs = catalogs.get(ServiceName.valueOf(selectedTarget));
            if (0 == msgs.length) {
                throw new RuntimeException("不存在此测试条目！！！");
            }
        } catch (Exception ex) {
            Logger.getLogger(TestCatalog.class.getName()).log(Level.SEVERE, null, ex);
//            return new Json(Type.array).addJson(new Json(Type.string, TestConfig.WRONG)).toJson();
            return TestConfig.WRONG;
        }
        Json array = new Json(Type.array);
        
        for (Message message : msgs) {
            array.addJson(message.getJson());
        }
        
        return array.toJson();
    }
    
    public static void main(String args[]) {
        TestCatalog testCatalog = new TestCatalog();
        Map<String, String[]> params = new HashMap<>();
        
        params.put("sTarget", new String[]{ "gm" });
        System.out.println(testCatalog.catalog(params));
        
        params.put("sTarget", new String[]{ "dbtest" });
        System.out.println(testCatalog.catalog(params));
        
        params.remove("sTarget");
        System.out.println(testCatalog.catalog(params));
    }
    
}
