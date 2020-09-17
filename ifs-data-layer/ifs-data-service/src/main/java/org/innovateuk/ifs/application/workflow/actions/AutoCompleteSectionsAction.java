package org.innovateuk.ifs.application.workflow.actions;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.resource.ApplicationEvent;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.application.transactional.AutoCompleteSectionsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;

/**
 * Auto mark-as-complete terms and conditions for EOI competitions
 */
@Component
public class AutoCompleteSectionsAction extends BaseApplicationAction {

    @Autowired
    private AutoCompleteSectionsUtil autoCompleteSectionsUtil;

    @Override
    protected void doExecute(final Application application,
                             final StateContext<ApplicationState, ApplicationEvent> context) {
        autoCompleteSectionsUtil.intitialiseCompleteSectionsForOrganisation(application
                , application.getLeadOrganisationId(),
                application.getLeadApplicantProcessRole().getId());
    }
}