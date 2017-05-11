package org.innovateuk.ifs.application.populator;

import org.innovateuk.ifs.applicant.resource.ApplicantSectionResource;
import org.innovateuk.ifs.application.finance.view.ApplicationFinanceOverviewModelManager;
import org.innovateuk.ifs.application.finance.view.FinanceHandler;
import org.innovateuk.ifs.application.form.ApplicationForm;
import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.application.resource.QuestionType;
import org.innovateuk.ifs.application.resource.SectionResource;
import org.innovateuk.ifs.application.resource.SectionType;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.application.viewmodel.OpenSectionViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.application.resource.SectionType.FINANCE;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;

@Component
public class FinanceOverviewPopulator {

    @Autowired
    private SectionService sectionService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private ApplicationFinanceOverviewModelManager applicationFinanceOverviewModelManager;

    @Autowired
    private FinanceHandler financeHandler;

    //TODO - INFUND-7482 - remove usages of Model model
    public void addOverviewDetails(OpenSectionViewModel openSectionViewModel, Model model, ApplicationForm form, ApplicantSectionResource applicantSection) {
        List<SectionResource> allSections = sectionService.getAllByCompetitionId(applicantSection.getCompetition().getId());
        List<SectionResource> financeSections = getSectionsByType(allSections, FINANCE);

        boolean hasFinanceSection = !financeSections.isEmpty();

        if(hasFinanceSection) {
            Long organisationType = applicantSection.getCurrentApplicant().getOrganisation().getOrganisationType();
            List<QuestionResource> costsQuestions = questionService.getQuestionsBySectionIdAndType(financeSections.get(0).getId(), QuestionType.COST);

            applicationFinanceOverviewModelManager.addFinanceDetails(model, applicantSection.getCompetition().getId(), applicantSection.getApplication().getId(), Optional.of(applicantSection.getCurrentApplicant().getOrganisation().getId()));
            if(!form.isAdminMode()){

                if(applicantSection.getCompetition().isOpen()) {
                    openSectionViewModel.setFinanceViewModel(financeHandler.getFinanceModelManager(organisationType).getFinanceViewModel(applicantSection.getApplication().getId(), costsQuestions, applicantSection.getCurrentUser().getId(), form, applicantSection.getCurrentApplicant().getOrganisation().getId()));
                }
            }
        }
    }


    private List<SectionResource> getSectionsByType(List<SectionResource> list, SectionType type){
        return simpleFilter(list, s -> type.equals(s.getType()));
    }

}
