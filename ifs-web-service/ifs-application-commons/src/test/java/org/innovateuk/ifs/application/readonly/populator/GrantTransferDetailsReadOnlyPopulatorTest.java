package org.innovateuk.ifs.application.readonly.populator;

import org.innovateuk.ifs.application.readonly.ApplicationReadOnlyData;
import org.innovateuk.ifs.application.readonly.viewmodel.GrantTransferDetailsReadOnlyViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.granttransfer.resource.EuActionTypeResource;
import org.innovateuk.ifs.granttransfer.service.EuGrantTransferRestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.time.LocalDate;

import static java.util.Collections.emptyList;
import static java.util.Optional.empty;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.application.readonly.ApplicationReadOnlySettings.defaultSettings;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.form.builder.QuestionResourceBuilder.newQuestionResource;
import static org.innovateuk.ifs.granttransfer.resource.EuGrantTransferResourceBuilder.newEuGrantTransferResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GrantTransferDetailsReadOnlyPopulatorTest {

    @InjectMocks
    private GrantTransferDetailsReadOnlyPopulator populator;

    @Mock
    private EuGrantTransferRestService euGrantTransferRestService;

    @Test
    public void populate() {
        ApplicationResource application = newApplicationResource()
                .build();
        CompetitionResource competition = newCompetitionResource()
                .build();
        QuestionResource question = newQuestionResource()
                .withShortName("Application details")
                .build();

        EuActionTypeResource actionTypeResource = new EuActionTypeResource();
        when(euGrantTransferRestService.findDetailsByApplicationId(application.getId())).thenReturn(restSuccess(newEuGrantTransferResource()
                .withActionType(actionTypeResource)
                .withFundingContribution(BigDecimal.TEN)
                .withProjectName("project")
                .withProjectStartDate(LocalDate.now())
                .withProjectEndDate(LocalDate.now())
                .withParticipantId("123456789")
                .withGrantAgreementNumber("123456")
                .withProjectCoordinator(true)
                .build()));

        ApplicationReadOnlyData data = new ApplicationReadOnlyData(application, competition, newUserResource().build(), empty(), emptyList(), emptyList(), emptyList(), emptyList(), emptyList());

        GrantTransferDetailsReadOnlyViewModel viewModel = populator.populate(question, data, defaultSettings());

        assertEquals(viewModel.getActionType(), actionTypeResource);
        assertEquals(viewModel.getFundingContribution(), BigDecimal.TEN);
        assertEquals(viewModel.getProjectName(), "project");
        assertEquals(viewModel.getParticipantId(), "123456789");
        assertEquals(viewModel.getGrantAgreementNumber(), "123456");
        assertEquals(viewModel.getProjectCoordinator(), true);

        assertEquals("Application details", viewModel.getName());
        assertEquals(application.getId(), (Long) viewModel.getApplicationId());
        assertEquals(question.getId(), (Long) viewModel.getQuestionId());
        assertFalse(viewModel.isComplete());
        assertFalse(viewModel.isLead());
    }
}
