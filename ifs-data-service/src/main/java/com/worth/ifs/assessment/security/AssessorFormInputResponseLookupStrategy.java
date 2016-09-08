package com.worth.ifs.assessment.security;

import com.worth.ifs.assessment.mapper.AssessorFormInputResponseMapper;
import com.worth.ifs.assessment.repository.AssessorFormInputResponseRepository;
import com.worth.ifs.assessment.resource.AssessorFormInputResponseResource;
import com.worth.ifs.security.PermissionEntityLookupStrategies;
import com.worth.ifs.security.PermissionEntityLookupStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Lookup strategy for {@link com.worth.ifs.assessment.domain.AssessorFormInputResponse}, used for permissioning.
 */
@Component
@PermissionEntityLookupStrategies
public class AssessorFormInputResponseLookupStrategy {

    @Autowired
    private AssessorFormInputResponseRepository assessorFormInputResponseRepository;

    @Autowired
    private AssessorFormInputResponseMapper assessorFormInputResponseMapper;

    @PermissionEntityLookupStrategy
    public AssessorFormInputResponseResource getAssessorFormInputResponseResource(Long id) {
        return assessorFormInputResponseMapper.mapToResource(assessorFormInputResponseRepository.findOne(id));
    }
}