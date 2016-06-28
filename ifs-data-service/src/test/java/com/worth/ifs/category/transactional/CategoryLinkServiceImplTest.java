package com.worth.ifs.category.transactional;

import static com.worth.ifs.category.builder.CategoryBuilder.newCategory;
import static com.worth.ifs.category.builder.CategoryLinkBuilder.newCategoryLink;
import static java.util.Arrays.asList;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import com.worth.ifs.BaseUnitTestMocksTest;
import com.worth.ifs.category.domain.Category;
import com.worth.ifs.category.domain.CategoryLink;
import com.worth.ifs.category.resource.CategoryType;
import com.worth.ifs.util.CollectionFunctions;

@RunWith(MockitoJUnitRunner.class)
public class CategoryLinkServiceImplTest extends BaseUnitTestMocksTest {

    @InjectMocks
    private CategoryLinkServiceImpl categoryLinkService;
    
    private String className;
    private Long classPk;
    
    private Category category1;
    private Category category2;
    private Category category3;
    
    private CategoryLink categoryLink1;
    private CategoryLink categoryLink2;
    private CategoryLink categoryLink3;
    
    @Before
    public void setUp() throws Exception {
    	
    	className = "ClassName";
    	classPk = 2L;
    	
    	category1 = newCategory().build();
    	category2 = newCategory().build();
    	category3 = newCategory().build();
    	
        categoryLink1 = newCategoryLink()
        		.withClassName(className)
        		.withCategory(category1)
        		.withClassPk(classPk)
        		.build();
        
        categoryLink2 = newCategoryLink()
        		.withClassName(className)
        		.withCategory(category2)
        		.withClassPk(classPk)
        		.build();
        
        categoryLink3 = newCategoryLink()
        		.withClassName(className)
        		.withCategory(category3)
        		.withClassPk(classPk)
        		.build();
    }

    @Test
    public void test_deleteLinks() throws Exception {
    	
        when(categoryLinkRepositoryMock.findByClassNameAndClassPkAndCategory_Type(className, classPk, CategoryType.INNOVATION_AREA)).thenReturn(asList(categoryLink1, categoryLink2));

        categoryLinkService.updateCategoryLink(null, CategoryType.INNOVATION_AREA, className, classPk).getSuccessObject();

        verify(categoryLinkRepositoryMock, times(1)).delete(asList(categoryLink1, categoryLink2));
    }

    @Test
    public void test_addLink() throws Exception {
        
        when(categoryRepositoryMock.findAll(CollectionFunctions.asLinkedSet(category1.getId()))).thenReturn(asList(category1));

        when(categoryLinkRepositoryMock.findByClassNameAndClassPkAndCategory_Type(className, classPk, CategoryType.INNOVATION_AREA)).thenReturn(asList());

        categoryLinkService.updateCategoryLink(category1.getId(), CategoryType.INNOVATION_AREA, className, classPk).getSuccessObject();

        verify(categoryLinkRepositoryMock, times(1)).save(argThat(new CategoryLinkListMatcher(category1)));
    }
    
    @Test
    public void test_addLinks() throws Exception {
        
        when(categoryRepositoryMock.findAll(CollectionFunctions.asLinkedSet(category1.getId(), category2.getId(), category3.getId()))).thenReturn(asList(category1, category2, category3));

        when(categoryLinkRepositoryMock.findByClassNameAndClassPkAndCategory_Type(className, classPk, CategoryType.INNOVATION_AREA)).thenReturn(asList());

        categoryLinkService.updateCategoryLinks(CollectionFunctions.asLinkedSet(category1.getId(), category2.getId(), category3.getId()), CategoryType.INNOVATION_AREA, className, classPk).getSuccessObject();

        verify(categoryLinkRepositoryMock, times(1)).save(argThat(new CategoryLinkListMatcher(category1, category2, category3)));
    }
    
    @Test
    public void test_updateLinks() throws Exception {
        
        when(categoryRepositoryMock.findAll(CollectionFunctions.asLinkedSet(category2.getId(), category3.getId()))).thenReturn(asList(category2, category3));

        when(categoryLinkRepositoryMock.findByClassNameAndClassPkAndCategory_Type(className, classPk, CategoryType.INNOVATION_AREA)).thenReturn(asList(categoryLink1, categoryLink2));

        categoryLinkService.updateCategoryLinks(CollectionFunctions.asLinkedSet(category2.getId(), category3.getId()), CategoryType.INNOVATION_AREA, className, classPk).getSuccessObject();

        verify(categoryLinkRepositoryMock, times(1)).delete(argThat(new CategoryLinkListMatcher(category1)));
        verify(categoryLinkRepositoryMock, times(1)).save(argThat(new CategoryLinkListMatcher(category3)));
    }
    
    

    private class CategoryLinkListMatcher extends ArgumentMatcher<List<CategoryLink>> {

    	private List<Category> category;
    	
    	public CategoryLinkListMatcher(Category... category) {
    		this.category = asList(category);
    	}
    	
		@Override
		public boolean matches(Object argument) {
			List<CategoryLink> arg = (List<CategoryLink>) argument;
			
			if(arg.size() != category.size()) {
				return false;
			}
			for(int i = 0; i< arg.size(); i++){
				CategoryLink link = arg.get(i);
				Category expectedCategory = category.get(i);
				if(!expectedCategory.getId().equals(link.getCategory().getId())){
					return false;
				}
			}
			
			return true;
		}
    	
    }
}