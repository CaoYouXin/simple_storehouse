/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.clscpu.test;

import cn.clscpu.storehouse.ServiceMapper;
import cn.clscpu.storehouse.security.ResourceId;
import java.lang.annotation.Annotation;

/**
 *
 * @author CPU
 */
public class AnnotationTester {

	public static void main(String args[]) {
		System.out.println(ServiceMapper.getResources());
//		AnnotationTester at = new AnnotationTester();
//		try {
//            at.getInfo();
//        } catch (NoSuchMethodException | SecurityException e) {
//            e.printStackTrace();
//        }
	}

	@ResourceId("zhangxun")
	public static void getInfo() throws NoSuchMethodException, SecurityException {
		//获取TestAnnotationInfo对象的getInfo()方法中包含的所有注解
		Annotation[] annos = AnnotationTester.class.getMethod("getInfo").getAnnotations();
		//遍历annos数组
		for (Annotation ann : annos) {
            //ann就是一个Annotation 对象
			//判断当前ann是否是MyAnnotation注解类型
			System.out.println(ann);
			if (ann instanceof ResourceId) {
				System.out.println(((ResourceId) ann).value());
			}
		}
	}

}
