package com.nli.probation.repository;

import com.nli.probation.entity.UserAccountEntity;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

public interface UserAccountRepository extends CrudRepository<UserAccountEntity, Integer>, JpaSpecificationExecutor<UserAccountEntity> {
    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);
}