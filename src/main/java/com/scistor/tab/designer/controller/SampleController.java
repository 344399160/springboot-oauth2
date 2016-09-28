package com.scistor.tab.designer.controller;

import com.scistor.tab.designer.model.Sample;
import com.scistor.tab.designer.service.SampleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 描述：左侧报表列表
 * author qiaobin   2016/8/29 16:23.
 */
@RestController
@RequestMapping("sample")
public class SampleController {

    @Autowired
    private SampleService sampleService;

    /**
    *   数据库读取示范
    * */
    @RequestMapping(value = "/report/{name}", method = RequestMethod.GET)
    public List<Sample> findByName(@PathVariable("name") String name){
        return sampleService.getSample(name);
    }

}
