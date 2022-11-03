package com.nli.probation.service;

import com.nli.probation.constant.EntityStatusEnum;
import com.nli.probation.converter.PaginationConverter;
import com.nli.probation.customexception.DuplicatedEntityException;
import com.nli.probation.customexception.NoSuchEntityException;
import com.nli.probation.entity.OfficeEntity;
import com.nli.probation.entity.TeamEntity;
import com.nli.probation.metamodel.OfficeEntity_;
import com.nli.probation.metamodel.TeamEntity_;
import com.nli.probation.model.RequestPaginationModel;
import com.nli.probation.model.ResourceModel;
import com.nli.probation.model.office.OfficeModel;
import com.nli.probation.model.team.CreateTeamModel;
import com.nli.probation.model.team.TeamModel;
import com.nli.probation.model.team.UpdateTeamModel;
import com.nli.probation.repository.TeamRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TeamService {
    private TeamRepository teamRepository;
    private ModelMapper modelMapper;

    public TeamService(TeamRepository teamRepository,
                       ModelMapper modelMapper) {
        this.teamRepository = teamRepository;
        this.modelMapper = modelMapper;
    }

    /**
     * Create new team
     * @param createTeamModel
     * @return saved team
     */
    public TeamModel createTeam(CreateTeamModel createTeamModel) {
        //Check exist team name
        if (teamRepository.existsByName(createTeamModel.getName()))
            throw new DuplicatedEntityException("Duplicated name of team");

        //Check exist short name
        if (teamRepository.existsByShortName(createTeamModel.getShortName()))
            throw new DuplicatedEntityException("Duplicated short name of team");

        //Prepare saved entity
        TeamEntity teamEntity = modelMapper.map(createTeamModel, TeamEntity.class);
        teamEntity.setStatus(EntityStatusEnum.TeamStatusEnum.ACTIVE.ordinal());

        //Save entity to DB
        TeamEntity savedEntity = teamRepository.save(teamEntity);
        TeamModel responseTeamModel = modelMapper.map(savedEntity, TeamModel.class);

        return responseTeamModel;
    }

    /**
     * Find team by id
     * @param id
     * @return found team
     */
    public TeamModel findTeamById(int id) {
        //Find team by id
        Optional<TeamEntity> searchedTeamOptional = teamRepository.findById(id);
        TeamEntity teamEntity = searchedTeamOptional.orElseThrow(() -> new NoSuchEntityException("Not found team"));
        return modelMapper.map(teamEntity, TeamModel.class);
    }

    /**
     * Delete a team
     * @param id
     * @return deleted model
     */
    public TeamModel deleteTeamById(int id) {
        //Find team by id
        Optional<TeamEntity> deletedTeamOptional = teamRepository.findById(id);
        TeamEntity deletedTeamEntity = deletedTeamOptional.orElseThrow(() -> new NoSuchEntityException("Not found team with id"));

        //Set status for entity
        deletedTeamEntity.setStatus(EntityStatusEnum.TeamStatusEnum.DISABLE.ordinal());

        //Save entity to DB
        TeamEntity responseEntity = teamRepository.save(deletedTeamEntity);
        return modelMapper.map(responseEntity, TeamModel.class);
    }

    /**
     * Update team information
     * @param updateTeamModel
     * @return updated team
     */
    public TeamModel updateTeam (UpdateTeamModel updateTeamModel) {
        //Find team by id
        Optional<TeamEntity> foundTeamOptional = teamRepository.findById(updateTeamModel.getId());
        TeamEntity foundTeamEntity = foundTeamOptional.orElseThrow(() -> new NoSuchEntityException("Not found team with id"));

        //Check existed team with name
        if(teamRepository.existsByNameAndIdNot(updateTeamModel.getName(), updateTeamModel.getId()))
            throw new DuplicatedEntityException("Duplicate name for team");

        //Check existed team with short name
        if(teamRepository.existsByShortNameAndIdNot(updateTeamModel.getShortName(), updateTeamModel.getId()))
            throw new DuplicatedEntityException("Duplicate short name for team");

        //Save entity to database
        TeamEntity savedEntity = teamRepository.save(modelMapper.map(updateTeamModel, TeamEntity.class));
        return modelMapper.map(savedEntity, TeamModel.class);
    }

    /**
     * Specification for search name
     * @param searchValue
     * @return specification
     */
    private Specification<TeamEntity> containsName(String searchValue) {
        return ((root, query, criteriaBuilder) -> {
            String pattern = searchValue != null ? "%" + searchValue + "%" : "%%";
            return criteriaBuilder.like(root.get(TeamEntity_.NAME), pattern);
        });
    }

    /**
     * Specification for search short name
     * @param searchValue
     * @return specification
     */
    private Specification<TeamEntity> containsShortName(String searchValue) {
        return ((root, query, criteriaBuilder) -> {
            String pattern = searchValue != null ? "%" + searchValue + "%" : "%%";
            return criteriaBuilder.like(root.get(TeamEntity_.SHORT_NAME), pattern);
        });
    }

    /**
     * Search team like name or short name
     * @param searchValue
     * @param paginationModel
     * @return resource of data
     */
    public ResourceModel<TeamModel> searchTeams(String searchValue, RequestPaginationModel paginationModel) {
        PaginationConverter<TeamModel, TeamEntity> paginationConverter = new PaginationConverter<>();

        //Build pageable
        String defaultSortBy = TeamEntity_.ID;
        Pageable pageable = paginationConverter.convertToPageable(paginationModel, defaultSortBy, TeamEntity.class);

        //Find all teams
        Page<TeamEntity> teamEntityPage = teamRepository.findAll(containsName(searchValue)
                .and(containsShortName(searchValue)), pageable);

        //Convert list of offices entity to list of offices model
        List<TeamModel> teamModels = new ArrayList<>();
        for(TeamEntity entity : teamEntityPage) {
            teamModels.add(modelMapper.map(entity, TeamModel.class));
        }

        //Prepare resource for return
        ResourceModel<TeamModel> resourceModel = new ResourceModel<>();
        resourceModel.setData(teamModels);
        resourceModel.setSearchText(searchValue);
        resourceModel.setSortBy(defaultSortBy);
        resourceModel.setSortType(paginationModel.getSortType());
        paginationConverter.buildPagination(paginationModel, teamEntityPage, resourceModel);
        return resourceModel;
    }

}
