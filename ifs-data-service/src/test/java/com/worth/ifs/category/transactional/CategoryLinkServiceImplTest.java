package com.worth.ifs.category.transactional;

import com.worth.ifs.BaseUnitTestMocksTest;
import com.worth.ifs.category.domain.CategoryLink;
import com.worth.ifs.category.resource.CategoryType;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;

import static com.worth.ifs.category.builder.CategoryBuilder.newCategory;
import static org.mockito.Mockito.*;

public class CategoryLinkServiceImplTest extends BaseUnitTestMocksTest {

    @InjectMocks
    private final CategoryLinkService categoryLinkService = new CategoryLinkServiceImpl();

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void test_deleteLink() throws Exception {
        final CategoryLink categoryLink = new CategoryLink();
        categoryLink.setClassName("Classname");
        categoryLink.setCategory(newCategory().build());
        categoryLink.setClassPk(2L);
        categoryLink.setId(1L);

        when(categoryLinkRepositoryMock.findByClassNameAndClassPkAndCategory_Type(categoryLink.getClassName(), categoryLink.getClassPk(), CategoryType.INNOVATION_AREA)).thenReturn(categoryLink);

        categoryLinkService.updateCategoryLink(null, CategoryType.INNOVATION_AREA, categoryLink.getClassName(), categoryLink.getClassPk()).getSuccessObject();

        verify(categoryLinkRepositoryMock, times(1)).delete(categoryLink);
    }


    @Test
    public void test_updateLink() throws Exception {
        final CategoryLink categoryLink = new CategoryLink();
        categoryLink.setClassName("Classname");
        categoryLink.setCategory(newCategory().build());
        categoryLink.setClassPk(2L);
        categoryLink.setId(1L);

        when(categoryLinkRepositoryMock.findByClassNameAndClassPkAndCategory_Type(categoryLink.getClassName(), categoryLink.getClassPk(), CategoryType.INNOVATION_AREA)).thenReturn(categoryLink);

        categoryLinkService.updateCategoryLink(2L, CategoryType.INNOVATION_AREA, categoryLink.getClassName(), categoryLink.getClassPk()).getSuccessObject();

        verify(categoryLinkRepositoryMock, times(1)).save(categoryLink);
    }

    @Test
    public void test_addLink() throws Exception {
        final CategoryLink categoryLink = new CategoryLink();
        categoryLink.setClassName("Classname");
        categoryLink.setCategory(newCategory().build());
        categoryLink.setClassPk(2L);
        categoryLink.setId(1L);

        when(categoryLinkRepositoryMock.findByClassNameAndClassPkAndCategory_Type(categoryLink.getClassName(), categoryLink.getClassPk(), CategoryType.INNOVATION_AREA)).thenReturn(null);

        categoryLinkService.updateCategoryLink(2L, CategoryType.INNOVATION_AREA, categoryLink.getClassName(), categoryLink.getClassPk()).getSuccessObject();

        verify(categoryLinkRepositoryMock, times(1)).save(isA(CategoryLink.class));
    }

}