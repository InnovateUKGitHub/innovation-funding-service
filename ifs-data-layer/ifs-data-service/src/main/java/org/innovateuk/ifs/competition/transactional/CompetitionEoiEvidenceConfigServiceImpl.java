package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.CompetitionEoiDocument;
import org.innovateuk.ifs.competition.mapper.CompetitionEoiDocumentMapper;
import org.innovateuk.ifs.competition.mapper.CompetitionEoiEvidenceConfigMapper;
import org.innovateuk.ifs.competition.repository.CompetitionEoiDocumentRepository;
import org.innovateuk.ifs.competition.resource.CompetitionEoiDocumentResource;
import org.innovateuk.ifs.competition.resource.CompetitionEoiEvidenceConfigResource;
import org.innovateuk.ifs.file.domain.FileType;
import org.innovateuk.ifs.file.repository.FileTypeRepository;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.file.resource.FileTypeCategory.*;
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
    private CompetitionService competitionService;

    private static final String PDF_FILE_TYPE = "PDF";
    private static final String SPREADSHEET_FILE_TYPE = "Spreadsheet";
    private static final String DOCUMENT_FILE_TYPE ="text document";

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
    public ServiceResult<List<CompetitionEoiDocumentResource>> findAllByCompetitionEoiEvidenceConfigId(long competitionEoiEvidenceConfigId) {
        return find(competitionEoiDocumentRepository.findByCompetitionEoiEvidenceConfigId(competitionEoiEvidenceConfigId), notFoundError(CompetitionEoiDocumentResource.class, competitionEoiEvidenceConfigId))
                .andOnSuccessReturn(competitionEoiDocumentMapper::mapToResource);
    }

    @Override
    public ServiceResult<List<String>> getValidMediaTypesForEoiEvidence(long competitionEoiEvidenceConfigId) {
        return findAllByCompetitionEoiEvidenceConfigId(competitionEoiEvidenceConfigId).andOnSuccessReturn(eoiEvidenceConfig -> getMediaTypes(eoiEvidenceConfig.stream().map(CompetitionEoiDocumentResource::getFileTypeId).collect(Collectors.toList())));
    }

    private List<String> getMediaTypes(List<Long> fileTypeIds) {
        List<String> validMediaTypes = new ArrayList<>();

//        fileTypeIds.forEach(fileTypeId -> validMediaTypes.add(fileTypeRepository.findById(fileTypeId).get().getExtension()));
        for (Long fileTypeId : fileTypeIds) {
            switch (fileTypeRepository.findById(fileTypeId).get().getName()) {
                case PDF_FILE_TYPE:
                    validMediaTypes.addAll(PDF.getMimeTypes());
                    break;
                case SPREADSHEET_FILE_TYPE:
                    validMediaTypes.addAll(SPREADSHEET.getMimeTypes());
                    break;
                case DOCUMENT_FILE_TYPE:
                    validMediaTypes.addAll(DOCUMENT.getMimeTypes());
                    break;
                default:
                    // do nothing
            }
        }
        return validMediaTypes;
    }

}
