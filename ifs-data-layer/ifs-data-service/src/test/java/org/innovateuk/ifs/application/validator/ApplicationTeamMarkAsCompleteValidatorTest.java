package org.innovateuk.ifs.application.validator;

import org.innovateuk.ifs.application.builder.ApplicationBuilder;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.resource.ApplicationKtaInviteResource;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.invite.transactional.ApplicationInviteService;
import org.innovateuk.ifs.invite.transactional.ApplicationKtaInviteService;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.EDIStatus;
import org.innovateuk.ifs.user.resource.ProcessRoleType;
import org.innovateuk.ifs.user.resource.Title;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.validation.Errors;

import java.util.List;

import static java.util.Collections.singletonList;
import static org.assertj.core.util.Lists.emptyList;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.invite.builder.ApplicationInviteResourceBuilder.newApplicationInviteResource;
import static org.innovateuk.ifs.invite.builder.ApplicationKtaInviteResourceBuilder.newApplicationKtaInviteResource;
import static org.innovateuk.ifs.invite.builder.InviteOrganisationResourceBuilder.newInviteOrganisationResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationTeamMarkAsCompleteValidatorTest {

    @InjectMocks
    private ApplicationTeamMarkAsCompleteValidator validator;

    @Mock
    private ApplicationInviteService applicationInviteService;

    @Mock
    private ApplicationKtaInviteService applicationKtaInviteService;

    @Test
    public void rejectWithUnopenedInvite() {
        // given
        Application application = ApplicationBuilder.newApplication().withCompetition(newCompetition().build()).build();

        List<ApplicationInviteResource> inviteResources = singletonList(newApplicationInviteResource().withStatus(InviteStatus.SENT).build());
        InviteOrganisationResource inviteOrganisationResource = newInviteOrganisationResource().withInviteResources(inviteResources).build();
        given(applicationInviteService.getInvitesByApplication(application.getId())).willReturn(serviceSuccess(singletonList(inviteOrganisationResource)));

        Errors errors = mock(Errors.class);

        // when
        validator.validate(application, errors);

        // then
        verify(errors).reject(eq("validation.applicationteam.pending.invites"), any(), eq("validation.applicationteam.pending.invites"));
    }

    @Test
    public void acceptWithAllInvitesOpened() {
        // given
        Application application = ApplicationBuilder.newApplication().withCompetition(newCompetition().build()).build();

        List<ApplicationInviteResource> inviteResources = singletonList(newApplicationInviteResource().withStatus(InviteStatus.OPENED).build());
        InviteOrganisationResource inviteOrganisationResource = newInviteOrganisationResource().withInviteResources(inviteResources).build();
        given(applicationInviteService.getInvitesByApplication(application.getId())).willReturn(serviceSuccess(singletonList(inviteOrganisationResource)));

        Errors errors = mock(Errors.class);

        // when
        validator.validate(application, errors);

        // then
        verifyZeroInteractions(errors);
    }

    @Test
    public void acceptWithKtpCompetitionWithKtaProcessRole() {
        // given
        Application application = ApplicationBuilder.newApplication()
                .withProcessRole(newProcessRole().withRole(ProcessRoleType.KNOWLEDGE_TRANSFER_ADVISER).build())
                .withCompetition(newCompetition().withFundingType(FundingType.KTP).build()).build();

        given(applicationInviteService.getInvitesByApplication(application.getId())).willReturn(serviceSuccess(emptyList()));

        Errors errors = mock(Errors.class);

        // when
        validator.validate(application, errors);

        // then
        verifyZeroInteractions(applicationKtaInviteService, errors);
    }

    @Test
    public void rejectWithKtpCompetitionNoKtaProcessRoleAndNoInvite() {
        // given
        Application application = ApplicationBuilder.newApplication().withCompetition(newCompetition().withFundingType(FundingType.KTP).build()).build();

        given(applicationInviteService.getInvitesByApplication(application.getId())).willReturn(serviceSuccess(emptyList()));

        given(applicationKtaInviteService.getKtaInviteByApplication(application.getId())).willReturn(serviceSuccess(null));

        Errors errors = mock(Errors.class);

        // when
        validator.validate(application, errors);

        // then
        verify(errors).reject(eq("validation.kta.missing.invite"), any(), eq("validation.kta.missing.invite"));
    }

    @Test
    public void rejectWithKtpCompetitionNoKtaProcessRoleAndUnopenedKtaInvite() {
        // given
        Application application = ApplicationBuilder.newApplication().withCompetition(newCompetition().withFundingType(FundingType.KTP).build()).build();

        given(applicationInviteService.getInvitesByApplication(application.getId())).willReturn(serviceSuccess(emptyList()));

        ApplicationKtaInviteResource ktaInvite = newApplicationKtaInviteResource().withStatus(InviteStatus.SENT).build();
        given(applicationKtaInviteService.getKtaInviteByApplication(application.getId())).willReturn(serviceSuccess(ktaInvite));

        Errors errors = mock(Errors.class);

        // when
        validator.validate(application, errors);

        // then
        verify(errors).reject(eq("validation.kta.pending.invite"), any(), eq("validation.kta.pending.invite"));
    }

    @Test
    public void acceptWithKtpCompetitionNoKtaProcessRoleAllInvitesOpened() {
        // given
        Application application = ApplicationBuilder.newApplication().withCompetition(newCompetition().withFundingType(FundingType.KTP).build()).build();

        given(applicationInviteService.getInvitesByApplication(application.getId())).willReturn(serviceSuccess(emptyList()));

        ApplicationKtaInviteResource ktaInvite = newApplicationKtaInviteResource().withStatus(InviteStatus.OPENED).build();
        given(applicationKtaInviteService.getKtaInviteByApplication(application.getId())).willReturn(serviceSuccess(ktaInvite));

        Errors errors = mock(Errors.class);

        // when
        validator.validate(application, errors);

        // then
        verifyZeroInteractions(errors);
    }

    @Test
    public void acceptWithLeadApplicantInCompleteEDIStatus() {
        ReflectionTestUtils.setField(validator, "isEDIUpdateEnabled", true);
        User leadApplicant = newUser()
                .withTitle(Title.Dr)
                .withEmailAddress("Email@email.com")
                .withFirstName("Nico")
                .withEdiStatus(EDIStatus.INCOMPLETE)
                .build();

        Application application = newApplication().
                                   withCompetition(newCompetition().build()).
                                   withProcessRoles(newProcessRole().withUser(leadApplicant).withRole(ProcessRoleType.LEADAPPLICANT).build()).build();

        List<ApplicationInviteResource> inviteResources = singletonList(newApplicationInviteResource().withStatus(InviteStatus.OPENED).build());
        InviteOrganisationResource inviteOrganisationResource = newInviteOrganisationResource().withInviteResources(inviteResources).build();
        when(applicationInviteService.getInvitesByApplication(application.getId())).thenReturn(serviceSuccess(singletonList(inviteOrganisationResource)));

        Errors errors = mock(Errors.class);
        validator.validate(application, errors);

        verify(errors).reject(eq("validation.applicationteam.edi.status"), any(), eq("validation.applicationteam.edi.status"));
    }
}
