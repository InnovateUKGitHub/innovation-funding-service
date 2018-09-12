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

import java.util.List;

import static org.innovateuk.ifs.commons.error.CommonFailureKeys.FILES_SELECT_AT_LEAST_ONE_FILE_TYPE;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleAnyMatch;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * Service for operations around the usage and processing of Project Documents
 */
@Service
public class CompetitionSetupProjectDocumentServiceImpl extends BaseTransactionalService implements CompetitionSetupProjectDocumentService {

    @Autowired
    private ProjectDocumentMapper projectDocumentMapper;

    @Autowired
    private ProjectDocumentRepository projectDocumentRepository;

    @Override
    @Transactional
    public ServiceResult<ProjectDocumentResource> save(ProjectDocumentResource projectDocumentResource) {

        return validateProjectDocument(projectDocumentResource).andOnSuccess(() -> {

            ProjectDocument projectDocument = projectDocumentMapper.mapToDomain(projectDocumentResource);

            ProjectDocument savedProjectDocument = projectDocumentRepository.save(projectDocument);
            return serviceSuccess(projectDocumentMapper.mapToResource(savedProjectDocument));
        });
    }

    private ServiceResult<Void> validateProjectDocument(ProjectDocumentResource projectDocumentResource) {
        return projectDocumentResource.getFileTypes() != null && projectDocumentResource.getFileTypes().size() > 0 ?
                serviceSuccess() : serviceFailure(FILES_SELECT_AT_LEAST_ONE_FILE_TYPE);
    }

    @Override
    @Transactional
    public ServiceResult<List<ProjectDocumentResource>> saveAll(List<ProjectDocumentResource> projectDocumentResources) {

        return validateProjectDocument(projectDocumentResources).andOnSuccess(() -> {

            List<ProjectDocument> projectDocuments = simpleMap(projectDocumentResources,
                    projectDocumentResource -> projectDocumentMapper.mapToDomain(projectDocumentResource));

            List<ProjectDocument> savedProjectDocuments = (List<ProjectDocument>) projectDocumentRepository.save(projectDocuments);
            return serviceSuccess(simpleMap(savedProjectDocuments, savedProjectDocument -> projectDocumentMapper.mapToResource(savedProjectDocument)));
        });
    }

    private ServiceResult<Void> validateProjectDocument(List<ProjectDocumentResource> projectDocumentResources) {

        return simpleAnyMatch(projectDocumentResources,
                projectDocumentResource -> projectDocumentResource.getFileTypes() == null || projectDocumentResource.getFileTypes().size() <= 0) ?
                serviceFailure(FILES_SELECT_AT_LEAST_ONE_FILE_TYPE) : serviceSuccess();
    }

    @Override
    @Transactional
    public ServiceResult<ProjectDocumentResource> findOne(long id) {
        ProjectDocument projectDocument = projectDocumentRepository.findOne(id);
        return serviceSuccess(projectDocumentMapper.mapToResource(projectDocument));
    }

    @Override
    @Transactional
    public ServiceResult<List<ProjectDocumentResource>> findByCompetitionId(long competitionId) {
        List<ProjectDocument> projectDocuments = projectDocumentRepository.findByCompetitionId(competitionId);
        return serviceSuccess(simpleMap(projectDocuments, projectDocument -> projectDocumentMapper.mapToResource(projectDocument)));
    }

    @Override
    @Transactional
    public ServiceResult<Void> delete(long id) {
        projectDocumentRepository.delete(id);
        return serviceSuccess();
    }
}

