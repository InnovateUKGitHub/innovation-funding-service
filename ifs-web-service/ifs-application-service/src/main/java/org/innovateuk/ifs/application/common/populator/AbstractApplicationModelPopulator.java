package org.innovateuk.ifs.application.common.populator;

import org.innovateuk.ifs.application.overview.viewmodel.ApplicationOverviewCompletedViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.util.CollectionFunctions;

import java.util.*;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.form.resource.SectionType.FINANCE;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;

public abstract class AbstractApplicationModelPopulator {

    private SectionService sectionService;
    private QuestionService questionService;

    public AbstractApplicationModelPopulator(SectionService sectionService, QuestionService questionService) {
        this.sectionService = sectionService;
        this.questionService = questionService;
    }

    protected Map<Long, List<QuestionResource>> getSectionQuestions(Long competitionId) {
        List<SectionResource> allSections = sectionService.getAllByCompetitionId(competitionId);
        List<SectionResource> parentSections = sectionService.filterParentSections(allSections);

        List<QuestionResource> questions = questionService.findByCompetition(competitionId);

        return parentSections.stream()
                .collect(Collectors.toMap(
                        SectionResource::getId,
                        s -> getQuestionsBySection(s.getQuestions(), questions)
                ));
    }

    protected Map<Long, SectionResource> getSections(Long competitionId) {
        List<SectionResource> allSections = sectionService.getAllByCompetitionId(competitionId);
        List<SectionResource> parentSections = sectionService.filterParentSections(allSections);

        return parentSections.stream().collect(CollectionFunctions.toLinkedMap(SectionResource::getId,
                        Function.identity()));
    }

    protected ApplicationOverviewCompletedViewModel getCompletedDetails(ApplicationResource application, Optional<OrganisationResource> userOrganisation) {
        Future<Set<Long>> markedAsComplete = getMarkedAsCompleteDetails(application, userOrganisation); // List of question ids
        Map<Long, Set<Long>> completedSectionsByOrganisation = sectionService.getCompletedSectionsByOrganisation(application.getId());
        Set<Long> sectionsMarkedAsComplete = getCombinedMarkedAsCompleteSections(completedSectionsByOrganisation);
        boolean userFinanceSectionCompleted = isUserFinanceSectionCompleted(application, userOrganisation.get(), completedSectionsByOrganisation);

        ApplicationOverviewCompletedViewModel viewModel = new ApplicationOverviewCompletedViewModel(sectionsMarkedAsComplete, markedAsComplete, userFinanceSectionCompleted);
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

    private boolean isUserFinanceSectionCompleted(ApplicationResource application, OrganisationResource userOrganisation, Map<Long, Set<Long>> completedSectionsByOrganisation) {

        return sectionService.getAllByCompetitionId(application.getCompetition())
                .stream()
                .filter(section -> section.getType().equals(FINANCE))
                .map(SectionResource::getId)
                .anyMatch(id -> completedSectionsByOrganisation.get(userOrganisation.getId()).contains(id));
    }

    private List<QuestionResource> getQuestionsBySection(final List<Long> questionIds, final List<QuestionResource> questions) {
        return simpleFilter(questions, q -> questionIds.contains(q.getId()));
    }
}
