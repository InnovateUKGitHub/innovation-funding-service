package com.worth.ifs.category.transactional;

import com.worth.ifs.category.domain.Category;
import com.worth.ifs.category.domain.CategoryLink;
import com.worth.ifs.category.resource.CategoryType;
import com.worth.ifs.category.repository.CategoryLinkRepository;
import com.worth.ifs.category.repository.CategoryRepository;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryLinkServiceImpl extends BaseTransactionalService implements CategoryLinkService {
    @Autowired
    private CategoryLinkRepository categoryLinkRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    /**
     * This method is for object that only have one categorylink for this CategoryType.
     * For example, a Competition instance can only have one InnovationSector category link.
     * This method checks if there already is a CategoryLink, if there is it updates that CategoryLink.
     * If there is no CategoryLink, it creates it.
     * If the param categoryId is empty, the CategoryLink is removed if it is existing.
     * @param className The Class name of the object to link the Category to.
     * @param classPk The Primary Key of the object to link the Category to.
     * @param categoryType The type of the Category.
     * @param categoryId The ID of the Category instance. If null the (optional) existing CategoryLink is deleted.
     */
    @Override
    public ServiceResult<Void> addOrUpdateOrDeleteLink(String className, Long classPk, CategoryType categoryType, Long categoryId){
        CategoryLink existingCategoryLink = categoryLinkRepository.findByClassNameAndClassPkAndCategory_Type(className, classPk, categoryType);

        if (categoryId == null) {
            // Not category id available, so remove existing categoryLink
            if(existingCategoryLink != null){
                categoryLinkRepository.delete(existingCategoryLink);
            }
        } else {
            // Got a Category id, so either add or update the CategoryLink
            Category category = categoryRepository.findOne(categoryId);
            if (existingCategoryLink == null) {
                categoryLinkRepository.save(new CategoryLink(category, className, classPk));
            } else {
                existingCategoryLink.setCategory(category);
                categoryLinkRepository.save(existingCategoryLink);
            }
        }
        return ServiceResult.serviceSuccess();
    }


}
