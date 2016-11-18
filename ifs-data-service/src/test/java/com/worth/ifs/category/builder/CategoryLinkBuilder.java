package com.worth.ifs.category.builder;

import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

import java.util.List;
import java.util.function.BiConsumer;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.category.domain.Category;
import com.worth.ifs.category.domain.CategoryLink;

public class CategoryLinkBuilder extends BaseBuilder<CategoryLink, CategoryLinkBuilder> {

    public static CategoryLinkBuilder newCategoryLink() {
        return new CategoryLinkBuilder(emptyList()).with(uniqueIds());
    }

    private CategoryLinkBuilder(List<BiConsumer<Integer, CategoryLink>> multiActions) {
        super(multiActions);
    }

    @Override
    protected CategoryLinkBuilder createNewBuilderWithActions(List<BiConsumer<Integer, CategoryLink>> actions) {
        return new CategoryLinkBuilder(actions);
    }

    @Override
    protected CategoryLink createInitial() {
        return new CategoryLink();
    }
    
    public CategoryLinkBuilder withCategory(Category... categories) {
        return withArray((category, categoryLink) -> categoryLink.setCategory(category), categories);
    }
    
    public CategoryLinkBuilder withClassName(String... classNames) {
        return withArray((className, categoryLink) -> categoryLink.setClassName(className), classNames);
    }
    
    public CategoryLinkBuilder withClassPk(Long... classPks) {
        return withArray((classPk, categoryLink) -> categoryLink.setClassPk(classPk), classPks);
    }

}
