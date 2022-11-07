package com.nli.probation.service;

import com.nli.probation.constant.EntityStatusEnum;
import com.nli.probation.customexception.DuplicatedEntityException;
import com.nli.probation.customexception.NoSuchEntityException;
import com.nli.probation.entity.RoleEntity;
import com.nli.probation.model.role.CreateRoleModel;
import com.nli.probation.model.role.RoleModel;
import com.nli.probation.repository.RoleRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RoleService {
    private RoleRepository roleRepository;
    private ModelMapper modelMapper;

    public RoleService(RoleRepository roleRepository,
                       ModelMapper modelMapper) {
        this.roleRepository = roleRepository;
        this.modelMapper = modelMapper;
    }

    /**
     * Create new role
     * @param createRoleModel
     * @return saved role
     */
    public RoleModel createRole(CreateRoleModel createRoleModel) {
        //Check exist role name
        if (roleRepository.existsByName(createRoleModel.getName()))
            throw new DuplicatedEntityException("Duplicated name of role");

        //Check exist short name
        if (roleRepository.existsByShortName(createRoleModel.getShortName()))
            throw new DuplicatedEntityException("Duplicated short name of role");

        //Prepare saved entity
        RoleEntity roleEntity = modelMapper.map(createRoleModel, RoleEntity.class);
        roleEntity.setStatus(EntityStatusEnum.RoleStatusEnum.ACTIVE.ordinal());

        //Save entity to DB
        RoleEntity savedEntity = roleRepository.save(roleEntity);
        RoleModel responseRoleModel = modelMapper.map(savedEntity, RoleModel.class);

        return responseRoleModel;
    }

    /**
     * Find rolw by id
     * @param id
     * @return found role
     */
    public RoleModel findRoleById(int id) {
        //Find role by id
        Optional<RoleEntity> searchedRoleOptional = roleRepository.findById(id);
        RoleEntity roleEntity = searchedRoleOptional.orElseThrow(() -> new NoSuchEntityException("Not found role"));
        return modelMapper.map(roleEntity, RoleModel.class);
    }
}
