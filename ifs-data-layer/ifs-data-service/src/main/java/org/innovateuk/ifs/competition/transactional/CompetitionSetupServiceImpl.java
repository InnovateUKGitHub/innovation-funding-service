package org.innovateuk.ifs.competition.transactional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.*;
import org.innovateuk.ifs.competition.mapper.CompetitionMapper;
import org.innovateuk.ifs.competition.mapper.CompetitionTypeMapper;
import org.innovateuk.ifs.competition.mapper.GrantTermsAndConditionsMapper;
import org.innovateuk.ifs.competition.repository.CompetitionTypeRepository;
import org.innovateuk.ifs.competition.repository.GrantTermsAndConditionsRepository;
import org.innovateuk.ifs.competition.repository.InnovationLeadRepository;
import org.innovateuk.ifs.competition.repository.MilestoneRepository;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSubsection;
import org.innovateuk.ifs.competition.resource.CompetitionTypeResource;
import org.innovateuk.ifs.publiccontent.repository.PublicContentRepository;
import org.innovateuk.ifs.publiccontent.transactional.PublicContentService;
import org.innovateuk.ifs.setup.repository.SetupStatusRepository;
import org.innovateuk.ifs.setup.resource.SetupStatusResource;
import org.innovateuk.ifs.setup.transactional.SetupStatusService;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.innovateuk.ifs.user.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

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
    private InnovationLeadRepository innovationLeadRepository;
    @Autowired
    private CompetitionFunderService competitionFunderService;
    @Autowired
    private PublicContentService publicContentService;
    @Autowired
    private CompetitionSetupTemplateService competitionSetupTemplateService;
    @Autowired
    private SetupStatusService setupStatusService;
    @Autowired
    private SetupStatusRepository setupStatusRepository;
    @Autowired
    private GrantTermsAndConditionsRepository grantTermsAndConditionsRepository;
    @Autowired
    private GrantTermsAndConditionsMapper termsAndConditionsMapper;
    @Autowired
    private PublicContentRepository publicContentRepository;
    @Autowired
    private MilestoneRepository milestoneRepository;

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
            List<String> codes = openingSameMonth.stream().map(Competition::getCode).sorted().peek(c -> LOG.info("Codes : " + c)).collect(Collectors.toList());
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
    public ServiceResult<CompetitionResource> save(Long id, CompetitionResource competitionResource) {
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
    public ServiceResult<Void> updateCompetitionInitialDetails(final Long competitionId, final CompetitionResource
            competitionResource, final Long existingInnovationLeadId) {

        return deleteExistingInnovationLead(competitionId, existingInnovationLeadId)
                .andOnSuccess(() -> attachCorrectTermsAndConditions(competitionResource))
                .andOnSuccess(() -> save(competitionId, competitionResource))
                .andOnSuccess(this::saveInnovationLead);
    }

    private ServiceResult<Void> attachCorrectTermsAndConditions(CompetitionResource competitionResource) {

        Long competitionTypeId = competitionResource.getCompetitionType();

        // it is possible during autosave for this competition type to not yet be selected.  Therefore we need a null check
        // here
        if (competitionTypeId != null) {
            CompetitionType competitionTypeSelected = competitionTypeRepository.findOne(competitionTypeId);
            GrantTermsAndConditions termsAndConditions = competitionTypeSelected.getTemplate().getTermsAndConditions();
            competitionResource.setTermsAndConditions(termsAndConditionsMapper.mapToResource(termsAndConditions));
        }

        return serviceSuccess();
    }

    private ServiceResult<Void> deleteExistingInnovationLead(Long competitionId, Long existingInnovationLeadId) {

        if (existingInnovationLeadId != null) {
            innovationLeadRepository.deleteInnovationLead(competitionId, existingInnovationLeadId);
        }

        return serviceSuccess();
    }

    private ServiceResult<Void> saveInnovationLead(CompetitionResource competitionResource) {

        if (competitionResource.getLeadTechnologist() != null) {
            Competition competition = competitionMapper.mapToDomain(competitionResource);

            if (!doesInnovationLeadAlreadyExist(competition)) {
                User innovationLead = competition.getLeadTechnologist();
                innovationLeadRepository.save(new InnovationLead(competition, innovationLead));
            }
        }

        return serviceSuccess();
    }

    private boolean doesInnovationLeadAlreadyExist(Competition competition) {
        return innovationLeadRepository.existsInnovationLead(competition.getId(), competition.getLeadTechnologist().getId());
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
    public ServiceResult<Map<CompetitionSetupSection, Optional<Boolean>>> getSectionStatuses(Long competitionId) {
        List<SetupStatusResource> setupStatuses = setupStatusService
                .findByTargetClassNameAndTargetId(Competition.class.getName(), competitionId).getSuccess();

        return serviceSuccess(Arrays.stream(CompetitionSetupSection.values())
                .collect(Collectors.toMap(section -> section, section -> findStatus(setupStatuses, section.getClass().getName(), section.getId()))));
    }

    @Override
    public ServiceResult<Map<CompetitionSetupSubsection, Optional<Boolean>>> getSubsectionStatuses(Long competitionId) {
        List<SetupStatusResource> setupStatuses = setupStatusService
                .findByTargetClassNameAndTargetId(Competition.class.getName(), competitionId).getSuccess();

        return serviceSuccess(Arrays.stream(CompetitionSetupSubsection.values())
                .collect(Collectors.toMap(subsection -> subsection, subsection -> findStatus(setupStatuses, subsection.getClass().getName(), subsection.getId()))));
    }

    private Optional<Boolean> findStatus(List<SetupStatusResource> setupStatuses, String className, Long classPk) {
        return setupStatuses.stream()
                .filter(setupStatusResource ->
                        setupStatusResource.getClassName().equals(className) &&
                        setupStatusResource.getClassPk().equals(classPk))
                .map(SetupStatusResource::getCompleted)
                .findAny();
    }

    @Override
    @Transactional
    public ServiceResult<SetupStatusResource> markSectionComplete(Long competitionId, CompetitionSetupSection section) {
        SetupStatusResource setupStatus = findOrCreateSetupStatusResource(competitionId, section.getClass().getName(), section.getId(), Optional.empty());
        setupStatus.setCompleted(Boolean.TRUE);

        return setupStatusService.saveSetupStatus(setupStatus);
    }

    @Override
    @Transactional
    public ServiceResult<SetupStatusResource> markSectionIncomplete(Long competitionId, CompetitionSetupSection section) {
        SetupStatusResource setupStatus = findOrCreateSetupStatusResource(competitionId, section.getClass().getName(), section.getId(), Optional.empty());
        setupStatus.setCompleted(Boolean.FALSE);

        return setupStatusService.saveSetupStatus(setupStatus);
    }

    @Override
    @Transactional
    public ServiceResult<SetupStatusResource> markSubsectionComplete(Long competitionId, CompetitionSetupSection parentSection, CompetitionSetupSubsection subsection) {
        SetupStatusResource setupStatus = findOrCreateSetupStatusResource(competitionId, subsection.getClass().getName(), subsection.getId(), Optional.of(parentSection));
        setupStatus.setCompleted(Boolean.TRUE);

        return setupStatusService.saveSetupStatus(setupStatus);
    }

    @Override
    @Transactional
    public ServiceResult<SetupStatusResource> markSubsectionIncomplete(Long competitionId, CompetitionSetupSection parentSection, CompetitionSetupSubsection subsection) {
        SetupStatusResource setupStatus = findOrCreateSetupStatusResource(competitionId, subsection.getClass().getName(), subsection.getId(), Optional.of(parentSection));
        setupStatus.setCompleted(Boolean.FALSE);

        return setupStatusService.saveSetupStatus(setupStatus);
    }

    private SetupStatusResource findOrCreateSetupStatusResource(Long competitionId, String sectionClassName, Long sectionId, Optional<CompetitionSetupSection> parentSection) {
        Optional<SetupStatusResource> setupStatusOpt = setupStatusService.findSetupStatusAndTarget(sectionClassName, sectionId,Competition.class.getName(), competitionId)
                .getOptionalSuccessObject();

        return setupStatusOpt.orElseGet(() -> createNewSetupStatus(competitionId, sectionClassName, sectionId, parentSection));
    }

    private SetupStatusResource createNewSetupStatus(Long competitionId, String sectionClassName, Long sectionId, Optional<CompetitionSetupSection> parentSectionOpt) {
        SetupStatusResource newSetupStatusResource = new SetupStatusResource(sectionClassName, sectionId, Competition.class.getName(), competitionId);

        parentSectionOpt.ifPresent(parentSection -> {
            Optional<SetupStatusResource> parentSetupStatusOpt =
                    setupStatusService.findSetupStatusAndTarget(parentSection.getClass().getName(), parentSection.getId(),Competition.class.getName(), competitionId)
                    .getOptionalSuccessObject();

            newSetupStatusResource.setParentId(
                    parentSetupStatusOpt
                        .orElseGet(() -> markSectionIncomplete(competitionId, parentSection).getSuccess())
                        .getId()
            );
        });

        return newSetupStatusResource;
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
        return competitionSetupTemplateService.initializeCompetitionByCompetitionTemplate(competitionId, competitionTypeId)
                .andOnSuccess(() -> serviceSuccess());
    }

    @Override
    @Transactional
    public ServiceResult<Void> deleteCompetition(long competitionId) {
        return getCompetition(competitionId).andOnSuccess(competition ->
                deletePublicContentForCompetition(competition).andOnSuccess(() -> {
                    deleteFormValidatorsForCompetitionQuestions(competition);
                    deleteMilestonesForCompetition(competition);
                    deleteInnovationLead(competition);
                    deleteSetupStatus(competition);
                    competitionRepository.delete(competition);
                    return serviceSuccess();
                }));
    }

    private void deleteSetupStatus(Competition competition) {
        setupStatusRepository.deleteByTargetClassNameAndTargetId(Competition.class.getName(), competition.getId());
    }

    private void deleteMilestonesForCompetition(Competition competition) {
        competition.getMilestones().clear();
        milestoneRepository.deleteByCompetitionId(competition.getId());
    }

    private void deleteInnovationLead(Competition competition) {
        innovationLeadRepository.deleteAllInnovationLeads(competition.getId());
    }

    private ServiceResult<Void> deletePublicContentForCompetition(Competition competition) {
        return find(publicContentRepository.findByCompetitionId(competition.getId()), notFoundError(Competition.class,
                competition.getId())).andOnSuccess(publicContent -> {
            publicContentRepository.delete(publicContent);
            return serviceSuccess();
        });
    }

    private void deleteFormValidatorsForCompetitionQuestions(Competition competition) {
        competition.getSections().forEach(section ->
                section.getQuestions().forEach(question ->
                        question.getFormInputs().forEach(formInput ->
                                formInput.getFormValidators().clear())));
        competitionRepository.save(competition);
    }

    private ServiceResult<CompetitionResource> persistNewCompetition(Competition competition) {
        GrantTermsAndConditions defaultTermsAndConditions = grantTermsAndConditionsRepository.findOneByTemplate
                (GrantTermsAndConditionsRepository.DEFAULT_TEMPLATE_NAME);

        competition.setTermsAndConditions(defaultTermsAndConditions);

        Competition savedCompetition = competitionRepository.save(competition);
        return publicContentService.initialiseByCompetitionId(savedCompetition.getId())
                .andOnSuccessReturn(() -> competitionMapper.mapToResource(savedCompetition));
    }
}
