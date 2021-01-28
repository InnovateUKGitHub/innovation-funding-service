package org.innovateuk.ifs.assessment.security;

import org.innovateuk.ifs.assessment.mapper.AssessorProfileMapper;
import org.innovateuk.ifs.assessment.resource.AssessorProfileResource;
import org.innovateuk.ifs.assessment.resource.ProfileResource;
import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategies;
import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategy;
import org.innovateuk.ifs.profile.repository.ProfileRepository;
import org.innovateuk.ifs.user.mapper.UserMapper;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

        UserResource userResource = userMapper.mapToResource(userRepository.findById(assessorId).orElse(null));
        ProfileResource profileResource = assessorProfileMapper.mapToResource(profileRepository.findById(assessorId).orElse(null));

        if (userResource == null || profileResource == null){
            return null;
        }

        return new AssessorProfileResource(userResource, profileResource);
    }

}
