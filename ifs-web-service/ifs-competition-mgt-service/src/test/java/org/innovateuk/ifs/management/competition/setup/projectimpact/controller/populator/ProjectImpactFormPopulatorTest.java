package org.innovateuk.ifs.management.competition.setup.projectimpact.controller.populator;


import org.innovateuk.ifs.competition.builder.CompetitionApplicationConfigResourceBuilder;
import org.innovateuk.ifs.competition.resource.CompetitionApplicationConfigResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionApplicationConfigRestService;
import org.innovateuk.ifs.management.competition.setup.projectimpact.form.ProjectImpactForm;
import org.innovateuk.ifs.management.competition.setup.projectimpact.populator.ProjectImpactFormPopulator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
@RunWith(MockitoJUnitRunner.Silent.class)
public class ProjectImpactFormPopulatorTest {

    @InjectMocks
    private ProjectImpactFormPopulator service;

    @Mock
    private CompetitionApplicationConfigRestService competitionApplicationConfigRestService;

    @Test
    public void populateFormWithRequiredTrue() {
        long competitionId = 100L;
        CompetitionResource competitionResource = newCompetitionResource().withId(competitionId).build();
        CompetitionApplicationConfigResource competitionApplicationConfigResource = CompetitionApplicationConfigResourceBuilder
                .newCompetitionApplicationConfigResource()
                .withIMSurveyRequired(true).build();

        when(competitionApplicationConfigRestService.findOneByCompetitionId(competitionResource.getId())).thenReturn(restSuccess(competitionApplicationConfigResource));
        ProjectImpactForm result = (ProjectImpactForm) service.populateForm(competitionResource);
        assertTrue(result.getProjectImpactSurveyApplicable());
    }

    @Test
    public void populateFormWithRequiredFalse() {
        long competitionId = 100L;
        CompetitionResource competitionResource = newCompetitionResource().withId(competitionId).build();
        CompetitionApplicationConfigResource competitionApplicationConfigResource = CompetitionApplicationConfigResourceBuilder
                .newCompetitionApplicationConfigResource()
                .withIMSurveyRequired(false).build();

        when(competitionApplicationConfigRestService.findOneByCompetitionId(competitionResource.getId())).thenReturn(restSuccess(competitionApplicationConfigResource));
        ProjectImpactForm result = (ProjectImpactForm) service.populateForm(competitionResource);
        assertFalse(result.getProjectImpactSurveyApplicable());
    }


}