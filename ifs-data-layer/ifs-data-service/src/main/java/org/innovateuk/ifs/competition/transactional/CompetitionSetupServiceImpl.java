package org.innovateuk.ifs.competition.transactional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.mapper.CompetitionMapper;
import org.innovateuk.ifs.competition.mapper.CompetitionTypeMapper;
import org.innovateuk.ifs.competition.repository.AssessorCountOptionRepository;
import org.innovateuk.ifs.competition.repository.CompetitionTypeRepository;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.resource.CompetitionTypeResource;
import org.innovateuk.ifs.invite.domain.CompetitionParticipant;
import org.innovateuk.ifs.invite.domain.CompetitionParticipantRole;
import org.innovateuk.ifs.invite.domain.ParticipantStatus;
import org.innovateuk.ifs.invite.repository.CompetitionParticipantRepository;
import org.innovateuk.ifs.publiccontent.transactional.PublicContentService;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.innovateuk.ifs.user.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;

/**
 * Service for operations around the usage and processing of Competitions
 */
@Service
public class CompetitionSetupServiceImpl extends BaseTransactionalService implements CompetitionSetupService {
    private static final Log LOG = LogFactory.getLog(CompetitionSetupServiceImpl.class);

    @Autowired
    private CompetitionMapper competitionMapper;
    @Autowired
    private CompetitionTypeMapper competitionTypeMapper;
    @Autowired
    private CompetitionTypeRepository competitionTypeRepository;
    @Autowired
    private CompetitionParticipantRepository competitionParticipantRepository;
    @Autowired
    private CompetitionFunderService competitionFunderService;
    @Autowired
    private AssessorCountOptionRepository assessorCountOptionRepository;
    @Autowired
    private PublicContentService publicContentService;
    @Autowired
    private CompetitionSetupTemplateService competitionSetupTemplateService;

    public static final BigDecimal DEFAULT_ASSESSOR_PAY = new BigDecimal(100);

    @Override
    @Transactional
    public ServiceResult<String> generateCompetitionCode(Long id, ZonedDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYMM");
        Competition competition = competitionRepository.findById(id);
        String datePart = formatter.format(dateTime);
        List<Competition> openingSameMonth = competitionRepository.findByCodeLike("%" + datePart + "%");

        String unusedCode = "";
        if (StringUtils.hasText(competition.getCode())) {
            return serviceSuccess(competition.getCode());
        } else if (openingSameMonth.isEmpty()) {
            unusedCode = datePart + "-1";
        } else {
            List<String> codes = openingSameMonth.stream().map(c -> c.getCode()).sorted().peek(c -> LOG.info("Codes : " + c)).collect(Collectors.toList());
            for (int i = 1; i < 10000; i++) {
                unusedCode = datePart + "-" + i;
                if (!codes.contains(unusedCode)) {
                    break;
                }
            }
        }

        competition.setCode(unusedCode);
        competitionRepository.save(competition);
        return serviceSuccess(unusedCode);
    }

    @Override
    @Transactional
    public ServiceResult<CompetitionResource> update(Long id, CompetitionResource competitionResource) {
        Competition competition = competitionMapper.mapToDomain(competitionResource);

        saveFunders(competitionResource);
        competition = competitionRepository.save(competition);
        return serviceSuccess(competitionMapper.mapToResource(competition));
    }

    private void saveFunders(CompetitionResource competitionResource) {
        competitionFunderService.reinsertFunders(competitionResource);
    }

    @Override
    @Transactional
    public ServiceResult<Void> updateCompetitionInitialDetails(Long competitionId, CompetitionResource competitionResource, Long existingLeadTechnologistId) {

        return deleteExistingLeadTechnologist(competitionId, existingLeadTechnologistId)
                .andOnSuccess(() -> update(competitionId, competitionResource))
                .andOnSuccess(updatedCompetitionResource -> saveLeadTechnologist(updatedCompetitionResource));
    }

    private ServiceResult<Void> deleteExistingLeadTechnologist(Long competitionId, Long existingLeadTechnologistId) {

        if (existingLeadTechnologistId != null) {

            CompetitionParticipant competitionParticipant =
                    competitionParticipantRepository.getByCompetitionIdAndUserIdAndRole(competitionId,
                            existingLeadTechnologistId, CompetitionParticipantRole.INNOVATION_LEAD);

            if (competitionParticipant != null) {
                competitionParticipantRepository.delete(competitionParticipant);
            }
        }

        return serviceSuccess();
    }

    private ServiceResult<Void> saveLeadTechnologist(CompetitionResource competitionResource) {

        if (competitionResource.getLeadTechnologist() != null) {
            Competition competition = competitionMapper.mapToDomain(competitionResource);

            if (!doesLeadTechnologistAlreadyExist(competition)) {
                User leadTechnologist = competition.getLeadTechnologist();

                CompetitionParticipant competitionParticipant = new CompetitionParticipant();
                competitionParticipant.setProcess(competition);
                competitionParticipant.setUser(leadTechnologist);
                competitionParticipant.setRole(CompetitionParticipantRole.INNOVATION_LEAD);
                competitionParticipant.setStatus(ParticipantStatus.ACCEPTED);

                competitionParticipantRepository.save(competitionParticipant);
            }
        }

        return serviceSuccess();
    }

    private boolean doesLeadTechnologistAlreadyExist(Competition competition) {

        CompetitionParticipant existingCompetitionParticipant =
                competitionParticipantRepository.getByCompetitionIdAndUserIdAndRole(competition.getId(),
                        competition.getLeadTechnologist().getId(), CompetitionParticipantRole.INNOVATION_LEAD);

        return existingCompetitionParticipant != null;
    }

    @Override
    @Transactional
    public ServiceResult<CompetitionResource> create() {
        Competition competition = new Competition();
        competition.setSetupComplete(false);
        return persistNewCompetition(competition);
    }

    @Override
    @Transactional
    public ServiceResult<CompetitionResource> createNonIfs() {
        Competition competition = new Competition();
        competition.setNonIfs(true);
        return persistNewCompetition(competition);
    }

    @Override
    @Transactional
    public ServiceResult<Void> markSectionComplete(Long competitionId, CompetitionSetupSection section) {
        Competition competition = competitionRepository.findById(competitionId);
        competition.getSectionSetupStatus().put(section, Boolean.TRUE);
        return serviceSuccess();
    }

    @Override
    @Transactional
    public ServiceResult<Void> markSectionInComplete(Long competitionId, CompetitionSetupSection section) {
        Competition competition = competitionRepository.findById(competitionId);
        competition.getSectionSetupStatus().put(section, Boolean.FALSE);
        return serviceSuccess();
    }

    @Override
    @Transactional
    public ServiceResult<Void> returnToSetup(Long competitionId) {
        Competition competition = competitionRepository.findById(competitionId);
        competition.setSetupComplete(false);
        return serviceSuccess();
    }

    @Override
    @Transactional
    public ServiceResult<Void> markAsSetup(Long competitionId) {
        Competition competition = competitionRepository.findById(competitionId);
        competition.setSetupComplete(true);
        return serviceSuccess();
    }

    @Override
    public ServiceResult<List<CompetitionTypeResource>> findAllTypes() {
        return serviceSuccess((List) competitionTypeMapper.mapToResource(competitionTypeRepository.findAll()));
    }

    @Override
    @Transactional
    public ServiceResult<Void> copyFromCompetitionTypeTemplate(Long competitionId, Long competitionTypeId) {
        return competitionSetupTemplateService.createCompetitionByCompetitionTemplate(competitionId, competitionTypeId)
                .andOnSuccess(() -> serviceSuccess());
    }

    private ServiceResult<CompetitionResource> persistNewCompetition(Competition competition) {
        Competition savedCompetition = competitionRepository.save(competition);
        return publicContentService.initialiseByCompetitionId(savedCompetition.getId())
                .andOnSuccessReturn(() -> competitionMapper.mapToResource(savedCompetition));
    }
}
