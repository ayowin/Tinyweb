package com.wz.tinyweb.controller;

import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletRequest;

public class IndexController {

    public String index(HttpServletRequest request){
        return "index";
    }

    public String test(JSONObject request){
        return "test";
    }
}
