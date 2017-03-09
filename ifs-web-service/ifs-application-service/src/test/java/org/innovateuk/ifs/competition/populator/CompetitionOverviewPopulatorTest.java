package org.innovateuk.ifs.competition.populator;

import org.innovateuk.ifs.category.service.CategoryRestService;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentItemResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.competition.viewmodel.CompetitionOverviewViewModel;
import org.innovateuk.ifs.competition.viewmodel.publiccontent.AbstractPublicSectionContentViewModel;
import org.innovateuk.ifs.publiccontent.service.PublicContentItemRestServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDateTime;

import static org.innovateuk.ifs.publiccontent.builder.PublicContentItemResourceBuilder.newPublicContentItemResource;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentResourceBuilder.newPublicContentResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CompetitionOverviewPopulatorTest {

    @InjectMocks
    private CompetitionOverviewPopulator populator;

    @Mock
    private PublicContentItemRestServiceImpl publicContentItemRestService;

    @Mock
    private CategoryRestService categoryRestService;

    private final LocalDateTime openDate = LocalDateTime.of(2017,1,1,0,0);
    private final LocalDateTime closeDate = LocalDateTime.of(2017,1,1,0,0);
    private final String competitionTitle = "Title of competition";
    private final String nonIfsUrl = "www.google.co.uk";

    @Mock
    private AbstractPublicSectionContentViewModel sectionContentViewModel;

    @Before
    public void setup() {
        when(sectionContentViewModel.getSectionType()).thenReturn(PublicContentSectionType.SUMMARY);
    }

    @Test
    public void populateViewModelTest_Default() throws Exception {
        PublicContentResource publicContentResource = newPublicContentResource()
                .withShortDescription("Short description")
                .withCompetitionId(23523L)
                .build();

        CompetitionOverviewViewModel viewModel = populator.populateViewModel(setupPublicContent(publicContentResource), sectionContentViewModel);

        assertEquals(publicContentResource.getShortDescription(), viewModel.getShortDescription());
        assertEquals(publicContentResource.getCompetitionId(), viewModel.getCompetitionId());
        assertEquals(openDate, viewModel.getCompetitionOpenDate());
        assertEquals(closeDate, viewModel.getCompetitionCloseDate());
        assertEquals(closeDate.minusDays(7), viewModel.getRegistrationCloseDate());
        assertEquals(competitionTitle, viewModel.getCompetitionTitle());
        assertEquals(nonIfsUrl, viewModel.getNonIfsUrl());
    }

    @Test
    public void populateViewModelTest_EmptyPublicContentResource() throws Exception {
        PublicContentResource publicContentResource = newPublicContentResource().build();

        CompetitionOverviewViewModel viewModel = populator.populateViewModel(setupPublicContent(publicContentResource), sectionContentViewModel);

        assertEquals(null, viewModel.getShortDescription());
        assertEquals(null, viewModel.getCompetitionId());
        assertEquals(openDate, viewModel.getCompetitionOpenDate());
        assertEquals(closeDate, viewModel.getCompetitionCloseDate());
        assertEquals(competitionTitle, viewModel.getCompetitionTitle());
    }

    @Test
    public void populateViewModelTest_NullPublicContentResource() throws Exception {
        CompetitionOverviewViewModel viewModel = populator.populateViewModel(setupPublicContent(null), sectionContentViewModel);

        assertEquals(null, viewModel.getShortDescription());
        assertEquals(null, viewModel.getCompetitionId());
        assertEquals(openDate, viewModel.getCompetitionOpenDate());
        assertEquals(closeDate, viewModel.getCompetitionCloseDate());
        assertEquals(competitionTitle, viewModel.getCompetitionTitle());
    }


    private PublicContentItemResource setupPublicContent(PublicContentResource publicContentResource) {
        PublicContentItemResource publicContentItem = newPublicContentItemResource()
                .withCompetitionOpenDate(openDate)
                .withCompetitionCloseDate(closeDate)
                .withCompetitionTitle(competitionTitle)
                .withContentSection(publicContentResource)
                .withNonIfsUrl(nonIfsUrl)
                .withNonIfs(Boolean.FALSE)
                .build();

        return publicContentItem;
    }
}