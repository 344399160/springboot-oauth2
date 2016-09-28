package com.scistor.tab.auth.model;

import com.scistor.tab.auth.annotations.JsonDefaultDateFormat;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Wei Xing
 */
@Entity
@Table(name = "groups")
public class Group {
    public static final String UNASSIGNED_ID = "-1";
    public static final String UNASSIGNED = "UNASSIGNED";
    @Id
    private String id;

    @Column(nullable = false, unique = true)
    private String name;
    
    //创建时间
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date createTime;
    
    //创建者
    private String userName;
    
    //资源池
    @OneToOne
    @NotFound(action= NotFoundAction.IGNORE)
    private ResourcePool resourcePool;
    
    @Column(nullable = true)
    private String description;

    public Group() {
        this(UNASSIGNED_ID, UNASSIGNED);
    }

    public Group(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Group setName(String name) {
        this.name = name;
        return this;
    }

    public String getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Group setId(String id) {
        this.id = id;
        return this;
    }

    public String getResourcePoolName() {
        if (resourcePool == null) {
            return null;
        }
        return resourcePool.getName();
    }
    
    public String getResourcePoolQueueName() {
        if (resourcePool == null) {
            return null;
        }
        return resourcePool.getQueueName();
    }
    
    public void setResourcePool(ResourcePool resourcePool) {
        this.resourcePool = resourcePool;
    }
    
    @JsonDefaultDateFormat
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
    
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    public ResourcePool getResourcePool() {
		return resourcePool;
	}
    
	private static final Group DEFAULT_GROUP = new Group() {
        @Override
        public Group setId(String id) {
            return new Group(id, this.getName());
        }

        @Override
        public Group setName(String name) {
            return new Group(this.getId(), name);
        }
    };

    public static final Group defaultGroup() {
        return DEFAULT_GROUP;
    }
}
