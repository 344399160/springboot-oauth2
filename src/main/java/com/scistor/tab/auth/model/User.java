package com.scistor.tab.auth.model;


import com.google.common.collect.Sets;
import com.scistor.tab.auth.annotations.JsonDefaultDateFormat;
import com.scistor.tab.auth.util.Base64Utils;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

/**
 * @author Wei Xing
 */
@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    private String id;

    @Column(unique = true, nullable = false)
    private String username;

    private String password;

    @ManyToOne
    private Group group = Group.defaultGroup();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_authorities")
    @Column(name = "user_authority")
    @Enumerated(EnumType.STRING)
    private Set<Authority> authorities = Sets.newHashSet(Authority.USER);

    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModifiedTime;
    
    @Column(nullable = true)
    private String email;
    
    @Column(nullable = true)
    private String contact;

    public User() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = Base64Utils.encode(password);
    }

    public String getGroupId() {
        return group.getId();
    }

    public void setGroupId(String id) {
        this.group = this.group.setId(id);
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public String getGroupName() {
        return group.getName();
    }
    
    public String getResourcePoolQueueName() {
        return group.getResourcePoolQueueName();
    }

    public void setAuthorities(Set<Authority> authorities) {
        this.authorities = authorities;
    }

    @JsonDefaultDateFormat
    public Date getLastModifiedTime() {
        return lastModifiedTime;
    }

    public void setLastModifiedTime(Date lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }

    @JsonDefaultDateFormat
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String getPassword() {
        return Base64Utils.decode(password);
    }

    @Override
    public Set<Authority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }
}