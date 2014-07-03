/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.clscpu.storehouse.storage;

import cn.clscpu.storehouse.Constants;
import cn.clscpu.storehouse.cache.Cache;
import cn.clscpu.storehouse.cache.Synchronizer;
import cn.clscpu.storehouse.goodsmanage.Goods;
import cn.clscpu.storehouse.goodsmanage.GoodsManager;
import static cn.clscpu.storehouse.util.DateUtil.TIMEUNIT.day;
import static cn.clscpu.storehouse.util.DateUtil.formatDate;
import static cn.clscpu.storehouse.util.DateUtil.last;
import static cn.clscpu.storehouse.util.DateUtil.parseDate;
import static cn.clscpu.storehouse.util.StringUtil.join;
import static cn.clscpu.storehouse.util.StringUtil.splitToIntList;
import cn.clscpu.storehouse.util.db.DB;
import cn.clscpu.storehouse.util.db.DBConfig;
import cn.clscpu.storehouse.util.db.MyResultSet;
import cn.clscpu.storehouse.util.db.sql.SQLBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author CPU
 */
public final class StorageCache {

	private static class StoragePK {

		private Date date;
		private int goodsId;

		public StoragePK(Date date, int goodsId) {
			this.date = date;
			this.goodsId = goodsId;
		}

		@Override
		public int hashCode() {
			int hash = 7;
			hash = 17 * hash + Objects.hashCode(this.date);
			hash = 17 * hash + this.goodsId;
			return hash;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			final StoragePK other = (StoragePK) obj;
			if (!Objects.equals(this.date, other.date)) {
				return false;
			}
			if (this.goodsId != other.goodsId) {
				return false;
			}
			return true;
		}

		@Override
		public String toString() {
			return "StoragePK{" + "date=" + date + ", goodsId=" + goodsId + '}';
		}
	}

	private static final class Count {

		private final int count;

		public Count(int count) {
			this.count = count;
		}

		public Count add(int add) {
			return new Count(count + add);
		}

		public Count minus(int minus) {
			return new Count(count - minus);
		}

		@Override
		public String toString() {
			return Integer.toString(count);
		}
	}

	private static final Cache<StoragePK, List<Count>> cache = new Cache<>(new Synchronizer<StoragePK, List<Count>>() {

		@Override
		public boolean put(StoragePK key, List<Count> value) {
			return true;
		}

		@Override
		public boolean remove(StoragePK key, List<Count> value) {
			throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
		}

		@Override
		public void init(Map<StoragePK, List<Count>> data) {
			MyResultSet rs = DB.instance().simpleQuery(new SQLBuilder(DBConfig.SCHEMA, DBConfig.STORAGE).select(null, "date", "goods_id", "nums"));
			while (rs.next()) {
				List<Integer> ints = splitToIntList(rs.getString("nums"), GoodsClause.SEPRATOR);
				List<Count> nums = new ArrayList();
				for (int i = 0; i < ints.size(); i++) {
					nums.add(new Count(ints.get(i)));
				}
				data.put(new StoragePK(rs.getDate("date"), rs.getInt("goods_id")), nums);
			}
			System.out.println(data);
		}
	});

	synchronized static void store(Date date, boolean isIn, List<GoodsClause> goodses, StorageChanged storage) {
		Map<StoragePK, List<Count>> changes = new HashMap<>();
		List<String> strNumsList = new ArrayList<>();

		for (int idx = 0; idx < goodses.size(); idx++) {
			GoodsClause goodsClause = goodses.get(idx);
			StoragePK storagePK = new StoragePK(date, goodsClause.getGoodsid());
			List<Count> cacheData = latestCacheData(storagePK);
			List<Count> changeData = new ArrayList<>();
			List<Integer> nums = goodsClause.getNums();
			for (int i = 0; i < nums.size(); i++) {
				if (null == cacheData) {
					if (isIn) {
						changeData.add(new Count(0 + nums.get(i)));
					} else {
						changeData.add(new Count(0 - nums.get(i)));
					}
				} else {
					if (isIn) {
						changeData.add(cacheData.get(i).add(nums.get(i)));
					} else {
						changeData.add(cacheData.get(i).minus(nums.get(i)));
					}
				}
			}
			changes.put(storagePK, changeData);
			strNumsList.add(join(changeData, GoodsClause.SEPRATOR));
		}

		if (storage.changed(Collections.unmodifiableList(strNumsList))) {
			changes.entrySet().stream().forEach((entry) -> {
				StoragePK storagePK = entry.getKey();
				List<Count> list = entry.getValue();
				cache.putButNotSync(storagePK, list);
			});
		}
	}

	private static List<Count> latestCacheData(StoragePK storagePK) {
		List<Count> latest = null;
		long minLast = Long.MAX_VALUE;
		for (Entry<StoragePK, List<Count>> entry : cache.getEntries()) {
			if (entry.getKey().goodsId == storagePK.goodsId) {
				long last = last(entry.getKey().date, storagePK.date, day);
				if (last < minLast) {
					latest = entry.getValue();
					minLast = last;
				}
			}
		}
		return latest;
	}

	static List<Storage> query(Date date, String code, String name, String color) {
		List<Storage> rets = new ArrayList<>();
		GoodsManager.getAll().stream().forEach((Goods goods) -> {
			if (goods.getCode().contains(code) && goods.getName().contains(name) && goods.getColor().contains(color)) {
//				System.out.println(String.format("%d %s %s %s", goods.getId(), goods.getCode(), goods.getName(), goods.getColor()));
				List<Count> nums = cache.get(new StoragePK(date, goods.getId()));
				if (null != nums) {
					List<Integer> iNums = new ArrayList<>();
					for (int i = 0; i < nums.size(); i++) {
						Count count = nums.get(i);
						iNums.add(count.count);
					}
					rets.add(new Storage(date, goods.getId(), iNums));
				}
			}
		});
		return rets;
	}

	private static boolean isStarted = false;
	private static ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(1);

	public static void start() {
		cache.init();
		System.out.println("IsStarted" + isStarted);
		if (isStarted) {
			return;
		}
		scheduledThreadPool.scheduleAtFixedRate(() -> {
			doUpdate();
		}, 0, 30, TimeUnit.MINUTES);
		isStarted = true;
	}

	public static void stop() {
		scheduledThreadPool.shutdown();
	}
	
	synchronized static void doUpdate() {
//		System.out.println("do update");
		Date today = parseDate(formatDate(new Date()));
		List<GoodsClause> goodses = new ArrayList<>();
		Integer[] inums = new Integer[Constants.CLOTH_SIZE_DIFF];
		for (int i = 0; i < inums.length; i++) {
			inums[i] = 0;
		}
		List<Integer> nums = Arrays.asList(inums);
		GoodsManager.getAll().stream().forEach((goods) -> {
			goodses.add(new GoodsClause(goods.getId(), "", nums));
		});
//		System.out.println(goodses);
		store(today, true, goodses, (goodsesNums) -> {
			System.out.println(goodsesNums);
			return DB.instance().batchExecute(new SQLBuilder(DBConfig.SCHEMA, DBConfig.STORAGE)
					.insertWithUpdate("`nums` = ?", "date", "goods_id", "nums"), goodses.size(), (int rowId) -> {
						GoodsClause clause = goodses.get(rowId);
						String strNums = goodsesNums.get(rowId);
						return new Object[]{today, clause.getGoodsid(), strNums, strNums};
					});
		});
	}

}
