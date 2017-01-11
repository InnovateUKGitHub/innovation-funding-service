package org.innovateuk.ifs.category.transactional;

import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.category.domain.InnovationArea;
import org.innovateuk.ifs.category.domain.InnovationSector;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.category.builder.InnovationAreaBuilder.newInnovationArea;
import static org.innovateuk.ifs.category.builder.InnovationAreaResourceBuilder.newInnovationAreaResource;
import static org.innovateuk.ifs.category.builder.InnovationSectorBuilder.newInnovationSector;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class CategoryServiceImplTest extends BaseUnitTestMocksTest {

    @InjectMocks
    private final CategoryService categoryService = new CategoryServiceImpl();

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void test_getByType() throws Exception {
        final InnovationArea cat1 = newInnovationArea().build();
        final InnovationArea cat2 = newInnovationArea().build();

        final List<InnovationArea> categories = new ArrayList<>(asList(cat1, cat2));

        final InnovationAreaResource expected1 = newInnovationAreaResource().build();

        final InnovationAreaResource expected2 = newInnovationAreaResource().build();

        when(innovationAreaRepositoryMock.findAllByOrderByNameAsc()).thenReturn(categories);
        when(innovationAreaMapperMock.mapToResource(refEq(categories))).thenReturn(asList(expected1, expected2));

        final List<InnovationAreaResource> found = categoryService.getInnovationAreas().getSuccessObject();

        assertEquals(expected1, found.get(0));
        assertEquals(expected2, found.get(1));
        verify(innovationAreaRepositoryMock, times(1)).findAllByOrderByNameAsc();
    }

    @Test
    public void test_getByParent() throws Exception {
        List<InnovationArea> categories = newInnovationArea().build(2);

        InnovationSector parent = newInnovationSector()
                .withChildren(categories)
                .build();

        Long parentId = 1L;

        final InnovationAreaResource expected1 = newInnovationAreaResource().build();
        final InnovationAreaResource expected2 = newInnovationAreaResource().build();

        when(innovationSectorRepositoryMock.exists(parentId)).thenReturn(true);
        when(innovationSectorRepositoryMock.findOne(parentId)).thenReturn(parent);
        when(innovationAreaMapperMock.mapToResource(refEq(categories))).thenReturn(asList(expected1, expected2));

        final List<InnovationAreaResource> found = categoryService.getInnovationAreaBySector(parentId).getSuccessObject();

        assertEquals(expected1, found.get(0));
        assertEquals(expected2, found.get(1));
        verify(innovationSectorRepositoryMock, times(1)).findOne(parentId);
    }
}
