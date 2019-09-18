package org.innovateuk.ifs.competitionsetup.transactional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.GrantTermsAndConditions;
import org.innovateuk.ifs.competition.domain.InnovationLead;
import org.innovateuk.ifs.competition.mapper.CompetitionMapper;
import org.innovateuk.ifs.competition.mapper.GrantTermsAndConditionsMapper;
import org.innovateuk.ifs.competition.repository.*;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSubsection;
import org.innovateuk.ifs.competition.transactional.CompetitionFunderService;
import org.innovateuk.ifs.competitionsetup.domain.CompetitionDocument;
import org.innovateuk.ifs.file.controller.FileControllerUtils;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.file.domain.FileType;
import org.innovateuk.ifs.file.mapper.FileEntryMapper;
import org.innovateuk.ifs.file.repository.FileTypeRepository;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.FilesizeAndTypeFileValidator;
import org.innovateuk.ifs.file.transactional.FileEntryService;
import org.innovateuk.ifs.file.transactional.FileService;
import org.innovateuk.ifs.publiccontent.repository.PublicContentRepository;
import org.innovateuk.ifs.publiccontent.transactional.PublicContentService;
import org.innovateuk.ifs.setup.repository.SetupStatusRepository;
import org.innovateuk.ifs.setup.resource.SetupStatusResource;
import org.innovateuk.ifs.setup.transactional.SetupStatusService;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.innovateuk.ifs.user.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.aggregate;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.resource.CompetitionDocumentResource.COLLABORATION_AGREEMENT_TITLE;
import static org.innovateuk.ifs.util.CollectionFunctions.combineLists;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;
import static org.springframework.util.ReflectionUtils.*;

/**
 * Service for operations around the usage and processing of Competitions
 */
@Service
public class CompetitionSetupServiceImpl extends BaseTransactionalService implements CompetitionSetupService {
    private static final Log LOG = LogFactory.getLog(CompetitionSetupServiceImpl.class);

    @Autowired
    private CompetitionMapper competitionMapper;
    @Autowired
    private CompetitionTypeRepository competitionTypeRepository;
    @Autowired
    private InnovationLeadRepository innovationLeadRepository;
    @Autowired
    private StakeholderRepository stakeholderRepository;
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

    @Value("${ifs.data.service.file.storage.competition.terms.max.filesize.bytes}")
    private Long maxFileSize;

    @Value("${ifs.data.service.file.storage.competition.terms.valid.media.types}")
    private List<String> validMediaTypes;

    @Autowired
    private FileService fileService;

    @Autowired
    private FileEntryService fileEntryService;

    @Autowired
    @Qualifier("mediaTypeStringsFileValidator")
    private FilesizeAndTypeFileValidator<List<String>> fileValidator;

    @Autowired
    private FileTypeRepository fileTypeRepository;

    @Autowired
    private FileEntryMapper fileEntryMapper;

    private FileControllerUtils fileControllerUtils = new FileControllerUtils();

    public static final BigDecimal DEFAULT_ASSESSOR_PAY = new BigDecimal(100);

    @Override
    @Transactional
    public ServiceResult<String> generateCompetitionCode(Long id, ZonedDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYMM");
        Competition competition = competitionRepository.findById(id).get();
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

        Competition existingCompetition = competitionRepository.findById(competitionResource.getId()).orElse(null);
        Competition competition = competitionMapper.mapToDomain(competitionResource);
        competition = setCompetitionAuditableFields(competition, existingCompetition);

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
                .andOnSuccess(() -> save(competitionId, competitionResource))
                .andOnSuccess(this::saveInnovationLead);
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
        setupStatus.setCompleted(true);

        return setupStatusService.saveSetupStatus(setupStatus);
    }

    @Override
    @Transactional
    public ServiceResult<List<SetupStatusResource>> markSectionIncomplete(long competitionId, CompetitionSetupSection section) {

        List<CompetitionSetupSection> allSectionsToMarkIncomplete = getAllSectionsToMarkIncomplete(section);

        List<ServiceResult<SetupStatusResource>> markIncompleteResults = simpleMap(allSectionsToMarkIncomplete,
                sectionToMarkIncomplete -> setSectionIncompleteAndUpdate(competitionId, sectionToMarkIncomplete));

        return aggregate(markIncompleteResults);
    }

    /**
     * When marking a section as incomplete, mark any next sections as incomplete also as they will need revisiting
     * based on changes to this section.
     */
    private List<CompetitionSetupSection> getAllSectionsToMarkIncomplete(CompetitionSetupSection section) {
        return combineLists(section, section.getAllNextSections());
    }

    private ServiceResult<SetupStatusResource> setSectionIncompleteAndUpdate(Long competitionId, CompetitionSetupSection section) {
        SetupStatusResource setupStatus = findOrCreateSetupStatusResource(competitionId, section.getClass().getName(), section.getId(), Optional.empty());
        setupStatus.setCompleted(false);
        return setupStatusService.saveSetupStatus(setupStatus);
    }

    @Override
    @Transactional
    public ServiceResult<SetupStatusResource> markSubsectionComplete(Long competitionId, CompetitionSetupSection parentSection, CompetitionSetupSubsection subsection) {
        SetupStatusResource setupStatus = findOrCreateSetupStatusResource(competitionId, subsection.getClass().getName(), subsection.getId(), Optional.of(parentSection));
        setupStatus.setCompleted(true);

        return setupStatusService.saveSetupStatus(setupStatus);
    }

    @Override
    @Transactional
    public ServiceResult<SetupStatusResource> markSubsectionIncomplete(Long competitionId, CompetitionSetupSection parentSection, CompetitionSetupSubsection subsection) {
        SetupStatusResource setupStatus = findOrCreateSetupStatusResource(competitionId, subsection.getClass().getName(), subsection.getId(), Optional.of(parentSection));
        setupStatus.setCompleted(false);

        return setupStatusService.saveSetupStatus(setupStatus);
    }

    private SetupStatusResource findOrCreateSetupStatusResource(Long competitionId, String sectionClassName, Long sectionId, Optional<CompetitionSetupSection> parentSection) {
        Optional<SetupStatusResource> setupStatusOpt = setupStatusService.findSetupStatusAndTarget(sectionClassName, sectionId, Competition.class.getName(), competitionId)
                .getOptionalSuccessObject();

        return setupStatusOpt.orElseGet(() -> createNewSetupStatus(competitionId, sectionClassName, sectionId, parentSection));
    }

    private SetupStatusResource createNewSetupStatus(Long competitionId, String sectionClassName, Long sectionId, Optional<CompetitionSetupSection> parentSectionOpt) {
        SetupStatusResource newSetupStatusResource = new SetupStatusResource(sectionClassName, sectionId, Competition.class.getName(), competitionId);

        parentSectionOpt.ifPresent(parentSection -> {

            Optional<SetupStatusResource> parentSetupStatusOpt =
                    setupStatusService.findSetupStatusAndTarget(parentSection.getClass().getName(), parentSection.getId(), Competition.class.getName(), competitionId)
                            .getOptionalSuccessObject();

            long parentStatus = parentSetupStatusOpt.orElseGet(() ->
                    markSectionIncomplete(competitionId, parentSection).getSuccess().get(0)).getId();

            newSetupStatusResource.setParentId(
                    parentStatus
            );
        });

        return newSetupStatusResource;
    }

    @Override
    @Transactional
    public ServiceResult<Void> returnToSetup(Long competitionId) {
        Competition competition = competitionRepository.findById(competitionId).get();
        competition.setSetupComplete(false);
        return serviceSuccess();
    }

    @Override
    @Transactional
    public ServiceResult<Void> markAsSetup(Long competitionId) {
        Competition competition = competitionRepository.findById(competitionId).get();
        competition.setSetupComplete(true);
        return serviceSuccess();
    }

    @Override
    @Transactional
    public ServiceResult<Void> copyFromCompetitionTypeTemplate(Long competitionId, Long competitionTypeId) {
        return competitionSetupTemplateService.initializeCompetitionByCompetitionTemplate(competitionId, competitionTypeId)
                .andOnSuccessReturnVoid();
    }

    @Override
    @Transactional
    public ServiceResult<Void> deleteCompetition(long competitionId) {
        return getCompetition(competitionId).andOnSuccess(competition ->
                deletePublicContentForCompetition(competition).andOnSuccess(() -> {
                    deleteFormValidatorsForCompetitionQuestions(competition);
                    deleteMilestonesForCompetition(competition);
                    deleteInnovationLead(competition);
                    deleteAllStakeholders(competition);
                    deleteSetupStatus(competition);
                    competitionRepository.delete(competition);
                    return serviceSuccess();
                }));
    }

    @Override
    @Transactional
    public ServiceResult<FileEntryResource> uploadCompetitionTerms(String contentType, String contentLength, String originalFilename, long competitionId, HttpServletRequest request) {
        return findCompetition(competitionId)
                .andOnSuccess(competition -> fileControllerUtils.handleFileUpload(contentType, contentLength, originalFilename, fileValidator, validMediaTypes, maxFileSize, request,
                        (fileAttributes, inputStreamSupplier) -> fileService.createFile(fileAttributes.toFileEntryResource(), inputStreamSupplier)
                                .andOnSuccessReturn(created -> {
                                    competition.setCompetitionTerms(created.getValue());
                                    return fileEntryMapper.mapToResource(created.getValue());
                                })
                        )
                        .toServiceResult()
                );
    }

    @Override
    @Transactional
    public ServiceResult<Void> deleteCompetitionTerms(long competitionId) {
        return findCompetition(competitionId)
                .andOnSuccess(competition -> find(competition.getCompetitionTerms(), notFoundError(FileEntry.class))
                        .andOnSuccess(competitionTerms -> fileService.deleteFileIgnoreNotFound(competitionTerms.getId()))
                        .andOnSuccessReturnVoid(() -> competition.setCompetitionTerms(null)));
    }

    private ServiceResult<Competition> findCompetition(long competitionId) {
        return find(competitionRepository.findById(competitionId), notFoundError(Competition.class, competitionId));
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

    private void deleteAllStakeholders(Competition competition) {
        stakeholderRepository.deleteAllStakeholders(competition.getId());
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
        competition.setCompetitionDocuments(createDefaultProjectDocuments(competition));

        Competition savedCompetition = competitionRepository.save(competition);
        return publicContentService.initialiseByCompetitionId(savedCompetition.getId())
                .andOnSuccessReturn(() -> competitionMapper.mapToResource(savedCompetition));
    }

    private List<CompetitionDocument> createDefaultProjectDocuments(Competition competition) {

        FileType pdfFileType = fileTypeRepository.findByName("PDF");

        List<CompetitionDocument> defaultCompetitionDocuments = new ArrayList<>();
        defaultCompetitionDocuments.add(createCollaborationAgreement(competition, singletonList(pdfFileType)));
        defaultCompetitionDocuments.add(createExploitationPlan(competition, singletonList(pdfFileType)));

        return defaultCompetitionDocuments;
    }

    private CompetitionDocument createCollaborationAgreement(Competition competition, List<FileType> fileTypes) {
        return new CompetitionDocument(competition, COLLABORATION_AGREEMENT_TITLE, "<p>The collaboration agreement covers how the consortium will work together on the project and exploit its results. It must be signed by all partners.</p>\n" +
                "\n" +
                "<p>Please allow enough time to complete this document before your project start date.</p>\n" +
                "\n" +
                "<p>Guidance on completing a collaboration agreement can be found on the <a target=\"_blank\" href=\"http://www.ipo.gov.uk/lambert\">Lambert Agreement website</a>.</p>\n" +
                "\n" +
                "<p>Your collaboration agreement must be:</p>\n" +
                "<ul class=\"list-bullet\"><li>in portable document format (PDF)</li>\n" +
                "<li>legible at 100% magnification</li>\n" +
                "<li>less than 10MB in file size</li></ul>",
                false, true, fileTypes);
    }

    private CompetitionDocument createExploitationPlan(Competition competition, List<FileType> fileTypes) {
        return new CompetitionDocument(competition, "Exploitation plan", "<p>This is a confirmation of your overall plan, setting out the business case for your project. This plan will change during the lifetime of the project.</p>\n" +
                "\n" +
                "<p>It should also describe partner activities that will exploit the results of the project so that:</p>\n" +
                "<ul class=\"list-bullet\"><li>changes in the commercial environment can be monitored and accounted for</li>\n" +
                "<li>adequate resources are committed to exploitation</li>\n" +
                "<li>exploitation can be monitored by the stakeholders</li></ul>\n" +
                "\n" +
                "<p>You can download an <a href=\"/files/exploitation_plan.doc\" class=\"govuk-link\">exploitation plan template</a>.</p>\n" +
                "\n" +
                "<p>The uploaded exploitation plan must be:</p>\n" +
                "<ul class=\"list-bullet\"><li>in portable document format (PDF)</li>\n" +
                "<li>legible at 100% magnification</li>\n" +
                "<li>less than 10MB in file size</li></ul>",
                false, true, fileTypes);
    }

    private Competition setCompetitionAuditableFields(Competition competition, Competition existingCompetition) {
        Field createdBy = findField(Competition.class, "createdBy");
        Field createdOn = findField(Competition.class, "createdOn");
        makeAccessible(createdBy);
        makeAccessible(createdOn);
        setField(createdBy, competition, existingCompetition.getCreatedBy());
        setField(createdOn, competition, existingCompetition.getCreatedOn());

        return competition;
    }
}
