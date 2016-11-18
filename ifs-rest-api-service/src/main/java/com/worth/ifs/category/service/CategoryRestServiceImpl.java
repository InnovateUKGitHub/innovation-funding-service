package com.worth.ifs.category.service;


import com.worth.ifs.category.resource.CategoryResource;
import com.worth.ifs.category.resource.CategoryType;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.BaseRestService;
import com.worth.ifs.commons.service.ParameterizedTypeReferences;
import com.worth.ifs.competition.service.CompetitionsRestServiceImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.worth.ifs.commons.service.ParameterizedTypeReferences.categoryResourceListType;

@Service
public class CategoryRestServiceImpl extends BaseRestService implements CategoryRestService {

    @SuppressWarnings("unused")
    private static final Log LOG = LogFactory.getLog(CompetitionsRestServiceImpl.class);
    private String categoryRestURL = "/category";

    @Override
    public RestResult<List<CategoryResource>> getByType(CategoryType type) {
        return getWithRestResult(categoryRestURL + "/findByType/" + type.getName(), ParameterizedTypeReferences.categoryResourceListType());
    }

    @Override
    public RestResult<List<CategoryResource>> getByParent(Long parentId) {
        return getWithRestResult(categoryRestURL + "/findByParent/" + parentId, ParameterizedTypeReferences.categoryResourceListType());
    }

}
