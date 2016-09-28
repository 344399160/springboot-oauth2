package com.scistor.tab.auth.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.scistor.tab.auth.common.Constants;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Wen Senlin
 */
@Entity
@Table(name = "Resource_pools")
public class ResourcePool {
    @Id
    private String id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String queueName;
    
    @OneToOne
    @NotFound(action= NotFoundAction.IGNORE)
    private Group group;
    
    @Column(nullable = false)
    private Integer cpuNum;
    
    @Column(nullable = false)
    private Long memSize;
    
    @Column(nullable = false)
    private Date lastUpateTime;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCpuNum() {
        return cpuNum;
    }

    public void setCpuNum(Integer cpuNum) {
        this.cpuNum = cpuNum;
    }

    public Long getMemSize() {
        return memSize;
    }

    public void setMemSize(Long memSize) {
        this.memSize = memSize;
    }
    
    @JsonIgnore
    public Date getLastUpateTime() {
        return lastUpateTime;
    }

    public void setLastUpateTime(Date lastUpateTime) {
        this.lastUpateTime = lastUpateTime;
    }
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQueueName() {
		return queueName;
	}

	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}

	public String getGroupName() {
	    if (group == null) {
            return null;
        }
		return group.getName();
	}

	public void setGroup(Group group) {
		this.group = group;
	}
	
	public static final ResourcePool defaultResourcePool() {
		ResourcePool resourcePool = new ResourcePool();
		resourcePool.setId("-1");
		resourcePool.setName(Constants.DEFAULT_ADMIN_NAME);
		resourcePool.setQueueName(Constants.DEFAULT_QUEUE_NAME);
		resourcePool.setLastUpateTime(new Date());
		resourcePool.setCpuNum(0);
		resourcePool.setMemSize(0L);
		return resourcePool;
    }

	@Override
    public String toString() {
        return "ResourcePool [id=" + id + ", name=" + name + ", cpuNum=" + cpuNum + ", memSize=" + memSize + ", lastUpateTime=" + lastUpateTime + "]";
    } 
}

