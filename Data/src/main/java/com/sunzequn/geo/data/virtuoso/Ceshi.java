package com.sunzequn.geo.data.virtuoso;

import com.sunzequn.geo.data.crawler.parser.PullText;
import org.apache.jena.query.ParameterizedSparqlString;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFactory;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * Created by sunzequn on 2016/4/20.
 */
public class Ceshi extends PullText {

    public static void main(String[] args) throws Exception {

        ParameterizedSparqlString sparqlstr = new ParameterizedSparqlString("select * where{ ?s ?property ?object } limit 10");
//        sparqlstr.setIri("some_parameter", "");

        URL queryURL = new URL("http://210.28.132.62:8891/sparql?default-graph-uri=&query=" + URLEncoder.encode(sparqlstr.toString(), "UTF-8") + "&format=xml%2Fhtml&timeout=0&debug=on");
        System.out.println(queryURL.toString());

        URLConnection connAPI = queryURL.openConnection();
        connAPI.setConnectTimeout(20000);
        connAPI.connect();

        ResultSet rs = ResultSetFactory.fromXML(connAPI.getInputStream());
        while (rs.hasNext()) {
            QuerySolution qs = rs.next();
            System.out.println("ok");
            System.out.println(qs.get("s"));
        }
    }
}
