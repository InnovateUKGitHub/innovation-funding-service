package org.innovateuk.ifs.assessment.security;

import org.innovateuk.ifs.assessment.mapper.AssessorFormInputResponseMapper;
import org.innovateuk.ifs.assessment.repository.AssessorFormInputResponseRepository;
import org.innovateuk.ifs.assessment.resource.AssessorFormInputResponseResource;
import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategies;
import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Lookup strategy for {@link org.innovateuk.ifs.assessment.domain.AssessorFormInputResponse}, used for permissioning.
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
