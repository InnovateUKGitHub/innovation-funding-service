package org.innovateuk.ifs.application.forms.questions.grantagreement.populator;

import org.innovateuk.ifs.application.forms.questions.grantagreement.model.GrantAgreementViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.application.service.QuestionStatusRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.granttransfer.service.EuGrantTransferRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.hibernate.validator.internal.util.CollectionHelper.asSet;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restFailure;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GrantAgreementViewModelPopulatorTest {

    @InjectMocks
    private GrantAgreementViewModelPopulator populator;

    @Mock
    private ApplicationRestService applicationRestService;

    @Mock
    private EuGrantTransferRestService euGrantTransferRestService;

    @Mock
    private QuestionStatusRestService questionStatusRestService;

    @Mock
    private OrganisationRestService organisationRestService;

    @Mock
    private CompetitionRestService competitionRestService;

    @Test
    public void populate() throws ExecutionException, InterruptedException {
        long applicationId = 1L;
        long questionId = 2L;
        long userId = 3L;
        long competitionId = 4L;
        ApplicationResource application = newApplicationResource()
                .withId(applicationId)
                .withCompetition(competitionId)
                .withApplicationState(ApplicationState.OPEN)
                .build();
        CompetitionResource competition = newCompetitionResource()
                .withCompetitionStatus(CompetitionStatus.OPEN)
                .build();
        OrganisationResource organisation = newOrganisationResource().build();
        Future<Set<Long>> future = mock(Future.class);

        when(applicationRestService.getApplicationById(applicationId)).thenReturn(restSuccess(application));
        when(competitionRestService.getCompetitionById(competitionId)).thenReturn(restSuccess(competition));
        when(euGrantTransferRestService.findGrantAgreement(applicationId)).thenReturn(restFailure(Collections.emptyList(), HttpStatus.NOT_FOUND));
        when(organisationRestService.getByUserAndApplicationId(userId, applicationId)).thenReturn(restSuccess(organisation));
        when(questionStatusRestService.getMarkedAsComplete(applicationId, organisation.getId())).thenReturn(future);
        when(future.get()).thenReturn(asSet(questionId));

        GrantAgreementViewModel viewModel = populator.populate(1L, 2L, 3L);

        assertEquals(viewModel.getApplicationId(), applicationId);
        assertEquals(viewModel.getQuestionId(), questionId);
        assertEquals(viewModel.isComplete(), true);
        assertEquals(viewModel.isOpen(), true);
        assertEquals(viewModel.isReadonly(), true);
    }
}
