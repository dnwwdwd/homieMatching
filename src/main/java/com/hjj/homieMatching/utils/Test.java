package com.hjj.homieMatching.utils;

import com.hjj.homieMatching.model.domain.User;

public class Test {

    public static void main(String[] args) {
        User xiaoli = new User();
        xiaoli.setUsername("xiaoli");
        User xiaoli1 = new User();
        xiaoli1.setUsername("xiaoli");
        System.out.println(xiaoli1 == xiaoli);
        System.out.println(xiaoli1.equals(xiaoli));
        Integer one = new Integer(1);
        Integer one1 = new Integer(1);
        System.out.println(one.equals(one1));
    }
}
