package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.mapper.TermsAndConditionsMapper;
import org.innovateuk.ifs.competition.repository.TermsAndConditionsRepository;
import org.innovateuk.ifs.competition.resource.TermsAndConditionsResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;

/**
 * Service for operations around the usage and processing of TermsAndConditions
 */
@Service
public class TermsAndConditionsServiceImpl implements TermsAndConditionsService {

    @Autowired
    TermsAndConditionsRepository termsAndConditionsRepository;

    @Autowired
    TermsAndConditionsMapper termsAndConditionsMapper;

    @Override
    public ServiceResult<List<TermsAndConditionsResource>> getLatestTermsAndConditions() {
        return serviceSuccess((List<TermsAndConditionsResource>)
                termsAndConditionsMapper.mapToResource(termsAndConditionsRepository.findAll()));
    }
}
