package org.innovateuk.ifs.activitylog.domain;

import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.user.builder.UserBuilder;
import org.innovateuk.ifs.user.domain.User;
import org.junit.Assert;
import org.junit.Test;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.activitylog.domain.ActivityLogBuilder.newActivityLog;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.organisation.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.project.core.builder.PartnerOrganisationBuilder.newPartnerOrganisation;
import static org.innovateuk.ifs.project.core.builder.ProjectBuilder.newProject;

public class ActivityLogTest {

    @Test
    public void organisationRemovedShouldBeFalseWhenTheOrganisationIsOnTheProject(){
        Organisation org = newOrganisation().build();
        ActivityLog activityLog = newActivityLog()
                .withOrganisation(org) // This activity relates to org
                .withApplication(newApplication()
                        .withProject(newProject()
                                .withPartnerOrganisations(
                                        asList(newPartnerOrganisation()
                                                .withOrganisation(org) // This project still contains org
                                                .build()))
                                .build())
                        .build())
                .build();
        Assert.assertFalse(activityLog.isOrganisationRemoved());
    }

    @Test
    public void organisationRemovedShouldBeTrueWhenTheOrganisationIsNotOnTheProject(){
        Organisation org = newOrganisation().build();
        ActivityLog activityLog = newActivityLog()
                .withOrganisation(org) // This activity relates to org which is no longer present
                .withApplication(newApplication()
                        .withProject(newProject()
                                .withPartnerOrganisations(
                                        asList(newPartnerOrganisation()
                                                .withOrganisation(newOrganisation()
                                                        .build()) // This project does not contain the organisation
                                                .build()))
                                .build())
                        .build())
                .build();
        Assert.assertTrue(activityLog.isOrganisationRemoved());
    }

    @Test
    public void organisationRemovedShouldBeFalseWhenNoOrganisationOnTheActivityLog(){
        ActivityLog activityLog = newActivityLog()
                .withApplication(newApplication()
                        .withProject(newProject()
                                .withPartnerOrganisations(
                                        asList(newPartnerOrganisation()
                                                .withOrganisation(newOrganisation()
                                                        .build())
                                                .build()))
                                .build())
                        .build())
                .build();
        Assert.assertFalse(activityLog.isOrganisationRemoved());
    }

    @Test
    public void testAuthorIsCreatedByWhenNotExpicitlySpecified(){
        User createdBy = UserBuilder.newUser().build();
        ActivityLog activityLog = newActivityLog().withCreatedBy(createdBy).build();
        Assert.assertEquals(createdBy, activityLog.getAuthor());
    }




}
