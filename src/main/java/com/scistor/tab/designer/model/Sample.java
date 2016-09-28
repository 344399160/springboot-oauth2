package com.scistor.tab.designer.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 描述：示范实体
 * author qiaobin   2016/9/28 10:31.
 */

@Data  //生成Getter Setter
@Entity
@Table(name = "`SAMPLE`")
@AllArgsConstructor  //全参构造
@NoArgsConstructor   //无参构造
public class Sample {

    /**
    *  主键
    */
    @Id
    private String id;

    /**
    *  名称
    */
    @Column(name="`NAME`")
    private String name;
}
