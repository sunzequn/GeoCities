package com.sunzequn.geo.data.crawler;

import com.sunzequn.geo.data.climate.dao.ContinentDao;
import org.junit.Test;

/**
 * Created by Sloriac on 16/1/6.
 */
public class ContinentDaoTest {

    private ContinentDao continentDao = new ContinentDao();

    @Test
    public void getAllTest() {
        System.out.println(continentDao.getAll());
    }

    @Test
    public void getByIdTest() {
        System.out.println(continentDao.getById(1));
    }
}
