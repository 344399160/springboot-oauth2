package com.scistor.tab.auth.repository;


import com.scistor.tab.auth.model.ResourcePool;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author Wen Senlin
 */
public interface ResourcePoolRepository extends JpaSpecificationExecutor<ResourcePool>, JpaRepository<ResourcePool, String> {

    ResourcePool findByName(String name);
}
