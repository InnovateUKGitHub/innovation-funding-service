package org.innovateuk.ifs.assessment.profile.populator;

import org.innovateuk.ifs.assessment.profile.viewmodel.AssessorProfileBaseSkillsViewModel;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.user.resource.ProfileSkillsResource;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.*;

/**
 * Abstract populator for the Assessor Skills views.
 */
public abstract class AssessorProfileBaseSkillsModelPopulator<T extends AssessorProfileBaseSkillsViewModel> {

    public abstract T populateModel(ProfileSkillsResource profileSkillsResource);

    Map<String, List<String>> getInnovationAreasSectorMap(ProfileSkillsResource profileSkillsResource) {
        return profileSkillsResource.getInnovationAreas().stream()
                .collect(groupingBy(InnovationAreaResource::getSectorName, LinkedHashMap::new,
                        mapping(InnovationAreaResource::getName, toList())));
    }

}