package org.innovateuk.ifs.category.transactional;

import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.category.domain.InnovationArea;
import org.innovateuk.ifs.category.domain.InnovationSector;
import org.innovateuk.ifs.category.domain.ResearchCategory;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.category.resource.InnovationSectorResource;
import org.innovateuk.ifs.category.resource.ResearchCategoryResource;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;

import java.util.List;

import static java.util.Collections.EMPTY_LIST;
import static org.innovateuk.ifs.category.builder.InnovationAreaBuilder.newInnovationArea;
import static org.innovateuk.ifs.category.builder.InnovationAreaResourceBuilder.newInnovationAreaResource;
import static org.innovateuk.ifs.category.builder.InnovationSectorBuilder.newInnovationSector;
import static org.innovateuk.ifs.category.builder.InnovationSectorResourceBuilder.newInnovationSectorResource;
import static org.innovateuk.ifs.category.builder.ResearchCategoryBuilder.newResearchCategory;
import static org.innovateuk.ifs.category.builder.ResearchCategoryResourceBuilder.newResearchCategoryResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class CategoryServiceImplTest extends BaseUnitTestMocksTest {

    @InjectMocks
    private final CategoryService categoryService = new CategoryServiceImpl();

    @Test
    public void getInnovationAreas() {
        List<InnovationArea> innovationAreas = newInnovationArea().build(2);
        List<InnovationAreaResource> expectedInnovationAreaResources = newInnovationAreaResource().build(2);

        when(innovationAreaRepositoryMock.findAllByOrderByPriorityAsc()).thenReturn(innovationAreas);
        when(innovationAreaMapperMock.mapToResource(refEq(innovationAreas))).thenReturn(expectedInnovationAreaResources);

        List<InnovationAreaResource> actualInnovationAreaResources = categoryService.getInnovationAreas().getSuccessObject();

        assertEquals(expectedInnovationAreaResources, actualInnovationAreaResources);

        InOrder inOrder = inOrder(innovationAreaRepositoryMock, innovationAreaMapperMock, questionMapperMock);
        inOrder.verify(innovationAreaRepositoryMock).findAllByOrderByPriorityAsc();
        inOrder.verify(innovationAreaMapperMock).mapToResource(innovationAreas);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getInnovationSectors() {
        List<InnovationSector> innovationSectors = newInnovationSector().build(2);
        List<InnovationSectorResource> expectedInnovationSectorResources = newInnovationSectorResource().build(2);

        when(innovationSectorRepositoryMock.findAllByOrderByPriorityAsc()).thenReturn(innovationSectors);
        when(innovationSectorMapperMock.mapToResource(refEq(innovationSectors))).thenReturn(expectedInnovationSectorResources);

        List<InnovationSectorResource> actualInnovationSectorResources = categoryService.getInnovationSectors().getSuccessObject();

        assertEquals(expectedInnovationSectorResources, actualInnovationSectorResources);

        InOrder inOrder = inOrder(innovationSectorRepositoryMock, innovationSectorMapperMock);
        inOrder.verify(innovationSectorRepositoryMock).findAllByOrderByPriorityAsc();
        inOrder.verify(innovationSectorMapperMock).mapToResource(innovationSectors);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getResearchCategories() {
        List<ResearchCategory> researchCategories = newResearchCategory().build(2);
        List<ResearchCategoryResource> expectedResearchCategoryResources = newResearchCategoryResource().build(2);

        when(researchCategoryRepositoryMock.findAllByOrderByPriorityAsc()).thenReturn(researchCategories);
        when(researchCategoryMapperMock.mapToResource(refEq(researchCategories))).thenReturn(expectedResearchCategoryResources);

        List<ResearchCategoryResource> actualResearchCategoryResources = categoryService.getResearchCategories().getSuccessObject();

        assertEquals(expectedResearchCategoryResources, actualResearchCategoryResources);

        InOrder inOrder = inOrder(researchCategoryRepositoryMock, researchCategoryMapperMock);
        inOrder.verify(researchCategoryRepositoryMock).findAllByOrderByPriorityAsc();
        inOrder.verify(researchCategoryMapperMock).mapToResource(researchCategories);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getInnovationAreasBySector() {
        List<InnovationArea> innovationAreas = newInnovationArea().build(2);
        InnovationSector innovationSector = newInnovationSector()
                .withChildren(innovationAreas)
                .build();

        long sectorId = 1L;

        List<InnovationAreaResource> expectedInnovationAreaResources = newInnovationAreaResource().build(2);

        when(innovationSectorRepositoryMock.findOne(sectorId)).thenReturn(innovationSector);
        when(innovationAreaMapperMock.mapToResource(refEq(innovationAreas))).thenReturn(expectedInnovationAreaResources);

        List<InnovationAreaResource> actualInnovationAreaResources = categoryService.getInnovationAreasBySector(sectorId).getSuccessObject();

        assertEquals(expectedInnovationAreaResources, actualInnovationAreaResources);

        verify(innovationSectorRepositoryMock, times(1)).findOne(sectorId);

        InOrder inOrder = inOrder(innovationSectorRepositoryMock, innovationAreaMapperMock);
        inOrder.verify(innovationSectorRepositoryMock).findOne(sectorId);
        inOrder.verify(innovationAreaMapperMock).mapToResource(innovationAreas);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getInnovationAreasByOpenSector() {
        List<InnovationArea> innovationAreas = EMPTY_LIST;
        InnovationSector innovationSector = newInnovationSector()
                .withChildren(innovationAreas)
                .build();

        long sectorId = 1L;

        List<InnovationArea> allInnovationAreas = newInnovationArea().build(2);
        List<InnovationAreaResource> expectedInnovationAreaResources = newInnovationAreaResource().build(2);

        when(innovationSectorRepositoryMock.findOne(sectorId)).thenReturn(innovationSector);
        when(innovationAreaMapperMock.mapToResource(refEq(innovationAreas))).thenReturn(EMPTY_LIST);
        when(innovationAreaRepositoryMock.findAllByOrderByPriorityAsc()).thenReturn(allInnovationAreas);
        when(innovationAreaMapperMock.mapToResource(refEq(allInnovationAreas))).thenReturn(expectedInnovationAreaResources);

        List<InnovationAreaResource> actualInnovationAreaResources = categoryService.getInnovationAreasBySector(sectorId).getSuccessObject();

        assertEquals(expectedInnovationAreaResources, actualInnovationAreaResources);

        verify(innovationSectorRepositoryMock, times(1)).findOne(sectorId);

        InOrder inOrder = inOrder(innovationAreaRepositoryMock, innovationAreaMapperMock);
        inOrder.verify(innovationAreaMapperMock).mapToResource(innovationAreas);
        inOrder.verify(innovationAreaRepositoryMock).findAllByOrderByPriorityAsc();
        inOrder.verify(innovationAreaMapperMock).mapToResource(allInnovationAreas);
        inOrder.verifyNoMoreInteractions();
    }
}
