package org.innovateuk.ifs.publiccontent.modelpopulator.section;

import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.publiccontent.viewmodel.section.SearchInformationViewModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.ZonedDateTime;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentResourceBuilder.newPublicContentResource;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentSectionResourceBuilder.newPublicContentSectionResource;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SearchInformationViewModelPopulatorTest {

    private static final Long COMPETITION_ID = 1L;
    private static final String FUNDING_RANGE = "FUNDING_RANGE";
    private static final String ELIGIBILITY_SUMMARY = "SUMMARY";
    private static final String SHORT_DESCRIPTION = "SHORT_DESCRIPTION";
    private static final List<String> KEYWORDS = asList("keyword1", "keyword2");

    @InjectMocks
    private SearchInformationViewModelPopulator target;

    @Mock
    private CompetitionService competitionService;

    @Test
    public void testPopulate() {
        boolean readOnly = true;
        PublicContentSectionResource section = newPublicContentSectionResource()
                .withType(PublicContentSectionType.SEARCH).build();
        PublicContentResource resource = newPublicContentResource()
                .withProjectFundingRange(FUNDING_RANGE)
                .withEligibilitySummary(ELIGIBILITY_SUMMARY)
                .withKeywords(KEYWORDS)
                .withShortDescription(SHORT_DESCRIPTION)
                .withPublishDate(ZonedDateTime.now())
                .withCompetitionId(COMPETITION_ID)
                .withContentSections(asList(section)).build();
        CompetitionResource competition = CompetitionResourceBuilder.newCompetitionResource().build();
        when(competitionService.getById(COMPETITION_ID)).thenReturn(competition);

        SearchInformationViewModel viewModel = target.populate(resource, readOnly);

        assertThat(viewModel.isReadOnly(), equalTo(readOnly));
        assertThat(viewModel.getCompetition(), equalTo(competition));
        assertThat(viewModel.getSection(), equalTo(section));
        assertThat(viewModel.isPublished(), equalTo(true));
    }
}
