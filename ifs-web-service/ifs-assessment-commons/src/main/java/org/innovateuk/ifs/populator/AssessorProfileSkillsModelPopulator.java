package org.innovateuk.ifs.populator;

import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.viewmodel.AssessorProfileDetailsViewModel;
import org.innovateuk.ifs.viewmodel.AssessorProfileSkillsViewModel;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.profile.service.ProfileRestService;
import org.innovateuk.ifs.user.resource.ProfileSkillsResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.*;

@Component
public class AssessorProfileSkillsModelPopulator {

    private AssessorProfileDetailsModelPopulator assessorProfileDetailsModelPopulator;
    private ProfileRestService profileRestService;

    public AssessorProfileSkillsModelPopulator(AssessorProfileDetailsModelPopulator assessorProfileDetailsModelPopulator,
                                               ProfileRestService profileRestService) {
        this.assessorProfileDetailsModelPopulator = assessorProfileDetailsModelPopulator;
        this.profileRestService = profileRestService;
    }

    public AssessorProfileSkillsViewModel populateModel(UserResource user, AddressResource addressResource) {

        AssessorProfileDetailsViewModel assessorProfileDetailsViewModel = assessorProfileDetailsModelPopulator.populateModel(user, addressResource);

        ProfileSkillsResource profileSkillsResource = profileRestService.getProfileSkills(user.getId()).getSuccess();

        return new AssessorProfileSkillsViewModel(
                assessorProfileDetailsViewModel,
                getInnovationAreasSectorMap(profileSkillsResource),
                profileSkillsResource.getSkillsAreas()
        );
    }

    Map<String, List<String>> getInnovationAreasSectorMap(ProfileSkillsResource profileSkillsResource) {
        return profileSkillsResource.getInnovationAreas().stream()
                .collect(groupingBy(InnovationAreaResource::getSectorName, LinkedHashMap::new,
                        mapping(InnovationAreaResource::getName, toList())));
    }
}
