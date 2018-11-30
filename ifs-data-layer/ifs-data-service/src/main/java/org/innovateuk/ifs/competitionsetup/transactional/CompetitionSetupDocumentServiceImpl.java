package org.innovateuk.ifs.competitionsetup.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionDocumentResource;
import org.innovateuk.ifs.competitionsetup.domain.CompetitionDocument;
import org.innovateuk.ifs.competitionsetup.mapper.CompetitionDocumentMapper;
import org.innovateuk.ifs.competitionsetup.repository.CompetitionDocumentConfigRepository;
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
public class CompetitionSetupDocumentServiceImpl extends BaseTransactionalService implements CompetitionSetupDocumentService {

    @Autowired
    private CompetitionDocumentMapper competitionDocumentMapper;

    @Autowired
    private CompetitionDocumentConfigRepository competitionDocumentConfigRepository;

    @Override
    @Transactional
    public ServiceResult<CompetitionDocumentResource> save(CompetitionDocumentResource competitionDocumentResource) {

        return validateProjectDocument(competitionDocumentResource).andOnSuccess(() -> {

            CompetitionDocument competitionDocument = competitionDocumentMapper.mapToDomain(competitionDocumentResource);

            CompetitionDocument savedCompetitionDocument = competitionDocumentConfigRepository.save(competitionDocument);
            return serviceSuccess(competitionDocumentMapper.mapToResource(savedCompetitionDocument));
        });
    }

    private ServiceResult<Void> validateProjectDocument(CompetitionDocumentResource competitionDocumentResource) {
        return competitionDocumentResource.getFileTypes() != null && competitionDocumentResource.getFileTypes().size() > 0 ?
                serviceSuccess() : serviceFailure(FILES_SELECT_AT_LEAST_ONE_FILE_TYPE);
    }

    @Override
    @Transactional
    public ServiceResult<List<CompetitionDocumentResource>> saveAll(List<CompetitionDocumentResource> competitionDocumentResources) {

        return validateProjectDocument(competitionDocumentResources).andOnSuccess(() -> {

            List<CompetitionDocument> competitionDocuments = simpleMap(competitionDocumentResources,
                    projectDocumentResource -> competitionDocumentMapper.mapToDomain(projectDocumentResource));

            List<CompetitionDocument> savedCompetitionDocuments = (List<CompetitionDocument>) competitionDocumentConfigRepository.save(competitionDocuments);
            return serviceSuccess(simpleMap(savedCompetitionDocuments, savedProjectDocument -> competitionDocumentMapper.mapToResource(savedProjectDocument)));
        });
    }

    private ServiceResult<Void> validateProjectDocument(List<CompetitionDocumentResource> competitionDocumentResources) {

        return simpleAnyMatch(competitionDocumentResources,
                projectDocumentResource -> projectDocumentResource.getFileTypes() == null || projectDocumentResource.getFileTypes().size() <= 0) ?
                serviceFailure(FILES_SELECT_AT_LEAST_ONE_FILE_TYPE) : serviceSuccess();
    }

    @Override
    @Transactional
    public ServiceResult<CompetitionDocumentResource> findOne(long id) {
        CompetitionDocument competitionDocument = competitionDocumentConfigRepository.findOne(id);
        return serviceSuccess(competitionDocumentMapper.mapToResource(competitionDocument));
    }

    @Override
    @Transactional
    public ServiceResult<List<CompetitionDocumentResource>> findByCompetitionId(long competitionId) {
        List<CompetitionDocument> competitionDocuments = competitionDocumentConfigRepository.findByCompetitionId(competitionId);
        return serviceSuccess(simpleMap(competitionDocuments, projectDocument -> competitionDocumentMapper.mapToResource(projectDocument)));
    }

    @Override
    @Transactional
    public ServiceResult<Void> delete(long id) {
        competitionDocumentConfigRepository.delete(id);
        return serviceSuccess();
    }
}

