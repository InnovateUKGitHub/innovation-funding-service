package com.worth.ifs.application.transactional;

import com.worth.ifs.application.domain.ApplicationStatus;
import com.worth.ifs.application.repository.ApplicationStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApplicationStatusServiceImpl implements ApplicationStatusService {
    @Autowired
    private ApplicationStatusRepository applicationStatusRepository;

    @Override
    public ApplicationStatus getById(Long id) {
        return applicationStatusRepository.findOne(id);
    }
}
