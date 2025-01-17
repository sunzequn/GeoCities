package com.sunzequn.geo.data.algorithm.location.distance;

/**
 * Created by Sloriac on 16/2/5.
 * <p>
 * 经纬度相关计算
 */
public class LongLatCalculator {

    /**
     * 根据经纬度计算两点之间的距离,使用haversine公式
     *
     * @param lat1 第一个点的纬度(角度)
     * @param lon1 第一个点的经度(角度)
     * @param lat2 第二个点的纬度(角度)
     * @param lon2 第二个点的经度(角度)
     * @return 两个点之间的距离, 单位是米
     */
    public double distance(double lat1, double lon1, double lat2, double lon2) {
        return HaversineDis.distance(lat1, lon1, lat2, lon2);
    }

    /**
     * 判断两个点在设定的距离阈值下是否相近
     *
     * @param lat1      第一个点的纬度(角度)
     * @param lon1      第一个点的经度(角度)
     * @param lat2      第二个点的纬度(角度)
     * @param lon2      第二个点的经度(角度)
     * @param threshold 设定的距离阈值,单位是米
     * @return 如果两点距离小于或等于设定的阈值, 则返回真, 否则返回假
     */
    public boolean isNear(double lat1, double lon1, double lat2, double lon2, double threshold) {
        double dis = HaversineDis.distance(lat1, lon1, lat2, lon2);
        if (dis <= threshold) {
            return true;
        }
        System.out.println("dis : " + dis);
        return false;
    }


}
