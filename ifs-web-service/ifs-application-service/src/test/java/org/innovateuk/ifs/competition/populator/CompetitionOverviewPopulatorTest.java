package org.innovateuk.ifs.competition.populator;

import org.innovateuk.ifs.category.service.CategoryRestService;
import org.innovateuk.ifs.competition.populator.publiccontent.section.*;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentItemResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.competition.viewmodel.CompetitionOverviewViewModel;
import org.innovateuk.ifs.competition.viewmodel.publiccontent.AbstractPublicSectionContentViewModel;
import org.innovateuk.ifs.competition.viewmodel.publiccontent.section.*;
import org.innovateuk.ifs.publiccontent.service.PublicContentItemRestServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentItemResourceBuilder.newPublicContentItemResource;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentResourceBuilder.newPublicContentResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CompetitionOverviewPopulatorTest {

    @InjectMocks
    private CompetitionOverviewPopulator populator;

    @Mock
    private PublicContentItemRestServiceImpl publicContentItemRestService;

    @Mock
    private CategoryRestService categoryRestService;

    @Mock
    private List<AbstractPublicSectionContentViewModel> sectionContentViewModel;

    @Mock
    private DatesViewModelPopulator datesViewModelPopulator;

    @Mock
    private EligibilityViewModelPopulator eligibilityViewModelPopulator;

    @Mock
    private HowToApplyViewModelPopulator howToApplyViewModelPopulator;

    @Mock
    private ScopeViewModelPopulator scopeViewModelPopulator;

    @Mock
    private SummaryViewModelPopulator summaryViewModelPopulator;

    @Mock
    private SupportingInformationViewModelPopulator supportingInformationViewModelPopulator;

    private final LocalDateTime openDate = LocalDateTime.of(2017,1,1,0,0);
    private final LocalDateTime closeDate = LocalDateTime.of(2017,1,1,0,0);
    private final String competitionTitle = "Title of competition";
    private final String nonIfsUrl = "www.google.co.uk";

    @Before
    public void setup() {
        when(datesViewModelPopulator.getType()).thenReturn(PublicContentSectionType.DATES);
        when(datesViewModelPopulator.populate(any(PublicContentResource.class), anyBoolean(), any(PublicContentSectionType.class), any(PublicContentSectionType.class))).thenReturn(new DatesViewModel());

        when(eligibilityViewModelPopulator.getType()).thenReturn(PublicContentSectionType.ELIGIBILITY);
        when(eligibilityViewModelPopulator.populate(any(PublicContentResource.class), anyBoolean(), any(PublicContentSectionType.class), any(PublicContentSectionType.class))).thenReturn(new EligibilityViewModel());

        when(howToApplyViewModelPopulator.getType()).thenReturn(PublicContentSectionType.HOW_TO_APPLY);
        when(howToApplyViewModelPopulator.populate(any(PublicContentResource.class), anyBoolean(), any(PublicContentSectionType.class), any(PublicContentSectionType.class))).thenReturn(new HowToApplyViewModel());

        when(summaryViewModelPopulator.getType()).thenReturn(PublicContentSectionType.SUMMARY);
        when(summaryViewModelPopulator.populate(any(PublicContentResource.class), anyBoolean(), any(PublicContentSectionType.class), any(PublicContentSectionType.class))).thenReturn(new SummaryViewModel());

        when(scopeViewModelPopulator.getType()).thenReturn(PublicContentSectionType.SCOPE);
        when(scopeViewModelPopulator.populate(any(PublicContentResource.class), anyBoolean(), any(PublicContentSectionType.class), any(PublicContentSectionType.class))).thenReturn(new ScopeViewModel());

        when(supportingInformationViewModelPopulator.getType()).thenReturn(PublicContentSectionType.SUPPORTING_INFORMATION);
        when(supportingInformationViewModelPopulator.populate(any(PublicContentResource.class), anyBoolean(), any(PublicContentSectionType.class), any(PublicContentSectionType.class))).thenReturn(new SupportingInformationViewModel());

        populator.setSectionPopulator(asList(datesViewModelPopulator, eligibilityViewModelPopulator,
                howToApplyViewModelPopulator, summaryViewModelPopulator,
                scopeViewModelPopulator, supportingInformationViewModelPopulator));
    }

    @Test
    public void populateViewModelTest_Default() throws Exception {
        PublicContentResource publicContentResource = newPublicContentResource()
                .withShortDescription("Short description")
                .withCompetitionId(23523L)
                .build();

        CompetitionOverviewViewModel viewModel = populator.populateViewModel(setupPublicContent(publicContentResource), true);

        assertEquals(publicContentResource.getShortDescription(), viewModel.getShortDescription());
        assertEquals(publicContentResource.getCompetitionId(), viewModel.getCompetitionId());
        assertEquals(openDate, viewModel.getCompetitionOpenDate());
        assertEquals(closeDate, viewModel.getCompetitionCloseDate());
        assertEquals(closeDate.minusDays(7), viewModel.getRegistrationCloseDate());
        assertEquals(competitionTitle, viewModel.getCompetitionTitle());
        assertEquals(nonIfsUrl, viewModel.getNonIfsUrl());
        assertEquals(true, viewModel.isUserIsLoggedIn());
    }

    @Test
    public void populateViewModelTest_EmptyPublicContentResource() throws Exception {
        PublicContentResource publicContentResource = newPublicContentResource().build();

        CompetitionOverviewViewModel viewModel = populator.populateViewModel(setupPublicContent(publicContentResource), true);

        assertEquals(null, viewModel.getShortDescription());
        assertEquals(null, viewModel.getCompetitionId());
        assertEquals(openDate, viewModel.getCompetitionOpenDate());
        assertEquals(closeDate, viewModel.getCompetitionCloseDate());
        assertEquals(competitionTitle, viewModel.getCompetitionTitle());
        assertEquals(true, viewModel.isUserIsLoggedIn());
    }

    @Test
    public void populateViewModelTest_NullPublicContentResource() throws Exception {
        CompetitionOverviewViewModel viewModel = populator.populateViewModel(setupPublicContent(null), true);

        assertEquals(null, viewModel.getShortDescription());
        assertEquals(null, viewModel.getCompetitionId());
        assertEquals(openDate, viewModel.getCompetitionOpenDate());
        assertEquals(closeDate, viewModel.getCompetitionCloseDate());
        assertEquals(competitionTitle, viewModel.getCompetitionTitle());
        assertEquals(true, viewModel.isUserIsLoggedIn());
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