package com.scistor.tab.auth.repository;

import com.scistor.tab.auth.model.Group;
import com.scistor.tab.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

/**
 * @author Wei Xing
 */
public interface UserRepository extends JpaSpecificationExecutor<User>, JpaRepository<User, String>, UserDetailsService {

    public User findByUsername(String username);
    
    public List<User> findByGroup(Group group);
    
    @Query("SELECT u FROM User u WHERE u.username = ?1")
    public User loadUserByUsername(String username);
}
