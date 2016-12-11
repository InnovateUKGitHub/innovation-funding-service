package org.innovateuk.ifs.category.service;


import org.innovateuk.ifs.category.resource.CategoryResource;
import org.innovateuk.ifs.category.resource.CategoryType;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.commons.service.ParameterizedTypeReferences;
import org.innovateuk.ifs.competition.service.CompetitionsRestServiceImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.categoryResourceListType;

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
