package com.nli.probation.service;

import com.nli.probation.constant.EntityStatusEnum;
import com.nli.probation.converter.PaginationConverter;
import com.nli.probation.customexception.DuplicatedEntityException;
import com.nli.probation.customexception.NoSuchEntityException;
import com.nli.probation.entity.OfficeEntity;
import com.nli.probation.entity.TeamEntity;
import com.nli.probation.entity.UserAccountEntity;
import com.nli.probation.metamodel.OfficeEntity_;
import com.nli.probation.metamodel.TeamEntity_;
import com.nli.probation.metamodel.UserAccountEntity_;
import com.nli.probation.model.RequestPaginationModel;
import com.nli.probation.model.ResourceModel;
import com.nli.probation.model.office.OfficeModel;
import com.nli.probation.model.team.TeamModel;
import com.nli.probation.model.team.UpdateTeamModel;
import com.nli.probation.model.useraccount.CreateUserAccountModel;
import com.nli.probation.model.useraccount.UpdateUserAccountModel;
import com.nli.probation.model.useraccount.UserAccountModel;
import com.nli.probation.repository.OfficeRepository;
import com.nli.probation.repository.TeamRepository;
import com.nli.probation.repository.UserAccountRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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
        userAccountEntity.setOfficeEntity(existedOfficeEntity);
        userAccountEntity.setTeamEntity(existedTeamEntity);

        //Save entity to DB
        UserAccountEntity savedEntity = userAccountRepository.save(userAccountEntity);
        UserAccountModel responseUserAccountModel = modelMapper.map(savedEntity, UserAccountModel.class);
        responseUserAccountModel.setOfficeModel(modelMapper.map(existedOfficeEntity, OfficeModel.class));
        responseUserAccountModel.setTeamModel(modelMapper.map(existedTeamEntity, TeamModel.class));

        return responseUserAccountModel;
    }

    /**
     * Find user account by id
     * @param id
     * @return found user account
     */
    public UserAccountModel findUserAccountById(int id) {
        //Find user account by id
        Optional<UserAccountEntity> searchedAccountOptional = userAccountRepository.findById(id);
        UserAccountEntity userAccountEntity = searchedAccountOptional.orElseThrow(() -> new NoSuchEntityException("Not found user account"));
        UserAccountModel userAccountModel = modelMapper.map(userAccountEntity, UserAccountModel.class);
        userAccountModel.setTeamModel(modelMapper.map(userAccountEntity.getTeamEntity(), TeamModel.class));
        userAccountModel.setOfficeModel(modelMapper.map(userAccountEntity.getOfficeEntity(), OfficeModel.class));
        return userAccountModel;
    }

    /**
     * Delete a user account
     * @param id
     * @return deleted model
     */
    public UserAccountModel deleteUserAccountById(int id) {
        //Find user account by id
        Optional<UserAccountEntity> deletedAccountOptional = userAccountRepository.findById(id);
        UserAccountEntity deletedAccountEntity = deletedAccountOptional.orElseThrow(() -> new NoSuchEntityException("Not found user account with id"));

        //Set status for entity
        deletedAccountEntity.setStatus(EntityStatusEnum.UserAccountStatusEnum.DISABLE.ordinal());

        //Save entity to DB
        UserAccountEntity responseEntity = userAccountRepository.save(deletedAccountEntity);
        UserAccountModel userAccountModel = modelMapper.map(responseEntity, UserAccountModel.class);
        userAccountModel.setTeamModel(modelMapper.map(responseEntity.getTeamEntity(), TeamModel.class));
        userAccountModel.setOfficeModel(modelMapper.map(responseEntity.getOfficeEntity(), OfficeModel.class));
        return userAccountModel;
    }

    /**
     * Update user account information
     * @param updateUserAccountModel
     * @return updated user account
     */
    public UserAccountModel updateUserAccount (UpdateUserAccountModel updateUserAccountModel) {
        //Find user account by id
        Optional<UserAccountEntity> foundAccountOptional = userAccountRepository.findById(updateUserAccountModel.getId());
        UserAccountEntity foundAccountEntity = foundAccountOptional
                .orElseThrow(() -> new NoSuchEntityException("Not found user account with id"));

        //Check existed user account with email
        if(userAccountRepository.existsByEmailAndIdNot(updateUserAccountModel.getEmail(),
                updateUserAccountModel.getId()))
            throw new DuplicatedEntityException("Duplicate email for user account");

        //Check existed user account with phone
        if(userAccountRepository.existsByEmailAndIdNot(updateUserAccountModel.getPhone(),
                updateUserAccountModel.getId()))
            throw new DuplicatedEntityException("Duplicate phone for user account");

        //Check exist team
        Optional<TeamEntity> existedTeamOptional = teamRepository.findById(updateUserAccountModel.getTeamId());
        TeamEntity existedTeamEntity = existedTeamOptional
                .orElseThrow(() -> new NoSuchEntityException("Not found team"));

        //Check exist office
        Optional<OfficeEntity> existedOfficeOptional = officeRepository.findById(updateUserAccountModel.getOfficeId());
        OfficeEntity existedOfficeEntity = existedOfficeOptional
                .orElseThrow(() -> new NoSuchEntityException("Not found office"));

        //Prepare saved entity
        UserAccountEntity userAccountEntity = modelMapper.map(updateUserAccountModel, UserAccountEntity.class);
        userAccountEntity.setOfficeEntity(existedOfficeEntity);
        userAccountEntity.setTeamEntity(existedTeamEntity);

        //Save entity to DB
        UserAccountEntity savedEntity = userAccountRepository.save(userAccountEntity);
        UserAccountModel responseUserAccountModel = modelMapper.map(savedEntity, UserAccountModel.class);
        responseUserAccountModel.setOfficeModel(modelMapper.map(existedOfficeEntity, OfficeModel.class));
        responseUserAccountModel.setTeamModel(modelMapper.map(existedTeamEntity, TeamModel.class));

        return responseUserAccountModel;
    }

    /**
     * Specification for search name
     * @param searchValue
     * @return specification
     */
    private Specification<UserAccountEntity> containsName(String searchValue) {
        return ((root, query, criteriaBuilder) -> {
            String pattern = searchValue != null ? "%" + searchValue + "%" : "%%";
            return criteriaBuilder.like(root.get(UserAccountEntity_.NAME), pattern);
        });
    }

    /**
     * Specification for search email
     * @param searchValue
     * @return specification
     */
    private Specification<UserAccountEntity> containsEmail(String searchValue) {
        return ((root, query, criteriaBuilder) -> {
            String pattern = searchValue != null ? "%" + searchValue + "%" : "%%";
            return criteriaBuilder.like(root.get(UserAccountEntity_.EMAIL), pattern);
        });
    }

    /**
     * Search user account like name or email
     * @param searchValue
     * @param paginationModel
     * @return resource of data
     */
    public ResourceModel<UserAccountModel> searchAccounts(String searchValue, RequestPaginationModel paginationModel) {
        PaginationConverter<UserAccountModel, UserAccountEntity> paginationConverter = new PaginationConverter<>();

        //Build pageable
        String defaultSortBy = UserAccountEntity_.ID;
        Pageable pageable = paginationConverter.convertToPageable(paginationModel, defaultSortBy, UserAccountEntity.class);

        //Find all user accounts
        Page<UserAccountEntity> accountEntityPage = userAccountRepository.findAll(containsEmail(searchValue)
                .and(containsName(searchValue)), pageable);

        //Convert list of user accounts entity to list of user account model
        List<UserAccountModel> accountModels = new ArrayList<>();
        for(UserAccountEntity entity : accountEntityPage) {
            UserAccountModel userAccountModel = modelMapper.map(entity, UserAccountModel.class);
            userAccountModel.setTeamModel(modelMapper.map(entity.getTeamEntity(), TeamModel.class));
            userAccountModel.setOfficeModel(modelMapper.map(entity.getOfficeEntity(), OfficeModel.class));
            accountModels.add(userAccountModel);
        }

        //Prepare resource for return
        ResourceModel<UserAccountModel> resourceModel = new ResourceModel<>();
        resourceModel.setData(accountModels);
        resourceModel.setSearchText(searchValue);
        resourceModel.setSortBy(defaultSortBy);
        resourceModel.setSortType(paginationModel.getSortType());
        paginationConverter.buildPagination(paginationModel, accountEntityPage, resourceModel);
        return resourceModel;
    }
}
