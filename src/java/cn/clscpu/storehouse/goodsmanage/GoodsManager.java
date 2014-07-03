/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.clscpu.storehouse.goodsmanage;

import cn.clscpu.storehouse.ServiceBase;
import cn.clscpu.storehouse.cache.Cache;
import cn.clscpu.storehouse.cache.Synchronizer;
import cn.clscpu.storehouse.idgen.IDGenerator;
import cn.clscpu.storehouse.security.ResourceId;
import cn.clscpu.storehouse.security.SecurityFacade;
import cn.clscpu.storehouse.util.Suc;
import cn.clscpu.storehouse.util.db.DB;
import cn.clscpu.storehouse.util.db.DBConfig;
import cn.clscpu.storehouse.util.db.MyResultSet;
import cn.clscpu.storehouse.util.db.sql.SQLBuilder;
import cn.clscpu.storehouse.util.json.Json;
import cn.clscpu.storehouse.util.json.Json.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 *
 * @author CPU
 */
public final class GoodsManager extends ServiceBase {

	public static final String ADD = "添加商品";
	
	private static final Cache<Integer, Goods> cache = new Cache<>(new Synchronizer<Integer, Goods>() {
		@Override
		public boolean put(Integer key, Goods value) {
			return DB.instance().preparedExecute(new SQLBuilder(DBConfig.SCHEMA, DBConfig.GOODS)
					.insert("id", "code", "name", "color"), 1, value.getId(), value.getCode(), value.getName(), value.getColor());
		}

		@Override
		public boolean remove(Integer key, Goods value) {
			throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
		}

		@Override
		public void init(Map<Integer, Goods> data) {
			MyResultSet rs = DB.instance().simpleQuery(new SQLBuilder(DBConfig.SCHEMA, DBConfig.GOODS).select(null, "id", "code", "name", "color"));
			while (rs.next()) {
				data.put(rs.getInt("id"), new Goods(rs.getInt("id"), rs.getString("code"), rs.getString("name"), rs.getString("color")));
			}
		}
	});

	@ResourceId(SecurityFacade.COMMON)
	public final String getAll(Map<String, String[]> params) {
		List<Goods> all = new ArrayList<>(getAll());
		Collections.sort(all, (Goods o1, Goods o2) -> o1.getId() - o2.getId());
		Json json = new Json(Type.array);
		all.stream().forEach((Goods goods) -> {
			json.addJson(goods.getJson());
		});
		return json.toJson();
	}

	@ResourceId(SecurityFacade.COMMON)
	public final String getAllDesc(Map<String, String[]> params) {
		List<Goods> all = new ArrayList<>(getAll());
		Collections.sort(all, (Goods o1, Goods o2) -> o1.getId() - o2.getId());
		Json json = new Json(Type.array);
		all.stream().forEach((Goods goods) -> {
			Json o = new Json(Type.object);
			o.addKeyValue("id", new Json(Type.string, "" + goods.getId()));
			o.addKeyValue("desc", new Json(Type.string, goods.getCode() + "," + goods.getName() + "," + goods.getColor()));
			json.addJson(o);
		});
		return json.toJson();
	}

	@ResourceId(SecurityFacade.COMMON)
	public final String getAllCode(Map<String, String[]> params) {
		List<Goods> all = new ArrayList<>(getAll());
		Collections.sort(all, (Goods o1, Goods o2) -> o1.getId() - o2.getId());
		Json json = new Json(Type.array);
		List<String> codes = new ArrayList<>();
		all.stream().forEach((Goods goods) -> {
			if (!codes.contains(goods.getCode())) {
				codes.add(goods.getCode());
				Json o = new Json(Type.object);
				o.addKeyValue("code", new Json(Type.string, goods.getCode()));
				json.addJson(o);
			}
		});
		return json.toJson();
	}

	@ResourceId(SecurityFacade.COMMON)
	public final String getAllName(Map<String, String[]> params) {
		List<Goods> all = new ArrayList<>(getAll());
		Collections.sort(all, (Goods o1, Goods o2) -> o1.getId() - o2.getId());
		Json json = new Json(Type.array);
		List<String> names = new ArrayList<>();
		all.stream().forEach((Goods goods) -> {
			if (!names.contains(goods.getName())) {
				names.add(goods.getName());
				Json o = new Json(Type.object);
				o.addKeyValue("name", new Json(Type.string, goods.getName()));
				json.addJson(o);
			}
		});
		return json.toJson();
	}

	@ResourceId(SecurityFacade.COMMON)
	public final String getAllColor(Map<String, String[]> params) {
		List<Goods> all = new ArrayList<>(getAll());
		Collections.sort(all, (Goods o1, Goods o2) -> o1.getId() - o2.getId());
		Json json = new Json(Type.array);
		List<String> colors = new ArrayList<>();
		all.stream().forEach((Goods goods) -> {
			if (!colors.contains(goods.getColor())) {
				colors.add(goods.getColor());
				Json o = new Json(Type.object);
				o.addKeyValue("color", new Json(Type.string, goods.getColor()));
				json.addJson(o);
			}
		});
		return json.toJson();
	}

	@ResourceId(ADD)
	public final String addGoodsAndReturnId(Map<String, String[]> params) {
		return "" + addGoodsAndReturnId(new Goods(params.get("code")[0],
				params.get("name")[0], params.get("color")[0]));
	}

	public static Collection<Goods> getAll() {
		return cache.getValues();
	}

	private static int addGoodsAndReturnId(Goods goods) {
		Suc suc = new Suc();
		IDGenerator.next(DBConfig.GOODS, (long id) -> {
			suc.val(cache.put((int) id, goods.setId((int) id)));
			return suc.val();
		});
		return suc.val() ? goods.getId() : -1;
	}
	
	public static Goods getById(int id) {
		Goods get = cache.get(id);
		return null == get ? Goods.getDefault() : get;
	}

}
