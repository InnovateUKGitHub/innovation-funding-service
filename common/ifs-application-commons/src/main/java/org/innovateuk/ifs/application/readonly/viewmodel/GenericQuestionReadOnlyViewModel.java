package org.innovateuk.ifs.application.readonly.viewmodel;

import org.innovateuk.ifs.application.readonly.ApplicationReadOnlyData;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.horizon.resource.ApplicationHorizonWorkProgrammeResource;
import org.innovateuk.ifs.question.resource.QuestionSetupType;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class GenericQuestionReadOnlyViewModel extends AbstractQuestionReadOnlyViewModel {

    private final String displayName;
    private final String question;
    private final boolean multipleStatuses;
    private final String answer;
    private final List<GenericQuestionAnswerRowReadOnlyViewModel>  answers;
    private final List<ApplicationHorizonWorkProgrammeResource> workProgrammeAnswers;
    private final boolean statusDetailPresent;
    private final List<GenericQuestionFileViewModel> appendices;
    private final GenericQuestionFileViewModel templateFile;
    private final String templateDocumentTitle;
    private final long competitionId;
    private final List<String> feedback;
    private final List<BigDecimal> scores;
    private final QuestionResource questionResource;
    private final int inScope;
    private final int totalScope;
    private final boolean hasScope;
    private final boolean isLoanPartBEnabled;
    private final boolean isHecpCompetition;
    private final boolean isWorkProgrammeQuestionMarkedAsComplete;

    public GenericQuestionReadOnlyViewModel(ApplicationReadOnlyData data,
                                            QuestionResource questionResource,
                                            String displayName,
                                            String question,
                                            boolean multipleStatuses,
                                            String answer,
                                            List<GenericQuestionAnswerRowReadOnlyViewModel> answers,
                                            List<ApplicationHorizonWorkProgrammeResource> workProgrammeAnswers,
                                            boolean statusDetailPresent,
                                            List<GenericQuestionFileViewModel> appendices,
                                            GenericQuestionFileViewModel templateFile,
                                            String templateDocumentTitle,
                                            List<String> feedback,
                                            List<BigDecimal> scores,
                                            int inScope,
                                            int totalScope,
                                            boolean hasScope,
                                            boolean isLoanPartBEnabled,
                                            boolean isWorkProgrammeQuestionMarkedAsComplete) {
        super(data, questionResource);
        this.displayName = displayName;
        this.question = question;
        this.multipleStatuses = multipleStatuses;
        this.answer = answer;
        this.answers = answers;
        this.workProgrammeAnswers = workProgrammeAnswers;
        this.statusDetailPresent = statusDetailPresent;
        this.appendices = appendices;
        this.templateFile = templateFile;
        this.templateDocumentTitle = templateDocumentTitle;
        this.competitionId = data.getCompetition().getId();
        this.feedback = feedback;
        this.scores = scores;
        this.questionResource = questionResource;
        this.inScope = inScope;
        this.totalScope = totalScope;
        this.hasScope = hasScope;
        this.isLoanPartBEnabled = isLoanPartBEnabled;
        this.isHecpCompetition = data.getCompetition().isHorizonEuropeGuarantee();
        this.isWorkProgrammeQuestionMarkedAsComplete = isWorkProgrammeQuestionMarkedAsComplete;
    }

    public int getInScope() { return inScope; }

    public int getTotalScope() { return totalScope; }

    public String getDisplayName() {
        return displayName;
    }

    public String getQuestion() {
        return question;
    }

    public boolean isMultipleStatuses() {
        return multipleStatuses;
    }

    public String getAnswer() {
        return answer;
    }

    public List<GenericQuestionAnswerRowReadOnlyViewModel> getAnswers() {
        return answers;
    }

    public boolean hasAnswerNotMarkedAsComplete() {
        return answers.stream().anyMatch(a -> !a.isMarkedAsComplete());
    }

    public List<GenericQuestionAnswerRowReadOnlyViewModel> getNonMarkedAsCompletePartners() {
        return answers.stream().filter(a -> !a.isMarkedAsComplete()).collect(Collectors.toList());
    }

    public boolean isStatusDetailPresent() {
        return statusDetailPresent;
    }

    public List<GenericQuestionFileViewModel> getAppendices() {
        return appendices;
    }

    public GenericQuestionFileViewModel getTemplateFile() {
        return templateFile;
    }

    public String getTemplateDocumentTitle() {
        return templateDocumentTitle;
    }

    public long getCompetitionId() {
        return competitionId;
    }

    public List<String> getFeedback() {
        return feedback;
    }

    public List<BigDecimal> getScores() {
        return scores;
    }

    @Override
    public String getName() {
        return displayName;
    }

    @Override
    public String getFragment() {
        return "generic";
    }

    public boolean hasFeedback() {
        return !feedback.isEmpty();
    }

    @Override
    public boolean hasScore() { return !scores.isEmpty(); }

    public boolean hasScope() {
        return hasScope;
    }

    public boolean hasAssessorResponse() {
        return hasFeedback() && hasScore();
    }

    public boolean isLoanPartBEnabled() { return isLoanPartBEnabled; }

    public QuestionResource getQuestionResource() { return questionResource; }

    public boolean isHecpCompetition() {
        return isHecpCompetition;
    }

    public List<ApplicationHorizonWorkProgrammeResource> getWorkProgrammeAnswers() {
        return workProgrammeAnswers;
    }

    public boolean isWorkProgrammeQuestionMarkedAsComplete() {
        return isWorkProgrammeQuestionMarkedAsComplete;
    }

    public int getAssessorMaximumScore() {
        if(!hasScore()) {
            return 0;
        }
        return questionResource.getAssessorMaximumScore();
    };
    
    public BigDecimal getAverageScore() {
        BigDecimal totalScore = BigDecimal.ZERO;
        for (int i = 0; i < scores.size(); i++){
            totalScore = totalScore.add(scores.get(i));
        };

        BigDecimal average = totalScore.divide(BigDecimal.valueOf(scores.size()), 1, BigDecimal.ROUND_HALF_UP);

        return average;
    };

    public boolean isKtpAssessmentQuestion() {
        return questionResource != null && questionResource.getQuestionSetupType() == QuestionSetupType.KTP_ASSESSMENT;
    }

    public boolean isCompletedLoanBusinessAndFinancialInformation() {
        return isLoanPartBEnabled &&
                questionResource != null &&
                QuestionSetupType.LOAN_BUSINESS_AND_FINANCIAL_INFORMATION == questionResource.getQuestionSetupType() &&
                isComplete();
    }

    public boolean isImpactManagementQuestion() {
        return QuestionSetupType.IMPACT_MANAGEMENT_SURVEY == questionResource.getQuestionSetupType();
    }

    public boolean isInCompleteLoanBusinessAndFinancialInformation() {
        return isLoanPartBEnabled &&
                questionResource != null &&
                QuestionSetupType.LOAN_BUSINESS_AND_FINANCIAL_INFORMATION == questionResource.getQuestionSetupType() &&
                !isComplete();
    }

    public boolean isHecpWorkProgrammeQuestion() {
        return isHecpCompetition && questionResource != null && questionResource.getQuestionSetupType() == QuestionSetupType.HORIZON_WORK_PROGRAMME;
    }

    public boolean isWorkProgrammeQuestionCompleted() {
        return !getWorkProgrammeAnswers().isEmpty() && isWorkProgrammeQuestionMarkedAsComplete();
    }
}
