package com.worth.ifs.category.transactional;

import com.worth.ifs.category.domain.Category;
import com.worth.ifs.category.mapper.CategoryMapper;
import com.worth.ifs.category.repository.CategoryRepository;
import com.worth.ifs.category.resource.CategoryResource;
import com.worth.ifs.category.resource.CategoryType;
import com.worth.ifs.commons.error.CommonErrors;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.worth.ifs.commons.service.ServiceResult.serviceFailure;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;

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
