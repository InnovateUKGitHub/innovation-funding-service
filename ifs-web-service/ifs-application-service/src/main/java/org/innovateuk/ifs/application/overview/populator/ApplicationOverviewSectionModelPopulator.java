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
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.innovateuk.ifs.form.resource.SectionType.FINANCE;
import static org.innovateuk.ifs.util.CollectionFunctions.*;

@Component
public class ApplicationOverviewSectionModelPopulator {

    private SectionService sectionService;
    private ApplicantRestService applicantRestService;
    private AssignButtonsPopulator assignButtonsPopulator;

    public ApplicationOverviewSectionModelPopulator(SectionService sectionService,
                                                    ApplicantRestService applicantRestService,
                                                    AssignButtonsPopulator assignButtonsPopulator) {
        this.sectionService = sectionService;
        this.applicantRestService = applicantRestService;
        this.assignButtonsPopulator = assignButtonsPopulator;
    }

    public ApplicationOverviewSectionViewModel populate(CompetitionResource competition,
                                                        ApplicationResource application, Long userId) {

        final List<SectionResource> allSections = sectionService.getAllByCompetitionId(competition.getId());
        final List<SectionResource> parentSections = sectionService.filterParentSections(allSections);
        List<ApplicantSectionResource> parentApplicantSections = simpleMap(parentSections,
                sectionResource -> applicantRestService.getSection(userId, application.getId(),
                        sectionResource.getId()));

        final Map<Long, List<SectionResource>> subSections = simpleToMap(parentSections,
                SectionResource::getId,
                s -> getSectionsFromListByIdList(s.getChildSections(), allSections));
        final Map<Long, List<QuestionResource>> sectionQuestions = simpleToMap(parentApplicantSections,
                s -> s.getSection().getId(),
                s -> simpleMap(s.getApplicantQuestions(), ApplicantQuestionResource::getQuestion));

        final Long financeSectionId = getFinanceSectionId(parentSections);
        final boolean hasFinanceSection = financeSectionId != null;

        Map<Long, AssignButtonsViewModel> assignButtonViewModels = new HashMap<>();
        parentApplicantSections.forEach(applicantSectionResource ->
                applicantSectionResource.getApplicantQuestions().forEach(questionResource ->
                        assignButtonViewModels.put(questionResource.getQuestion().getId(),
                                assignButtonsPopulator.populate(applicantSectionResource, questionResource,
                                        questionResource.isCompleteByApplicant(applicantSectionResource.getCurrentApplicant())))
                )
        );

        return new ApplicationOverviewSectionViewModel(parentSections, subSections, sectionQuestions, hasFinanceSection,
                financeSectionId, assignButtonViewModels);
    }

    private List<SectionResource> getSectionsFromListByIdList(final List<Long> childSections,
                                                              List<SectionResource> allSections) {
        allSections.sort(Comparator.comparing(SectionResource::getPriority,
                Comparator.nullsLast(Comparator.naturalOrder())));
        return simpleFilter(allSections, section -> childSections.contains(section.getId()));
    }

    private Long getFinanceSectionId(List<SectionResource> sections) {
        return simpleFindFirst(sections, s -> FINANCE.equals(s.getType()))
                .map(SectionResource::getId).orElse(null);
    }
}