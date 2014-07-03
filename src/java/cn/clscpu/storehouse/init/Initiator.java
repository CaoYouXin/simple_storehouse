/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.clscpu.storehouse.init;

import cn.clscpu.storehouse.Constants;
import cn.clscpu.storehouse.ServiceBase;
import cn.clscpu.storehouse.storage.StorageCache;
import cn.clscpu.storehouse.util.Bool;
import cn.clscpu.storehouse.util.StringUtil;
import static cn.clscpu.storehouse.util.StringUtil.valueOf;
import cn.clscpu.storehouse.util.db.DB;
import cn.clscpu.storehouse.util.db.DBConfig;
import cn.clscpu.storehouse.util.db.MyResultSet;
import cn.clscpu.storehouse.util.db.sql.SQLBuilder;
import java.util.Map;
/**
 *
 * @author CPU
 */
public final class Initiator extends ServiceBase {

	private static final String OK = Bool.True.toString() + "初始化完成，可以创建用户了！";
	private static final String CANNOT_DO_IT = Bool.False.toString() + "不能逆向升级啊！";
	private static final String JUST_DO_IT = Bool.False.toString() + "不必再初始化一次，已经可以创建用户了！";
	private static final String ERROR = Bool.False.toString() + "纳尼？初始化就有问题！请联系我（" + Constants.MOBILE_NUMBER + "）";

	public String init(Map<String, String[]> params) {
		try {
			checkVersion();
		} catch (RuntimeException ex) {
//			Logger.getLogger(Initiator.class.getName()).log(Level.SEVERE, null, ex);
			return valueOf(ex.getMessage());
		}

		Init init = new InitScript();
		if (init.init()) {
			return valueOf(OK);
		} else {
			return valueOf(ERROR);
		}
	}

	private void checkVersion() {
		MyResultSet rs = DB.instance().simpleQuery(String.format("show tables in `%s`", DBConfig.TEST_SCHEMA));
		boolean initOk = false;
		while (rs.next()) {
			if ("version".equals(rs.getString("Tables_in_test"))) {
				initOk = true;
				break;
			}
		}
		if (initOk) {
			rs = DB.instance().simpleQuery(new SQLBuilder(DBConfig.TEST_SCHEMA, DBConfig.VERSION).select("1=1", "cv"));
			while (rs.next()) {
				String currentVersion = rs.getString("cv");
				double cv = Double.valueOf(currentVersion);
				if (cv > InitConfig.VERSION) {
					throw new RuntimeException(CANNOT_DO_IT);
				} else if (cv == InitConfig.VERSION) {
					throw new RuntimeException(JUST_DO_IT);
				}
			}
		}
	}

	public String needInit(Map<String, String[]> params) {
		try {
			checkVersion();
			return Bool.True.strVal();
		} catch (RuntimeException ex) {
//			Logger.getLogger(Initiator.class.getName()).log(Level.SEVERE, null, ex);
			return Bool.False.strVal();
		}
	}

}
