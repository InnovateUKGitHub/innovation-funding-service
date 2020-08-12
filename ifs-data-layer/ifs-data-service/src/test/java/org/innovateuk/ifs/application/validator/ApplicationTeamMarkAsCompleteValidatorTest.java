package org.innovateuk.ifs.application.validator;

import org.innovateuk.ifs.application.builder.ApplicationBuilder;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.resource.ApplicationKtaInviteResource;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.invite.transactional.ApplicationInviteService;
import org.innovateuk.ifs.invite.transactional.ApplicationKtaInviteService;
import org.innovateuk.ifs.user.resource.Role;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.Errors;

import java.util.List;

import static java.util.Collections.singletonList;
import static org.assertj.core.util.Lists.emptyList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.invite.builder.ApplicationInviteResourceBuilder.newApplicationInviteResource;
import static org.innovateuk.ifs.invite.builder.ApplicationKtaInviteResourceBuilder.newApplicationKtaInviteResource;
import static org.innovateuk.ifs.invite.builder.InviteOrganisationResourceBuilder.newInviteOrganisationResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
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
                .withProcessRole(newProcessRole().withRole(Role.KNOWLEDGE_TRANSFER_ADVISOR).build())
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

        given(applicationKtaInviteService.getKtaInvitesByApplication(application.getId())).willReturn(serviceSuccess(emptyList()));

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
        given(applicationKtaInviteService.getKtaInvitesByApplication(application.getId())).willReturn(serviceSuccess(singletonList(ktaInvite)));

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
        given(applicationKtaInviteService.getKtaInvitesByApplication(application.getId())).willReturn(serviceSuccess(singletonList(ktaInvite)));

        Errors errors = mock(Errors.class);

        // when
        validator.validate(application, errors);

        // then
        verifyZeroInteractions(errors);
    }
}
