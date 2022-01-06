package com.wz.tinyweb;

import com.wz.tinyweb.core.Tinyweb;

public class Application {

    public static void main(String[] args){
        Tinyweb tinyweb = Tinyweb.singleton();
        tinyweb.execute();
    }

}
