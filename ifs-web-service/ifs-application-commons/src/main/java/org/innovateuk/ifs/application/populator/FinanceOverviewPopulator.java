package org.innovateuk.ifs.application.populator;

import org.innovateuk.ifs.applicant.resource.ApplicantSectionResource;
import org.innovateuk.ifs.application.finance.view.ApplicationFinanceOverviewModelManager;
import org.innovateuk.ifs.application.finance.view.FinanceViewHandlerProvider;
import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.application.viewmodel.OpenSectionViewModel;
import org.innovateuk.ifs.form.ApplicationForm;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.resource.QuestionType;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.form.resource.SectionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import java.util.List;

import static org.innovateuk.ifs.form.resource.SectionType.FINANCE;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;

/**
 * Populator for the {@link OpenSectionViewModel} in the Finance part of the application
 */
@Component
public class FinanceOverviewPopulator {

    @Autowired
    private SectionService sectionService;

    @Autowired
    private QuestionRestService questionRestService;

    @Autowired
    private ApplicationFinanceOverviewModelManager applicationFinanceOverviewModelManager;

    @Autowired
    private FinanceViewHandlerProvider financeViewHandlerProvider;

    public void addOverviewDetails(OpenSectionViewModel openSectionViewModel, Model model, ApplicationForm form, ApplicantSectionResource applicantSection) {
        List<SectionResource> allSections = sectionService.getAllByCompetitionId(applicantSection.getCompetition().getId());
        List<SectionResource> financeSections = getSectionsByType(allSections, FINANCE);

        boolean hasFinanceSection = !financeSections.isEmpty();

        if(hasFinanceSection) {
            Long organisationType = applicantSection.getCurrentApplicant().getOrganisation().getOrganisationType();
            List<QuestionResource> costsQuestions = questionRestService.getQuestionsBySectionIdAndType(financeSections.get(0).getId(), QuestionType.COST).getSuccess();

            applicationFinanceOverviewModelManager.addFinanceDetails(model, applicantSection.getCompetition().getId(), applicantSection.getApplication().getId());
            if(!form.isAdminMode() && applicantSection.getCompetition().isOpen()) {
                openSectionViewModel.setFinanceViewModel(financeViewHandlerProvider.getFinanceModelManager(organisationType).getFinanceViewModel(applicantSection.getApplication().getId(), costsQuestions, applicantSection.getCurrentUser().getId(), form, applicantSection.getCurrentApplicant().getOrganisation().getId()));
            }
        }
    }


    private List<SectionResource> getSectionsByType(List<SectionResource> list, SectionType type){
        return simpleFilter(list, s -> type.equals(s.getType()));
    }

}
