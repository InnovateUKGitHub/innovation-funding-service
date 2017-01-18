package org.innovateuk.ifs.application.finance.view;

import org.innovateuk.ifs.application.finance.service.FinanceService;
import org.innovateuk.ifs.application.finance.view.jes.JESFinanceFormHandler;
import org.innovateuk.ifs.application.finance.view.jes.JESFinanceModelManager;
import org.innovateuk.ifs.application.finance.view.jes.JESProjectFinanceModelManager;
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

    @Autowired
    private ApplicationFinanceRestService applicationFinanceRestService;

    @Autowired
    private ProjectFinanceRestService projectFinanceRestService;

    @Autowired
    private SectionService sectionService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private FinanceService financeService;

    @Autowired
    private FileEntryRestService fileEntryRestService;

    @Autowired
    private FormInputService formInputService;

    @Autowired
    private FinanceHandler financeHander;

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

    public FinanceModelManager getProjectFinanceModelManager(String organisationType) {
        switch(organisationType) {
            case "University (HEI)":
                return getJESProjectFinanceModelManager();
            default:
                return getDefaultProjectFinanceModelManager();
        }
    }

    public FinanceOverviewModelManager getFinanceOverviewModelManager(String financeDataSource){
        switch(financeDataSource){
            case PROJECT_FINANCE_DATA_SOURCE :
                return new ProjectFinanceOverviewModelManager(sectionService, questionService, formInputService, financeHander);
            default:
                return new ApplicationFinanceOverviewModelManager(applicationFinanceRestService, sectionService, financeService, questionService, fileEntryRestService, formInputService);
        }
    }

    public OrganisationFinanceOverview getOrganisationProjectFinanceOverview(Long projectId){
        return new OrganisationProjectFinanceOverview(financeService, fileEntryRestService, projectId);
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

    @Bean
    protected FinanceModelManager getJESProjectFinanceModelManager() {
        return new JESProjectFinanceModelManager();
    }

    @Bean
    protected FinanceModelManager getDefaultProjectFinanceModelManager() {
        return new DefaultProjectFinanceModelManager();
    }
}


