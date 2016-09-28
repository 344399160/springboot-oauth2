package com.scistor.tab.designer.service.impl;

import com.scistor.tab.designer.model.Sample;
import com.scistor.tab.designer.repository.SampleRepository;
import com.scistor.tab.designer.service.SampleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 描述：Sample业务实现类
 * author qiaobin   2016/9/28 10:39.
 */
@Service
public class SampleServiceImpl implements SampleService {

    @Autowired
    private SampleRepository sampleRepository;

    /**
     * 功能描述：根据名称获取Sample
     * @author qiaobin
     * @date 2016/9/28  10:37
     * @param name 名称
     */
    @Override
    public List<Sample> getSample(String name) {
        return sampleRepository.findByName(name);
    }

}
