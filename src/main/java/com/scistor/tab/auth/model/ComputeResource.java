package com.scistor.tab.auth.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author Wen Senlin
 */
@Entity
@Table(name = "compute_resources")
public class ComputeResource {
    @Id
    private String id;
    
    @Column(nullable = false)
    private Integer totalCpuNum;

    private Integer useCpuNum;
    
    private Integer freeCpuNum;
    
    @Column(nullable = false)
    private Long totalMemSize;
    
    private Long useMemSize;
    
    private Long freeMemSize;
    
    @Column(nullable = false)
    private Date lastUpateTime;

    public Integer getTotalCpuNum() {
        return totalCpuNum;
    }

    public void setTotalCpuNum(Integer totalCpuNum) {
        this.totalCpuNum = totalCpuNum;
    }

    public Integer getUseCpuNum() {
        return useCpuNum;
    }

    public void setUseCpuNum(Integer useCpuNum) {
        this.useCpuNum = useCpuNum;
    }

    public Integer getFreeCpuNum() {
        return freeCpuNum;
    }

    public void setFreeCpuNum(Integer freeCpuNum) {
        this.freeCpuNum = freeCpuNum;
    }

    public Long getTotalMemSize() {
        return totalMemSize;
    }

    public void setTotalMemSize(Long totalMemSize) {
        this.totalMemSize = totalMemSize;
    }

    public Long getUseMemSize() {
        return useMemSize;
    }

    public void setUseMemSize(Long useMemSize) {
        this.useMemSize = useMemSize;
    }

    public Long getFreeMemSize() {
        return freeMemSize;
    }

    public void setFreeMemSize(Long freeMemSize) {
        this.freeMemSize = freeMemSize;
    }

    @JsonIgnore
    public Date getLastUpateTime() {
        return lastUpateTime;
    }

    public void setLastUpateTime(Date lastUpateTime) {
        this.lastUpateTime = lastUpateTime;
    }

    @JsonIgnore
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "CalcResource [totalCpuNum=" + totalCpuNum + ", useCpuNum=" + useCpuNum + ", freeCpuNum=" + freeCpuNum + ", totalMemSize=" + totalMemSize + ", useMemSize=" + useMemSize
                + ", freeMemSize=" + freeMemSize + "]";
    }
}

