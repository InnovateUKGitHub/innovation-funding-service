package org.innovateuk.ifs.category.service;


import org.innovateuk.ifs.category.resource.*;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.*;

@Service
public class CategoryRestServiceImpl extends BaseRestService implements CategoryRestService {

    @SuppressWarnings("unused")
    private static final Log LOG = LogFactory.getLog(CategoryRestServiceImpl.class);
    private String categoryRestURL = "/category";

    @Override
    public RestResult<List<InnovationAreaResource>> getInnovationAreas() {
        return getWithRestResultAnonymous(categoryRestURL + "/findInnovationAreas", innovationAreaResourceListType());
    }

    @Override
    public RestResult<List<InnovationSectorResource>> getInnovationSectors() {
        return getWithRestResult(categoryRestURL + "/findInnovationSectors", innovationSectorResourceListType());
    }

    @Override
    public RestResult<List<ResearchCategoryResource>> getResearchCategories() {
        return getWithRestResult(categoryRestURL + "/findResearchCategories", researchCategoryResourceListType());
    }

    @Override
    public RestResult<List<InnovationAreaResource>> getInnovationAreasBySector(long sectorId) {
        return getWithRestResult(categoryRestURL + "/findByInnovationSector/" + sectorId, innovationAreaResourceListType());
    }
}
