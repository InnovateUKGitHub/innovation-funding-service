package org.innovateuk.ifs.category.transactional;

import org.innovateuk.ifs.category.domain.Category;
import org.innovateuk.ifs.category.mapper.CategoryMapper;
import org.innovateuk.ifs.category.repository.CategoryRepository;
import org.innovateuk.ifs.category.resource.CategoryResource;
import org.innovateuk.ifs.category.resource.CategoryType;
import org.innovateuk.ifs.commons.error.CommonErrors;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;

@Service
public class CategoryServiceImpl extends BaseTransactionalService implements CategoryService {
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    CategoryMapper categoryMapper;

    @Override
    public ServiceResult<List<CategoryResource>> getByType(CategoryType type){
        return serviceSuccess((List) categoryMapper.mapToResource(categoryRepository.findByType(type)));
    }

    @Override
    public ServiceResult<List<CategoryResource>> getByParent(Long parentId){
        if(categoryRepository.exists(parentId)){
            Category parent = categoryRepository.findOne(parentId);
            return serviceSuccess((List) categoryMapper.mapToResource(parent.getChildren()));
        }else{
            return serviceFailure(CommonErrors.notFoundError(Category.class, parentId));
        }
    }
}
