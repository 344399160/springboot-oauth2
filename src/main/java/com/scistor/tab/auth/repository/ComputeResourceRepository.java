package com.scistor.tab.auth.repository;


import com.scistor.tab.auth.model.ComputeResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author Wen Senlin
 */
public interface ComputeResourceRepository extends JpaSpecificationExecutor<ComputeResource>, JpaRepository<ComputeResource, String> {

    ComputeResource findById(String id);

}
