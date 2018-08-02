package org.innovateuk.ifs.application.populator;

import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.application.viewmodel.ApplicationCompletedViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Future;


@Component
public class ApplicationCompletedModelPopulator extends AbstractApplicationModelPopulator{

    private SectionService sectionService;
    private QuestionService questionService;
    private QuestionRestService questionRestService;

    public ApplicationCompletedModelPopulator(SectionService sectionService,
                                              QuestionService questionService,
                                              QuestionRestService questionRestService) {
        super(sectionService, questionService, questionRestService);
        this.sectionService = sectionService;
        this.questionService = questionService;
    }

    public ApplicationCompletedViewModel populate(ApplicationResource application, Optional<OrganisationResource> userOrganisation) {

        Future<Set<Long>> markedAsComplete = getMarkedAsCompleteDetails(application, userOrganisation); // List of question ids
        Map<Long, Set<Long>> completedSectionsByOrganisation = sectionService.getCompletedSectionsByOrganisation(application.getId());
        Set<Long> sectionsMarkedAsComplete = getCombinedMarkedAsCompleteSections(completedSectionsByOrganisation);
        boolean userFinanceSectionCompleted = isUserFinanceSectionCompleted(application, userOrganisation.get(), completedSectionsByOrganisation);

        ApplicationCompletedViewModel viewModel = new ApplicationCompletedViewModel(sectionsMarkedAsComplete, markedAsComplete, userFinanceSectionCompleted);
        userOrganisation.ifPresent(org -> viewModel.setCompletedSections(completedSectionsByOrganisation.get(org.getId())));
        return viewModel;
    }

    private Future<Set<Long>> getMarkedAsCompleteDetails(ApplicationResource application, Optional<OrganisationResource> userOrganisation) {

        Long organisationId = userOrganisation
                .map(OrganisationResource::getId)
                .orElse(0L);

        return questionService.getMarkedAsComplete(application.getId(), organisationId);
    }

    private Set<Long> getCombinedMarkedAsCompleteSections(Map<Long, Set<Long>> completedSectionsByOrganisation) {
        Set<Long> combinedMarkedAsComplete = new HashSet<>();

        completedSectionsByOrganisation.forEach((organisationId, completedSections) -> combinedMarkedAsComplete.addAll(completedSections));
        completedSectionsByOrganisation.forEach((key, values) -> combinedMarkedAsComplete.retainAll(values));

        return combinedMarkedAsComplete;
    }
}
