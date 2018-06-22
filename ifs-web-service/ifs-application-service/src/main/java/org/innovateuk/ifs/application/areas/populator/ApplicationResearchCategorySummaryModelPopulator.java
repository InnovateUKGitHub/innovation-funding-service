package org.innovateuk.ifs.application.areas.populator;

import org.innovateuk.ifs.applicant.service.ApplicantRestService;
import org.innovateuk.ifs.application.areas.viewmodel.ResearchCategorySummaryViewModel;
import org.innovateuk.ifs.application.populator.AbstractLeadOnlyModelPopulator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.category.resource.ResearchCategoryResource;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Populates the research category selection viewmodel.
 */
@Component
public class ApplicationResearchCategorySummaryModelPopulator extends AbstractLeadOnlyModelPopulator {

    public ApplicationResearchCategorySummaryModelPopulator(ApplicantRestService applicantRestService,
                                                            QuestionRestService questionRestService) {
        super(applicantRestService, questionRestService);
    }

    public ResearchCategorySummaryViewModel populate(ApplicationResource applicationResource, long loggedInUserId,
                                                     boolean userIsLeadApplicant) {
        String researchCategoryName = Optional.of(applicationResource.getResearchCategory())
                .map(ResearchCategoryResource::getName).orElse(null);

        ResearchCategorySummaryViewModel researchCategorySummaryViewModel = new ResearchCategorySummaryViewModel(applicationResource.getId(), researchCategoryName);
        researchCategorySummaryViewModel.setCanMarkAsComplete(userIsLeadApplicant);
        researchCategorySummaryViewModel.setClosed(!isCompetitionOpen(applicationResource));
        researchCategorySummaryViewModel.setComplete(isComplete(applicationResource, loggedInUserId));

        return researchCategorySummaryViewModel;
    }
}
