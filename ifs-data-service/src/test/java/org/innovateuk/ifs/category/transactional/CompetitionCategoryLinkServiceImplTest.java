package org.innovateuk.ifs.category.transactional;

import static org.innovateuk.ifs.category.builder.CategoryLinkBuilder.newCategoryLink;
import static java.util.Arrays.asList;
import static org.innovateuk.ifs.category.builder.InnovationAreaBuilder.newInnovationArea;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.innovateuk.ifs.competition.domain.Competition;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.category.domain.Category;
import org.innovateuk.ifs.category.domain.CompetitionCategoryLink;
import org.innovateuk.ifs.category.resource.CategoryType;
import org.innovateuk.ifs.util.CollectionFunctions;

@RunWith(MockitoJUnitRunner.class)
public class CompetitionCategoryLinkServiceImplTest extends BaseUnitTestMocksTest {

    @InjectMocks
    private CompetitionCategoryLinkServiceImpl categoryLinkService;
    
//    private String className;
//    private Long classPk;
    
    private Category category1;
    private Category category2;
    private Category category3;
    
    private CompetitionCategoryLink categoryLink1;
    private CompetitionCategoryLink categoryLink2;
    private CompetitionCategoryLink categoryLink3;

    private Competition competition;
    
    @Before
    public void setUp() throws Exception {
    	category1 = newInnovationArea().build();
    	category2 = newInnovationArea().build();
    	category3 = newInnovationArea().build();

        competition = newCompetition().build();
    	
        categoryLink1 = newCategoryLink()
        		.withCategory(category1)
        		.withCompetition(competition)
        		.build();
        
        categoryLink2 = newCategoryLink()
        		.withCategory(category2)
                .withCompetition(competition)
        		.build();
        
        categoryLink3 = newCategoryLink()
        		.withCategory(category3)
                .withCompetition(competition)
        		.build();
    }

    @Test
    public void test_deleteLinks() throws Exception {
    	
        when(competitionCategoryLinkRepositoryMock.findAllByCompetitionIdAndCategoryType(competition.getId(), CategoryType.INNOVATION_AREA)).thenReturn(asList(categoryLink1, categoryLink2));

        categoryLinkService.updateCategoryLink(null, CategoryType.INNOVATION_AREA, competition).getSuccessObject();

        verify(competitionCategoryLinkRepositoryMock, times(1)).delete(asList(categoryLink1, categoryLink2));
    }

    @Test
    public void test_addLink() throws Exception {
        
        when(categoryRepositoryMock.findAll(CollectionFunctions.asLinkedSet(category1.getId()))).thenReturn(asList(category1));

        when(competitionCategoryLinkRepositoryMock.findAllByCompetitionIdAndCategoryType(competition.getId(), CategoryType.INNOVATION_AREA)).thenReturn(asList());

        categoryLinkService.updateCategoryLink(category1.getId(), CategoryType.INNOVATION_AREA, competition).getSuccessObject();

        verify(competitionCategoryLinkRepositoryMock, times(1)).save(argThat(new CategoryLinkListMatcher(category1)));
    }
    
    @Test
    public void test_addLinks() throws Exception {
        
        when(categoryRepositoryMock.findAll(CollectionFunctions.asLinkedSet(category1.getId(), category2.getId(), category3.getId()))).thenReturn(asList(category1, category2, category3));

        when(competitionCategoryLinkRepositoryMock.findAllByCompetitionIdAndCategoryType(competition.getId(), CategoryType.INNOVATION_AREA)).thenReturn(asList());

        categoryLinkService.updateCategoryLinks(CollectionFunctions.asLinkedSet(category1.getId(), category2.getId(), category3.getId()), CategoryType.INNOVATION_AREA, competition).getSuccessObject();

        verify(competitionCategoryLinkRepositoryMock, times(1)).save(argThat(new CategoryLinkListMatcher(category1, category2, category3)));
    }
    
    @Test
    public void test_updateLinks() throws Exception {
        
        when(categoryRepositoryMock.findAll(CollectionFunctions.asLinkedSet(category2.getId(), category3.getId()))).thenReturn(asList(category2, category3));

        when(competitionCategoryLinkRepositoryMock.findAllByCompetitionIdAndCategoryType(competition.getId(), CategoryType.INNOVATION_AREA)).thenReturn(asList(categoryLink1, categoryLink2));

        categoryLinkService.updateCategoryLinks(CollectionFunctions.asLinkedSet(category2.getId(), category3.getId()), CategoryType.INNOVATION_AREA, competition).getSuccessObject();

        verify(competitionCategoryLinkRepositoryMock, times(1)).delete(argThat(new CategoryLinkListMatcher(category1)));
        verify(competitionCategoryLinkRepositoryMock, times(1)).save(argThat(new CategoryLinkListMatcher(category3)));
    }
    
    

    private class CategoryLinkListMatcher extends ArgumentMatcher<List<CompetitionCategoryLink>> {

    	private List<Category> category;
    	
    	public CategoryLinkListMatcher(Category... category) {
    		this.category = asList(category);
    	}
    	
		@Override
		public boolean matches(Object argument) {
			List<CompetitionCategoryLink> arg = (List<CompetitionCategoryLink>) argument;
			
			if(arg.size() != category.size()) {
				return false;
			}
			for(int i = 0; i< arg.size(); i++){
				CompetitionCategoryLink link = arg.get(i);
				Category expectedCategory = category.get(i);
				if(!expectedCategory.getId().equals(link.getCategory().getId())){
					return false;
				}
			}
			
			return true;
		}
    	
    }
}
