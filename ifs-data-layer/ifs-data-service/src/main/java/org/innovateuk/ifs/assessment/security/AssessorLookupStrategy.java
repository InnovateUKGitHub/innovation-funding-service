package org.innovateuk.ifs.assessment.security;

import org.innovateuk.ifs.assessment.mapper.AssessorProfileMapper;
import org.innovateuk.ifs.assessment.resource.AssessorProfileResource;
import org.innovateuk.ifs.assessment.resource.ProfileResource;
import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategies;
import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategy;
import org.innovateuk.ifs.form.domain.FormValidator;
import org.innovateuk.ifs.profile.repository.ProfileRepository;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.mapper.UserMapper;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

@Component
@PermissionEntityLookupStrategies
public class AssessorLookupStrategy {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AssessorProfileMapper assessorProfileMapper;

    private UserResource userResource;

    @PermissionEntityLookupStrategy
    public AssessorProfileResource getAssessorProfile(final Long assessorId) {

        UserResource userResource = userMapper.mapToResource(userRepository.findOne(assessorId));
        ProfileResource profileResource = assessorProfileMapper.mapToResource(profileRepository.findOne(assessorId));

         return new AssessorProfileResource(userResource, profileResource);
    }

}
