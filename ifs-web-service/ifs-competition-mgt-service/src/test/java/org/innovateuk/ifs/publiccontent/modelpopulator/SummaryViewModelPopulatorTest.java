package org.innovateuk.ifs.publiccontent.modelpopulator;

import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.publiccontent.modelpopulator.section.SummaryViewModelPopulator;
import org.innovateuk.ifs.publiccontent.viewmodel.section.SummaryViewModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.ZonedDateTime;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentResourceBuilder.newPublicContentResource;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentSectionResourceBuilder.newPublicContentSectionResource;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SummaryViewModelPopulatorTest {

    private static final Long COMPETITION_ID = 1L;
    private static final String DESCRIPTION = "SUMMARY";
    private static final FundingType FUNDING_TYPE = FundingType.GRANT;
    private static final String PROJECT_SIZE = "PROJECT_SIZE";

    @InjectMocks
    private SummaryViewModelPopulator target;

    @Mock
    private CompetitionService competitionService;

    @Test
    public void testPopulate() {
        boolean readOnly = true;
        PublicContentSectionResource section = newPublicContentSectionResource()
                .withType(PublicContentSectionType.SUMMARY).build();

        PublicContentResource resource = newPublicContentResource()
                .withSummary(DESCRIPTION)
                .withFundingType(FUNDING_TYPE)
                .withProjectSize(PROJECT_SIZE)
                .withPublishDate(ZonedDateTime.now())
                .withCompetitionId(COMPETITION_ID)
                .withContentSections(asList(section)).build();

        CompetitionResource competition = CompetitionResourceBuilder.newCompetitionResource().build();
        when(competitionService.getById(COMPETITION_ID)).thenReturn(competition);

        SummaryViewModel viewModel = target.populate(resource, readOnly);

        assertThat(viewModel.isReadOnly(), equalTo(readOnly));
        assertThat(viewModel.getCompetition(), equalTo(competition));
        assertThat(viewModel.getSection(), equalTo(section));
        assertThat(viewModel.isPublished(), equalTo(true));
    }
}
