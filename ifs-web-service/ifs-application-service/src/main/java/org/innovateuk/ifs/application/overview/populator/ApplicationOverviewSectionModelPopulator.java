package org.innovateuk.ifs.application.overview.populator;

import org.innovateuk.ifs.applicant.resource.ApplicantQuestionResource;
import org.innovateuk.ifs.applicant.resource.ApplicantSectionResource;
import org.innovateuk.ifs.applicant.service.ApplicantRestService;
import org.innovateuk.ifs.application.overview.viewmodel.ApplicationOverviewSectionViewModel;
import org.innovateuk.ifs.application.populator.AssignButtonsPopulator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.application.viewmodel.AssignButtonsViewModel;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.form.resource.SectionType;
import org.innovateuk.ifs.util.CollectionFunctions;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;

@Component
public class ApplicationOverviewSectionModelPopulator {

    private SectionService sectionService;
    private ApplicantRestService applicantRestService;
    private AssignButtonsPopulator assignButtonsPopulator;

    public ApplicationOverviewSectionModelPopulator(SectionService sectionService, ApplicantRestService applicantRestService, AssignButtonsPopulator assignButtonsPopulator) {
        this.sectionService = sectionService;
        this.applicantRestService = applicantRestService;
        this.assignButtonsPopulator = assignButtonsPopulator;
    }

    public ApplicationOverviewSectionViewModel populate (CompetitionResource competition, ApplicationResource application, Long userId) {

        final List<SectionResource> allSections = sectionService.getAllByCompetitionId(competition.getId());
        final List<SectionResource> parentSections = sectionService.filterParentSections(allSections);
        final List<ApplicantSectionResource> parentApplicantSections = parentSections.stream().map(sectionResource -> applicantRestService.getSection(userId, application.getId(), sectionResource.getId())).collect(Collectors.toList());
        final SortedMap<Long, SectionResource> sections = CollectionFunctions.toSortedMap(parentSections, SectionResource::getPriorityAsLong,
                Function.identity());

        final Map<Long, List<SectionResource>> subSections = parentSections.stream()
                .collect(Collectors.toMap(
                        SectionResource::getId, s -> getSectionsFromListByIdList(s.getChildSections(), allSections)
                ));

        final Map<Long, List<QuestionResource>> sectionQuestions = parentApplicantSections.stream()
                .collect(Collectors.toMap(
                        s -> s.getSection().getId(),
                        s -> s.getApplicantQuestions().stream().map(ApplicantQuestionResource::getQuestion).collect(Collectors.toList()))
                );

        final List<SectionResource> financeSections = getFinanceSectionIds(parentSections);

        boolean hasFinanceSection = false;
        Long financeSectionId = null;
        if (!financeSections.isEmpty()) {
            hasFinanceSection = true;
            financeSectionId = financeSections.get(0).getId();
        }

        Map<Long, AssignButtonsViewModel> assignButtonViewModels = new HashMap<>();
        parentApplicantSections.forEach(applicantSectionResource ->
                applicantSectionResource.getApplicantQuestions().forEach(questionResource ->
                        assignButtonViewModels.put(questionResource.getQuestion().getId(), assignButtonsPopulator.populate(applicantSectionResource, questionResource, questionResource.isCompleteByApplicant(applicantSectionResource.getCurrentApplicant())))
                )
        );

        return new ApplicationOverviewSectionViewModel(sections, subSections, sectionQuestions, financeSections, hasFinanceSection, financeSectionId, assignButtonViewModels);
    }

    private List<SectionResource> getSectionsFromListByIdList(final List<Long> childSections, List<SectionResource> allSections) {
        allSections.sort(Comparator.comparing(SectionResource::getPriority, Comparator.nullsLast(Comparator.naturalOrder())));
        return simpleFilter(allSections, section -> childSections.contains(section.getId()));
    }


    private List<SectionResource> getFinanceSectionIds(List<SectionResource> sections){
        return sections.stream()
                .filter(s -> SectionType.FINANCE.equals(s.getType()))
                .collect(Collectors.toList());
    }
}