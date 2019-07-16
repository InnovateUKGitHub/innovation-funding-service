package org.innovateuk.ifs.management.publiccontent.modelpopulator;

import org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.management.publiccontent.modelpopulator.section.SummaryViewModelPopulator;
import org.innovateuk.ifs.management.publiccontent.viewmodel.section.SummaryViewModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.ZonedDateTime;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentResourceBuilder.newPublicContentResource;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentSectionResourceBuilder.newPublicContentSectionResource;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class SummaryViewModelPopulatorTest {

    private static final Long COMPETITION_ID = 1L;
    private static final String DESCRIPTION = "SUMMARY";
    private static final FundingType FUNDING_TYPE = FundingType.GRANT;
    private static final String PROJECT_SIZE = "PROJECT_SIZE";

    @InjectMocks
    private SummaryViewModelPopulator target;

    @Mock
    private CompetitionRestService competitionRestService;

    @Test
    public void testPopulate() {
        boolean readOnly = true;
        PublicContentSectionResource section = newPublicContentSectionResource()
                .withType(PublicContentSectionType.SUMMARY).build();

        PublicContentResource resource = newPublicContentResource()
                .withSummary(DESCRIPTION)
                .withProjectSize(PROJECT_SIZE)
                .withPublishDate(ZonedDateTime.now())
                .withCompetitionId(COMPETITION_ID)
                .withContentSections(asList(section)).build();

        CompetitionResource competition = CompetitionResourceBuilder.newCompetitionResource().build();
        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competition));

        SummaryViewModel viewModel = target.populate(resource, readOnly);

        assertThat(viewModel.isReadOnly(), equalTo(readOnly));
        assertThat(viewModel.getCompetition(), equalTo(competition));
        assertThat(viewModel.getSection(), equalTo(section));
        assertThat(viewModel.isPublished(), equalTo(true));
    }
}
