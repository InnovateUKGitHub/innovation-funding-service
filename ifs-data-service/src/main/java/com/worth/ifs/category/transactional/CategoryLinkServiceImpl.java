package com.worth.ifs.category.transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.worth.ifs.category.domain.Category;
import com.worth.ifs.category.domain.CategoryLink;
import com.worth.ifs.category.repository.CategoryLinkRepository;
import com.worth.ifs.category.repository.CategoryRepository;
import com.worth.ifs.category.resource.CategoryType;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.transactional.BaseTransactionalService;
import com.worth.ifs.util.CollectionFunctions;


/**
 * Service to link categories to every possible class
 */
@Service
public class CategoryLinkServiceImpl extends BaseTransactionalService implements CategoryLinkService {
    @Autowired
    private CategoryLinkRepository categoryLinkRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public ServiceResult<Void> updateCategoryLink(Long categoryId, CategoryType categoryType, String className, Long classPk){
    	Set<Long> categoryIds;
    	if(categoryId == null){
    		categoryIds = new HashSet<>();
    	} else {
    		categoryIds = CollectionFunctions.asLinkedSet(categoryId);
    	}
    	return updateCategoryLinks(categoryIds, categoryType, className, classPk);
    }
    
    @Override
    public ServiceResult<Void> updateCategoryLinks(Set<Long> categoryIds, CategoryType categoryType, String className, Long classPk){
        List<CategoryLink> existingCategoryLinks = categoryLinkRepository.findByClassNameAndClassPkAndCategory_Type(className, classPk, categoryType);

        if (categoryIds.isEmpty()) {
            // Not category ids provided, so remove existing categoryLinks
            if(!existingCategoryLinks.isEmpty()){
                categoryLinkRepository.delete(existingCategoryLinks);
            }
        } else {
            // Got a Category id, so either add or update the CategoryLink
            Iterable<Category> categories = categoryRepository.findAll(categoryIds);
            
            // determine what to leave, add or remove.
            List<CategoryLink> toAdd = toAdd(categories, existingCategoryLinks, className, classPk);
            List<CategoryLink> toRemove = toRemove(categories, existingCategoryLinks, className, classPk);
            
            if(!toRemove.isEmpty()) {
            	categoryLinkRepository.delete(toRemove);
            }
            
            if(!toAdd.isEmpty()) {
            	categoryLinkRepository.save(toAdd);
            }
        }
        return ServiceResult.serviceSuccess();
    }

	private List<CategoryLink> toAdd(Iterable<Category> categoriesWanted, List<CategoryLink> alreadyInDb, String className, Long classPk) {
		return StreamSupport.stream(categoriesWanted.spliterator(), false)
				.filter(cat ->
					!alreadyInDb.stream().anyMatch(link -> link.getCategory().getId().equals(cat.getId()))
                )
				.map(cat -> new CategoryLink(cat, className, classPk))
				.collect(Collectors.toList());
	}
	
	private List<CategoryLink> toRemove(Iterable<Category> categoriesWanted, List<CategoryLink> alreadyInDb, String className, Long classPk) {
		return alreadyInDb.stream()
				.filter(link ->
					!StreamSupport.stream(categoriesWanted.spliterator(), false).anyMatch(cat -> link.getCategory().getId().equals(cat.getId()))
				)
				.collect(Collectors.toList());
	}


}
