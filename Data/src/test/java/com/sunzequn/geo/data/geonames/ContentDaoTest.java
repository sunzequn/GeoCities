package com.sunzequn.geo.data.geonames;

import com.sunzequn.geo.data.geonames.filebean.Content;
import com.sunzequn.geo.data.geonames.filebean.ContentDao;
import org.junit.Test;

/**
 * Created by Sloriac on 16/1/21.
 */
public class ContentDaoTest {

    @Test
    public void saveTest() {

        ContentDao contentDao = new ContentDao("contains");
        Content content = new Content(1, "szq");
        contentDao.save(content);
    }
}
