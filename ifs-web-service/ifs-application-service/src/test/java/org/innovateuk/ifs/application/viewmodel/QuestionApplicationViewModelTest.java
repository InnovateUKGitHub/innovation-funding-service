package org.innovateuk.ifs.application.viewmodel;

import org.innovateuk.ifs.application.constant.ApplicationStatusConstants;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.OrganisationTypeEnum;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashSet;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class QuestionApplicationViewModelTest {

    private QuestionApplicationViewModel businessTypeViewModel;
    private QuestionApplicationViewModel academicTypeViewModel;

    @Before
    public void setup(){
        ApplicationResource currentApplication = newApplicationResource().withApplicationStatus(ApplicationStatusConstants.SUBMITTED.getId()).build();
        CompetitionResource competitionResource = newCompetitionResource().withCompetitionStatus(CompetitionStatus.CLOSED).build();
        OrganisationResource academicUserOrganisation = newOrganisationResource().withOrganisationType(OrganisationTypeEnum.ACADEMIC.getOrganisationTypeId()).build();
        OrganisationResource businessUserOrganisation = newOrganisationResource().withOrganisationType(OrganisationTypeEnum.BUSINESS.getOrganisationTypeId()).build();

        businessTypeViewModel = new QuestionApplicationViewModel(new HashSet<>(asList(0L)), Boolean.FALSE, currentApplication, competitionResource, businessUserOrganisation);
        academicTypeViewModel = new QuestionApplicationViewModel(new HashSet<>(asList(0L)), Boolean.FALSE, currentApplication, competitionResource, academicUserOrganisation);
    }

    @Test
    public void testGetApplicationIsReadOnly() {
        assertEquals(Boolean.TRUE, businessTypeViewModel.getApplicationIsReadOnly());
    }

    @Test
    public void testGetApplicationIsClosed() {
        assertEquals(Boolean.TRUE, businessTypeViewModel.getApplicationIsClosed());
    }

    @Test
    public void testGetUserOrganisationTypeIsAcademic() {
        assertEquals(Boolean.TRUE, academicTypeViewModel.getUserOrganisationTypeIsAcademic());
    }

    @Test
    public void testGetUserOrganisationTypeIsNotAcademic() {
        assertEquals(Boolean.FALSE, businessTypeViewModel.getUserOrganisationTypeIsAcademic());
    }
}
