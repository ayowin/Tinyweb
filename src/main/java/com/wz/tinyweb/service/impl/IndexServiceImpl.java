package com.wz.tinyweb.service.impl;

import com.wz.tinyweb.core.Autowired;
import com.wz.tinyweb.core.Inject;
import com.wz.tinyweb.mapper.IndexMapper;
import com.wz.tinyweb.service.IndexService;

@Inject
public class IndexServiceImpl implements IndexService {

    @Autowired
    IndexMapper indexMapper;

    @Override
    public String select() {
        return indexMapper.select();
    }

}
