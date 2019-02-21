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

import static java.util.Arrays.asList;
import java.util.List;

import static org.innovateuk.ifs.commons.error.CommonFailureKeys.FILES_SELECT_AT_LEAST_ONE_FILE_TYPE;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.PROJECT_DOCUMENT_TITLE_HAS_BEEN_USED;
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

        return validateCompetitionDocument(asList(competitionDocumentResource)).andOnSuccess(() -> {

            CompetitionDocument competitionDocument = competitionDocumentMapper.mapToDomain(competitionDocumentResource);

            CompetitionDocument savedCompetitionDocument = competitionDocumentConfigRepository.save(competitionDocument);
            return serviceSuccess(competitionDocumentMapper.mapToResource(savedCompetitionDocument));

        });
    }

    private boolean validateAtLeastOneFileType(CompetitionDocumentResource competitionDocumentResource) {
        return competitionDocumentResource.getFileTypes() != null && competitionDocumentResource.getFileTypes().size() > 0 ;
    }

    private boolean validateUniqueDocumentTitle(CompetitionDocumentResource competitionDocumentResource)
    {
        Long competitionId = competitionDocumentResource.getCompetition();
        if (competitionId != null) {
            List<CompetitionDocumentResource> currentDocuments = findByCompetitionId(competitionId).getSuccess();
            for (CompetitionDocumentResource currentDocument : currentDocuments) {
                if (currentDocument.getTitle().equals(competitionDocumentResource.getTitle()) &&
                        !currentDocument.getId().equals(competitionDocumentResource.getId())) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    @Transactional
    public ServiceResult<List<CompetitionDocumentResource>> saveAll(List<CompetitionDocumentResource> competitionDocumentResources) {

        return validateCompetitionDocument(competitionDocumentResources).andOnSuccess(() -> {

            List<CompetitionDocument> competitionDocuments = simpleMap(competitionDocumentResources,
                    competitionDocumentResource -> competitionDocumentMapper.mapToDomain(competitionDocumentResource));

            List<CompetitionDocument> savedCompetitionDocuments = (List<CompetitionDocument>) competitionDocumentConfigRepository.saveAll(competitionDocuments);
            return serviceSuccess(simpleMap(savedCompetitionDocuments, savedCompeitionDocument -> competitionDocumentMapper.mapToResource(savedCompeitionDocument)));
        });
    }

    private ServiceResult<Void> validateCompetitionDocument(List<CompetitionDocumentResource> competitionDocumentResources) {

        if (simpleAnyMatch(competitionDocumentResources,
                competitionDocumentResource -> !validateUniqueDocumentTitle(competitionDocumentResource)))
        {
            return serviceFailure(PROJECT_DOCUMENT_TITLE_HAS_BEEN_USED);
        }

        if (simpleAnyMatch(competitionDocumentResources,
                competitionDocumentResource -> !validateAtLeastOneFileType(competitionDocumentResource)))
        {
            return serviceFailure(FILES_SELECT_AT_LEAST_ONE_FILE_TYPE);
        }

        return serviceSuccess();
    }

    @Override
    @Transactional
    public ServiceResult<CompetitionDocumentResource> findOne(long id) {
        CompetitionDocument competitionDocument = competitionDocumentConfigRepository.findById(id).get();
        return serviceSuccess(competitionDocumentMapper.mapToResource(competitionDocument));
    }

    @Override
    @Transactional
    public ServiceResult<List<CompetitionDocumentResource>> findByCompetitionId(long competitionId) {
        List<CompetitionDocument> competitionDocuments = competitionDocumentConfigRepository.findByCompetitionId(competitionId);
        return serviceSuccess(simpleMap(competitionDocuments, competitionDocument -> competitionDocumentMapper.mapToResource(competitionDocument)));
    }

    @Override
    @Transactional
    public ServiceResult<Void> delete(long id) {
        competitionDocumentConfigRepository.deleteById(id);
        return serviceSuccess();
    }
}
