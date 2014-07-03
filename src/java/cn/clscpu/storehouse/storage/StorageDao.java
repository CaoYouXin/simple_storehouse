/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cn.clscpu.storehouse.storage;

import cn.clscpu.storehouse.idgen.IDGenerator;
import cn.clscpu.storehouse.partition.PartitionManager;
import cn.clscpu.storehouse.util.Bool;
import static cn.clscpu.storehouse.util.DateUtil.TIMEUNIT.day;
import static cn.clscpu.storehouse.util.DateUtil.formatDate;
import static cn.clscpu.storehouse.util.DateUtil.later;
import static cn.clscpu.storehouse.util.DateUtil.parseDate;
import static cn.clscpu.storehouse.util.StringUtil.join;
import static cn.clscpu.storehouse.util.StringUtil.splitToIntList;
import cn.clscpu.storehouse.util.Suc;
import cn.clscpu.storehouse.util.db.DB;
import cn.clscpu.storehouse.util.db.DBConfig;
import cn.clscpu.storehouse.util.db.MyResultSet;
import cn.clscpu.storehouse.util.db.sql.SQLBuilder;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author CPU
 */
public final class StorageDao {
	
	static int add(Bill bill) {
		Suc suc = new Suc();
		PartitionManager.insert(DBConfig.BILL, (Date now) -> {
			PartitionManager.insert(DBConfig.GOODS_CLAUSE, now, (Date created) -> {
				Date storeDate = parseDate(formatDate(created));
				StorageCache.store(storeDate, bill.isIn(), bill.getGoodses(), (List<String> nums) -> {
					IDGenerator.next(DBConfig.BILL, (long id) -> {
						bill.setId((int) id);
						suc.val(DB.instance().transaction((Connection conn) -> {
							DB.instance().preparedExecute(conn, new SQLBuilder(DBConfig.SCHEMA, DBConfig.BILL)
									.insert("id", "code", "customer_id", "recorder_id", "checker_id", "remark", "created", "is_in"), 1,
									bill.getId(), bill.getCode(), bill.getCustomerId(), bill.getRecorderId(),
									bill.getCheckerId(), bill.getRemark(), created, bill.isIn());
							final List<GoodsClause> goodses = bill.getGoodses();
							DB.instance().batchExecute(conn, new SQLBuilder(DBConfig.SCHEMA, DBConfig.GOODS_CLAUSE)
									.insert("created", "goods_id", "nums", "remark", "bill_id"), goodses.size(), (int rowId) -> {
										GoodsClause clause = goodses.get(rowId);
										return new Object[]{created, clause.getGoodsid(),
												clause.getStrNums(), clause.getRemark(), bill.getId()};
									});
							DB.instance().batchExecute(conn, new SQLBuilder(DBConfig.SCHEMA, DBConfig.STORAGE)
									.insertWithUpdate("`nums` = ?", "date", "goods_id", "nums"), goodses.size(), (int rowId) -> {
										GoodsClause clause = goodses.get(rowId);
										String strNums = nums.get(rowId);
										return new Object[]{storeDate, clause.getGoodsid(), strNums, strNums};
									});
						}));
						return suc.val();
					});
					return suc.val();
				});
			});
		});
		return suc.val() ? bill.getId() : -1;
	}

	static List<Bill> query(Date from, Date to, String code, int customerId, int recorderId, int checkerId, String remark, Bool isIn) {
		Map<Integer, Bill> bills = new HashMap<>();
		PartitionManager.select(DBConfig.BILL, from, to, (List<String> billParts) -> {
			PartitionManager.select(DBConfig.GOODS_CLAUSE, from, to, (List<String> gcParts) -> {
				StringBuilder where = new StringBuilder();
				if (null != isIn.val()) {
					where.append("`b`.`is_in` = ? and ");
				}
				if (customerId != -1) {
					where.append("`b`.`customer_id` = ? and ");
				}
				if (recorderId != -1) {
					where.append("`b`.`recorder_id` = ? and ");
				}
				if (checkerId != -1) {
					where.append("`b`.`checker_id` = ? and ");
				}
				String sql = String.format("select distinct `b`.`id`, `b`.`code`, `b`.`customer_id`, `b`.`recorder_id`, `b`.`checker_id`, `b`.`created`, "
						+ "`b`.`remark`, `b`.`is_in` from `storehouse`.`bill` partition (%s) as `b` "
						+ "join `storehouse`.`goods_clause` partition (%s) as `g` "
						+ "on `b`.`id` = `g`.`bill_id` "
						+ "where %s (`b`.`created` >= ? and `b`.`created` < ?) and `b`.`code` like ? "
						+ "and (`b`.`remark` like ? or `g`.`remark` like ?)",
						join(billParts, ","), join(gcParts, ","), where);
				System.out.println(sql);
				
				List<Object> params = new ArrayList<>();
				if (null != isIn.val()) {
					params.add(isIn.val());
				}
				if (customerId != -1) {
					params.add(customerId);
				}
				if (recorderId != -1) {
					params.add(recorderId);
				}
				if (checkerId != -1) {
					params.add(checkerId);
				}
				params.add(from);
				params.add(later(1, day, to));
				params.add(String.format("%%%s%%", code));
				String likeRemark = String.format("%%%s%%", remark);
				params.add(likeRemark);
				params.add(likeRemark);
				
				MyResultSet rs = DB.instance().preparedQuery(sql, params.toArray());
				while (rs.next()) {
					int id = rs.getInt("id");
					Bill bill = bills.get(id);
					if (null == bill) {
						bill = new Bill(id, rs.getString("code"), rs.getInt("customer_id"),
								rs.getInt("recorder_id"), rs.getInt("checker_id"), rs.getString("remark"),
								rs.getDate("created"), rs.getBoolean("is_in"));
						bills.put(id, bill);
					}
				}
			});
		});
		return new ArrayList<>(bills.values());
	}

	static synchronized Bool check(int id, int checkerId) {
		String where = "`id`=?";
		String checker_id = "checker_id";
		MyResultSet rs = DB.instance().preparedQuery(new SQLBuilder(DBConfig.SCHEMA, DBConfig.BILL).select(where, checker_id), id);
		while (rs.next()) {
			if (0 != rs.getInt(checker_id)) {
				return Bool.False;
			}
			return Bool.valuseOf(DB.instance().preparedExecute(new SQLBuilder(DBConfig.SCHEMA, DBConfig.BILL).update(where, checker_id), 1, checkerId, id));
		}
		return Bool.False;
	}

	static List<GoodsClause> getBillById(int billId, Date datetime) {
		List<GoodsClause> goodses = new ArrayList<>();
		PartitionManager.select(DBConfig.GOODS_CLAUSE, datetime, datetime, (List<String> gcParts) -> {
			String sql = String.format("select `g`.`goods_id`, `g`.`nums`, `g`.`remark` "
					+ "from `storehouse`.`goods_clause` partition (%s) as `g` "
					+ "where `g`.`bill_id` = ?",
					join(gcParts, ","));
			System.out.println(sql);

			MyResultSet rs = DB.instance().preparedQuery(sql, new Object[]{billId});
			while (rs.next()) {
				goodses.add(new GoodsClause(rs.getInt("goods_id"), rs.getString("remark"),
						splitToIntList(rs.getString("nums"), GoodsClause.SEPRATOR)));
			}
		});
		return goodses;
	}
	
}
