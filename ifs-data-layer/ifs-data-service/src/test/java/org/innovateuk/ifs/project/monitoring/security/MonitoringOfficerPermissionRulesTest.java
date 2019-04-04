package org.innovateuk.ifs.project.monitoring.security;

import org.innovateuk.ifs.RootPermissionRulesTest;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;

import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.APPLICANT;
import static org.innovateuk.ifs.user.resource.Role.COMP_ADMIN;
import static org.innovateuk.ifs.user.resource.Role.MONITORING_OFFICER;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MonitoringOfficerPermissionRulesTest extends RootPermissionRulesTest<MonitoringOfficerPermissionRules> {

    @Override
    protected MonitoringOfficerPermissionRules supplyPermissionRulesUnderTest() {
        return new MonitoringOfficerPermissionRules();
    }

    @Test
    public void monitoringOfficerCanSeeTheirOwnProjects() {
        UserResource monitoringOfficer = newUserResource().withRoleGlobal(MONITORING_OFFICER).build();
        UserResource otherMonitoringOfficer = newUserResource().withRoleGlobal(APPLICANT).build();
        UserResource nonMonitoringOfficer = newUserResource().withRoleGlobal(APPLICANT).build();

        assertTrue(rules.monitoringOfficerCanSeeTheirOwnProjects(monitoringOfficer, monitoringOfficer));

        assertFalse(rules.monitoringOfficerCanSeeTheirOwnProjects(monitoringOfficer, otherMonitoringOfficer));
        assertFalse(rules.monitoringOfficerCanSeeTheirOwnProjects(monitoringOfficer, nonMonitoringOfficer));
    }


    @Test
    public void internalUsersCanSeeMonitoringOfficerProjects() {
        UserResource monitoringOfficer = newUserResource().withRoleGlobal(APPLICANT).build();
        UserResource internal = newUserResource().withRoleGlobal(COMP_ADMIN).build();
        UserResource nonInternal = newUserResource().withRoleGlobal(APPLICANT).build();

        assertTrue(rules.internalUsersCanSeeMonitoringOfficerProjects(monitoringOfficer, internal));

        assertFalse(rules.internalUsersCanSeeMonitoringOfficerProjects(monitoringOfficer, nonInternal));
    }
}
