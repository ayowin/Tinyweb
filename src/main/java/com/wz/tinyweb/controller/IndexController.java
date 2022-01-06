package com.wz.tinyweb.controller;

import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletRequest;

public class IndexController {

    public String index(HttpServletRequest request){
        return request.toString();
    }

    public String string(String request){
        return request;
    }

    public String json(JSONObject request){
        return request.toJSONString();
    }

}
