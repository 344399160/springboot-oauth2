package com.scistor.tab.designer.service;

import com.scistor.tab.designer.model.Sample;

import java.util.List;

/**
 * 描述：Sample业务处理类
 * author qiaobin   2016/9/28 10:36.
 */
public interface SampleService {

    /**
      * 功能描述：根据名称获取Sample
      * @author qiaobin
      * @date 2016/9/28  10:37
     * @param name 名称
     */
    public List<Sample> getSample(String name);
}
