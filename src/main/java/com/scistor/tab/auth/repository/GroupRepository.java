package com.scistor.tab.auth.repository;

import com.scistor.tab.auth.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author Wei Xing
 */
public interface GroupRepository extends JpaSpecificationExecutor<Group>, JpaRepository<Group, String> {

    public Group findByName(String name);

}
