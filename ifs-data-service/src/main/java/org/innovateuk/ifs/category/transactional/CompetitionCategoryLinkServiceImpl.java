package org.innovateuk.ifs.category.transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.innovateuk.ifs.category.domain.CompetitionCategoryLink;
import org.innovateuk.ifs.category.repository.CompetitionCategoryLinkRepository;
import org.innovateuk.ifs.competition.domain.Competition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.innovateuk.ifs.category.domain.Category;
import org.innovateuk.ifs.category.repository.CategoryRepository;
import org.innovateuk.ifs.category.resource.CategoryType;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.innovateuk.ifs.util.CollectionFunctions;


/**
 * Service to link categories to every possible class
 */
@Service
public class CompetitionCategoryLinkServiceImpl extends BaseTransactionalService implements CompetitionCategoryLinkService {
    @Autowired
    private CompetitionCategoryLinkRepository competitionCategoryLinkRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public ServiceResult<Void> updateCategoryLink(Long categoryId, CategoryType categoryType, Competition competition){
    	Set<Long> categoryIds;
    	if(categoryId == null){
    		categoryIds = new HashSet<>();
    	} else {
    		categoryIds = CollectionFunctions.asLinkedSet(categoryId);
    	}
    	return updateCategoryLinks(categoryIds, categoryType, competition);
    }
    
    @Override
    public ServiceResult<Void> updateCategoryLinks(Set<Long> categoryIds, CategoryType categoryType, Competition competition){
        List<CompetitionCategoryLink> existingCategoryLinks = competitionCategoryLinkRepository.findAllByCompetitionIdAndCategoryType(competition.getId(), categoryType);

        if (categoryIds.isEmpty()) {
            // Not category ids provided, so remove existing categoryLinks
            if(!existingCategoryLinks.isEmpty()){
                competitionCategoryLinkRepository.delete(existingCategoryLinks);
            }
        } else {
            // Got a Category id, so either add or update the CompetitionCategoryLink
            List<Category> categories = categoryRepository.findAll(categoryIds);
            
            // determine what to leave, add or remove.
            List<CompetitionCategoryLink> toAdd = toAdd(categories, existingCategoryLinks, competition);
            List<CompetitionCategoryLink> toRemove = toRemove(categories, existingCategoryLinks);
            
            if(!toRemove.isEmpty()) {
                competitionCategoryLinkRepository.delete(toRemove);
            }
            
            if(!toAdd.isEmpty()) {
                competitionCategoryLinkRepository.save(toAdd);
            }
        }
        return ServiceResult.serviceSuccess();
    }

	private List<CompetitionCategoryLink> toAdd(List<Category> categoriesWanted, List<CompetitionCategoryLink> alreadyInDb, Competition competition) {
		return categoriesWanted.stream()
				.filter(category ->
					!alreadyInDb.stream().anyMatch(link -> link.getCategory().getId().equals(category.getId()))
                )
				.map(category -> new CompetitionCategoryLink(competition, category))
				.collect(Collectors.toList());
	}
	
	private List<CompetitionCategoryLink> toRemove(List<Category> categoriesWanted, List<CompetitionCategoryLink> alreadyInDb) {
		return alreadyInDb.stream()
				.filter(link ->
					!StreamSupport.stream(categoriesWanted.spliterator(), false).anyMatch(cat -> link.getCategory().getId().equals(cat.getId()))
				)
				.collect(Collectors.toList());
	}
}
