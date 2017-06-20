package org.innovateuk.ifs.competition.populator;

import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.category.service.CategoryRestService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentItemPageResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentItemResource;
import org.innovateuk.ifs.competition.viewmodel.CompetitionSearchViewModel;
import org.innovateuk.ifs.publiccontent.service.PublicContentItemRestServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.category.builder.InnovationAreaResourceBuilder.newInnovationAreaResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentItemPageResourceBuilder.newPublicContentItemPageResource;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentItemResourceBuilder.newPublicContentItemResource;
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

    @Test
    public void createItemSearchViewModel() throws Exception {
        Optional<Long> expectedInnovationAreaId = Optional.of(10L);
        Optional<String> expectedKeywords = Optional.of("test%20abc");
        Optional<Integer> expectedPageNumber = Optional.of(1);

        List<PublicContentItemResource> publicContentItemResources = newPublicContentItemResource().build(2);

        PublicContentItemPageResource publicContentItemPageResourceList = newPublicContentItemPageResource()
                .withTotalElements(2L)
                .withContent(publicContentItemResources).build();

        RestResult<PublicContentItemPageResource> restResult = restSuccess(publicContentItemPageResourceList);

        List<InnovationAreaResource> innovationAreaResources = newInnovationAreaResource().build(2);

        when(publicContentItemRestService.getByFilterValues(expectedInnovationAreaId,
                expectedKeywords, expectedPageNumber, CompetitionSearchViewModel.PAGE_SIZE)).thenReturn(restResult);
        when(categoryRestService.getInnovationAreas()).thenReturn(restSuccess(innovationAreaResources));

        CompetitionSearchViewModel viewModel = competitionSearchPopulator.createItemSearchViewModel(expectedInnovationAreaId, expectedKeywords, expectedPageNumber);


        verify(publicContentItemRestService, times(1)).getByFilterValues(expectedInnovationAreaId,expectedKeywords, expectedPageNumber,CompetitionSearchViewModel.PAGE_SIZE);
        verify(categoryRestService, times(1)).getInnovationAreas();

        assertEquals(publicContentItemResources, viewModel.getPublicContentItems());
        assertEquals(publicContentItemPageResourceList.getTotalElements(), (long) viewModel.getTotalResults());
        assertEquals(1, (long) viewModel.getPageNumber());
    }

    @Test
    public void createItemSearchViewModel_noSearchParamsShouldResultInDefaultSearch() throws Exception {
        Optional<Long> expectedInnovationAreaId = Optional.empty();
        Optional<String> expectedKeywords = Optional.empty();
        Optional<Integer> expectedPageNumber = Optional.empty();

        List<PublicContentItemResource> publicContentItemResources = newPublicContentItemResource().build(2);

        PublicContentItemPageResource publicContentItemPageResourceList = newPublicContentItemPageResource()
                .withTotalElements(2L)
                .withContent(publicContentItemResources).build();

        RestResult<PublicContentItemPageResource> restResult = restSuccess(publicContentItemPageResourceList);

        List<InnovationAreaResource> innovationAreaResources = newInnovationAreaResource().build(2);

        when(publicContentItemRestService.getByFilterValues(expectedInnovationAreaId,
                expectedKeywords, expectedPageNumber, CompetitionSearchViewModel.PAGE_SIZE)).thenReturn(restResult);
        when(categoryRestService.getInnovationAreas()).thenReturn(restSuccess(innovationAreaResources));

        CompetitionSearchViewModel viewModel = competitionSearchPopulator.createItemSearchViewModel(expectedInnovationAreaId, expectedKeywords, expectedPageNumber);

        assertEquals(publicContentItemResources, viewModel.getPublicContentItems());
        assertEquals(publicContentItemPageResourceList.getTotalElements(), (long)viewModel.getTotalResults());
        assertEquals(false, viewModel.hasPreviousPage());
        assertEquals(false, viewModel.hasNextPage());
    }

    @Test
    public void createItemSearchViewModel_firstPageShouldProvidePageModelParams() throws Exception {
        Optional<Long> expectedInnovationAreaId = Optional.of(10L);
        Optional<String> expectedKeywords = Optional.of("test");
        Optional<Integer> expectedPageNumber = Optional.of(0);

        List<PublicContentItemResource> publicContentItemResources = newPublicContentItemResource().build(15);

        PublicContentItemPageResource publicContentItemPageResourceList = newPublicContentItemPageResource()
                .withTotalElements(15L)
                .withContent(publicContentItemResources).build();

        RestResult<PublicContentItemPageResource> restResult = restSuccess(publicContentItemPageResourceList);

        List<InnovationAreaResource> innovationAreaResources = newInnovationAreaResource().build(2);

        when(publicContentItemRestService.getByFilterValues(expectedInnovationAreaId,
                expectedKeywords, expectedPageNumber, CompetitionSearchViewModel.PAGE_SIZE)).thenReturn(restResult);
        when(categoryRestService.getInnovationAreas()).thenReturn(restSuccess(innovationAreaResources));

        CompetitionSearchViewModel viewModel = competitionSearchPopulator.createItemSearchViewModel(expectedInnovationAreaId, expectedKeywords, expectedPageNumber);

        assertEquals(publicContentItemResources, viewModel.getPublicContentItems());
        assertEquals(publicContentItemPageResourceList.getTotalElements(), (long)viewModel.getTotalResults());
        assertEquals(0L, (long)viewModel.getPageNumber());
        assertEquals(15L, (long)viewModel.getTotalResults());
        assertEquals(false, viewModel.hasPreviousPage());

        assertEquals(true, viewModel.hasNextPage());
        assertEquals(11, viewModel.getNextPageStart());
        assertEquals(20, viewModel.getNextPageEnd());
        assertEquals("innovationAreaId=10&keywords=test&page=1", viewModel.getNextPageLink());
    }

    @Test
    public void createItemSearchViewModel_middlePageShouldProvidePageModelParams() throws Exception {
        Optional<Long> expectedInnovationAreaId = Optional.of(10L);
        Optional<String> expectedKeywords = Optional.of("test");
        Optional<Integer> expectedPageNumber = Optional.of(1);

        List<PublicContentItemResource> publicContentItemResources = newPublicContentItemResource().build(21);

        PublicContentItemPageResource publicContentItemPageResourceList = newPublicContentItemPageResource()
                .withTotalElements(21L)
                .withContent(publicContentItemResources).build();

        RestResult<PublicContentItemPageResource> restResult = restSuccess(publicContentItemPageResourceList);

        List<InnovationAreaResource> innovationAreaResources = newInnovationAreaResource().build(2);

        when(publicContentItemRestService.getByFilterValues(expectedInnovationAreaId,
                expectedKeywords, expectedPageNumber, CompetitionSearchViewModel.PAGE_SIZE)).thenReturn(restResult);
        when(categoryRestService.getInnovationAreas()).thenReturn(restSuccess(innovationAreaResources));

        CompetitionSearchViewModel viewModel = competitionSearchPopulator.createItemSearchViewModel(expectedInnovationAreaId, expectedKeywords, expectedPageNumber);

        assertEquals(publicContentItemResources, viewModel.getPublicContentItems());
        assertEquals(publicContentItemPageResourceList.getTotalElements(), (long)viewModel.getTotalResults());
        assertEquals(1L, (long)viewModel.getPageNumber());
        assertEquals(21L, (long)viewModel.getTotalResults());

        assertEquals(true, viewModel.hasPreviousPage());
        assertEquals(1, viewModel.getPreviousPageStart());
        assertEquals(10, viewModel.getPreviousPageEnd());
        assertEquals("innovationAreaId=10&keywords=test&page=0", viewModel.getPreviousPageLink());

        assertEquals(true, viewModel.hasNextPage());
        assertEquals(21, viewModel.getNextPageStart());
        assertEquals(30, viewModel.getNextPageEnd());
        assertEquals("innovationAreaId=10&keywords=test&page=2", viewModel.getNextPageLink());

    }

    @Test
    public void createItemSearchViewModel_endPageShouldProvidePageModelParams() throws Exception {
        Optional<Long> expectedInnovationAreaId = Optional.of(10L);
        Optional<String> expectedKeywords = Optional.of("test");
        Optional<Integer> expectedPageNumber = Optional.of(2);

        List<PublicContentItemResource> publicContentItemResources = newPublicContentItemResource().build(21);

        PublicContentItemPageResource publicContentItemPageResourceList = newPublicContentItemPageResource()
                .withTotalElements(21L)
                .withContent(publicContentItemResources).build();

        RestResult<PublicContentItemPageResource> restResult = restSuccess(publicContentItemPageResourceList);

        List<InnovationAreaResource> innovationAreaResources = newInnovationAreaResource().build(2);

        when(publicContentItemRestService.getByFilterValues(expectedInnovationAreaId,
                expectedKeywords, expectedPageNumber, CompetitionSearchViewModel.PAGE_SIZE)).thenReturn(restResult);
        when(categoryRestService.getInnovationAreas()).thenReturn(restSuccess(innovationAreaResources));

        CompetitionSearchViewModel viewModel = competitionSearchPopulator.createItemSearchViewModel(expectedInnovationAreaId, expectedKeywords, expectedPageNumber);

        assertEquals(publicContentItemResources, viewModel.getPublicContentItems());
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

        List<PublicContentItemResource> publicContentItemResources = newPublicContentItemResource().build(21);
        PublicContentItemPageResource publicContentItemPageResourceList = newPublicContentItemPageResource()
                .withTotalElements(21L)
                .withContent(publicContentItemResources).build();
        RestResult<PublicContentItemPageResource> restResult = restSuccess(publicContentItemPageResourceList);

        List<InnovationAreaResource> innovationAreaResources = newInnovationAreaResource().build(2);

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

        List<PublicContentItemResource> publicContentItemResources = newPublicContentItemResource().build(21);
        PublicContentItemPageResource publicContentItemPageResourceList = newPublicContentItemPageResource()
                .withTotalElements(21L)
                .withContent(publicContentItemResources).build();
        RestResult<PublicContentItemPageResource> restResult = restSuccess(publicContentItemPageResourceList);

        List<InnovationAreaResource> innovationAreaResources = newInnovationAreaResource().build(2);

        when(publicContentItemRestService.getByFilterValues(expectedInnovationAreaId,
                expectedKeywords, expectedPageNumber, CompetitionSearchViewModel.PAGE_SIZE)).thenReturn(restResult);
        when(categoryRestService.getInnovationAreas()).thenReturn(restSuccess(innovationAreaResources));

        CompetitionSearchViewModel viewModel = competitionSearchPopulator.createItemSearchViewModel(expectedInnovationAreaId, expectedKeywords, expectedPageNumber);

        assertEquals("innovationAreaId=10&page=3", viewModel.getNextPageLink());
        assertEquals("innovationAreaId=10&page=1", viewModel.getPreviousPageLink());
    }

}