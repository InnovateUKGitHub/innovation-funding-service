package org.innovateuk.ifs.category.transactional;

import org.innovateuk.ifs.category.domain.InnovationArea;
import org.innovateuk.ifs.category.domain.InnovationSector;
import org.innovateuk.ifs.category.domain.ResearchCategory;
import org.innovateuk.ifs.category.mapper.InnovationAreaMapper;
import org.innovateuk.ifs.category.mapper.InnovationSectorMapper;
import org.innovateuk.ifs.category.mapper.ResearchCategoryMapper;
import org.innovateuk.ifs.category.repository.InnovationAreaRepository;
import org.innovateuk.ifs.category.repository.InnovationSectorRepository;
import org.innovateuk.ifs.category.repository.ResearchCategoryRepository;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.category.resource.InnovationSectorResource;
import org.innovateuk.ifs.category.resource.ResearchCategoryResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

@Service
public class CategoryServiceImpl extends BaseTransactionalService implements CategoryService {

    @Autowired
    private InnovationAreaRepository innovationAreaRepository;

    @Autowired
    private InnovationSectorRepository innovationSectorRepository;

    @Autowired
    private ResearchCategoryRepository researchCategoryRepository;

    @Autowired
    private InnovationAreaMapper innovationAreaMapper;

    @Autowired
    private InnovationSectorMapper innovationSectorMapper;

    @Autowired
    private ResearchCategoryMapper researchCategoryMapper;

    @Override
    public ServiceResult<List<InnovationAreaResource>> getInnovationAreas() {
        return find(innovationAreaRepository.findAllByOrderByPriorityAsc(), notFoundError(InnovationArea.class))
                .andOnSuccessReturn(innovationAreaMapper::mapToResource);
    }

    @Override
    public ServiceResult<List<InnovationSectorResource>> getInnovationSectors() {
        return find(innovationSectorRepository.findAllByOrderByPriorityAsc(), notFoundError(InnovationSector.class))
                .andOnSuccessReturn(innovationSectorMapper::mapToResource);
    }

    @Override
    public ServiceResult<List<ResearchCategoryResource>> getResearchCategories() {
        return find(researchCategoryRepository.findAllByOrderByPriorityAsc(), notFoundError(ResearchCategory.class))
                .andOnSuccessReturn(researchCategoryMapper::mapToResource);
    }

    @Override
    public ServiceResult<List<InnovationAreaResource>> getInnovationAreasBySector(long sectorId) {
        return find(innovationSectorRepository.findOne(sectorId), notFoundError(InnovationSector.class, sectorId))
                .andOnSuccess(parent -> getInnovationAreasFromParent(parent));
    }

    private ServiceResult<List<InnovationAreaResource>> getInnovationAreasFromParent(InnovationSector parent) {
        List<InnovationAreaResource> childrenList = innovationAreaMapper.mapToResource(parent.getChildren());

        ServiceResult<List<InnovationAreaResource>> innovationAreas;
        if (childrenList.isEmpty()) {
            innovationAreas = getInnovationAreas();
        } else {
            innovationAreas = serviceSuccess(childrenList);
        }
        return innovationAreas;
    }
}
