package org.innovateuk.ifs.application.populator.section;

import org.innovateuk.ifs.applicant.resource.ApplicantSectionResource;
import org.innovateuk.ifs.application.populator.OpenSectionModelPopulator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.application.viewmodel.ApplicationCompletedViewModel;
import org.innovateuk.ifs.form.ApplicationForm;
import org.innovateuk.ifs.application.populator.forminput.FormInputViewModelGenerator;
import org.innovateuk.ifs.application.viewmodel.OpenSectionViewModel;
import org.innovateuk.ifs.application.viewmodel.section.FinanceOverviewSectionViewModel;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.form.resource.SectionType;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.util.CollectionFunctions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.*;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.form.resource.SectionType.FINANCE;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;

/**
 * Finance overview section view models.
 */
@Component
public class FinanceOverviewSectionPopulator extends AbstractSectionPopulator<FinanceOverviewSectionViewModel> {

    @Autowired
    private OpenSectionModelPopulator openSectionModelPopulator;
    @Autowired
    private FormInputViewModelGenerator formInputViewModelGenerator;

    @Override
    protected void populateNoReturn(ApplicantSectionResource section, ApplicationForm form, FinanceOverviewSectionViewModel viewModel, Model model, BindingResult bindingResult, Boolean readOnly, Optional<Long> applicantOrganisationId) {
        viewModel.setOpenSectionViewModel((OpenSectionViewModel) openSectionModelPopulator.populateModel(form, model, bindingResult, section));
    }

    @Override
    protected FinanceOverviewSectionViewModel createNew(ApplicantSectionResource section, ApplicationForm form, Boolean readOnly, Optional<Long> applicantOrganisationId, Boolean readOnlyAllApplicantApplicationFinances) {
        return new FinanceOverviewSectionViewModel(section, formInputViewModelGenerator.fromSection(section, section, form, readOnly), getNavigationViewModel(section), readOnly, applicantOrganisationId, readOnlyAllApplicantApplicationFinances);
    }

    @Override
    public SectionType getSectionType() {
        return SectionType.OVERVIEW_FINANCES;
    }

    public abstract static class AbstractApplicationModelPopulator {

        private SectionService sectionService;
        private QuestionService questionService;
        private QuestionRestService questionRestService;

        public AbstractApplicationModelPopulator(SectionService sectionService,
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

        protected ApplicationCompletedViewModel getCompletedDetails(ApplicationResource application, Optional<OrganisationResource> userOrganisation) {
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

        protected boolean isUserFinanceSectionCompleted(ApplicationResource application, OrganisationResource userOrganisation, Map<Long, Set<Long>> completedSectionsByOrganisation) {

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
}

