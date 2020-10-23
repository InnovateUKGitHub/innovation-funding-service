package org.innovateuk.ifs.supporter.mapper;

import org.innovateuk.ifs.supporter.domain.SupporterAssignment;
import org.innovateuk.ifs.supporter.resource.SupporterAssignmentResource;
import org.innovateuk.ifs.commons.mapper.BaseResourceMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.profile.domain.Profile;
import org.innovateuk.ifs.profile.repository.ProfileRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@Mapper(config = GlobalMapperConfig.class)
public abstract class SupporterAssignmentMapper extends BaseResourceMapper<SupporterAssignment, SupporterAssignmentResource> {

    @Autowired
    private ProfileRepository profileRepository;

    @Mappings({
            @Mapping(source = "participant.name", target = "userName"),
            @Mapping(source = "participant.email", target = "userEmail"),
            @Mapping(source = "supporterOutcome.comment", target = "comments"),
            @Mapping(source = "processState", target = "state"),
            @Mapping(source = "id", target = "assignmentId"),
    })
    @Override
    public abstract SupporterAssignmentResource mapToResource(SupporterAssignment domain);

    @AfterMapping
    public void setStagesOnDomain(@MappingTarget SupporterAssignmentResource resource, SupporterAssignment domain) {
       Optional<Profile> profile = profileRepository.findById(domain.getParticipant().getProfileId());
        profile.ifPresent(value -> resource.setUserSimpleOrganisation(value.getSimpleOrganisation().getName()));
    }
}
