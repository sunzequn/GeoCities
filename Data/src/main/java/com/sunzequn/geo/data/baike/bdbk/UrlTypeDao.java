package com.sunzequn.geo.data.baike.bdbk;

import com.sunzequn.geo.data.dao.BaseDao;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunzequn on 2016/4/11.
 */
public class UrlTypeDao extends BaseDao {

    private static final String DATABASE = "baidubaike";
    private static final String TABLE = "url_type";
    private Connection connection;

    public UrlTypeDao() {
        connection = getConnection(DATABASE);
    }

    public int addType(String url, String type, int confidence) {
        String sql = "insert into " + TABLE + " (url, type, confidence) values (?, ?, ?)";
        Object[] parmas = {url, type, confidence};
        return execute(connection, sql, parmas);
    }

    public int[] addTypeBatch(List<UrlType> urlTypes) {
        String sql = "insert into " + TABLE + " (url, type, confidence) values (?, ?, ?)";
        Object[][] parmas = new Object[urlTypes.size()][3];
        for (int i = 0; i < urlTypes.size(); i++) {
            UrlType urlType = urlTypes.get(i);
            parmas[i][0] = urlType.getUrl();
            parmas[i][1] = urlType.getType();
            parmas[i][2] = urlType.getConfidence();
        }
        return batch(connection, sql, parmas);
    }

    public List<UrlType> getAllUrlWithNull(int limit) {
        String sql = "select distinct url from " + TABLE + " where title is null limit " + limit;
        return query(connection, sql, null, UrlType.class);
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
        List<UrlType> urlTypes = new ArrayList<>();
        UrlType u1 = new UrlType("1", "s1", 1);
        UrlType u2 = new UrlType("2", "s2", 1);
        UrlType u3 = new UrlType("3", "s3", 1);
        urlTypes.add(u1);
        urlTypes.add(u2);
        urlTypes.add(u3);
        dao.addTypeBatch(urlTypes);
    }
}
