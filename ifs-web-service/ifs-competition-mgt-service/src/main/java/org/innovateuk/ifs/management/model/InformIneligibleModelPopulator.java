package org.innovateuk.ifs.management.model;

import org.innovateuk.ifs.application.UserApplicationRole;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.management.viewmodel.InformIneligibleViewModel;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.service.ProcessRoleService;
import org.innovateuk.ifs.user.service.UserRestService;
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

    public InformIneligibleViewModel populateModel(ApplicationResource applicationResource) {

        List<ProcessRoleResource> processRoles = processRoleService.findProcessRolesByApplicationId(applicationResource.getId());
        String leadApplilcant = processRoles.stream()
                .filter(pr -> pr.getRoleName().equals(UserApplicationRole.LEAD_APPLICANT.getRoleName()))
                .map(ProcessRoleResource::getUserName)
                .findFirst().orElse("");


        return new InformIneligibleViewModel(
                applicationResource.getCompetition(),
                applicationResource.getId(),
                applicationResource.getCompetitionName(),
                applicationResource.getName(),
                leadApplilcant);
    }
}
