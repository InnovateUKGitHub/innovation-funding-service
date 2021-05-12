package org.innovateuk.ifs.file.transactional;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.application.transactional.QuestionSetupService;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.competition.resource.*;
import org.innovateuk.ifs.competition.transactional.CompetitionAssessmentConfigService;
import org.innovateuk.ifs.competition.transactional.MilestoneService;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.QuestionBuilder;
import org.innovateuk.ifs.competitionsetup.transactional.CompetitionSetupService;
import org.innovateuk.ifs.form.domain.Question;
import org.innovateuk.ifs.form.domain.Section;
import org.innovateuk.ifs.form.repository.SectionRepository;
import org.innovateuk.ifs.form.resource.*;
import org.innovateuk.ifs.form.transactional.QuestionService;
import org.innovateuk.ifs.form.transactional.SectionService;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.innovateuk.ifs.question.transactional.template.QuestionPriorityOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.FormInputBuilder.aFormInput;
import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.QuestionBuilder.aQuestion;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

@Component
public class BuildDataFromFile {
    private static final Log LOG = LogFactory.getLog(BuildDataFromFile.class);

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
    private MilestoneService milestoneService;

    public void buildFromFile(InputStream input) {
        try {
            CSVReaderBuilder builder = new CSVReaderBuilder(new InputStreamReader(input));
            CSVReader reader = builder.withSkipLines(1).build();
            List<String[]> data = reader.readAll();
            List<List<String>> lists = simpleMap(data, Arrays::asList);
            List<BuildDataFromFileLine> lines = lists.stream()
                    .map(
                            rows -> new BuildDataFromFileLine(rows.get(0), rows.get(1), rows.get(2), rows.get(3))
                    ).collect(Collectors.toList());
            buildData(lines);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void buildData(List<BuildDataFromFileLine> lines) {
        Set<BuildCompetition> competitions = new LinkedHashSet<>();
        Set<BuildQuestion> questions = new LinkedHashSet<>();
        Set<BuildApplication> applications = new LinkedHashSet<>();
        Set<BuildResponse> responses = new LinkedHashSet<>();

        lines.forEach(line -> {
            competitions.add(new BuildCompetition(line.getCompetitionName()));
            questions.add(new BuildQuestion(line.getQuestionName(), line.getCompetitionName()));
            applications.add(new BuildApplication(line.getApplicationName(), line.getCompetitionName()));
            responses.add(new BuildResponse(line.getResponse(), line.getQuestionName(), line.getApplicationName()));
        });

        createCompetitions(competitions, questions);

    }

    private void createCompetitions(Set<BuildCompetition> competitions, Set<BuildQuestion> questions) {
        Multimap<String, BuildQuestion> compToQuestionMap = Multimaps.index(questions, BuildQuestion::getCompetition);
        competitions.forEach(c -> {
            String name = c.getName() + LocalDateTime.now();
            LOG.error("CREATING COMP " + name);
            CompetitionResource competition = competitionSetupService.create().getSuccess();
            competition.setName(name);
            competition.setFundingType(FundingType.GRANT);
            competition.setFundingRules(FundingRules.NOT_AID);
            competition.setCompetitionType(13L);
            competitionSetupService.save(competition.getId(), competition).getSuccess();
            competitionSetupService.copyFromCompetitionTypeTemplate(competition.getId(), 13L).getSuccess();
            MilestoneResource milestone = milestoneService.getMilestoneByTypeAndCompetitionId(MilestoneType.OPEN_DATE, competition.getId())
                    .getOrElse(new MilestoneResource(MilestoneType.OPEN_DATE, ZonedDateTime.now().minusDays(20 - MilestoneType.OPEN_DATE.getPriority()), competition.getId()));
            milestoneService.updateMilestone(milestone).getSuccess();
            milestoneService.updateCompletionStage(competition.getId(), CompetitionCompletionStage.RELEASE_FEEDBACK);
            milestoneService.getAllMilestonesByCompetitionId(competition.getId()).getSuccess()
                    .stream()
                    .forEach(m -> {
                        if (m.getType().getPriority() < MilestoneType.ASSESSOR_ACCEPTS.getPriority()) {
                            m.setDate(ZonedDateTime.now().minusDays(20 - m.getType().getPriority()));
                        } else {
                            m.setDate(ZonedDateTime.now().plusDays(20 + m.getType().getPriority()));
                        }
                        milestoneService.updateMilestone(m).getSuccess();
                    });
            createQuestions(competition, compToQuestionMap.get(c.getName()));
            setAssesmentConfig(competition);

            markSetupSectionsAndSubsectionsAsComplete(competition);
            markSetupApplicationQuestionsAsComplete(competition);
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
}
