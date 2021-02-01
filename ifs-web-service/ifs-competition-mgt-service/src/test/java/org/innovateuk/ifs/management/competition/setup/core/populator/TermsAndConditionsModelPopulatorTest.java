package org.innovateuk.ifs.management.competition.setup.core.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.resource.GrantTermsAndConditionsResource;
import org.innovateuk.ifs.competition.service.TermsAndConditionsRestService;
import org.innovateuk.ifs.management.competition.setup.core.viewmodel.GeneralSetupViewModel;
import org.innovateuk.ifs.management.competition.setup.core.viewmodel.TermsAndConditionsViewModel;
import org.innovateuk.ifs.user.builder.UserResourceBuilder;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.builder.GrantTermsAndConditionsResourceBuilder.newGrantTermsAndConditionsResource;
import static org.innovateuk.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class TermsAndConditionsModelPopulatorTest {

    @InjectMocks
    private TermsAndConditionsModelPopulator populator;

    @Mock
    private TermsAndConditionsRestService termsAndConditionsRestService;

    @Mock
    private CompetitionSetupPopulator competitionSetupPopulator;

    @Test
    public void populateModel() {
        List<GrantTermsAndConditionsResource> termsAndConditions = newGrantTermsAndConditionsResource().build(1);

        CompetitionResource competitionResource = newCompetitionResource()
                .withTermsAndConditions(termsAndConditions.get(0))
                .withCompetitionTerms(newFileEntryResource().build())
                .build();

        UserResource userResource = newUserResource().build();

        when(termsAndConditionsRestService.getById(competitionResource.getTermsAndConditions().getId()))
                .thenReturn(restSuccess(competitionResource.getTermsAndConditions()));
        when(termsAndConditionsRestService.getLatestVersionsForAllTermsAndConditions()).thenReturn(
                restSuccess(termsAndConditions));
        when(competitionSetupPopulator.populateGeneralModelAttributes(competitionResource, userResource, CompetitionSetupSection.TERMS_AND_CONDITIONS)).thenReturn(getBasicGeneralSetupView(competitionResource));

        TermsAndConditionsViewModel viewModel = populator.populateModel(
                competitionResource,
                userResource,
                false);

        assertEquals(CompetitionSetupSection.TERMS_AND_CONDITIONS, viewModel.getGeneral().getCurrentSection());
        assertEquals(viewModel.getTermsAndConditionsList(), termsAndConditions);
        assertEquals(viewModel.getCurrentTermsAndConditions(), termsAndConditions.get(0));
        assertTrue(viewModel.isTermsAndConditionsDocUploaded());
    }

    private GeneralSetupViewModel getBasicGeneralSetupView(CompetitionResource competition) {
        return new GeneralSetupViewModel(Boolean.FALSE, false, competition, CompetitionSetupSection.TERMS_AND_CONDITIONS,
                CompetitionSetupSection.values(), Boolean.TRUE, Boolean.FALSE);
    }
}
