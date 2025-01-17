package com.sunzequn.geo.data.climate.dao;

import com.sunzequn.geo.data.climate.bean.Country;
import com.sunzequn.geo.data.dao.BaseDao;

import java.sql.Connection;
import java.util.List;

/**
 * Created by Sloriac on 16/1/7.
 * <p>
 * 对实体Country的数据库操作
 */
public class CountryDao extends BaseDao {
    private static final String DATABASE = "geocities";
    private static final String TABLE_NAME = "climate_seed_country";
    private Connection connection;

    public CountryDao() {
        connection = getConnection(DATABASE);
    }

    public int save(Country country) {
        String sql = "insert into " + TABLE_NAME + " values (?, ?, ?, ?, ?)";
        Object[] params = {country.getId(), country.getName(), country.getUrl(), country.getParentid(), country.getIfvisited()};
        return execute(connection, sql, params);
    }

    public List<Country> getAll() {
        String sql = "select * from " + TABLE_NAME;
        return query(connection, sql, null, Country.class);
    }

    public List<Country> getByParentId(int id) {
        String sql = "select * from " + TABLE_NAME + " where parentid = " + id;
        return query(connection, sql, null, Country.class);
    }

    public List<Country> getUnvisited() {
        String sql = "select * from " + TABLE_NAME + " where ifvisited = 0";
        return query(connection, sql, null, Country.class);
    }

    /**
     * 更新是否爬过的标识
     *
     * @param id        主键
     * @param ifvisited 是否爬过的标识,0表示没有爬过,1表示爬过
     * @return 更新是否成功
     */
    public int update(int id, int ifvisited) {
        String sql = "update " + TABLE_NAME + " set ifvisited = ? where id = ?";
        Object[] params = {ifvisited, id};
        return execute(connection, sql, params);
    }

    public int deleteById(int id) {

        return 0;
    }
}
