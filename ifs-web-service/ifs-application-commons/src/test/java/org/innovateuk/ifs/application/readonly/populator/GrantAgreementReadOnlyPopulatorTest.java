package org.innovateuk.ifs.application.readonly.populator;

import org.innovateuk.ifs.application.readonly.ApplicationReadOnlyData;
import org.innovateuk.ifs.application.readonly.viewmodel.GrantAgreementReadOnlyViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.granttransfer.service.EuGrantTransferRestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static java.util.Collections.emptyList;
import static java.util.Optional.empty;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.application.readonly.ApplicationReadOnlySettings.defaultSettings;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static org.innovateuk.ifs.form.builder.QuestionResourceBuilder.newQuestionResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GrantAgreementReadOnlyPopulatorTest {

    @InjectMocks
    private GrantAgreementReadOnlyPopulator populator;

    @Mock
    private EuGrantTransferRestService euGrantTransferRestService;

    @Test
    public void populate() {
        ApplicationResource application = newApplicationResource()
                .build();
        CompetitionResource competition = newCompetitionResource()
                .build();
        QuestionResource question = newQuestionResource()
                .withShortName("Grant agreement")
                .build();

        FileEntryResource file = newFileEntryResource().withName("file.name").build();
        when(euGrantTransferRestService.findGrantAgreement(application.getId())).thenReturn(restSuccess(file));

        ApplicationReadOnlyData data = new ApplicationReadOnlyData(application, competition, newUserResource().build(), empty(), emptyList(), emptyList(), emptyList(), emptyList(), emptyList());

        GrantAgreementReadOnlyViewModel viewModel = populator.populate(competition, question, data, defaultSettings());

        assertEquals("file.name", viewModel.getFilename());

        assertEquals("Grant agreement", viewModel.getName());
        assertEquals(application.getId(), (Long) viewModel.getApplicationId());
        assertEquals(question.getId(), (Long) viewModel.getQuestionId());
        assertFalse(viewModel.isComplete());
        assertFalse(viewModel.isLead());
    }
}
