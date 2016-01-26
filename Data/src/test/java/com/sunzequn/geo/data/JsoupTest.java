package com.sunzequn.geo.data;

import com.sunzequn.geo.data.utils.WriteUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Sloriac on 16/1/20.
 */
public class JsoupTest {

    public static void main(String[] args) throws IOException {
//        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("183.130.91.203", 9000));
//
//        URL url = new URL("http://sws.geonames.org/3020251/nearby.rdf");
//        HttpURLConnection action = (HttpURLConnection) url.openConnection(proxy);
//        action.setConnectTimeout(1000);
////        action.setReadTimeout(1000);
//        InputStream in = action.getInputStream();
//
//        BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
//        StringBuilder sb = new StringBuilder();
//        String lin = System.getProperty("line.separator");
//        for (String temp = br.readLine(); temp != null; temp = br.readLine()) {
//
//            sb.append(temp + lin);
//
//        }
//
//        br.close();
//        in.close();
//        System.out.println(sb);
        String FILE = "Data/src/main/resources/data/test.txt";
        String str = "dfsdfsgsdfdsf";
//        str = new String(str.getBytes("Unicode"),"UTF-16");
        WriteUtils writeUtils = new WriteUtils(FILE, false);
        writeUtils.write(str);
        writeUtils.close();
        System.out.println(isContainsChinese(str));
        System.out.println(URLEncoder.encode(str));
    }

    public static boolean isContainsChinese(String str) {
        String regEx = "[\u4e00-\u9fa5]";
        Pattern pat = Pattern.compile(regEx);
        Matcher matcher = pat.matcher(str);
        boolean flg = false;
        if (matcher.find()) {
            flg = true;
        }
        return flg;
    }
}
