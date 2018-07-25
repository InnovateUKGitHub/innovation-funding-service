package org.innovateuk.ifs.competitionsetup.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.ProjectDocumentResource;
import org.innovateuk.ifs.competitionsetup.domain.ProjectDocument;
import org.innovateuk.ifs.competitionsetup.mapper.ProjectDocumentMapper;
import org.innovateuk.ifs.competitionsetup.repository.ProjectDocumentRepository;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;

/**
 * Service for operations around the usage and processing of Project Documents
 */
@Service
public class CompetitionSetupProjectDocumentServiceImpl extends BaseTransactionalService implements CompetitionSetupProjectDocumentService {

    @Autowired
    private ProjectDocumentMapper projectDocumentMapper;

    @Autowired
    protected ProjectDocumentRepository projectDocumentRepository;

    @Override
    @Transactional
    public ServiceResult<ProjectDocumentResource> save(ProjectDocumentResource projectDocumentResource) {
        ProjectDocument projectDocument = projectDocumentMapper.mapToDomain(projectDocumentResource);

        projectDocument = projectDocumentRepository.save(projectDocument);
        return serviceSuccess(projectDocumentMapper.mapToResource(projectDocument));
    }
}

