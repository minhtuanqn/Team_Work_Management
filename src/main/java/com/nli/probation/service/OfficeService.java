package com.nli.probation.service;

import com.nli.probation.repository.OfficeRepository;
import org.springframework.stereotype.Service;

@Service
public class OfficeService {
    private OfficeRepository officeRepository;

    public OfficeService(OfficeRepository officeRepository) {
        this.officeRepository = officeRepository;
    }
}
