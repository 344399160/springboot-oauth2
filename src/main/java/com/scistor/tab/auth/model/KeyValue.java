package com.scistor.tab.auth.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Wei Xing
 */
@Data
@Entity
@Table(name = "`KEYVALUE`")
@NoArgsConstructor
@AllArgsConstructor
public class KeyValue {
    public static final int MAX_SIZE = 64 * 1024;

    @Id
    @Column(name="`KEY`")
    private String key;

    @Column(name="`VALUE`")
    public byte[] value;

}
