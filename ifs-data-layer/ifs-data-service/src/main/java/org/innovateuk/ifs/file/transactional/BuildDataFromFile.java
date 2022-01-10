package org.innovateuk.ifs.file.transactional;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.ApplicationExternalConfig;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.resource.*;
import org.innovateuk.ifs.application.transactional.*;
import org.innovateuk.ifs.category.domain.InnovationArea;
import org.innovateuk.ifs.category.domain.InnovationSector;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.commons.exception.IFSRuntimeException;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.CompetitionExternalConfig;
import org.innovateuk.ifs.competition.domain.Milestone;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.competition.repository.MilestoneRepository;
import org.innovateuk.ifs.competition.resource.*;
import org.innovateuk.ifs.competition.transactional.CompetitionAssessmentConfigService;
import org.innovateuk.ifs.competition.transactional.CompetitionExternalConfigService;
import org.innovateuk.ifs.competition.transactional.MilestoneService;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.QuestionBuilder;
import org.innovateuk.ifs.competitionsetup.transactional.CompetitionSetupService;
import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.form.domain.Question;
import org.innovateuk.ifs.form.domain.Section;
import org.innovateuk.ifs.form.repository.FormInputRepository;
import org.innovateuk.ifs.form.repository.QuestionRepository;
import org.innovateuk.ifs.form.repository.SectionRepository;
import org.innovateuk.ifs.form.resource.*;
import org.innovateuk.ifs.form.transactional.QuestionService;
import org.innovateuk.ifs.form.transactional.SectionService;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.repository.OrganisationRepository;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.innovateuk.ifs.question.transactional.template.QuestionPriorityOrderService;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.user.resource.ProcessRoleType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.FormInputBuilder.aFormInput;
import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.QuestionBuilder.aQuestion;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

@Slf4j
@Component
public class BuildDataFromFile {

    private static final String applicantDummyEmail = "dummy.person@example.com";
    private static final String applicantDummyOrganisation = "Dummy Organisation";


    @Autowired
    private ApplicationInnovationAreaService applicationInnovationAreaService;

    @Autowired
    private CompetitionAssessmentConfigService competitionAssessmentConfigService;

    @Autowired
    private CompetitionSetupService competitionSetupService;

    @Autowired
    private SectionService sectionService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private QuestionSetupService questionSetupService;

    @Autowired
    private QuestionPriorityOrderService questionPriorityOrderService;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private MilestoneRepository milestoneRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private MilestoneService milestoneService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrganisationRepository organisationRepository;

    @Autowired
    private FormInputResponseService formInputResponseService;

    @Autowired
    private ProcessRoleRepository processRoleRepository;

    @Autowired
    private FormInputRepository formInputRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuestionStatusService questionStatusService;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private CompetitionExternalConfigService competitionExternalConfigService;

    @Autowired
    private ApplicationExternalConfigService applicationExternalConfigService;

    public void buildFromFile(InputStream input) {
        try {
            CSVReaderBuilder builder = new CSVReaderBuilder(new InputStreamReader(input));
            CSVReader reader = builder.withSkipLines(1).build();
            List<String[]> data = reader.readAll();
            List<List<String>> lists = simpleMap(data, Arrays::asList);
            List<BuildDataFromFileLine> lines = lists.stream()
                    .map(
                          rows -> new BuildDataFromFileLine(rows)

                    ).collect(Collectors.toList());
            buildData(lines);
        } catch (FileNotFoundException e) {
            log.error("Unable to supply input stream for file ", e);
            throw new IllegalStateException("Unable to supply input stream for file ", e);
        } catch (IOException e) {
            log.error("Unable to open an input stream from request", e);
            throw new RuntimeException("Unable to open an input stream from request", e);
        } catch (CsvException e) {
            log.error("CSV Error", e);
            throw new RuntimeException("CSV Error", e);
        }
    }

    private void buildData(List<BuildDataFromFileLine> lines) {
        Set<BuildCompetition> competitions = new LinkedHashSet<>();
        Set<BuildExternalCompetition> externalCompetitions = new LinkedHashSet<>();
        Set<BuildApplication> applications = new LinkedHashSet<>();
        Set<BuildExternalApplication> externalApplications = new LinkedHashSet<>();
        Set<BuildQuestion> questions = new LinkedHashSet<>();
        Set<BuildResponse> responses = new LinkedHashSet<>();

        lines.forEach(line -> {
            competitions.add(new BuildCompetition(line.getCompetitionName()));
            externalCompetitions.add(new BuildExternalCompetition(line.getCompetitionName(), line.getExternalCompId()));
            applications.add(new BuildApplication(line.getApplicationName(), line.getCompetitionName()));
            externalApplications.add(new BuildExternalApplication(line.getApplicationName(),
                            line.getCompetitionName(), line.getExternalApplicationId(), line.getExternalApplicantName()));
            questions.add(new BuildQuestion(line.getQuestionName(), line.getCompetitionName()));
            responses.add(new BuildResponse(line.getResponse(), line.getQuestionName(), line.getApplicationName()));
        });

        Map<String, CompetitionResource> createdCompetitions = createCompetitionsAndQuestions(competitions, externalCompetitions, questions);
        createApplications(applications, responses, createdCompetitions,externalApplications);
        createdCompetitions.values().stream().forEach(c -> setMilestones(c, MilestoneType.ASSESSOR_BRIEFING));
    }

    private void createApplications(Set<BuildApplication> applications, Set<BuildResponse> responses, Map<String,
            CompetitionResource> createdCompetitions, Set<BuildExternalApplication> externalApplications) {
        Multimap<String, BuildResponse> appToResponseMap = Multimaps.index(responses, BuildResponse::getApplication);
        Multimap<String, BuildExternalApplication> appToExternalAppMap = Multimaps.index(externalApplications, BuildExternalApplication::getApplicationName);
        User user = userRepository.findByEmail(applicantDummyEmail).get();
        Organisation organisation = organisationRepository.findOneByName(applicantDummyOrganisation);
        applications.forEach(a -> {
            ApplicationResource application = applicationService.createApplicationByApplicationNameForUserIdAndCompetitionId(a.getName(), createdCompetitions.get(a.getCompetition()).getId(), user.getId(), organisation.getId()).getSuccess();
            application.setName(a.getName());
            List<Milestone> milestones = milestoneRepository.findAllByCompetitionId(application.getCompetition());
            Application appE = applicationRepository.findById(application.getId()).get();
            appE.getCompetition().getMilestones().addAll(milestones);
            entityManager.refresh(appE.getCompetition());
            entityManager.refresh(appE);

            applicationInnovationAreaService.setNoInnovationAreaApplies(application.getId()).getSuccess();
            application.setStartDate(LocalDate.now().plusDays(5));
            application.setDurationInMonths(2L);
            applicationService.saveApplicationDetails(application.getId(), application).getSuccess();
            setApplicationExternalConfig(application);
            setExternalApplicationData(appToExternalAppMap.get(a.getName()), application);
            ProcessRole role = processRoleRepository.findByUserIdAndRoleAndApplicationId(user.getId(), ProcessRoleType.LEADAPPLICANT, application.getId());
            saveResponses(application, appToResponseMap.get(a.getName()), role);

            Question applicationDetails = questionRepository.findFirstByCompetitionIdAndQuestionSetupType(application.getCompetition(), QuestionSetupType.APPLICATION_DETAILS);
            completeQuestion(application, applicationDetails, role);
            applicationService.updateApplicationState(application.getId(), ApplicationState.OPENED).getSuccess();
            applicationService.updateApplicationState(application.getId(), ApplicationState.SUBMITTED).getSuccess();
        });
    }

    private void saveResponses(ApplicationResource application, Collection<BuildResponse> buildResponses, ProcessRole role) {
        buildResponses.forEach(r -> {
            Question question = questionRepository.findByCompetitionIdAndName(application.getCompetition(), r.getQuestion());
            FormInput formInput = formInputRepository.findByQuestionIdAndScopeAndType(question.getId(), FormInputScope.APPLICATION, FormInputType.TEXTAREA);
            FormInputResponseCommand command = new FormInputResponseCommand(formInput.getId(), application.getId(), role.getUser().getId(), r.getResponse(), null);
            formInputResponseService.saveQuestionResponse(command).getSuccess();
            completeQuestion(application, question, role);
        });


    }

    private void completeQuestion(ApplicationResource application, Question question, ProcessRole role) {
        List<ValidationMessages> validations = questionStatusService.markAsComplete(new QuestionApplicationCompositeId(question.getId(), application.getId()), role.getId()).getSuccess();
        if (validations.isEmpty()) {
            return;
        }
        throw new IFSRuntimeException("error" + validations.toString());
    }

    private Map<String, CompetitionResource> createCompetitionsAndQuestions(Set<BuildCompetition> competitions, Set<BuildExternalCompetition> externalCompetitions, Set<BuildQuestion> questions) {
        Map<String, CompetitionResource> results = new HashMap<>();
        Multimap<String, BuildQuestion> compToQuestionMap = Multimaps.index(questions, BuildQuestion::getCompetition);
        Multimap<String, BuildExternalCompetition> compToExternalCompetitionMap = Multimaps.index(externalCompetitions, BuildExternalCompetition::getCompetitionName);
        competitions.forEach(c -> {
            String name = c.getName();
            log.error("CREATING COMP " + name);
            CompetitionResource competition = competitionSetupService.create().getSuccess();
            competition.setName(name);
            competition.setFundingType(FundingType.GRANT);
            competition.setFundingRules(FundingRules.NOT_AID);
            competition.setCompetitionType(13L);
            competition.setResubmission(false);
            competition.setMaxResearchRatio(100);
            competition.setMinProjectDuration(1);
            competition.setMaxProjectDuration(10);
            competition.setInnovationSector(InnovationSector.NONE);
            competition.setInnovationAreas(newHashSet(InnovationArea.NONE));
            competitionSetupService.save(competition.getId(), competition).getSuccess();
            competitionSetupService.copyFromCompetitionTypeTemplate(competition.getId(), 13L).getSuccess();
            setCompetitionExternalConfig(competition);
            setExternalCompData(compToExternalCompetitionMap.get(c.getName()), competition);
            MilestoneResource milestone = milestoneService.getMilestoneByTypeAndCompetitionId(MilestoneType.OPEN_DATE, competition.getId())
                    .getOrElse(new MilestoneResource(MilestoneType.OPEN_DATE, ZonedDateTime.now().minusDays(20 - MilestoneType.OPEN_DATE.getPriority()), competition.getId()));
            milestoneService.updateMilestone(milestone).getSuccess();
            milestoneService.updateCompletionStage(competition.getId(), CompetitionCompletionStage.RELEASE_FEEDBACK);

            createQuestions(competition, compToQuestionMap.get(c.getName()));
            setAssesmentConfig(competition);
            setMilestones(competition, MilestoneType.SUBMISSION_DATE);

            markSetupSectionsAndSubsectionsAsComplete(competition);
            markSetupApplicationQuestionsAsComplete(competition);

            results.put(c.getName(), competition);
        });
        return results;
    }

    private void setExternalCompData(Collection<BuildExternalCompetition> buildexternalCompetitions, CompetitionResource competitionResource) {
        buildexternalCompetitions.forEach(externalCompetition -> {
            if (competitionResource.getName().equals(externalCompetition.getCompetitionName())) {
                CompetitionExternalConfigResource competitionExternalConfigResource = new CompetitionExternalConfigResource();
                competitionExternalConfigResource.setExternalCompetitionId(externalCompetition.getExternalCompId());
                competitionExternalConfigService.update(competitionResource.getId(), competitionExternalConfigResource).getSuccess();
            }
        });

    }

    private void setMilestones(CompetitionResource competition, MilestoneType milestoneType) {
        milestoneService.getAllMilestonesByCompetitionId(competition.getId()).getSuccess()
                .stream()
                .forEach(m -> {
                    if (m.getType().getPriority() < milestoneType.getPriority()) {
                        m.setDate(ZonedDateTime.now().minusDays(20 - m.getType().getPriority()));
                    } else {
                        m.setDate(ZonedDateTime.now().plusDays(20 + m.getType().getPriority()));
                    }
                    milestoneService.updateMilestone(m).getSuccess();
                });
    }

    public void setAssesmentConfig(CompetitionResource competition) {
            CompetitionAssessmentConfigResource competitionAssessmentConfigResource = new CompetitionAssessmentConfigResource();
            competitionAssessmentConfigResource.setAssessorCount(1);
            competitionAssessmentConfigResource.setAssessorPay(BigDecimal.valueOf(100));
            competitionAssessmentConfigResource.setHasAssessmentPanel(false);
            competitionAssessmentConfigResource.setHasInterviewStage(false);
            competitionAssessmentConfigResource.setAssessorFinanceView(AssessorFinanceView.OVERVIEW);
            competitionAssessmentConfigResource.setIncludeAverageAssessorScoreInNotifications(false);
            competitionAssessmentConfigService.update(competition.getId(), competitionAssessmentConfigResource).getSuccess();
    }
    private void createQuestions(CompetitionResource competition, Collection<BuildQuestion> buildQuestions) {
        List<QuestionBuilder> builders = new ArrayList<>();
        buildQuestions.forEach(q -> {
            builders.add(aQuestion()
                    .withShortName(q.name)
                    .withName(q.name)
                    .withAssignEnabled(true)
                    .withMarkAsCompletedEnabled(true)
                    .withMultipleStatuses(false)
                    .withType(QuestionType.GENERAL)
                    .withQuestionSetupType(QuestionSetupType.ASSESSED_QUESTION)
                    .withAssessorMaximumScore(10)
                    .withFormInputs(newArrayList(
                            aFormInput()
                                    .withType(FormInputType.TEXTAREA)
                                    .withWordCount(400)
                                    .withActive(true)
                                    .withScope(FormInputScope.APPLICATION),
                            aFormInput()
                                    .withType(FormInputType.ASSESSOR_SCORE)
                                    .withScope(FormInputScope.ASSESSMENT)
                                    .withActive(true),
                            aFormInput()
                                    .withType(FormInputType.TEXTAREA)
                                    .withScope(FormInputScope.ASSESSMENT)
                                    .withActive(true)
                                    .withWordCount(100))
                    ));
        });

        Section applicationQuestionSection = sectionRepository.findByTypeAndCompetitionId(SectionType.APPLICATION_QUESTIONS, competition.getId()).get();
        List<Question> questions = builders.stream().map(QuestionBuilder::build).collect(toList());
        Competition competitionE = competitionRepository.findById(competition.getId()).get();
        questionPriorityOrderService.peristAndPrioritiesQuestions(competitionE, questions, applicationQuestionSection);
    }

    private void markSetupSectionsAndSubsectionsAsComplete(CompetitionResource competition) {
        Arrays.stream(CompetitionSetupSection.values())
                .filter(section -> section != CompetitionSetupSection.PROJECT_DOCUMENT)
                .forEach(competitionSetupSection -> {
                    competitionSetupService.markSectionComplete(competition.getId(), competitionSetupSection);
                    competitionSetupSection.getSubsections().forEach(subsection -> {
                        competitionSetupService.markSubsectionComplete(competition.getId(), competitionSetupSection, subsection);
                    });
                });
    }

    private void markSetupApplicationQuestionsAsComplete(CompetitionResource competition) {
        List<SectionResource> competitionSections = sectionService.getByCompetitionId(competition.getId()).getSuccess();
        List<QuestionResource> questionResources = questionService.findByCompetition(competition.getId()).getSuccess();

        // no application section or project details for h2020
        competitionSections.stream().filter(section -> section.getType() == SectionType.APPLICATION_QUESTIONS)
                .findFirst()
                .ifPresent(sectionResource -> markSectionQuestionsSetupComplete(questionResources, sectionResource, competition));
        competitionSections.stream().filter(section -> section.getType() == SectionType.PROJECT_DETAILS)
                .findFirst()
                .ifPresent(sectionResource -> markSectionQuestionsSetupComplete(questionResources, sectionResource, competition));
        // only for ktp competitions
        competitionSections.stream().filter(section -> section.getType() == SectionType.KTP_ASSESSMENT)
                .findFirst()
                .ifPresent(sectionResource -> markSectionQuestionsSetupComplete(questionResources, sectionResource, competition));
    }

    private void markSectionQuestionsSetupComplete(List<QuestionResource> questionResources, SectionResource section, CompetitionResource competition) {
        questionResources.stream()
                .filter(question -> question.getSection().equals(section.getId()))
                .forEach(question -> questionSetupService.markQuestionInSetupAsComplete(question.getId(), competition.getId(), CompetitionSetupSection.APPLICATION_FORM));

        competitionSetupService.markAsSetup(competition.getId());
    }

    private Competition setCompetitionExternalConfig(CompetitionResource competitionResource) {
        Competition competition = competitionRepository.findById(competitionResource.getId()).get();
        if (competition.getCompetitionExternalConfig() == null) {
            CompetitionExternalConfig competitionExternalConfig = new CompetitionExternalConfig();
            competitionExternalConfig.setCompetition(competition);
            competition.setCompetitionExternalConfig(competitionExternalConfig);
        }
        return competition;
    }

    private Application setApplicationExternalConfig(ApplicationResource applicationResource) {
        Application application = applicationRepository.findById(applicationResource.getId()).get();
        if (application.getApplicationExternalConfig() == null) {
            ApplicationExternalConfig applicationExternalConfig = new ApplicationExternalConfig();
            applicationExternalConfig.setApplication(application);
            application.setApplicationExternalConfig(applicationExternalConfig);
        }
        return application;
    }

    private void setExternalApplicationData(Collection<BuildExternalApplication> buildExternalApplication, ApplicationResource applicationResource) {
        buildExternalApplication.forEach((externalApplication -> {
            if(applicationResource.getName().equals(externalApplication.getApplicationName())) {
                ApplicationExternalConfigResource applicationExternalConfigResource = new ApplicationExternalConfigResource();
                applicationExternalConfigResource.setExternalApplicationId(externalApplication.getExternalApplicationId());
                applicationExternalConfigResource.setExternalApplicantName(externalApplication.getExternalApplicantName());
                applicationExternalConfigService.update(applicationResource.getId(), applicationExternalConfigResource).getSuccess();
            }
        }));

    }

    private static class BuildQuestion {
        private String name;
        private String competition;

        public BuildQuestion(String name, String competition) {
            this.name = name;
            this.competition = competition;
        }

        public String getName() {
            return name;
        }

        public BuildQuestion setName(String name) {
            this.name = name;
            return this;
        }

        public String getCompetition() {
            return competition;
        }

        public BuildQuestion setCompetition(String competition) {
            this.competition = competition;
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;

            if (o == null || getClass() != o.getClass()) return false;

            BuildQuestion that = (BuildQuestion) o;

            return new EqualsBuilder()
                    .append(name, that.name)
                    .append(competition, that.competition)
                    .isEquals();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder(17, 37)
                    .append(name)
                    .append(competition)
                    .toHashCode();
        }
    }
    private static class BuildApplication {
        private String name;
        private String competition;

        public BuildApplication(String name, String competition) {
            this.name = name;
            this.competition = competition;
        }

        public String getName() {
            return name;
        }

        public BuildApplication setName(String name) {
            this.name = name;
            return this;
        }

        public String getCompetition() {
            return competition;
        }

        public BuildApplication setCompetition(String competition) {
            this.competition = competition;
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;

            if (o == null || getClass() != o.getClass()) return false;

            BuildApplication that = (BuildApplication) o;

            return new EqualsBuilder()
                    .append(name, that.name)
                    .append(competition, that.competition)
                    .isEquals();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder(17, 37)
                    .append(name)
                    .append(competition)
                    .toHashCode();
        }
    }


    private static class BuildExternalApplication {
        private String applicationName;
        private String competitionName;
        private String externalApplicationId;
        private String externalApplicantName;

        public BuildExternalApplication(String applicationName, String competitionName, String externalApplicationId, String externalApplicantName) {
            this.applicationName = applicationName;
            this.competitionName = competitionName;
            this.externalApplicationId = externalApplicationId;
            this.externalApplicantName = externalApplicantName;
        }

        public String getApplicationName() {
            return applicationName;
        }

        public void setApplicationName(String applicationName) {
            this.applicationName = applicationName;
        }

        public String getCompetitionName() {
            return competitionName;
        }

        public void setCompetitionName(String competitionName) {
            this.competitionName = competitionName;
        }

        public String getExternalApplicationId() {
            return externalApplicationId;
        }

        public void setExternalApplicationId(String externalApplicationId) {
            this.externalApplicationId = externalApplicationId;
        }

        public String getExternalApplicantName() {
            return externalApplicantName;
        }

        public void setExternalApplicantName(String externalApplicantName) {
            this.externalApplicantName = externalApplicantName;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;

            if (o == null || getClass() != o.getClass()) return false;

            BuildExternalApplication that = (BuildExternalApplication) o;

            return new EqualsBuilder()
                    .append(applicationName, that.applicationName)
                    .append(competitionName, that.competitionName)
                    .append(externalApplicationId, that.externalApplicationId)
                    .append(externalApplicantName, that.externalApplicantName)
                    .isEquals();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder(17, 37)
                    .append(applicationName)
                    .append(competitionName)
                    .append(externalApplicationId)
                    .append(externalApplicantName)
                    .toHashCode();
        }
    }

    private static class BuildResponse {
        private String response;
        private String question;
        private String application;

        public BuildResponse(String response, String question, String application) {
            this.response = response;
            this.question = question;
            this.application = application;
        }

        public String getResponse() {
            return response;
        }

        public BuildResponse setResponse(String response) {
            this.response = response;
            return this;
        }

        public String getQuestion() {
            return question;
        }

        public BuildResponse setQuestion(String question) {
            this.question = question;
            return this;
        }

        public String getApplication() {
            return application;
        }

        public BuildResponse setApplication(String application) {
            this.application = application;
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;

            if (o == null || getClass() != o.getClass()) return false;

            BuildResponse that = (BuildResponse) o;

            return new EqualsBuilder()
                    .append(response, that.response)
                    .append(question, that.question)
                    .append(application, that.application)
                    .isEquals();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder(17, 37)
                    .append(response)
                    .append(question)
                    .append(application)
                    .toHashCode();
        }
    }
    private static class BuildCompetition {
        private String name;

        public BuildCompetition(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public BuildCompetition setName(String name) {
            this.name = name;
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;

            if (o == null || getClass() != o.getClass()) return false;

            BuildCompetition that = (BuildCompetition) o;

            return new EqualsBuilder()
                    .append(name, that.name)
                    .isEquals();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder(17, 37)
                    .append(name)
                    .toHashCode();
        }
    }

    private static class BuildExternalCompetition {
        private String competitionName;
        private String externalCompId;

        public BuildExternalCompetition(String competitionName, String externalCompId) {
            this.competitionName = competitionName;
            this.externalCompId = externalCompId;
        }

        public String getCompetitionName() {
            return competitionName;
        }

        public void setCompetitionName(String competitionName) {
            this.competitionName = competitionName;
        }

        public String getExternalCompId() {
            return externalCompId;
        }

        public void setExternalCompId(String externalCompId) {
            this.externalCompId = externalCompId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;

            if (o == null || getClass() != o.getClass()) return false;

            BuildExternalCompetition that = (BuildExternalCompetition) o;

            return new EqualsBuilder()
                    .append(competitionName, that.competitionName)
                    .append(externalCompId, that.externalCompId)
                    .isEquals();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder(17, 37)
                    .append(competitionName)
                    .append(externalCompId)
                    .toHashCode();
        }
    }
}
