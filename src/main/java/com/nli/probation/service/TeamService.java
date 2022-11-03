package com.nli.probation.service;

import com.nli.probation.constant.EntityStatusEnum;
import com.nli.probation.customexception.DuplicatedEntityException;
import com.nli.probation.customexception.NoSuchEntityException;
import com.nli.probation.entity.TeamEntity;
import com.nli.probation.model.team.CreateTeamModel;
import com.nli.probation.model.team.TeamModel;
import com.nli.probation.model.team.UpdateTeamModel;
import com.nli.probation.repository.TeamRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

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

}
