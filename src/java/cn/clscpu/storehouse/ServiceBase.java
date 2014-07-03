/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.clscpu.storehouse;

import cn.clscpu.storehouse.security.ResourceId;
import cn.clscpu.storehouse.security.SecurityFacade;
import cn.clscpu.storehouse.security.UserManager;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author CPU
 *
 * 该类的子类的公有方法皆按照以下方式构建签名： 1.返回值类型为String，将来可能会升级为json对象 2.参数类型为Map<String,String[]>
 *
 * 思来想去还是不要固定成Json对象得好，因为有些问题只需要简单的字符串即可
 */
public class ServiceBase {

	private static final Class<?>[] paramsTypes = {
		Map.class
	};

	private static final Class<?> returnType = String.class;

	private static boolean isSameParamsTypes(Method method) {
		Class<?>[] pTypes = method.getParameterTypes();
		for (int i = 0; i < pTypes.length; i++) {
			Class<?> class1 = pTypes[i];
			Class<?> class2 = paramsTypes[i];
			if (!class1.equals(class2)) {
				return false;
			}
		}
		return true;
	}

	private static boolean isSameReturnType(Method method) {
		return method.getReturnType().equals(returnType);
	}

	public final String handleMessage(String message, Map<String, String[]> params) {
		Method[] methods = this.getClass().getDeclaredMethods();
		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			if ((message.equals(method.getName()) && isSameParamsTypes(method)) && isSameReturnType(method)) {
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
	
	public final String getResource(String message) {
		Method[] methods = this.getClass().getDeclaredMethods();
		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			if ((message.equals(method.getName()) && isSameParamsTypes(method)) && isSameReturnType(method)) {
				ResourceId annotation = method.getAnnotation(ResourceId.class);
				if (null == annotation) {
					return null;
				}
				return annotation.value();
			}
		}
		return null;
	}
	
	public final List<String> getResources() {
		List<String> rets = new ArrayList<>();
		Class<? extends ServiceBase> clazz = this.getClass();
		Method[] methods = clazz.getDeclaredMethods();
		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			ResourceId resourceId = method.getAnnotation(ResourceId.class);
			if (null == resourceId) {
				continue;
			}
			System.out.println(resourceId.value() + String.format(" %s.%s", clazz.getName(), method.getName()));
			rets.add(resourceId.value());
		}
		return rets;
	}

}
