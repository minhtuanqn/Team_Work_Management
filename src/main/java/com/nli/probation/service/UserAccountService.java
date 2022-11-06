package com.nli.probation.service;

import com.nli.probation.constant.EntityStatusEnum;
import com.nli.probation.customexception.DuplicatedEntityException;
import com.nli.probation.customexception.NoSuchEntityException;
import com.nli.probation.entity.OfficeEntity;
import com.nli.probation.entity.TeamEntity;
import com.nli.probation.entity.UserAccountEntity;
import com.nli.probation.model.office.OfficeModel;
import com.nli.probation.model.team.TeamModel;
import com.nli.probation.model.useraccount.CreateUserAccountModel;
import com.nli.probation.model.useraccount.UserAccountModel;
import com.nli.probation.repository.OfficeRepository;
import com.nli.probation.repository.TeamRepository;
import com.nli.probation.repository.UserAccountRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserAccountService {
    private UserAccountRepository userAccountRepository;
    private TeamRepository teamRepository;
    private OfficeRepository officeRepository;
    private ModelMapper modelMapper;


    public UserAccountService(UserAccountRepository userAccountRepository,
                              TeamRepository teamRepository,
                              OfficeRepository officeRepository,
                              ModelMapper modelMapper) {
        this.userAccountRepository = userAccountRepository;
        this.teamRepository = teamRepository;
        this.officeRepository = officeRepository;
        this.modelMapper = modelMapper;
    }

    /**
     * Create new user account
     * @param createUserAccountModel
     * @return create user account
     */
    public UserAccountModel createUserAccount(CreateUserAccountModel createUserAccountModel) {
        //Check exist email
        if (userAccountRepository.existsByEmail(createUserAccountModel.getEmail()))
            throw new DuplicatedEntityException("Duplicated email of user account");

        //Check exist phone
        if(userAccountRepository.existsByPhone(createUserAccountModel.getPhone()))
            throw new DuplicatedEntityException("Duplicated phone of user account");

        //Check exist team
        Optional<TeamEntity> existedTeamOptional = teamRepository.findById(createUserAccountModel.getTeamId());
        TeamEntity existedTeamEntity = existedTeamOptional
                .orElseThrow(() -> new NoSuchEntityException("Not found team"));

        //Check exist office
        Optional<OfficeEntity> existedOfficeOptional = officeRepository.findById(createUserAccountModel.getOfficeId());
        OfficeEntity existedOfficeEntity = existedOfficeOptional
                .orElseThrow(() -> new NoSuchEntityException("Not found office"));

        //Prepare saved entity
        UserAccountEntity userAccountEntity = modelMapper.map(createUserAccountModel, UserAccountEntity.class);
        userAccountEntity.setStatus(EntityStatusEnum.UserAccountStatusEnum.ACTIVE.ordinal());

        //Save entity to DB
        UserAccountEntity savedEntity = userAccountRepository.save(userAccountEntity);
        UserAccountModel responseUserAccountModel = modelMapper.map(savedEntity, UserAccountModel.class);
        responseUserAccountModel.setOfficeModel(modelMapper.map(existedOfficeEntity, OfficeModel.class));
        responseUserAccountModel.setTeamModel(modelMapper.map(existedTeamEntity, TeamModel.class));

        return responseUserAccountModel;
    }
}
