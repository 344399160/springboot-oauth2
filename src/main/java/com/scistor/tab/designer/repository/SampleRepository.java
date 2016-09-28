package com.scistor.tab.designer.repository;

import com.scistor.tab.designer.model.Sample;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 描述：Sample数据库查询类
 * author qiaobin   2016/9/28 10:34.
 */
@Transactional
public interface SampleRepository extends JpaRepository<Sample, Long> {

    /*
    * 按名称相似性检索
    * */
    @Query("SELECT R FROM Sample R WHERE NAME LIKE %:name%")
    List<Sample> findByName(@Param("name") String name);
}
