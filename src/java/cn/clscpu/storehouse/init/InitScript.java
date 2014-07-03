/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.clscpu.storehouse.init;

import static cn.clscpu.storehouse.Constants.ADMIN_PWD;
import cn.clscpu.storehouse.ServiceMapper;
import cn.clscpu.storehouse.customer.CustomerManager;
import cn.clscpu.storehouse.goodsmanage.GoodsManager;
import cn.clscpu.storehouse.storage.StorageManager;
import static cn.clscpu.storehouse.util.DateUtil.TIMEUNIT.day;
import static cn.clscpu.storehouse.util.DateUtil.formatDate;
import static cn.clscpu.storehouse.util.DateUtil.later;
import static cn.clscpu.storehouse.util.DateUtil.later;
import static cn.clscpu.storehouse.util.DateUtil.parseDate;
import cn.clscpu.storehouse.util.Script;
import static cn.clscpu.storehouse.util.StringUtil.Md5;
import cn.clscpu.storehouse.util.db.DBConfig;
import cn.clscpu.storehouse.util.db.sql.ColumnInfo;
import cn.clscpu.storehouse.util.db.sql.ColumnInfo.Type;
import cn.clscpu.storehouse.util.db.sql.Index;
import cn.clscpu.storehouse.util.db.sql.Partition;
import cn.clscpu.storehouse.util.db.sql.Partitioning;
import cn.clscpu.storehouse.util.db.sql.Partitioning.PartitionBy;
import cn.clscpu.storehouse.util.db.sql.PrimaryKey;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author CPU
 */
public class InitScript implements Init, Script {

	private static final Date FIRST_PART_DATE = parseDate(formatDate(later(7, day, new Date())));
		
	protected List<Init> scripts = new ArrayList<>();

	@Override
	public boolean execute() {

		// 建库
		scripts.add(new InitSchema(DBConfig.SCHEMA));

		// 建表
		scripts.add(new InitTable(DBConfig.TEST_SCHEMA, DBConfig.VERSION)
				.addColumn(new ColumnInfo("cv", Type.shorttext))
		);

		scripts.add(new InitTable(DBConfig.SCHEMA, "goods")
				.addColumn(new ColumnInfo("id", Type.integer))
				.addColumn(new ColumnInfo("code", Type.shorttext))
				.addColumn(new ColumnInfo("name", Type.shorttext))
				.addColumn(new ColumnInfo("color", Type.shorttext))
				.setPk(new PrimaryKey("id"))
				.addIndex(new Index(true, "uniquecombi", "code", "name", "color"))
		);

		scripts.add(new InitTable(DBConfig.SCHEMA, DBConfig.IDGENERATOR)
				.addColumn(new ColumnInfo("table", Type.shorttext))
				.addColumn(new ColumnInfo("id", Type.bitint))
				.setPk(new PrimaryKey("table"))
		);

		scripts.add(new InitTable(DBConfig.SCHEMA, DBConfig.CUSTOMER)
				.addColumn(new ColumnInfo("id", Type.integer))
				.addColumn(new ColumnInfo("company", Type.shorttext))
				.addColumn(new ColumnInfo("name", Type.shorttext))
				.addColumn(new ColumnInfo("telephone", Type.shorttext))
				.addColumn(new ColumnInfo("mobile", Type.shorttext))
				.setPk(new PrimaryKey("id"))
				.addIndex(new Index(true, "uniquecombi", "company", "name", "telephone", "mobile"))
		);

		scripts.add(new InitTable(DBConfig.SCHEMA, DBConfig.BILL)
				.addColumn(new ColumnInfo("id", Type.integer))
				.addColumn(new ColumnInfo("code", Type.shorttext))
				.addColumn(new ColumnInfo("customer_id", Type.integer))
				.addColumn(new ColumnInfo("recorder_id", Type.integer))
				.addColumn(new ColumnInfo("checker_id", Type.integer))
				.addColumn(new ColumnInfo("remark", Type.longtext))
				.addColumn(new ColumnInfo("created", Type.datetime))
				.addColumn(new ColumnInfo("is_in", Type.bool))
				.setPartitioning(new Partitioning(PartitionBy.range, "TO_DAYS(created)", 1), (int idx) -> {
					return new Partition(DBConfig.BILL + idx, String.format("TO_DAYS('%s')",
									formatDate(FIRST_PART_DATE)));
				})
		);

		scripts.add(new InitTable(DBConfig.SCHEMA, DBConfig.GOODS_CLAUSE)
				.addColumn(new ColumnInfo("created", Type.datetime))
				.addColumn(new ColumnInfo("goods_id", Type.integer))
				.addColumn(new ColumnInfo("nums", Type.shorttext))
				.addColumn(new ColumnInfo("remark", Type.longtext))
				.addColumn(new ColumnInfo("bill_id", Type.integer))
				.setPartitioning(new Partitioning(PartitionBy.range, "TO_DAYS(created)", 1), (int idx) -> {
					return new Partition(DBConfig.GOODS_CLAUSE + idx, String.format("TO_DAYS('%s')",
									formatDate(FIRST_PART_DATE)));
				})
		);

		scripts.add(new InitTable(DBConfig.SCHEMA, DBConfig.PARTITION_INFO)
				.addColumn(new ColumnInfo("table", Type.shorttext))
				.addColumn(new ColumnInfo("part", Type.integer))
				.addColumn(new ColumnInfo("date", Type.datetime))
				.setPk(new PrimaryKey("table", "part"))
		);

		scripts.add(new InitTable(DBConfig.SCHEMA, DBConfig.STORAGE)
				.addColumn(new ColumnInfo("date", Type.datetime))
				.addColumn(new ColumnInfo("goods_id", Type.integer))
				.addColumn(new ColumnInfo("nums", Type.shorttext))
				.setPk(new PrimaryKey("date", "goods_id"))
		);
		
		scripts.add(new InitTable(DBConfig.SCHEMA, DBConfig.USER)
				.addColumn(new ColumnInfo("id", Type.integer))
				.addColumn(new ColumnInfo("name", Type.shorttext))
				.addColumn(new ColumnInfo("pwd", Type.shorttext))
				.addColumn(new ColumnInfo("role_id", Type.integer))
				.setPk(new PrimaryKey("id"))
				.addIndex(new Index(true, "unique_name", "name"))
		);
		
		scripts.add(new InitTable(DBConfig.SCHEMA, DBConfig.ROLE)
				.addColumn(new ColumnInfo("id", Type.integer))
				.addColumn(new ColumnInfo("name", Type.shorttext))
				.setPk(new PrimaryKey("id"))
				.addIndex(new Index(true, "unique_name", "name"))
		);
		
		scripts.add(new InitTable(DBConfig.SCHEMA, DBConfig.RESOURCE)
				.addColumn(new ColumnInfo("id", Type.integer))
				.addColumn(new ColumnInfo("name", Type.shorttext))
				.setPk(new PrimaryKey("id"))
				.addIndex(new Index(true, "unique_name", "name"))
		);
		
		scripts.add(new InitTable(DBConfig.SCHEMA, DBConfig.ROLE_RESOURCE)
				.addColumn(new ColumnInfo("role_id", Type.integer))
				.addColumn(new ColumnInfo("resource_id", Type.integer))
				.setPk(new PrimaryKey("role_id", "resource_id"))
		);
		
		// 插数据
		scripts.add(new InitData(DBConfig.TEST_SCHEMA, DBConfig.VERSION, "cv").addData(InitConfig.VERSION));

		scripts.add(new InitData(DBConfig.SCHEMA, DBConfig.PARTITION_INFO, "table", "part", "date")
				.addData(DBConfig.BILL, 0, FIRST_PART_DATE)
				.addData(DBConfig.GOODS_CLAUSE, 0, FIRST_PART_DATE)
		);
		
		scripts.add(new InitData(DBConfig.SCHEMA, DBConfig.USER, "id", "name", "pwd", "role_id")
				.addData(1, "test", "test", 0)
				.addData(2, "admin", Md5(ADMIN_PWD), 1)
		);
		
		scripts.add(new InitData(DBConfig.SCHEMA, DBConfig.ROLE, "id", "name")
				.addData(1, "系统管理员")
				.addData(2, "库房主管")
				.addData(3, "库房管理员")
				.addData(4, "客服")
				.addData(5, "运营总监")
				.addData(6, "总经理")
		);

		Map<String, Integer> datasource = new HashMap<>();
		List<String> resources = ServiceMapper.getResources();
		List<Object[]> data = new ArrayList<>();
		int idx = 1;
		for (String rName : resources) {
			Integer get = datasource.get(rName);
			if (null == get) {
				datasource.put(rName, idx);
				data.add(new Object[]{ idx, rName });
				idx++;
			}
		}
		scripts.add(new InitData(DBConfig.SCHEMA, DBConfig.RESOURCE, "id", "name").setData(data));
		
		scripts.add(new InitRoleResourceData(DBConfig.SCHEMA, DBConfig.ROLE_RESOURCE, datasource, "role_id", "resource_id")
				.addData(2, StorageManager.STORE, StorageManager.QUERY_STORE, StorageManager.CHECK)
				.addData(3, StorageManager.STORE, StorageManager.QUERY_STORE)
				.addData(4, StorageManager.QUERY_STORE)
				.addData(5, StorageManager.QUERY_STORE, CustomerManager.ADD, GoodsManager.ADD)
				.addData(6, StorageManager.QUERY_STORE, CustomerManager.ADD, GoodsManager.ADD)
		);
		
		scripts.add(new InitData(DBConfig.SCHEMA, DBConfig.IDGENERATOR, "table", "id")
				.addData(DBConfig.USER, 3)
				.addData(DBConfig.ROLE, 7)
		);
		
		boolean suc = true;
		for (Init script : scripts) {
			if (!script.init()) {
				suc = false;
				break;
			}
		}
		return suc;
	}

	@Override
	public boolean init() {
		return execute();
	}

}
