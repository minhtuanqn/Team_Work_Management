package com.nli.probation.repository;

import com.nli.probation.entity.OfficeEntity;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

public interface OfficeRepository extends CrudRepository<OfficeEntity, Integer>, JpaSpecificationExecutor<OfficeEntity> {
}
