package org.innovateuk.ifs.application.populator.section;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.application.viewmodel.ApplicationCompletedViewModel;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.util.CollectionFunctions;

import java.util.*;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.innovateuk.ifs.form.resource.SectionType.FINANCE;

public abstract class AbstractApplicationModelPopulator {

    private SectionService sectionService;
    private QuestionService questionService;
    private QuestionRestService questionRestService;

    protected AbstractApplicationModelPopulator(SectionService sectionService,
                                                QuestionService questionService,
                                                QuestionRestService questionRestService) {
        this.sectionService = sectionService;
        this.questionService = questionService;
        this.questionRestService = questionRestService;
    }

    protected Map<Long, List<QuestionResource>> getSectionQuestions(Long competitionId) {
        List<SectionResource> allSections = sectionService.getAllByCompetitionId(competitionId);
        List<SectionResource> parentSections = sectionService.filterParentSections(allSections);

        List<QuestionResource> questions = questionRestService.findByCompetition(competitionId).getSuccess();

        return parentSections.stream()
                .collect(toMap(
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

    protected ApplicationCompletedViewModel getCompletedDetails(ApplicationResource application,
                                                                Optional<OrganisationResource> userOrganisation) {
        long userOrganisationId = userOrganisation.get().getId();
        long financeSectionId = getFinanceSectionId(application.getCompetition());

        Future<Set<Long>> markedAsComplete = getMarkedAsCompleteDetails(application, userOrganisation); // List of
        // question ids
        Map<Long, Set<Long>> completedSectionsByOrganisation = sectionService.getCompletedSectionsByOrganisation
                (application.getId());
        Set<Long> combinedMarkedAsCompleteSections =
                getCombinedMarkedAsCompleteSections(completedSectionsByOrganisation);
        Set<Long> completedSectionsByUserOrganisation = completedSectionsByOrganisation.get(userOrganisationId);

        boolean financeSectionCompleted = completedSectionsByUserOrganisation.contains(financeSectionId);
        boolean financeOverviewSectionCompleted = combinedMarkedAsCompleteSections.contains(financeSectionId);

        return new ApplicationCompletedViewModel(combinedMarkedAsCompleteSections,
                markedAsComplete, completedSectionsByUserOrganisation, financeSectionCompleted,
                financeOverviewSectionCompleted);
    }

    private Future<Set<Long>> getMarkedAsCompleteDetails(ApplicationResource application,
                                                         Optional<OrganisationResource> userOrganisation) {
        long organisationId = userOrganisation
                .map(OrganisationResource::getId)
                .orElse(0L);

        return questionService.getMarkedAsComplete(application.getId(), organisationId);
    }

    private Set<Long> getCombinedMarkedAsCompleteSections(Map<Long, Set<Long>> completedSectionsByOrganisation) {
        Set<Long> combinedMarkedAsComplete = completedSectionsByOrganisation.values()
                .stream()
                .flatMap(Set::stream)
                .collect(Collectors.toSet());

        completedSectionsByOrganisation.forEach((organisationId, completedSections) ->
                combinedMarkedAsComplete.retainAll(completedSections));

        return combinedMarkedAsComplete;
    }

    private long getFinanceSectionId(long competitionId) {
        return sectionService.getSectionsForCompetitionByType(competitionId, FINANCE)
                .stream()
                .findFirst()
                .map(SectionResource::getId)
                .get();
    }

    private List<QuestionResource> getQuestionsBySection(final List<Long> questionIds, final List<QuestionResource>
            questions) {
        return questions.stream().filter(q -> questionIds.contains(q.getId()))
                .sorted()
                .collect(toList());
    }
}
