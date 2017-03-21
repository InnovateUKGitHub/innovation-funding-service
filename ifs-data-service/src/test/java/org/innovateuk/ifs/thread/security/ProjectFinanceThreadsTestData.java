package org.innovateuk.ifs.thread.security;

import org.innovateuk.ifs.finance.domain.ProjectFinance;
import org.innovateuk.ifs.project.domain.Project;
import org.innovateuk.ifs.project.domain.ProjectUser;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.resource.UserResource;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.finance.domain.builder.ProjectFinanceBuilder.newProjectFinance;
import static org.innovateuk.ifs.invite.domain.ProjectParticipantRole.PROJECT_FINANCE_CONTACT;
import static org.innovateuk.ifs.invite.domain.ProjectParticipantRole.PROJECT_PARTNER;
import static org.innovateuk.ifs.project.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.project.builder.ProjectUserBuilder.newProjectUser;
import static org.innovateuk.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;

public class ProjectFinanceThreadsTestData {

    public static ProjectFinance projectFinanceWithUserAsFinanceContact(UserResource user) {
        Organisation organisation = newOrganisation().withId(3L).build();
        organisation.addUser(newUser().withId(user.getId()).build());
        ProjectUser projectUser = newProjectUser().withUser(newUser().withId(user.getId()).build())
                .withRole(PROJECT_FINANCE_CONTACT)
                .withRole(PROJECT_PARTNER)
                .withOrganisation(organisation)
                .build();
        Project project = newProject().withProjectUsers(singletonList(projectUser)).build();
        return newProjectFinance().withProject(project).withOrganisation(organisation).build();
    }
}
