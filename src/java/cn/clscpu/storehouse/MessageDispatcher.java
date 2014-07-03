/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.clscpu.storehouse;

import cn.clscpu.storehouse.init.Initiator;
import static cn.clscpu.storehouse.security.SecurityFacade.canMessaging;
import cn.clscpu.storehouse.storage.StorageCache;
import cn.clscpu.storehouse.util.json.Json;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.catalina.tribes.util.Arrays;

/**
 *
 * @author CPU
 */
@WebServlet(name = "MessageDispatcher", urlPatterns = {"/MessageDispatcher"})
public class MessageDispatcher extends HttpServlet {

	/**
	 * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
	 *
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
	protected void processRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		Map<String, String[]> pm = request.getParameterMap();
		printParams(pm);
		String result;
		try {
			String target = null;
			String message = null;
			String userName = null;
			String pwd = null;
			Map<String, String[]> params = new HashMap<>();

			for (Map.Entry<String, String[]> entry : pm.entrySet()) {
				String key = entry.getKey();
				String[] strings = entry.getValue();

				switch (key) {
					case "target":
						target = strings[0];
						break;
					case "message":
						message = strings[0];
						break;
					case "username":
						userName = strings[0];
						params.put("username", strings);
						break;
					case "pwd":
						pwd = strings[0];
						break;
					case "testjson":
						parseTestJson(params, strings[0]);
						break;
					default:
						params.put(key, strings);
				}
			}

			System.out.println(String.format("（%s）访问（%s.%s）", userName, target, message));
			ServiceBase service = ServiceMapper.parseService(target);
			if (service.getClass().equals(Initiator.class)|| canMessaging(userName, pwd, service.getResource(message))) {
				result = service.handleMessage(message, Collections.unmodifiableMap(params));
				result = String.format("{\"success\":true,\"msg\":%s}", result);
			} else {
				result = String.format("{\"success\":false,\"msg\":\"%s\"}", "用户名密码错误或者无访问权限，请联系系统管理员！");
			}
		} catch (Exception ex) {
			Logger.getLogger(MessageDispatcher.class.getName()).log(Level.SEVERE, null, ex);
			result = String.format("{\"success\":false,\"msg\":\"%s\"}", "消息解析错误，请联系我（" + Constants.MOBILE_NUMBER + "）！");
		}

		System.out.println("Ret>" + result);
		response.setContentType("text/plain;charset=UTF-8");
		try (PrintWriter out = response.getWriter()) {
			out.println(result);
		}
	}

	// <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
	/**
	 * Handles the HTTP <code>GET</code> method.
	 *
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * Handles the HTTP <code>POST</code> method.
	 *
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * Returns a short description of the servlet.
	 *
	 * @return a String containing servlet description
	 */
	@Override
	public String getServletInfo() {
		return "Short description";
	}// </editor-fold>

	private void parseTestJson(Map<String, String[]> params, String string) {
		Json json = new Json(string);
		json.each((int idx, String key, Json value) -> {
			params.put(key, value.strList().toArray(new String[0]));
		});
	}

	private void printParams(Map<String, String[]> params) {
		System.out.print("--->");
		params.entrySet().stream().forEach((entry) -> {
			String key = entry.getKey();
			String[] strings = entry.getValue();
			System.out.print(key + "=" + Arrays.toString(strings) + ";");
		});
	}

	@Override
	public void destroy() {
		StorageCache.stop();
		super.destroy();
	}

	@Override
	public void init() throws ServletException {
		super.init(); //To change body of generated methods, choose Tools | Templates.
		StorageCache.start();
	}

}
