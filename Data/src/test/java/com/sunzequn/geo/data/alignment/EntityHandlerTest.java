package com.sunzequn.geo.data.alignment;

import com.sunzequn.geo.data.alignment.sameas.EntityHandler;
import com.sunzequn.geo.data.alignment.type.Clazz;
import com.sunzequn.geo.data.alignment.type.ClazzGraph;

import java.util.List;

/**
 * Created by Sloriac on 16/2/27.
 */
public class EntityHandlerTest {

    private static EntityHandler entityHandler = new EntityHandler();

    public static void main(String[] args) {
        String uri = "http://dbpedia.org/resource/Loaita_Island";
//        List<Clazz> clazzs = entityHandler.getTypes(uri);
//        System.out.println(clazzs);
//        System.out.println(entityHandler.removeEquivalent(clazzs));
        ClazzGraph clazzGraph = entityHandler.getGraph(uri);
    }
}
