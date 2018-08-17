package org.innovateuk.ifs.application.populator;

import org.innovateuk.ifs.application.populator.section.AbstractApplicationModelPopulator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.application.viewmodel.ApplicationCompletedViewModel;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ApplicationCompletedModelPopulator extends AbstractApplicationModelPopulator {

    public ApplicationCompletedModelPopulator(SectionService sectionService, QuestionService questionService) {
        super(sectionService, questionService);
    }

    public ApplicationCompletedViewModel populate(ApplicationResource application, Optional<OrganisationResource>
            userOrganisation) {
        return getCompletedDetails(application, userOrganisation);
    }
}
