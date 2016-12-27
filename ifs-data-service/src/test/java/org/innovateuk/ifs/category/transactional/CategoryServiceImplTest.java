package org.innovateuk.ifs.category.transactional;

import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.category.domain.Category;
import org.innovateuk.ifs.category.resource.CategoryResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;

import java.util.ArrayList;
import java.util.List;

import static org.innovateuk.ifs.category.builder.CategoryResourceBuilder.newCategoryResource;
import static java.util.Arrays.asList;
import static org.innovateuk.ifs.category.resource.CategoryType.INNOVATION_AREA;
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
        final Category cat1 = new Category();
        final Category cat2 = new Category();

        final List<Category> categories = new ArrayList<>(asList(cat1, cat2));

        final CategoryResource expected1 = newCategoryResource()
                .build();

        final CategoryResource expected2 = newCategoryResource()
                .build();

        when(categoryRepositoryMock.findByTypeOrderByNameAsc(INNOVATION_AREA)).thenReturn(categories);
        when(categoryMapperMock.mapToResource(refEq(categories))).thenReturn(asList(expected1, expected2));

        final List<CategoryResource> found = categoryService.getByType(INNOVATION_AREA).getSuccessObject();

        assertEquals(expected1, found.get(0));
        assertEquals(expected2, found.get(1));
        verify(categoryRepositoryMock, times(1)).findByTypeOrderByNameAsc(INNOVATION_AREA);
    }

    @Test
    public void test_getByParent() throws Exception {
        final Category parent = new Category();
        final Category cat1 = new Category();
        final Category cat2 = new Category();

        Long parentId = 1L;

        final List<Category> categories = new ArrayList<>(asList(cat1, cat2));
        parent.setChildren(categories);

        final CategoryResource expected1 = newCategoryResource()
                .build();

        final CategoryResource expected2 = newCategoryResource()
                .build();

        when(categoryRepositoryMock.exists(parentId)).thenReturn(true);
        when(categoryRepositoryMock.findOne(parentId)).thenReturn(parent);
        when(categoryMapperMock.mapToResource(refEq(categories))).thenReturn(asList(expected1, expected2));

        final List<CategoryResource> found = categoryService.getByParent(parentId).getSuccessObject();

        assertEquals(expected1, found.get(0));
        assertEquals(expected2, found.get(1));
        verify(categoryRepositoryMock, times(1)).findOne(parentId);
    }


}
