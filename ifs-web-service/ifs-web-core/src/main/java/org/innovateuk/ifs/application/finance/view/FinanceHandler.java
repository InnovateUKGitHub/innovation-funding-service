package org.innovateuk.ifs.application.finance.view;

import org.innovateuk.ifs.application.finance.service.FinanceService;
import org.innovateuk.ifs.application.finance.view.jes.JESFinanceFormHandler;
import org.innovateuk.ifs.application.finance.view.jes.JESFinanceModelManager;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.file.service.FileEntryRestService;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.innovateuk.ifs.form.service.FormInputService;
import org.innovateuk.ifs.project.finance.service.ProjectFinanceRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@Configuration
public class FinanceHandler {

    public static final String APPLICATION_DATA_SOURCE = "APPLICATION_FINANCE";
    public static final String PROJECT_FINANCE_DATA_SOURCE = "PROJECT_FINANCE";

    private ApplicationFinanceRestService applicationFinanceRestService;
    private ProjectFinanceRestService projectFinanceRestService;
    private SectionService sectionService;
    private QuestionService questionService;
    private FinanceService financeService;
    private FileEntryRestService fileEntryRestService;
    private FormInputService formInputService;
    private FinanceHandler financeHander;


    @Autowired
    public FinanceHandler(ProjectFinanceRestService projectFinanceRestService, SectionService sectionService, QuestionService questionService, FinanceService financeService, FileEntryRestService fileEntryRestService, FormInputService formInputService, FinanceHandler financeHander) {
        this.projectFinanceRestService = projectFinanceRestService;
        this.sectionService = sectionService;
        this.questionService = questionService;
        this.financeService = financeService;
        this.fileEntryRestService = fileEntryRestService;
        this.formInputService = formInputService;
        this.financeHander = financeHander;
    }

    public FinanceFormHandler getFinanceFormHandler(String organisationType) {
        switch(organisationType) {
            case "University (HEI)":
                return getJESFinanceFormHandler();
            default:
                return getDefaultFinanceFormHandler();
        }
    }

    public FinanceModelManager getFinanceModelManager(String organisationType) {
        switch(organisationType) {
            case "University (HEI)":
                return getJESFinanceModelManager();
            default:
                return getDefaultFinanceModelManager();
        }
    }

    public FinanceOverviewModelManager getFinanceOverviewModelManager(String financeDataSource){
        switch(financeDataSource){
            case PROJECT_FINANCE_DATA_SOURCE :
                return new ProjectFinanceOverviewModelManager(projectFinanceRestService, sectionService, financeService, questionService, fileEntryRestService, formInputService, financeHander);
            default:
                return new ApplicationFinanceOverviewModelManager(applicationFinanceRestService, sectionService, financeService, questionService, fileEntryRestService, formInputService);
        }
    }

    public OrganisationFinanceOverview getOrganisationFinanceOverview(String financeDataSource, Long applicationId){
        switch(financeDataSource) {
            case PROJECT_FINANCE_DATA_SOURCE:
                return new OrganisationProjectFinanceOverview(financeService, fileEntryRestService, applicationId);
            default:
                return new OrganisationApplicationFinanceOverview(financeService, fileEntryRestService, applicationId);
        }
    }

    @Bean
    protected FinanceFormHandler getJESFinanceFormHandler() {
        return new JESFinanceFormHandler();
    }

    @Bean
    protected FinanceFormHandler getDefaultFinanceFormHandler() {
        return new DefaultFinanceFormHandler();
    }

    @Bean
    protected FinanceModelManager getJESFinanceModelManager() {
        return new JESFinanceModelManager();
    }

    @Bean
    protected FinanceModelManager getDefaultFinanceModelManager() {
        return new DefaultFinanceModelManager();
    }
}


