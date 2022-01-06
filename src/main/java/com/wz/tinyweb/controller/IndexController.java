package com.wz.tinyweb.controller;

import com.alibaba.fastjson.JSONObject;
import com.wz.tinyweb.core.RequestMapping;

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

    @RequestMapping("/annotation")
    public String annotation(HttpServletRequest request){
        return request.toString();
    }

}
