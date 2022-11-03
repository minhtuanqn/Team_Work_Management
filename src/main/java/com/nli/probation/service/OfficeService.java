package com.nli.probation.service;

import com.nli.probation.constant.EntityStatusEnum;
import com.nli.probation.customexception.DuplicatedEntityException;
import com.nli.probation.entity.OfficeEntity;
import com.nli.probation.model.office.CreateOfficeModel;
import com.nli.probation.model.office.OfficeModel;
import com.nli.probation.repository.OfficeRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

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
}
