package com.wz.tinyweb.controller;

import com.alibaba.fastjson.JSONObject;
import com.wz.tinyweb.core.Autowired;
import com.wz.tinyweb.core.Inject;
import com.wz.tinyweb.core.RequestMapping;
import com.wz.tinyweb.service.IndexService;

import javax.servlet.http.HttpServletRequest;

@RequestMapping("/index")
@Inject
public class IndexController {

    @Autowired
    IndexService indexService;

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

    @RequestMapping("/select")
    public String select(HttpServletRequest request){
        return indexService.select();
    }
}
