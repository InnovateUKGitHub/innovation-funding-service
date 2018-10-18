package org.innovateuk.ifs.application.populator.section;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.application.viewmodel.ApplicationCompletedViewModel;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.form.resource.SectionType;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.util.CollectionFunctions;

import java.util.*;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.innovateuk.ifs.form.resource.SectionType.FINANCE;
import static org.innovateuk.ifs.form.resource.SectionType.OVERVIEW_FINANCES;

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

        Future<Set<Long>> markedAsComplete = getMarkedAsCompleteDetails(application, userOrganisation); // List of
        // question ids
        Map<Long, Set<Long>> completedSectionsByOrganisation = sectionService.getCompletedSectionsByOrganisation
                (application.getId());
        Set<Long> combinedMarkedAsCompleteSections =
                getCombinedMarkedAsCompleteSections(completedSectionsByOrganisation);
        Set<Long> completedSectionsByUserOrganisation = completedSectionsByOrganisation.get(userOrganisationId);

        boolean financeSectionCompleted = getCompletedSectionsContains(completedSectionsByUserOrganisation, application.getCompetition(), FINANCE);
        boolean financeOverviewSectionCompleted = getCompletedSectionsContains(completedSectionsByUserOrganisation, application.getCompetition(), OVERVIEW_FINANCES);

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

    private Optional<Long> getSectionIdByType(long competitionId, SectionType sectionType) {
        return sectionService.getSectionsForCompetitionByType(competitionId, sectionType)
                .stream()
                .findFirst()
                .map(SectionResource::getId);
    }

    private boolean getCompletedSectionsContains(Set<Long> completedSectionsByUserOrganisation, long competitionId, SectionType sectionType) {

        Optional<Long> sectionId = getSectionIdByType(competitionId, sectionType);

        boolean containsSection = true;
        if (sectionId.isPresent()) {
            containsSection = completedSectionsByUserOrganisation.contains(sectionId.get());
        }
        return containsSection;
    }

    private List<QuestionResource> getQuestionsBySection(final List<Long> questionIds, final List<QuestionResource>
            questions) {
        return questions.stream().filter(q -> questionIds.contains(q.getId()))
                .sorted()
                .collect(toList());
    }
}
