package com.worth.ifs.competition.transactional;

import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.domain.Section;
import com.worth.ifs.application.repository.QuestionRepository;
import com.worth.ifs.category.resource.CategoryType;
import com.worth.ifs.category.transactional.CategoryLinkService;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.competition.mapper.CompetitionMapper;
import com.worth.ifs.competition.mapper.CompetitionTypeMapper;
import com.worth.ifs.competition.repository.CompetitionTypeRepository;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionStatus;
import com.worth.ifs.competition.resource.CompetitionSetupSection;
import com.worth.ifs.competition.resource.CompetitionTypeResource;
import com.worth.ifs.competitiontemplate.domain.CompetitionTemplate;
import com.worth.ifs.competitiontemplate.domain.FormInputTemplate;
import com.worth.ifs.competitiontemplate.domain.QuestionTemplate;
import com.worth.ifs.competitiontemplate.domain.SectionTemplate;
import com.worth.ifs.competitiontemplate.repository.CompetitionTemplateRepository;
import com.worth.ifs.form.domain.FormInput;
import com.worth.ifs.form.repository.FormInputRepository;
import com.worth.ifs.form.resource.FormInputScope;
import com.worth.ifs.transactional.BaseTransactionalService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.worth.ifs.commons.error.CommonFailureKeys.COMPETITION_NOT_EDITABLE;
import static com.worth.ifs.commons.error.CommonFailureKeys.COMPETITION_NO_TEMPLATE;
import static com.worth.ifs.commons.service.ServiceResult.serviceFailure;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.competition.transactional.CompetitionServiceImpl.COMPETITION_CLASS_NAME;


/**
 * Service for operations around the usage and processing of Competitions
 */
@Service
public class CompetitionSetupServiceImpl extends BaseTransactionalService implements CompetitionSetupService {
    private static final Log LOG = LogFactory.getLog(CompetitionSetupServiceImpl.class);
    @Autowired
    private CategoryLinkService categoryLinkService;
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
    private CompetitionTemplateRepository competitionTemplateRepository;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private FormInputRepository formInputRepository;


    @Override
    public ServiceResult<String> generateCompetitionCode(Long id, LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYMM");
        Competition competition = competitionRepository.findById(id);
        String datePart = formatter.format(dateTime);
        List<Competition> openingSameMonth = competitionRepository.findByCodeLike("%"+datePart+"%");
        if(StringUtils.hasText(competition.getCode())){
            return serviceSuccess(competition.getCode());
        }else if(openingSameMonth.isEmpty()){
            String unusedCode = datePart + "-1";
            competition.setCode(unusedCode);
            competitionRepository.save(competition);
            return serviceSuccess(unusedCode);
        }else{
            List<String> codes = openingSameMonth.stream().map(c -> c.getCode()).sorted().peek(c -> LOG.info("Codes : "+ c)).collect(Collectors.toList());
            String unusedCode = "";
            for (int i = 1; i < 10000; i++) {
                unusedCode = datePart+"-"+i;
                if(!codes.contains(unusedCode)){
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
        saveCategories(competitionResource);
        saveFunders(competitionResource);
        competition = competitionRepository.save(competition);
        competitionService.addCategories(competition);
        return serviceSuccess(competitionMapper.mapToResource(competition));
    }

    private void saveCategories(CompetitionResource competitionResource) {
        saveInnovationArea(competitionResource);
        saveInnovationSector(competitionResource);
        saveResearchCategories(competitionResource);
    }

    private void saveFunders(CompetitionResource competitionResource) {
        competitionFunderService.reinsertFunders(competitionResource);
    }

    private void saveInnovationSector(CompetitionResource competitionResource) {
        Long sectorId = competitionResource.getInnovationSector();
        saveCategoryLink(competitionResource, sectorId, CategoryType.INNOVATION_SECTOR);
    }

    private void saveInnovationArea(CompetitionResource competitionResource) {
        Long areaId = competitionResource.getInnovationArea();
        saveCategoryLink(competitionResource, areaId, CategoryType.INNOVATION_AREA);
    }
    
    private void saveResearchCategories(CompetitionResource competitionResource) {
        Set<Long> researchCategories = competitionResource.getResearchCategories();
        saveCategoryLinks(competitionResource, researchCategories, CategoryType.RESEARCH_CATEGORY);
    }

	private void saveCategoryLink(CompetitionResource competitionResource, Long categoryId, CategoryType categoryType) {
        categoryLinkService.updateCategoryLink(categoryId, categoryType, COMPETITION_CLASS_NAME, competitionResource.getId());
    }
	
    private void saveCategoryLinks(CompetitionResource competitionResource, Set<Long> categoryIds, CategoryType categoryType) {
    	categoryLinkService.updateCategoryLinks(categoryIds, categoryType, COMPETITION_CLASS_NAME, competitionResource.getId());
	}

    @Override
    public ServiceResult<CompetitionResource> create() {
        Competition competition = new Competition();
        competition.setSetupComplete(false);
        return serviceSuccess(competitionMapper.mapToResource(competitionRepository.save(competition)));
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
	public ServiceResult<Void> initialiseFormForCompetitionType(Long competitionId, Long competitionTypeId) {
		CompetitionTemplate template = competitionTemplateRepository.findByCompetitionTypeId(competitionTypeId);
		Competition competition = competitionRepository.findById(competitionId);

        cleanUpCompetitionSections(competition);

        if(competition == null || !competition.getCompetitionStatus().equals(CompetitionStatus.COMPETITION_SETUP)) {
            return serviceFailure(new Error(COMPETITION_NOT_EDITABLE));
        }

        if(template == null) {
            return serviceFailure(new Error(COMPETITION_NO_TEMPLATE));
        }

        List<SectionTemplate> sectionsWithoutParentSections =
                template.getSectionTemplates().stream()
                        .filter(s -> s.getParentSectionTemplate() == null)
                        .collect(Collectors.toList());


		attachSections(competition, sectionsWithoutParentSections, null);
        sectionRepository.save(competition.getSections());

        competition.setCompetitionType(template.getCompetitionType());

        competitionRepository.save(competition);

        return serviceSuccess();
	}

	private void cleanUpCompetitionSections(Competition competition) {
        List<FormInput> formInputs = formInputRepository.findByCompetitionId(competition.getId());
        formInputRepository.delete(formInputs);

        List<Question> questions = questionRepository.findByCompetitionId(competition.getId());
        questionRepository.delete(questions);

        List<Section> sections = sectionRepository.findByCompetitionIdOrderByParentSectionIdAscPriorityAsc(competition.getId());
        competition.setSections(new ArrayList());
        sectionRepository.delete(sections);
    }


    private void attachSections(Competition competition, List<SectionTemplate> sectionTemplates, Section parentSection) {
		if(sectionTemplates == null) {
			return;
		}
		sectionTemplates.forEach(attachSection(competition, parentSection));
	}
	
	private Consumer<SectionTemplate> attachSection(Competition competition, Section parentSection) {
		return (SectionTemplate sectionTemplate) -> {
			Section section = new Section();
			section.setType(sectionTemplate.getType());
			section.setName(sectionTemplate.getName());
			section.setDescription(sectionTemplate.getDescription());
			section.setAssessorGuidanceDescription(sectionTemplate.getAssessorGuidanceDescription());
			section.setCompetition(competition);
			if(!competition.getSections().contains(section)){
				competition.getSections().add(section);
			}


			section.setQuestions(createQuestions(competition, section, sectionTemplate.getQuestionTemplates()));
            questionRepository.save(section.getQuestions());
			
			attachSections(competition, sectionTemplate.getChildSectionTemplates(), section);
			
			section.setParentSection(parentSection);
			if(parentSection != null) {
				if(parentSection.getChildSections() == null) {
					parentSection.setChildSections(new ArrayList<>());
				}
				parentSection.getChildSections().add(section);
			}
		};
	}
	
	private List<Question> createQuestions(Competition competition, Section section, List<QuestionTemplate> questions) {
		return questions.stream().map(createQuestion(competition, section)).collect(Collectors.toList());
	}

	private Function<QuestionTemplate, Question> createQuestion(Competition competition, Section section) {
		return (QuestionTemplate questionTemplate) -> {
			Question question = new Question();
			question.setDescription(questionTemplate.getDescription());
			question.setName(questionTemplate.getName());
			question.setShortName(questionTemplate.getShortName());
			question.setCompetition(competition);
			question.setSection(section);
            question.setQuestionNumber(questionTemplate.getQuestionNumber());

            questionRepository.save(question);

            question.setFormInputs(createFormInputs(competition, question, questionTemplate.getFormInputTemplates()));
            formInputRepository.save(question.getFormInputs());
			return question;
		};
	}
	
	private List<FormInput> createFormInputs(Competition competition, Question question, List<FormInputTemplate> formInputTemplates) {
		return formInputTemplates.stream().map(createFormInput(competition, question)).collect(Collectors.toList());
	}
	
	private Function<FormInputTemplate, FormInput> createFormInput(Competition competition, Question question) {
		return (FormInputTemplate formInputTemplate) -> {
			FormInput formInput = new FormInput();
			formInput.setDescription(formInputTemplate.getDescription());
			formInput.setGuidanceAnswer(formInputTemplate.getGuidanceAnswer());
			formInput.setGuidanceQuestion(formInputTemplate.getGuidanceQuestion());
			formInput.setFormInputType(formInputTemplate.getFormInputType());
			formInput.setIncludedInApplicationSummary(formInputTemplate.getIncludedInApplicationSummary());

            if(formInputTemplate.getInputValidators() != null && formInputTemplate.getInputValidators().size() > 0) {
                formInput.setInputValidators(new HashSet(formInputTemplate.getInputValidators()));
            }

            formInput.setCompetition(competition);
			formInput.setQuestion(question);
            formInput.setPriority(formInputTemplate.getPriority());
            formInput.setScope(FormInputScope.APPLICATION);
            return formInput;
		};
	}
}
