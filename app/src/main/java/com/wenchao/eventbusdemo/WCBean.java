package com.wenchao.eventbusdemo;

/**
 * @author wenchao
 * @date 2019/7/7.
 * @time 18:27
 * description：
 */
public class WCBean {

    private String one;
    private String two;

    public WCBean(String one, String two) {
        this.one = one;
        this.two = two;
    }

    public String getOne() {
        return one;
    }

    public void setOne(String one) {
        this.one = one;
    }

    public String getTwo() {
        return two;
    }

    public void setTwo(String two) {
        this.two = two;
    }

    @Override
    public String toString() {
        return "WCBean{" +
                "one='" + one + '\'' +
                ", two='" + two + '\'' +
                '}';
    }
}
