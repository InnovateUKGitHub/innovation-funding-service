package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.CompetitionEoiDocument;
import org.innovateuk.ifs.competition.mapper.CompetitionEoiDocumentMapper;
import org.innovateuk.ifs.competition.mapper.CompetitionEoiEvidenceConfigMapper;
import org.innovateuk.ifs.competition.repository.CompetitionEoiDocumentRepository;
import org.innovateuk.ifs.competition.repository.CompetitionEoiEvidenceConfigRepository;
import org.innovateuk.ifs.competition.resource.CompetitionEoiDocumentResource;
import org.innovateuk.ifs.competition.resource.CompetitionEoiEvidenceConfigResource;
import org.innovateuk.ifs.file.domain.FileType;
import org.innovateuk.ifs.file.repository.FileTypeRepository;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

@Service
public class CompetitionEoiEvidenceConfigServiceImpl extends BaseTransactionalService implements CompetitionEoiEvidenceConfigService {

    @Autowired
    private CompetitionEoiDocumentRepository competitionEoiDocumentRepository;

    @Autowired
    private CompetitionEoiEvidenceConfigMapper competitionEoiEvidenceConfigMapper;

    @Autowired
    private CompetitionEoiDocumentMapper competitionEoiDocumentMapper;

    @Autowired
    private FileTypeRepository fileTypeRepository;

    @Autowired
    private CompetitionEoiEvidenceConfigRepository competitionEoiEvidenceConfigRepository;

    @Override
    @Transactional
    public ServiceResult<CompetitionEoiEvidenceConfigResource> create(CompetitionEoiEvidenceConfigResource competitionEoiEvidenceConfigResource) {
        Long competitionId = competitionEoiEvidenceConfigResource.getCompetitionId();
        return find(competitionRepository.findById(competitionId), notFoundError(Competition.class, competitionId))
                .andOnSuccessReturn((competition) -> {
                    competition.setCompetitionEoiEvidenceConfig(competitionEoiEvidenceConfigMapper.mapToDomain(competitionEoiEvidenceConfigResource));
                    return competitionEoiEvidenceConfigMapper.mapToResource(competition.getCompetitionEoiEvidenceConfig());
                });
    }

    @Override
    @Transactional
    public ServiceResult<CompetitionEoiDocumentResource> createDocument(CompetitionEoiDocumentResource competitionEoiDocumentResource) {
        Long fileTypeId = competitionEoiDocumentResource.getFileTypeId();
        return find(fileTypeRepository.findById(fileTypeId), notFoundError(FileType.class, fileTypeId))
                .andOnSuccessReturn((fileType) -> {
                    CompetitionEoiDocument competitionEoiDocument = competitionEoiDocumentMapper.mapToDomain(competitionEoiDocumentResource);
                    return competitionEoiDocumentMapper.mapToResource(competitionEoiDocumentRepository.save(competitionEoiDocument));
                });
    }

    @Override
    public ServiceResult<CompetitionEoiEvidenceConfigResource> findOneByCompetitionId(long competitionId) {
        return find(competitionEoiEvidenceConfigRepository.findOneByCompetitionId(competitionId), notFoundError(CompetitionEoiEvidenceConfigResource.class, competitionId))
                .andOnSuccessReturn(competitionEoiEvidenceConfigMapper::mapToResource);
    }

    @Override
    public ServiceResult<List<CompetitionEoiDocumentResource>> findAllByCompetitionEoiDocumentResources(long competitionEoiEvidenceConfigId) {
        return find(competitionEoiDocumentRepository.findByCompetitionEoiEvidenceConfigId(competitionEoiEvidenceConfigId), notFoundError(CompetitionEoiDocumentResource.class, competitionEoiEvidenceConfigId))
                .andOnSuccessReturn(competitionEoiDocumentMapper::mapToResource);
    }

    @Override
    public ServiceResult<List<Long>> getValidFileTypesIdsForEoiEvidence(long competitionEoiEvidenceConfigId) {
        return serviceSuccess(findAllByCompetitionEoiDocumentResources(competitionEoiEvidenceConfigId)
                .getSuccess()
                .stream()
                .map(CompetitionEoiDocumentResource::getFileTypeId)
                .collect(Collectors.toList()));
    }
}
