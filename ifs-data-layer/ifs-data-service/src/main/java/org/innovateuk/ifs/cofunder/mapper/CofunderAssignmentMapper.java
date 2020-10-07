package org.innovateuk.ifs.cofunder.mapper;

import org.innovateuk.ifs.cofunder.domain.CofunderAssignment;
import org.innovateuk.ifs.cofunder.resource.CofunderAssignmentResource;
import org.innovateuk.ifs.commons.mapper.BaseResourceMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.profile.domain.Profile;
import org.innovateuk.ifs.profile.repository.ProfileRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@Mapper(config = GlobalMapperConfig.class)
public abstract class CofunderAssignmentMapper extends BaseResourceMapper<CofunderAssignment, CofunderAssignmentResource> {

    @Autowired
    private ProfileRepository profileRepository;

    @Mappings({
            @Mapping(source = "participant.name", target = "userName"),
            @Mapping(source = "participant.email", target = "userEmail"),
            @Mapping(source = "cofunderOutcome.comment", target = "comments"),
            @Mapping(source = "processState", target = "state"),
    })
    @Override
    public abstract CofunderAssignmentResource mapToResource(CofunderAssignment domain);

    @AfterMapping
    public void setStagesOnDomain(@MappingTarget CofunderAssignmentResource resource, CofunderAssignment domain) {
       Optional<Profile> profile = profileRepository.findById(domain.getParticipant().getProfileId());
        profile.ifPresent(value -> resource.setUserSimpleOrganisation(value.getSimpleOrganisation().getName()));
    }
}
