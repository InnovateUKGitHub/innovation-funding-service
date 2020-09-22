package org.innovateuk.ifs.application.forms.questions.applicantdetails.populator;

import org.innovateuk.ifs.BaseUnitTest;
import org.innovateuk.ifs.application.forms.questions.applicationdetails.model.ApplicationDetailsViewModel;
import org.innovateuk.ifs.application.forms.questions.applicationdetails.populator.ApplicationDetailsViewModelPopulator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.QuestionStatusRestService;
import org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.user.service.UserRestService;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDate;
import java.time.ZonedDateTime;

import static java.util.Collections.singleton;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.category.builder.InnovationAreaResourceBuilder.newInnovationAreaResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.publiccontent.resource.FundingType.KTP;
import static org.innovateuk.ifs.competition.publiccontent.resource.FundingType.PROCUREMENT;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.CLOSED;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.LEADAPPLICANT;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

public class ApplicationDetailsViewModelPopulatorTest extends BaseUnitTest {

    @InjectMocks
    private ApplicationDetailsViewModelPopulator populator;

    @Mock
    private QuestionStatusRestService questionStatusRestService;

    @Mock
    private OrganisationRestService organisationRestService;

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private UserRestService userRestService;


    @Test
    public void populate() {
        long questionId = 1L;

        CompetitionResource competitionResource = CompetitionResourceBuilder
                .newCompetitionResource()
                .withCompetitionStatus(CLOSED)
                .withFundingType(PROCUREMENT)
                .withEndDate(ZonedDateTime.now())
                .withMinProjectDuration(1)
                .withMaxProjectDuration(30)
                .build();

        ApplicationResource application = newApplicationResource()
                .withCompetition(competitionResource.getId())
                .withInnovationArea(newInnovationAreaResource().build())
                .build();

        UserResource user = newUserResource().build();

        ProcessRoleResource leadRole = newProcessRoleResource()
                .withRole(LEADAPPLICANT)
                .build();

        OrganisationResource organisation = newOrganisationResource().build();

        when(competitionRestService.getCompetitionById(competitionResource.getId())).thenReturn(restSuccess(competitionResource));
        when(organisationRestService.getByUserAndApplicationId(user.getId(), application.getId())).thenReturn(restSuccess(organisation));
        when(userRestService.findProcessRole(user.getId(), application.getId())).thenReturn(restSuccess(leadRole));
        when(questionStatusRestService.getMarkedAsComplete(application.getId(), organisation.getId())).thenReturn(completedFuture(singleton(questionId)));

        ApplicationDetailsViewModel viewModel = populator.populate(application, questionId, user);

        assertThat(viewModel.isReadonly(), equalTo(true));
        assertThat(viewModel.isComplete(), equalTo(true));
        assertThat(viewModel.isProcurementCompetition(), equalTo(true));
    }
}
