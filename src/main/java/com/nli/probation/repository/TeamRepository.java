package com.nli.probation.repository;

import com.nli.probation.entity.TeamEntity;
import com.nli.probation.model.team.TeamModel;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

public interface TeamRepository extends CrudRepository<TeamEntity, TeamModel>, JpaSpecificationExecutor<TeamEntity> {
    boolean existsByName(String name);
    boolean existsByShortName(String shortName);
}
