package org.innovateuk.ifs.competitionsetup.service.modelpopulator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competitionsetup.service.modelpopulator.application.AdditionalInfoModelPopulator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.time.LocalDateTime;

import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class AdditionalInfoModelPopulatorTest {

    @InjectMocks
    private AdditionalInfoModelPopulator populator;

    @Test
    public void testSectionToPopulateModel() {
        CompetitionSetupSection result = populator.sectionToPopulateModel();

        assertEquals(CompetitionSetupSection.ADDITIONAL_INFO, result);
    }

    @Test
    public void testPopulateModel() {
        Model model = new ExtendedModelMap();

        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);

        CompetitionResource competition = newCompetitionResource()
                .withSetupComplete(true)
                .withStartDate(yesterday)
                .withFundersPanelDate(yesterday)
                .build();

        populator.populateModel(model, competition);

        assertEquals(true, model.asMap().get("preventEdit"));
        assertEquals(true, model.asMap().get("isSetupAndLive"));
        assertEquals(true, model.asMap().get("setupComplete"));
    }

    @Test
    public void testPopulateModel_competitionNotSetupAndLive() {
        Model model = new ExtendedModelMap();

        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);

        CompetitionResource competition = newCompetitionResource()
                .withSetupComplete(false)
                .withFundersPanelDate(tomorrow)
                .withStartDate(yesterday)
                .build();

        populator.populateModel(model, competition);

        assertEquals(false, model.asMap().get("preventEdit"));
        assertEquals(false, model.asMap().get("isSetupAndLive"));
        assertEquals(false, model.asMap().get("setupComplete"));
    }
}
