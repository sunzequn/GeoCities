package com.sunzequn.geo.data.baike.bdbk;

import com.sunzequn.geo.data.dao.BaseDao;
import com.sunzequn.geo.data.utils.ListUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by sunzequn on 2016/4/11.
 */
public class UrlTypeDao extends BaseDao {

    private static final String DATABASE = "baidubaike";
    private static String TABLE = "url_type_zhengli_all";
    private Connection connection;

    public UrlTypeDao(String table) {
        TABLE = table;
        connection = getConnection(DATABASE);
    }
    public UrlTypeDao() {
        connection = getConnection(DATABASE);
    }

    public List<UrlType> getTypes() {
        String sql = "select distinct type from " + TABLE;
        System.out.println(sql);
        return query(connection, sql, null, UrlType.class);
    }


    public int deleteByUrl(String url) {
        String sql = "delete from " + TABLE + " where url = ?";
        Object[] params = {url};
        return execute(connection, sql, params);
    }

    public UrlType getByUrl(String url) {
        String sql = "select * from " + TABLE + " where url = ?";
        Object[] params = {url};
        List<UrlType> urlTypes = query(connection, sql, params, UrlType.class);
        if (ListUtils.isEmpty(urlTypes)) {
            return null;
        }
        return urlTypes.get(0);
    }

    public int updateType(String url, String type) {
        String sql = "update " + TABLE + " set type = ? where url = ?";
        Object[] params = {type, url};
        return execute(connection, sql, params);
    }

    public List<UrlType> getByTitle(String title1, String title2) {
        String sql = "select * from " + TABLE + " where title = ? or title = ?";
        Object[] params = {title1, title2};
        return query(connection, sql, params, UrlType.class);
    }

    public List<UrlType> getAll() {
        String sql = "select * from " + TABLE;
        return query(connection, sql, null, UrlType.class);
    }

    public List<UrlType> getAll1() {
        String sql = "select * from " + TABLE + " where confidence = 1";
//        String sql = "select * from " + TABLE ;
        return query(connection, sql, null, UrlType.class);
    }

    public List<UrlType> getAll(int confidence) {
        String sql = "select * from " + TABLE + " where confidence = ?";
//        String sql = "select * from " + TABLE ;
        Object[] params = {confidence};
        return query(connection, sql, params, UrlType.class);
    }

    public List<UrlType> getAbove(int confidence) {
        String sql = "select * from " + TABLE + " where confidence > ?";
//        String sql = "select * from " + TABLE ;
        Object[] params = {confidence};
        return query(connection, sql, params, UrlType.class);
    }

    public List<UrlType> getByType(String type) {
        String sql = "select * from " + TABLE + " where type = ? and confidence = 1";
        Object[] params = {type};
        return query(connection, sql, params, UrlType.class);
    }

    public List<UrlType> getByTypeConfidence(String type, int confidence) {
        String sql = "select * from " + TABLE + " where type = ? and confidence = ?";
        Object[] params = {type, confidence};
        return query(connection, sql, params, UrlType.class);
    }

    public int addType(UrlType urlType) {
        String sql = "insert into " + TABLE + " (url, type, confidence, title, subtitle, summary) values (?, ?, ?, ?, ?, ?)";
        Object[] parmas = {urlType.getUrl(), urlType.getType(), urlType.getConfidence(), urlType.getTitle(), urlType.getSubtitle(), urlType.getSummary()};
        return execute(connection, sql, parmas);
    }

    public int addType(String url, String type, int confidence) {
        String sql = "insert into " + TABLE + " (url, type, confidence) values (?, ?, ?)";
        Object[] parmas = {url, type, confidence};
        return execute(connection, sql, parmas);
    }

    public int[] addTypeBatch(List<UrlType> urlTypes) {
        String sql = "insert into " + TABLE + " (url, type, confidence, title, subtitle, summary) values (?, ?, ?, ?, ?, ?)";
        Object[][] parmas = new Object[urlTypes.size()][6];
        for (int i = 0; i < urlTypes.size(); i++) {
            UrlType urlType = urlTypes.get(i);
            parmas[i][0] = urlType.getUrl();
            parmas[i][1] = urlType.getType();
            parmas[i][2] = urlType.getConfidence();
            parmas[i][3] = urlType.getTitle();
            parmas[i][4] = urlType.getSubtitle();
            parmas[i][5] = urlType.getSummary();
        }
        return batch(connection, sql, parmas);
    }

    public List<UrlType> getAllUrlWithNull(int limit) {
        String sql = "select distinct url from " + TABLE + " where subtitle is null or subtitle = '' limit " + limit;
        return query(connection, sql, null, UrlType.class);
    }

    public int updateConfidence(String url, int confidence) {
        String sql = "update " + TABLE + " set confidence = ? where url = ?";
        Object[] params = {confidence, url};
        return execute(connection, sql, params);
    }

    public int[] updateConfidenceBatch(List<UrlType> urlTypes) {
        String sql = "update " + TABLE + " set confidence = ? where url = ?";
        Object[][] parmas = new Object[urlTypes.size()][2];
        for (int i = 0; i < urlTypes.size(); i++) {
            UrlType urlType = urlTypes.get(i);
            parmas[i][0] = urlType.getConfidence();
            parmas[i][1] = urlType.getUrl();
        }
        return batch(connection, sql, parmas);
    }

    public int[] updateBatch(List<UrlType> urlTypes) {
        String sql = "update " + TABLE + " set title = ?, subtitle = ?, summary = ? where url = ?";
        Object[][] parmas = new Object[urlTypes.size()][4];
        for (int i = 0; i < urlTypes.size(); i++) {
            UrlType urlType = urlTypes.get(i);
            parmas[i][0] = urlType.getTitle();
            parmas[i][1] = urlType.getSubtitle();
            parmas[i][2] = urlType.getSummary();
            parmas[i][3] = urlType.getUrl();
        }
        return batch(connection, sql, parmas);
    }

    public static void main(String[] args) {
        UrlTypeDao dao = new UrlTypeDao();
        System.out.println(dao.getAll1().size());
//        System.out.println(dao.getByTitle("丁陆海", "北京市"));
//        List<UrlType> urlTypes = new ArrayList<>();
//        UrlType u1 = new UrlType("1", "s1", 1);
//        UrlType u2 = new UrlType("2", "s2", 1);
//        UrlType u3 = new UrlType("3", "s3", 1);
//        urlTypes.add(u1);
//        urlTypes.add(u2);
//        urlTypes.add(u3);
//        dao.addBatch(urlTypes);
    }

}
