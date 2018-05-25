package org.innovateuk.ifs.management.model;

import org.innovateuk.ifs.application.service.ApplicationNotificationTemplateRestService;
import org.innovateuk.ifs.management.form.InformIneligibleForm;
import org.innovateuk.ifs.user.viewmodel.UserApplicationRole;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.management.viewmodel.InformIneligibleViewModel;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.service.ProcessRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Populator for {@link InformIneligibleViewModel}
 */
@Component
public class InformIneligibleModelPopulator {

    @Autowired
    private ProcessRoleService processRoleService;

    @Autowired
    private ApplicationNotificationTemplateRestService templateRestService;

    public InformIneligibleViewModel populateModel(ApplicationResource applicationResource, InformIneligibleForm form) {

        List<ProcessRoleResource> processRoles = processRoleService.findProcessRolesByApplicationId(applicationResource.getId());
        String leadApplilcant = processRoles.stream()
                .filter(pr -> pr.getRoleName().equals(UserApplicationRole.LEAD_APPLICANT.getRoleName()))
                .map(ProcessRoleResource::getUserName)
                .findFirst().orElse("");

        if (form.getMessage() == null) {
            form.setMessage(templateRestService.getIneligibleNotificationTemplate(applicationResource.getCompetition()).getSuccess().getMessageBody());
        }

        return new InformIneligibleViewModel(
                applicationResource.getCompetition(),
                applicationResource.getId(),
                applicationResource.getCompetitionName(),
                applicationResource.getName(),
                leadApplilcant);
    }
}
