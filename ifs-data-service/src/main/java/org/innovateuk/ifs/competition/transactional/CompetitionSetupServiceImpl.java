package org.innovateuk.ifs.competition.transactional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.application.domain.GuidanceRow;
import org.innovateuk.ifs.application.domain.Question;
import org.innovateuk.ifs.application.domain.Section;
import org.innovateuk.ifs.application.repository.GuidanceRowRepository;
import org.innovateuk.ifs.application.repository.QuestionRepository;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.AssessorCountOption;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.CompetitionType;
import org.innovateuk.ifs.competition.mapper.CompetitionMapper;
import org.innovateuk.ifs.competition.mapper.CompetitionTypeMapper;
import org.innovateuk.ifs.competition.repository.AssessorCountOptionRepository;
import org.innovateuk.ifs.competition.repository.CompetitionTypeRepository;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.competition.resource.CompetitionTypeResource;
import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.form.domain.FormValidator;
import org.innovateuk.ifs.form.repository.FormInputRepository;
import org.innovateuk.ifs.publiccontent.transactional.PublicContentService;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.commons.error.CommonFailureKeys.COMPETITION_NOT_EDITABLE;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.COMPETITION_NO_TEMPLATE;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * Service for operations around the usage and processing of Competitions
 */
@Service
public class CompetitionSetupServiceImpl extends BaseTransactionalService implements CompetitionSetupService {
    private static final Log LOG = LogFactory.getLog(CompetitionSetupServiceImpl.class);
    @Autowired
    private CompetitionService competitionService;
    @Autowired
    private CompetitionMapper competitionMapper;
    @Autowired
    private CompetitionTypeMapper competitionTypeMapper;
    @Autowired
    private CompetitionTypeRepository competitionTypeRepository;
    @Autowired
    private CompetitionFunderService competitionFunderService;
    @Autowired
    private AssessorCountOptionRepository competitionTypeAssessorOptionRepository;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private GuidanceRowRepository guidanceRowRepository;
    @Autowired
    private FormInputRepository formInputRepository;
    @Autowired
    private PublicContentService publicContentService;

    @PersistenceContext
    private EntityManager entityManager;

    public static final BigDecimal DEFAULT_ASSESSOR_PAY = new BigDecimal(100);

    @Override
    public ServiceResult<String> generateCompetitionCode(Long id, ZonedDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYMM");
        Competition competition = competitionRepository.findById(id);
        String datePart = formatter.format(dateTime);
        List<Competition> openingSameMonth = competitionRepository.findByCodeLike("%" + datePart + "%");
        if (StringUtils.hasText(competition.getCode())) {
            return serviceSuccess(competition.getCode());
        } else if (openingSameMonth.isEmpty()) {
            String unusedCode = datePart + "-1";
            competition.setCode(unusedCode);
            competitionRepository.save(competition);
            return serviceSuccess(unusedCode);
        } else {
            List<String> codes = openingSameMonth.stream().map(c -> c.getCode()).sorted().peek(c -> LOG.info("Codes : " + c)).collect(Collectors.toList());
            String unusedCode = "";
            for (int i = 1; i < 10000; i++) {
                unusedCode = datePart + "-" + i;
                if (!codes.contains(unusedCode)) {
                    break;
                }
            }
            competition.setCode(unusedCode);
            competitionRepository.save(competition);
            return serviceSuccess(unusedCode);
        }
    }

    @Override
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
    public ServiceResult<CompetitionResource> create() {
        Competition competition = new Competition();
        competition.setSetupComplete(false);
        return persistNewCompetition(competition);
    }

    @Override
    public ServiceResult<CompetitionResource> createNonIfs() {
        Competition competition = new Competition();
        competition.setNonIfs(true);
        return persistNewCompetition(competition);
    }

    @Override
    public ServiceResult<Void> markSectionComplete(Long competitionId, CompetitionSetupSection section) {
        Competition competition = competitionRepository.findById(competitionId);
        competition.getSectionSetupStatus().put(section, Boolean.TRUE);
        return serviceSuccess();
    }

    @Override
    public ServiceResult<Void> markSectionInComplete(Long competitionId, CompetitionSetupSection section) {
        Competition competition = competitionRepository.findById(competitionId);
        competition.getSectionSetupStatus().put(section, Boolean.FALSE);
        return serviceSuccess();
    }

    @Override
    public ServiceResult<Void> returnToSetup(Long competitionId) {
        Competition competition = competitionRepository.findById(competitionId);
        competition.setSetupComplete(false);
        return serviceSuccess();
    }

    @Override
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
    public ServiceResult<Void> copyFromCompetitionTypeTemplate(Long competitionId, Long competitionTypeId) {

        CompetitionType competitionType = competitionTypeRepository.findOne(competitionTypeId);
        Competition template = competitionType.getTemplate();

        Competition competition = competitionRepository.findById(competitionId);
        competition.setCompetitionType(competitionType);
        competition = setDefaultAssessorPayAndCount(competition);
        return copyFromCompetitionTemplate(competition, template);
    }

    @Override
    public ServiceResult<Void> copyFromCompetitionTemplate(Long competitionId, Long templateId) {
        Competition template = competitionRepository.findById(templateId);
        Competition competition = competitionRepository.findById(competitionId);
        return copyFromCompetitionTemplate(competition, template);
    }

    private ServiceResult<Void> copyFromCompetitionTemplate(Competition competition, Competition template) {
        cleanUpCompetitionSections(competition);

        if (competition == null || !competition.getCompetitionStatus().equals(CompetitionStatus.COMPETITION_SETUP)) {
            return serviceFailure(new Error(COMPETITION_NOT_EDITABLE));
        }

        if (template == null) {
            return serviceFailure(new Error(COMPETITION_NO_TEMPLATE));
        }


        List<Section> sectionsWithoutParentSections = template.getSections().stream()
                .filter(s -> s.getParentSection() == null)
                .collect(Collectors.toList());


        attachSections(competition, sectionsWithoutParentSections, null);

        competitionRepository.save(competition);

        return serviceSuccess();
    }

    private void cleanUpCompetitionSections(Competition competition) {
        List<GuidanceRow> scoreRows = guidanceRowRepository.findByFormInputQuestionCompetitionId(competition.getId());
        guidanceRowRepository.delete(scoreRows);

        List<FormInput> formInputs = formInputRepository.findByCompetitionId(competition.getId());
        formInputRepository.delete(formInputs);

        List<Question> questions = questionRepository.findByCompetitionId(competition.getId());
        questionRepository.delete(questions);

        List<Section> sections = sectionRepository.findByCompetitionIdOrderByParentSectionIdAscPriorityAsc(competition.getId());
        competition.setSections(new ArrayList());
        sectionRepository.delete(sections);
    }


    private void attachSections(Competition competition, List<Section> sectionTemplates, Section parentSection) {
        if (sectionTemplates == null) {
            return;
        }
        new ArrayList<>(sectionTemplates).forEach(attachSection(competition, parentSection));
    }

    private Consumer<Section> attachSection(Competition competition, Section parentSection) {
        return (Section section) -> {
            entityManager.detach(section);
            section.setCompetition(competition);
            section.setId(null);
            sectionRepository.save(section);
            if (!competition.getSections().contains(section)) {
                competition.getSections().add(section);
            }

            section.setQuestions(createQuestions(competition, section, section.getQuestions()));

            attachSections(competition, section.getChildSections(), section);

            section.setParentSection(parentSection);
            if (parentSection != null) {
                if (parentSection.getChildSections() == null) {
                    parentSection.setChildSections(new ArrayList<>());
                }
                if (!parentSection.getChildSections().contains(section)) {
                    parentSection.getChildSections().add(section);
                }
            }
        };
    }

    private List<Question> createQuestions(Competition competition, Section section, List<Question> questions) {
        return simpleMap(questions, createQuestion(competition, section));
    }

    private Function<Question, Question> createQuestion(Competition competition, Section section) {
        return (Question question) -> {
            entityManager.detach(question);
            question.setCompetition(competition);
            question.setSection(section);
            question.setId(null);
            questionRepository.save(question);

            question.setFormInputs(createFormInputs(competition, question, question.getFormInputs()));
            return question;
        };
    }


    private List<FormInput> createFormInputs(Competition competition, Question question, List<FormInput> formInputTemplates) {
        return simpleMap(formInputTemplates, createFormInput(competition, question));
    }

    private Function<FormInput, FormInput> createFormInput(Competition competition, Question question) {
        return (FormInput formInput) -> {
            // Extract the validators into a new Set as the hibernate Set contains persistence information which alters
            // the original FormValidator
            Set<FormValidator> copy = new HashSet<>(formInput.getFormValidators());
            entityManager.detach(formInput);
            formInput.setCompetition(competition);
            formInput.setQuestion(question);
            formInput.setId(null);
            formInput.setFormValidators(copy);
            formInputRepository.save(formInput);
            formInput.setGuidanceRows(createFormInputGuidanceRows(formInput, formInput.getGuidanceRows()));
            return formInput;
        };
    }


    private Competition setDefaultAssessorPayAndCount(Competition competition) {
        if (competition.getAssessorCount() == null) {
            Optional<AssessorCountOption> defaultAssessorOption = competitionTypeAssessorOptionRepository.findByCompetitionTypeIdAndDefaultOptionTrue(competition.getCompetitionType().getId());
            if (defaultAssessorOption.isPresent()) {
                competition.setAssessorCount(defaultAssessorOption.get().getOptionValue());
            }
        }

        if (competition.getAssessorPay() == null) {
            competition.setAssessorPay(DEFAULT_ASSESSOR_PAY);
        }
        return competition;
    }

    private List<GuidanceRow> createFormInputGuidanceRows(FormInput formInput, List<GuidanceRow> guidanceRows) {
        return simpleMap(guidanceRows, createFormInputGuidanceRow(formInput));
    }

    private Function<GuidanceRow, GuidanceRow> createFormInputGuidanceRow(FormInput formInput) {
        return (GuidanceRow row) -> {
            entityManager.detach(row);
            row.setFormInput(formInput);
            row.setId(null);
            guidanceRowRepository.save(row);
            return row;
        };
    }

    private ServiceResult<CompetitionResource> persistNewCompetition(Competition competition) {
        Competition savedCompetition = competitionRepository.save(competition);
        return publicContentService.initialiseByCompetitionId(savedCompetition.getId())
                .andOnSuccessReturn(() -> competitionMapper.mapToResource(savedCompetition));
    }
}
