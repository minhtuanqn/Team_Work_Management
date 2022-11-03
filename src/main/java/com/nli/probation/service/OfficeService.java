package com.nli.probation.service;

import com.nli.probation.constant.EntityStatusEnum;
import com.nli.probation.customexception.DuplicatedEntityException;
import com.nli.probation.customexception.NoSuchEntityException;
import com.nli.probation.entity.OfficeEntity;
import com.nli.probation.model.office.CreateOfficeModel;
import com.nli.probation.model.office.OfficeModel;
import com.nli.probation.model.office.UpdateOfficeModel;
import com.nli.probation.repository.OfficeRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OfficeService {
    private OfficeRepository officeRepository;
    private ModelMapper modelMapper;

    public OfficeService(OfficeRepository officeRepository,
                         ModelMapper modelMapper) {
        this.officeRepository = officeRepository;
        this.modelMapper = modelMapper;
    }

    /**
     * Create new office
     * @param createOfficeModel
     * @return saved office
     */
    public OfficeModel createOffice(CreateOfficeModel createOfficeModel) {
        //Check exist office
        if (officeRepository.existsByName(createOfficeModel.getName()))
            throw new DuplicatedEntityException("Duplicated name of office");

        //Prepare saved entity
        OfficeEntity officeEntity = modelMapper.map(createOfficeModel, OfficeEntity.class);
        officeEntity.setStatus(EntityStatusEnum.OfficeStatusEnum.ACTIVE.ordinal());

        //Save entity to DB
        OfficeEntity savedEntity = officeRepository.save(officeEntity);
        OfficeModel responseOfficeModel = modelMapper.map(savedEntity, OfficeModel.class);

        return responseOfficeModel;
    }

    /**
     * Find office by id
     * @param id
     * @return found office
     */
    public OfficeModel findOfficeById(int id) {
        //Find office by id
        Optional<OfficeEntity> searchedOfficeOptional = officeRepository.findById(id);
        OfficeEntity officeEntity = searchedOfficeOptional.orElseThrow(() -> new NoSuchEntityException("Not found office"));
        return modelMapper.map(officeEntity, OfficeModel.class);
    }

    /**
     * Delete a office
     * @param id
     * @return deleted model
     */
    public OfficeModel deleteOfficeById(int id) {
        //Find office by id
        Optional<OfficeEntity> deletedOfficeOptional = officeRepository.findById(id);
        OfficeEntity deletedOfficeEntity = deletedOfficeOptional.orElseThrow(() -> new NoSuchEntityException("Not found office with id"));

        //Set status for entity
        deletedOfficeEntity.setStatus(EntityStatusEnum.OfficeStatusEnum.DISABLE.ordinal());

        //Save entity to DB
        OfficeEntity responseEntity = officeRepository.save(deletedOfficeEntity);
        return modelMapper.map(responseEntity, OfficeModel.class);
    }

    /**
     * Update office information
     * @param updateOfficeModel
     * @return updated office
     */
    public OfficeModel updateOffice (UpdateOfficeModel updateOfficeModel) {
        //Find office by id
        Optional<OfficeEntity> foundOfficeOptional = officeRepository.findById(updateOfficeModel.getId());
        OfficeEntity foundOfficeEntity = foundOfficeOptional.orElseThrow(() -> new NoSuchEntityException("Not found office with id"));

        //Check existed office with name
        if(officeRepository.existsByNameAndIdNot(updateOfficeModel.getName(), updateOfficeModel.getId()))
            throw new DuplicatedEntityException("Duplicate name for office");

        //Save entity to database
        OfficeEntity savedEntity = officeRepository.save(modelMapper.map(updateOfficeModel, OfficeEntity.class));
        return modelMapper.map(savedEntity, OfficeModel.class);

    }
}
