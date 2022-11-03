package com.nli.probation.repository;

import com.nli.probation.entity.OfficeEntity;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface OfficeRepository extends CrudRepository<OfficeEntity, Integer>, JpaSpecificationExecutor<OfficeEntity> {
    boolean existsByName(String name);
}
