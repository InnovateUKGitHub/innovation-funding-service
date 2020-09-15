package org.innovateuk.ifs.category.transactional;

import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.category.domain.InnovationArea;
import org.innovateuk.ifs.category.domain.InnovationSector;
import org.innovateuk.ifs.category.domain.ResearchCategory;
import org.innovateuk.ifs.category.mapper.InnovationAreaMapper;
import org.innovateuk.ifs.category.mapper.InnovationSectorMapper;
import org.innovateuk.ifs.category.mapper.ResearchCategoryMapper;
import org.innovateuk.ifs.category.repository.InnovationAreaRepository;
import org.innovateuk.ifs.category.repository.InnovationSectorRepository;
import org.innovateuk.ifs.category.repository.ResearchCategoryRepository;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.category.resource.InnovationSectorResource;
import org.innovateuk.ifs.category.resource.ResearchCategoryResource;
import org.innovateuk.ifs.form.mapper.QuestionMapper;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;

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

    @Mock
    private InnovationAreaRepository innovationAreaRepository;

    @Mock
    private InnovationAreaMapper innovationAreaMapper;

    @Mock
    private QuestionMapper questionMapper;

    @Mock
    private InnovationSectorRepository innovationSectorRepository;

    @Mock
    private InnovationSectorMapper innovationSectorMapper;

    @Mock
    private ResearchCategoryRepository researchCategoryRepository;

    @Mock
    private ResearchCategoryMapper researchCategoryMapper;

    @Test
    public void getInnovationAreas() {
        List<InnovationArea> innovationAreas = newInnovationArea().build(2);
        List<InnovationAreaResource> expectedInnovationAreaResources = newInnovationAreaResource().build(2);

        when(innovationAreaRepository.findAllByOrderByNameAsc()).thenReturn(innovationAreas);
        when(innovationAreaMapper.mapToResource(refEq(innovationAreas))).thenReturn(expectedInnovationAreaResources);

        List<InnovationAreaResource> actualInnovationAreaResources = categoryService.getInnovationAreas().getSuccess();

        assertEquals(expectedInnovationAreaResources, actualInnovationAreaResources);

        InOrder inOrder = inOrder(innovationAreaRepository, innovationAreaMapper, questionMapper);
        inOrder.verify(innovationAreaRepository).findAllByOrderByNameAsc();
        inOrder.verify(innovationAreaMapper).mapToResource(innovationAreas);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getInnovationSectors() {
        List<InnovationSector> innovationSectors = newInnovationSector().build(2);
        List<InnovationSectorResource> expectedInnovationSectorResources = newInnovationSectorResource().build(2);

        when(innovationSectorRepository.findAllByOrderByPriorityAsc()).thenReturn(innovationSectors);
        when(innovationSectorMapper.mapToResource(refEq(innovationSectors))).thenReturn(expectedInnovationSectorResources);

        List<InnovationSectorResource> actualInnovationSectorResources = categoryService.getInnovationSectors().getSuccess();

        assertEquals(expectedInnovationSectorResources, actualInnovationSectorResources);

        InOrder inOrder = inOrder(innovationSectorRepository, innovationSectorMapper);
        inOrder.verify(innovationSectorRepository).findAllByOrderByPriorityAsc();
        inOrder.verify(innovationSectorMapper).mapToResource(innovationSectors);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getResearchCategories() {
        List<ResearchCategory> researchCategories = newResearchCategory().build(2);
        List<ResearchCategoryResource> expectedResearchCategoryResources = newResearchCategoryResource().build(2);

        when(researchCategoryRepository.findAllByOrderByPriorityAsc()).thenReturn(researchCategories);
        when(researchCategoryMapper.mapToResource(refEq(researchCategories))).thenReturn(expectedResearchCategoryResources);

        List<ResearchCategoryResource> actualResearchCategoryResources = categoryService.getResearchCategories().getSuccess();

        assertEquals(expectedResearchCategoryResources, actualResearchCategoryResources);

        InOrder inOrder = inOrder(researchCategoryRepository, researchCategoryMapper);
        inOrder.verify(researchCategoryRepository).findAllByOrderByPriorityAsc();
        inOrder.verify(researchCategoryMapper).mapToResource(researchCategories);
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

        when(innovationSectorRepository.findById(sectorId)).thenReturn(Optional.of(innovationSector));
        when(innovationAreaMapper.mapToResource(refEq(innovationAreas))).thenReturn(expectedInnovationAreaResources);

        List<InnovationAreaResource> actualInnovationAreaResources = categoryService.getInnovationAreasBySector(sectorId).getSuccess();

        assertEquals(expectedInnovationAreaResources, actualInnovationAreaResources);

        verify(innovationSectorRepository, times(1)).findById(sectorId);

        InOrder inOrder = inOrder(innovationSectorRepository, innovationAreaMapper);
        inOrder.verify(innovationSectorRepository).findById(sectorId);
        inOrder.verify(innovationAreaMapper).mapToResource(innovationAreas);
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

        when(innovationSectorRepository.findById(sectorId)).thenReturn(Optional.of(innovationSector));
        when(innovationAreaMapper.mapToResource(refEq(innovationAreas))).thenReturn(EMPTY_LIST);
        when(innovationAreaRepository.findAllByOrderByNameAsc()).thenReturn(allInnovationAreas);
        when(innovationAreaMapper.mapToResource(refEq(allInnovationAreas))).thenReturn(expectedInnovationAreaResources);

        List<InnovationAreaResource> actualInnovationAreaResources = categoryService.getInnovationAreasBySector(sectorId).getSuccess();

        assertEquals(expectedInnovationAreaResources, actualInnovationAreaResources);

        verify(innovationSectorRepository, times(1)).findById(sectorId);

        InOrder inOrder = inOrder(innovationAreaRepository, innovationAreaMapper);
        inOrder.verify(innovationAreaMapper).mapToResource(innovationAreas);
        inOrder.verify(innovationAreaRepository).findAllByOrderByNameAsc();
        inOrder.verify(innovationAreaMapper).mapToResource(allInnovationAreas);
        inOrder.verifyNoMoreInteractions();
    }
}
