package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.CompetitionEoiEvidenceConfig;
import org.innovateuk.ifs.competition.mapper.CompetitionEoiEvidenceConfigMapper;
import org.innovateuk.ifs.competition.repository.CompetitionEoiEvidenceConfigRepository;
import org.innovateuk.ifs.competition.resource.CompetitionEoiEvidenceConfigResource;
import org.innovateuk.ifs.file.domain.FileType;
import org.innovateuk.ifs.file.mapper.FileTypeMapper;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

@Service
public class CompetitionEoiEvidenceConfigServiceImpl extends BaseTransactionalService implements CompetitionEoiEvidenceConfigService {

    @Autowired
    private CompetitionEoiEvidenceConfigRepository competitionEoiEvidenceConfigRepository;

    @Autowired
    private CompetitionEoiEvidenceConfigMapper mapper;

    @Autowired
    private FileTypeMapper fileTypeMapper;

    @Override
    public ServiceResult<CompetitionEoiEvidenceConfigResource> findOneByCompetitionId(long competitionId) {
        Optional<CompetitionEoiEvidenceConfig> config = competitionEoiEvidenceConfigRepository.findOneByCompetitionId(competitionId);

        if (config.isPresent()) {
            return serviceSuccess(mapper.mapToResource(config.get()));
        }

        return serviceSuccess(new CompetitionEoiEvidenceConfigResource());
    }

    @Override
    @Transactional
    public ServiceResult<CompetitionEoiEvidenceConfigResource> create(CompetitionEoiEvidenceConfigResource competitionEoiEvidenceConfigResource) {
        Long competitionId = competitionEoiEvidenceConfigResource.getCompetitionId();
        return find(competitionRepository.findById(competitionId), notFoundError(Competition.class, competitionId))
                .andOnSuccessReturn((competition) -> {
                    competition.setCompetitionEoiEvidenceConfig(mapper.mapToDomain(competitionEoiEvidenceConfigResource));
                    return mapper.mapToResource(competition.getCompetitionEoiEvidenceConfig());
                });
    }

    @Override
    @Transactional
    public ServiceResult<Void> update(long competitionId, CompetitionEoiEvidenceConfigResource competitionEoiEvidenceConfigResource) {
        return find(competitionEoiEvidenceConfigRepository.findOneByCompetitionId(competitionId), notFoundError(CompetitionEoiEvidenceConfig.class, competitionId))
                .andOnSuccessReturnVoid((config) -> {
                    config.setEvidenceRequired(competitionEoiEvidenceConfigResource.isEvidenceRequired());
                    config.setEvidenceTitle(competitionEoiEvidenceConfigResource.getEvidenceTitle());
                    config.setEvidenceGuidance(competitionEoiEvidenceConfigResource.getEvidenceGuidance());

                    List<FileType> fileTypes = competitionEoiEvidenceConfigResource.getFileTypes().stream()
                            .map(fileTypeMapper::mapToDomain)
                            .collect(Collectors.toList());

                    config.setFileTypes(fileTypes);
                });
    }
}
