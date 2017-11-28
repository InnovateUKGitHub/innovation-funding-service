package org.innovateuk.ifs.competition.populator;

import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.category.service.CategoryRestService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.mapper.PublicContentItemViewModelMapper;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentItemPageResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentItemResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.competition.status.PublicContentStatusDeterminer;
import org.innovateuk.ifs.competition.status.PublicContentStatusText;
import org.innovateuk.ifs.competition.viewmodel.CompetitionSearchViewModel;
import org.innovateuk.ifs.competition.viewmodel.PublicContentItemViewModel;
import org.innovateuk.ifs.publiccontent.service.PublicContentItemRestServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.category.builder.InnovationAreaResourceBuilder.newInnovationAreaResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentItemPageResourceBuilder.newPublicContentItemPageResource;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentItemResourceBuilder.newPublicContentItemResource;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentResourceBuilder.newPublicContentResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CompetitionSearchPopulatorTest {

    @InjectMocks
    private CompetitionSearchPopulator competitionSearchPopulator;

    @Mock
    private PublicContentItemRestServiceImpl publicContentItemRestService;

    @Mock
    private CategoryRestService categoryRestService;

    @Mock
    private PublicContentStatusDeterminer publicContentStatusDeterminer;

    @Mock
    private PublicContentItemViewModelMapper publicContentItemViewModelMapper;

    private PublicContentItemPageResource publicContentItemPageResourceList;
    private List<InnovationAreaResource> innovationAreaResources;
    private List<PublicContentItemResource> publicContentItemResources;

    @Before
    public void setup() throws Exception {
        ZonedDateTime tomorrow = ZonedDateTime.now().plusDays(1);
        ZonedDateTime yesterday = ZonedDateTime.now().minusDays(1);


        PublicContentResource publicContentResource = newPublicContentResource()
                .withEligibilitySummary("summary")
                .withCompetitionId(1L)
                .withShortDescription("description")
                .build();

        publicContentItemResources = newPublicContentItemResource()
                .withCompetitionCloseDate(tomorrow)
                .withCompetitionOpenDate(yesterday)
                .withContentSection(publicContentResource)
                .build(2);

        publicContentItemPageResourceList = newPublicContentItemPageResource()
                .withTotalElements(2L)
                .withContent(publicContentItemResources).build();

        innovationAreaResources = newInnovationAreaResource().build(2);

        when(publicContentStatusDeterminer.getApplicablePublicContentStatusText(isA(PublicContentItemResource.class))).thenReturn(PublicContentStatusText.OPENING_SOON);
        when(categoryRestService.getInnovationAreas()).thenReturn(restSuccess(innovationAreaResources));
        when(publicContentItemViewModelMapper.mapToViewModel(any())).thenReturn(new PublicContentItemViewModel());
    }

    @Test
    public void createItemSearchViewModel() throws Exception {
        Optional<Long> expectedInnovationAreaId = Optional.of(10L);
        Optional<String> expectedKeywords = Optional.of("test%20abc");
        Optional<Integer> expectedPageNumber = Optional.of(1);

        RestResult<PublicContentItemPageResource> restResult = restSuccess(publicContentItemPageResourceList);

        when(publicContentItemRestService.getByFilterValues(expectedInnovationAreaId,
                expectedKeywords, expectedPageNumber, CompetitionSearchViewModel.PAGE_SIZE)).thenReturn(restResult);

        CompetitionSearchViewModel viewModel = competitionSearchPopulator.createItemSearchViewModel(expectedInnovationAreaId, expectedKeywords, expectedPageNumber);

        verify(publicContentItemRestService, times(1)).getByFilterValues(expectedInnovationAreaId,expectedKeywords, expectedPageNumber,CompetitionSearchViewModel.PAGE_SIZE);
        verify(categoryRestService, times(1)).getInnovationAreas();

        assertEquals(publicContentItemPageResourceList.getTotalElements(), (long) viewModel.getTotalResults());
        assertEquals(1, (long) viewModel.getPageNumber());
    }

    @Test
    public void createItemSearchViewModel_noSearchParamsShouldResultInDefaultSearch() throws Exception {
        Optional<Long> expectedInnovationAreaId = Optional.empty();
        Optional<String> expectedKeywords = Optional.empty();
        Optional<Integer> expectedPageNumber = Optional.empty();

        RestResult<PublicContentItemPageResource> restResult = restSuccess(publicContentItemPageResourceList);

        when(publicContentItemRestService.getByFilterValues(expectedInnovationAreaId,
                expectedKeywords, expectedPageNumber, CompetitionSearchViewModel.PAGE_SIZE)).thenReturn(restResult);
        when(categoryRestService.getInnovationAreas()).thenReturn(restSuccess(innovationAreaResources));

        CompetitionSearchViewModel viewModel = competitionSearchPopulator.createItemSearchViewModel(expectedInnovationAreaId, expectedKeywords, expectedPageNumber);

        assertEquals(publicContentItemPageResourceList.getTotalElements(), (long)viewModel.getTotalResults());
        assertEquals(false, viewModel.hasPreviousPage());
        assertEquals(false, viewModel.hasNextPage());
    }

    @Test
    public void createItemSearchViewModel_firstPageShouldProvidePageModelParams() throws Exception {
        Optional<Long> expectedInnovationAreaId = Optional.of(10L);
        Optional<String> expectedKeywords = Optional.of("test");
        Optional<Integer> expectedPageNumber = Optional.of(0);

        publicContentItemPageResourceList.setTotalElements(15L);

        RestResult<PublicContentItemPageResource> restResult = restSuccess(publicContentItemPageResourceList);

        when(publicContentItemRestService.getByFilterValues(expectedInnovationAreaId,
                expectedKeywords, expectedPageNumber, CompetitionSearchViewModel.PAGE_SIZE)).thenReturn(restResult);
        when(categoryRestService.getInnovationAreas()).thenReturn(restSuccess(innovationAreaResources));

        CompetitionSearchViewModel viewModel = competitionSearchPopulator.createItemSearchViewModel(expectedInnovationAreaId, expectedKeywords, expectedPageNumber);

        assertEquals(publicContentItemPageResourceList.getTotalElements(), (long)viewModel.getTotalResults());
        assertEquals(0L, (long)viewModel.getPageNumber());
        assertEquals(15L, (long)viewModel.getTotalResults());
        assertEquals(false, viewModel.hasPreviousPage());

        assertEquals(true, viewModel.hasNextPage());
        assertEquals(11, viewModel.getNextPageStart());
        assertEquals(15, viewModel.getNextPageEnd());
        assertEquals("innovationAreaId=10&keywords=test&page=1", viewModel.getNextPageLink());
    }

    @Test
    public void createItemSearchViewModel_middlePageShouldProvidePageModelParams() throws Exception {
        Optional<Long> expectedInnovationAreaId = Optional.of(10L);
        Optional<String> expectedKeywords = Optional.of("test");
        Optional<Integer> expectedPageNumber = Optional.of(1);

        publicContentItemPageResourceList.setTotalElements(21L);

        RestResult<PublicContentItemPageResource> restResult = restSuccess(publicContentItemPageResourceList);

        when(publicContentItemRestService.getByFilterValues(expectedInnovationAreaId,
                expectedKeywords, expectedPageNumber, CompetitionSearchViewModel.PAGE_SIZE)).thenReturn(restResult);
        when(categoryRestService.getInnovationAreas()).thenReturn(restSuccess(innovationAreaResources));

        CompetitionSearchViewModel viewModel = competitionSearchPopulator.createItemSearchViewModel(expectedInnovationAreaId, expectedKeywords, expectedPageNumber);

        assertEquals(publicContentItemPageResourceList.getTotalElements(), (long)viewModel.getTotalResults());
        assertEquals(1L, (long)viewModel.getPageNumber());
        assertEquals(21L, (long)viewModel.getTotalResults());

        assertEquals(true, viewModel.hasPreviousPage());
        assertEquals(1, viewModel.getPreviousPageStart());
        assertEquals(10, viewModel.getPreviousPageEnd());
        assertEquals("innovationAreaId=10&keywords=test&page=0", viewModel.getPreviousPageLink());

        assertEquals(true, viewModel.hasNextPage());
        assertEquals(21, viewModel.getNextPageStart());
        assertEquals(21, viewModel.getNextPageEnd());
        assertEquals("innovationAreaId=10&keywords=test&page=2", viewModel.getNextPageLink());

    }

    @Test
    public void createItemSearchViewModel_endPageShouldProvidePageModelParams() throws Exception {
        Optional<Long> expectedInnovationAreaId = Optional.of(10L);
        Optional<String> expectedKeywords = Optional.of("test");
        Optional<Integer> expectedPageNumber = Optional.of(2);

        publicContentItemPageResourceList.setTotalElements(21L);

        RestResult<PublicContentItemPageResource> restResult = restSuccess(publicContentItemPageResourceList);

        when(publicContentItemRestService.getByFilterValues(expectedInnovationAreaId,
                expectedKeywords, expectedPageNumber, CompetitionSearchViewModel.PAGE_SIZE)).thenReturn(restResult);
        when(categoryRestService.getInnovationAreas()).thenReturn(restSuccess(innovationAreaResources));

        CompetitionSearchViewModel viewModel = competitionSearchPopulator.createItemSearchViewModel(expectedInnovationAreaId, expectedKeywords, expectedPageNumber);

        assertEquals(publicContentItemPageResourceList.getTotalElements(), (long)viewModel.getTotalResults());
        assertEquals(2L, (long)viewModel.getPageNumber());
        assertEquals(21L, (long)viewModel.getTotalResults());

        assertEquals(true, viewModel.hasPreviousPage());
        assertEquals(11, viewModel.getPreviousPageStart());
        assertEquals(20, viewModel.getPreviousPageEnd());
        assertEquals("innovationAreaId=10&keywords=test&page=1", viewModel.getPreviousPageLink());

        assertEquals(false, viewModel.hasNextPage());
    }

    @Test
    public void createItemSearchViewModel_pageNumberShouldBeAddedToSearchParamInLinks() throws Exception {
        Optional<Long> expectedInnovationAreaId = Optional.of(10L);
        Optional<String> expectedKeywords = Optional.of("test");
        Optional<Integer> expectedPageNumber = Optional.empty();

        publicContentItemPageResourceList.setTotalElements(21L);

        RestResult<PublicContentItemPageResource> restResult = restSuccess(publicContentItemPageResourceList);

        when(publicContentItemRestService.getByFilterValues(expectedInnovationAreaId,
                expectedKeywords, expectedPageNumber, CompetitionSearchViewModel.PAGE_SIZE)).thenReturn(restResult);
        when(categoryRestService.getInnovationAreas()).thenReturn(restSuccess(innovationAreaResources));

        CompetitionSearchViewModel viewModel = competitionSearchPopulator.createItemSearchViewModel(expectedInnovationAreaId, expectedKeywords, expectedPageNumber);

        assertEquals("innovationAreaId=10&keywords=test&page=1", viewModel.getNextPageLink());
        assertEquals("innovationAreaId=10&keywords=test&page=-1", viewModel.getPreviousPageLink());
    }

    @Test
    public void createItemSearchViewModel_searchParamsShouldBePartiallyReflectedInLinks() throws Exception {
        Optional<Long> expectedInnovationAreaId = Optional.of(10L);
        Optional<String> expectedKeywords = Optional.empty();
        Optional<Integer> expectedPageNumber = Optional.of(2);

        publicContentItemPageResourceList.setTotalElements(21L);

        RestResult<PublicContentItemPageResource> restResult = restSuccess(publicContentItemPageResourceList);

        when(publicContentItemRestService.getByFilterValues(expectedInnovationAreaId,
                expectedKeywords, expectedPageNumber, CompetitionSearchViewModel.PAGE_SIZE)).thenReturn(restResult);
        when(categoryRestService.getInnovationAreas()).thenReturn(restSuccess(innovationAreaResources));

        CompetitionSearchViewModel viewModel = competitionSearchPopulator.createItemSearchViewModel(expectedInnovationAreaId, expectedKeywords, expectedPageNumber);

        assertEquals("innovationAreaId=10&page=3", viewModel.getNextPageLink());
        assertEquals("innovationAreaId=10&page=1", viewModel.getPreviousPageLink());
    }

    @Test
    public void createItemSearchViewModel_statusTextEnumShouldBeConsultedForStatusText() throws Exception {
        Optional<Long> expectedInnovationAreaId = Optional.of(10L);
        Optional<String> expectedKeywords = Optional.empty();
        Optional<Integer> expectedPageNumber = Optional.of(2);

        publicContentItemPageResourceList.setTotalElements(21L);

        RestResult<PublicContentItemPageResource> restResult = restSuccess(publicContentItemPageResourceList);

        when(publicContentItemRestService.getByFilterValues(expectedInnovationAreaId,
                expectedKeywords, expectedPageNumber, CompetitionSearchViewModel.PAGE_SIZE)).thenReturn(restResult);
        when(categoryRestService.getInnovationAreas()).thenReturn(restSuccess(innovationAreaResources));

        CompetitionSearchViewModel viewModel = competitionSearchPopulator.createItemSearchViewModel(expectedInnovationAreaId, expectedKeywords, expectedPageNumber);

        verify(publicContentStatusDeterminer, times(2)).getApplicablePublicContentStatusText(any());
    }

}