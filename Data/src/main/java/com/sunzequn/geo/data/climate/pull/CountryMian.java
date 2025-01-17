package com.sunzequn.geo.data.climate.pull;

import com.sunzequn.geo.data.climate.bean.*;
import com.sunzequn.geo.data.climate.dao.CountryDao;
import com.sunzequn.geo.data.climate.dao.PageUrlsDao;
import com.sunzequn.geo.data.climate.dao.PlaceDao;
import com.sunzequn.geo.data.climate.pull.parser.RegionParser;
import com.sunzequn.geo.data.crawler.proxy.ProxyBean;
import com.sunzequn.geo.data.crawler.proxy.ProxyHandler;
import com.sunzequn.geo.data.geonames.crawler.HttpConnector;
import com.sunzequn.geo.data.geonames.crawler.Response;
import com.sunzequn.geo.data.utils.ListUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Sloriac on 16/2/11.
 */
public class CountryMian {

    private static final int THREAD_NUM = 60;
    private static final int TIMEOUT = 7000;
    private static final String PREFIX = "http://en.climate-data.org/country/";
    private static final String SUFFIX = "/";
    private static final String TABLE_NAME = "climate_seed_place_from_country";

    private static LinkedList<Integer> ids = new LinkedList<>();
    private static ProxyHandler proxyHandler = new ProxyHandler();
    private static PlaceDao placeDao = new PlaceDao(TABLE_NAME);
    private static CountryDao countryDao = new CountryDao();
    private static PageUrlsDao pageUrlsDao = new PageUrlsDao();

    public static void main(String[] args) {
        initIds();
        for (int i = 0; i < THREAD_NUM; i++) {
            new Thread(() -> {
                ProxyBean proxy = getProxy();
                RegionParser regionParser = new RegionParser();
                while (true) {
                    try {
                        HttpConnector httpConnector = new HttpConnector();
                        int id = getId();
                        if (id == 0) {
                            System.out.println(Thread.currentThread().getName() + "is over");
                            return;
                        }
                        String url = PREFIX + id + SUFFIX;
                        Response response = httpConnector.setUrl(url)
                                .setProxy(proxy.getHost(), proxy.getPort())
                                .getConnection().setTimeout(TIMEOUT).getContent();

                        if (response.getCode() != 200) {
                            addId(id);
                            proxy = getProxy();
                            continue;
                        }
                        String string = response.getContent().trim();
                        Document document = Jsoup.parse(string);
                        PlaceWebWrapper placeWebWrapper = regionParser.parser(url, id, document);
                        if (placeWebWrapper != null) {
                            List<Place> places = placeWebWrapper.getPlaces();
                            List<String> nexts = placeWebWrapper.getNexts();
                            if (!ListUtils.isEmpty(places)) {
                                addPlace(places);
                            }
                            if (!ListUtils.isEmpty(nexts)) {
                                addPageUrls(nexts);
                            }
                            updateId(id, 1);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }, "thread" + i).start();
        }
    }

    private static void initIds() {
        List<Country> countries = countryDao.getUnvisited();
        if (countries != null || countries.size() > 0) {
            for (Country country : countries) {
                ids.add(country.getId());
            }
        }
        System.out.println("unvisited country : " + ids.size());
    }

    private static synchronized ProxyBean getProxy() {
        return proxyHandler.getProxy();
    }

    private static synchronized void addPageUrls(List<String> urls) {
        if (urls == null || urls.size() == 0) {
            return;
        }
        for (String url : urls) {
            PageUrls pageUrls = new PageUrls(url, 0);
            pageUrlsDao.save(pageUrls);
        }
    }

    private static synchronized int getId() {
        if (ids.size() > 0) {
            return ids.pop();
        }
        return 0;
    }

    private static synchronized void addId(int id) {
        ids.add(id);
    }

    private static synchronized void addPlace(List<Place> places) {
        if (places == null || places.size() == 0) {
            return;
        }
        for (Place place : places) {
            placeDao.save(place);
        }
    }

    private static synchronized void updateId(int id, int status) {
        countryDao.update(id, status);
    }

}
