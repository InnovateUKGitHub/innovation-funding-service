package org.innovateuk.ifs.project.monitoringofficer.controller;

import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.project.BaseProjectSetupControllerSecurityTest;
import org.innovateuk.ifs.project.monitoringofficer.form.MonitoringOfficerAssignProjectForm;
import org.innovateuk.ifs.project.monitoringofficer.form.MonitoringOfficerCreateForm;
import org.innovateuk.ifs.project.status.security.SetupSectionsPermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.function.Consumer;

import static org.innovateuk.ifs.user.resource.Role.*;

public class MonitoringOfficerControllerSecurityTest extends BaseProjectSetupControllerSecurityTest<MonitoringOfficerController> {

    @Override
    protected Class<? extends MonitoringOfficerController> getClassUnderTest() {
        return MonitoringOfficerController.class;
    }

    @Test
    public void viewMonitoringOfficer() {
        final long monitoringOfficerId = 1;
        final Model model = null;
        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.viewProjects(monitoringOfficerId, model), IFS_ADMINISTRATOR, PROJECT_FINANCE, COMP_ADMIN);
    }

    @Test
    public void assignProject() {
        final long monitoringOfficerId = 1;
        final MonitoringOfficerAssignProjectForm form = null;
        final BindingResult bindingResult = null;
        final ValidationHandler validationHandler = null;
        final Model model = null;
        final UserResource user = null;
        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.assignProject(monitoringOfficerId, form, bindingResult, validationHandler, model, user), IFS_ADMINISTRATOR, PROJECT_FINANCE, COMP_ADMIN);
    }

    @Test
    public void unassignProject() {
        final long monitoringOfficerId = 1;
        final long projectId = 1;
        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.unassignProject(monitoringOfficerId, projectId ), IFS_ADMINISTRATOR, PROJECT_FINANCE, COMP_ADMIN);
    }

    @Test
    public void viewAll() {
        final Model model = null;
        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.viewAll(false, model), IFS_ADMINISTRATOR, PROJECT_FINANCE, COMP_ADMIN);
    }

    @Test
    public void create() {
        final Model model = null;
        final String emailAddress = null;
        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.create(emailAddress, model), IFS_ADMINISTRATOR, PROJECT_FINANCE, COMP_ADMIN);
    }

    @Test
    public void createUser() {
        final MonitoringOfficerCreateForm form = null;
        final BindingResult bindingResult = null;
        final ValidationHandler validationHandler = null;
        final Model model = null;
        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.createUser(form, bindingResult, validationHandler, model), IFS_ADMINISTRATOR, PROJECT_FINANCE, COMP_ADMIN);
    }

    @Override
    protected Consumer<SetupSectionsPermissionRules> getVerification() {
        return null;
    }

}
