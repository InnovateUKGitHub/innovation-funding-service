package org.innovateuk.ifs.management.ineligible.populator;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationNotificationTemplateRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.management.ineligible.form.InformIneligibleForm;
import org.innovateuk.ifs.management.ineligible.viewmodel.InformIneligibleViewModel;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.ProcessRoleType;
import org.innovateuk.ifs.user.service.ProcessRoleRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Populator for {@link InformIneligibleViewModel}
 */
@Component
public class InformIneligibleModelPopulator {

    @Autowired
    private ProcessRoleRestService processRoleRestService;

    @Autowired
    private ApplicationNotificationTemplateRestService templateRestService;

    @Autowired
    private CompetitionRestService competitionRestService;

    public InformIneligibleViewModel populateModel(ApplicationResource applicationResource, InformIneligibleForm form) {

        List<ProcessRoleResource> processRoles = processRoleRestService.findProcessRole(applicationResource.getId()).getSuccess();
        Optional<ProcessRoleResource> leadApplicant = processRoles.stream()
                .filter(pr -> pr.getRole() == ProcessRoleType.LEADAPPLICANT)
                .findFirst();

        if (form.getMessage() == null) {
            form.setMessage(templateRestService.getIneligibleNotificationTemplate(applicationResource.getCompetition()).getSuccess().getMessageBody());
        }

        if (form.getSubject() == null) {
            CompetitionResource competition = competitionRestService.getCompetitionById(applicationResource.getCompetition()).getSuccess();
            if(competition.isH2020()) {
                form.setSubject(String.format(
                        "Notification regarding your submission %s: %s",
                        applicationResource.getId(),
                        applicationResource.getName()));
            } else {
                form.setSubject(String.format(
                        "Notification regarding your application %s: %s",
                        applicationResource.getId(),
                        applicationResource.getName()));
            }
        }

        return new InformIneligibleViewModel(
                applicationResource.getCompetition(),
                applicationResource.getId(),
                applicationResource.getCompetitionName(),
                applicationResource.getName(),
                leadApplicant.map(ProcessRoleResource::getUserName).orElse(""));
    }
}
