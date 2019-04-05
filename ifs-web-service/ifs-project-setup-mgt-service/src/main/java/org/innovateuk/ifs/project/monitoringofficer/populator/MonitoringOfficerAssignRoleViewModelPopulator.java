package org.innovateuk.ifs.project.monitoringofficer.populator;


import org.innovateuk.ifs.project.monitoringofficer.viewmodel.MonitoringOfficerAssignRoleViewModel;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MonitoringOfficerAssignRoleViewModelPopulator {

    @Autowired
    private UserRestService userRestService;

    public MonitoringOfficerAssignRoleViewModelPopulator() {
    }

    public MonitoringOfficerAssignRoleViewModel populate(long userId) {

        UserResource userResource = userRestService.retrieveUserById(userId).getSuccess();

        return new MonitoringOfficerAssignRoleViewModel(
                userId,
                userResource.getFirstName(),
                userResource.getLastName(),
                userResource.getEmail());
    }
}