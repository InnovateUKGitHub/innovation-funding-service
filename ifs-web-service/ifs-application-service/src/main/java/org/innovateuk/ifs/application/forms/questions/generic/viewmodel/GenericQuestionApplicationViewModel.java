package org.innovateuk.ifs.application.forms.questions.generic.viewmodel;

import org.innovateuk.ifs.analytics.BaseAnalyticsViewModel;
import org.innovateuk.ifs.application.viewmodel.AssignButtonsViewModel;
import org.innovateuk.ifs.file.resource.FileTypeCategory;
import org.innovateuk.ifs.form.resource.MultipleChoiceOptionResource;
import org.innovateuk.ifs.question.resource.QuestionSetupType;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;

import static org.innovateuk.ifs.question.resource.QuestionSetupType.ASSESSED_QUESTION;

public class GenericQuestionApplicationViewModel implements BaseAnalyticsViewModel {

    private final long questionId;
    private final long currentUser;
    private final long applicationId;
    private final String competitionName;

    private final String applicationName;
    private final String questionName;
    private final String questionNumber;
    private final String questionSubtitle;
    private final String questionDescription;
    private final String questionGuidanceTitle;
    private final String questionGuidance;
    private final QuestionSetupType questionType;

    private final Long textAreaFormInputId;
    private final Integer wordCount;
    private final Integer wordsLeft;

    private final Long appendixFormInputId;
    private final String appendixGuidance;
    private final Set<FileTypeCategory> appendixAllowedFileTypes;
    private final String appendixFilename;

    private final Long templateDocumentFormInputId;
    private final String templateDocumentTitle;
    private final String templateDocumentFilename;
    private final String templateDocumentResponseFilename;

    private final ZonedDateTime lastUpdated;
    private final String lastUpdatedByName;
    private final Long lastUpdatedBy;

    private final boolean open;
    private final boolean complete;
    private final boolean leadApplicant;

    private final AssignButtonsViewModel assignButtonsViewModel;

    private final Long multipleChoiceFormInputId;
    private final List<MultipleChoiceOptionResource> multipleChoiceOptions;
    private final String multipleChoiceFormInputText;

    public GenericQuestionApplicationViewModel(long applicationId, String competitionName ,long questionId,
                                               long currentUser, String applicationName, String questionName,
                                               String questionNumber, String questionSubtitle, String questionDescription,
                                               String questionGuidanceTitle, String questionGuidance, QuestionSetupType questionType,
                                               Long textAreaFormInputId, Integer wordCount, Integer wordsLeft, Long appendixFormInputId,
                                               String appendixGuidance, Set<FileTypeCategory> appendixAllowedFileTypes, String appendixFilename,
                                               Long templateDocumentFormInputId, String templateDocumentTitle, String templateDocumentFilename,
                                               String templateDocumentResponseFilename, ZonedDateTime lastUpdated, String lastUpdatedByName,
                                               Long lastUpdatedBy, boolean open, boolean complete, boolean leadApplicant,
                                               AssignButtonsViewModel assignButtonsViewModel, Long multipleChoiceFormInputId,
                                               List<MultipleChoiceOptionResource> multipleChoiceOptions, String multipleChoiceFormInputText) {
        this.applicationId = applicationId;
        this.competitionName = competitionName;
        this.questionId = questionId;
        this.currentUser = currentUser;
        this.applicationName = applicationName;
        this.questionName = questionName;
        this.questionNumber = questionNumber;
        this.questionSubtitle = questionSubtitle;
        this.questionDescription = questionDescription;
        this.questionGuidanceTitle = questionGuidanceTitle;
        this.questionGuidance = questionGuidance;
        this.questionType = questionType;
        this.textAreaFormInputId = textAreaFormInputId;
        this.wordCount = wordCount;
        this.wordsLeft = wordsLeft;
        this.appendixFormInputId = appendixFormInputId;
        this.appendixGuidance = appendixGuidance;
        this.appendixAllowedFileTypes = appendixAllowedFileTypes;
        this.appendixFilename = appendixFilename;
        this.templateDocumentFormInputId = templateDocumentFormInputId;
        this.templateDocumentTitle = templateDocumentTitle;
        this.templateDocumentFilename = templateDocumentFilename;
        this.templateDocumentResponseFilename = templateDocumentResponseFilename;
        this.lastUpdated = lastUpdated;
        this.lastUpdatedByName = lastUpdatedByName;
        this.lastUpdatedBy = lastUpdatedBy;
        this.open = open;
        this.complete = complete;
        this.leadApplicant = leadApplicant;
        this.assignButtonsViewModel = assignButtonsViewModel;
        this.multipleChoiceFormInputId = multipleChoiceFormInputId;
        this.multipleChoiceOptions = multipleChoiceOptions;
        this.multipleChoiceFormInputText = multipleChoiceFormInputText;
    }

    @Override
    public Long getApplicationId() {
        return applicationId;
    }

    @Override
    public String getCompetitionName() {
        return competitionName;
    }

    public long getQuestionId() {
        return questionId;
    }

    public long getCurrentUser() {
        return currentUser;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public String getQuestionName() {
        return questionName;
    }

    public String getQuestionNumber() {
        return questionNumber;
    }

    public String getQuestionSubtitle() {
        return questionSubtitle;
    }

    public String getQuestionDescription() {
        return questionDescription;
    }

    public String getQuestionGuidanceTitle() {
        return questionGuidanceTitle;
    }

    public String getQuestionGuidance() {
        return questionGuidance;
    }

    public QuestionSetupType getQuestionType() {
        return questionType;
    }

    public Long getTextAreaFormInputId() {
        return textAreaFormInputId;
    }

    public Integer getWordCount() {
        return wordCount;
    }

    public Integer getWordsLeft() {
        return wordsLeft;
    }

    public Long getAppendixFormInputId() {
        return appendixFormInputId;
    }

    public String getAppendixGuidance() {
        return appendixGuidance;
    }

    public Set<FileTypeCategory> getAppendixAllowedFileTypes() {
        return appendixAllowedFileTypes;
    }

    public String getAppendixFilename() {
        return appendixFilename;
    }

    public Long getTemplateDocumentFormInputId() {
        return templateDocumentFormInputId;
    }

    public String getTemplateDocumentTitle() {
        return templateDocumentTitle;
    }

    public String getTemplateDocumentFilename() {
        return templateDocumentFilename;
    }

    public String getTemplateDocumentResponseFilename() {
        return templateDocumentResponseFilename;
    }

    public ZonedDateTime getLastUpdated() {
        return lastUpdated;
    }

    public String getLastUpdatedByName() {
        return lastUpdatedByName;
    }

    public Long getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public boolean isOpen() {
        return open;
    }

    public boolean isComplete() {
        return complete;
    }

    public boolean isLeadApplicant() {
        return leadApplicant;
    }

    public AssignButtonsViewModel getAssignButtonsViewModel() {
        return assignButtonsViewModel;
    }

    public Long getMultipleChoiceFormInputId() {
        return multipleChoiceFormInputId;
    }

    public List<MultipleChoiceOptionResource> getMultipleChoiceOptions() {
        return multipleChoiceOptions;
    }

    public String getMultipleChoiceFormInputText() {
        return multipleChoiceFormInputText;
    }

    /* view logic */
    public boolean isReadOnly() {
        return !open || complete || !assignButtonsViewModel.isAssignedToCurrentUser();
    }

    public boolean shouldDisplayQuestionNumber() {
        return questionType == ASSESSED_QUESTION;
    }

    public boolean hasResponse() {
        return lastUpdated != null;
    }

    public boolean isRespondedByCurrentUser() {
        return currentUser == lastUpdatedBy;
    }

    public String getLastUpdatedText() {
        String userUpdated = isRespondedByCurrentUser() ? "you" : lastUpdatedByName;
        return " by " + userUpdated;
    }

    public boolean isSingleApplicant() {
        return assignButtonsViewModel != null && assignButtonsViewModel.getAssignableApplicants().size() == 1;
    }

    public boolean isTextAreaActive() {
        return textAreaFormInputId != null;
    }

    public boolean isAppendixActive() {
        return appendixFormInputId != null;
    }

    public boolean isTemplateDocumentActive() {
        return templateDocumentFormInputId != null;
    }

    public boolean isMultipleChoiceOptionsActive() {
        return multipleChoiceFormInputId != null;
    }

    public static final class GenericQuestionApplicationViewModelBuilder {
        private long applicationId;
        private String competitionName;
        private long questionId;
        private long currentUser;
        private String applicationName;
        private String questionName;
        private String questionNumber;
        private String questionSubtitle;
        private String questionDescription;
        private String questionGuidanceTitle;
        private String questionGuidance;
        private QuestionSetupType questionType;
        private Long textAreaFormInputId;
        private Integer wordCount;
        private Integer wordsLeft;
        private Long appendixFormInputId;
        private String appendixGuidance;
        private Set<FileTypeCategory> appendixAllowedFileTypes;
        private String appendixFilename;
        private Long templateDocumentFormInputId;
        private String templateDocumentTitle;
        private String templateDocumentFilename;
        private String templateDocumentResponseFilename;
        private ZonedDateTime lastUpdated;
        private String lastUpdatedByName;
        private Long lastUpdatedBy;
        private boolean open;
        private boolean complete;
        private boolean leadApplicant;
        private AssignButtonsViewModel assignButtonsViewModel;
        private Long multipleChoiceFormInputId;
        private List<MultipleChoiceOptionResource> multipleChoiceOptions;
        private String multipleChoiceFormInputText;

        private GenericQuestionApplicationViewModelBuilder() {
        }

        public static GenericQuestionApplicationViewModelBuilder aGenericQuestionApplicationViewModel() {
            return new GenericQuestionApplicationViewModelBuilder();
        }

        public GenericQuestionApplicationViewModelBuilder withApplicationId(long applicationId) {
            this.applicationId = applicationId;
            return this;
        }

        public GenericQuestionApplicationViewModelBuilder withQuestionId(long questionId) {
            this.questionId = questionId;
            return this;
        }

        public GenericQuestionApplicationViewModelBuilder withCurrentUser(long currentUser) {
            this.currentUser = currentUser;
            return this;
        }

        public GenericQuestionApplicationViewModelBuilder withApplicationName(String applicationName) {
            this.applicationName = applicationName;
            return this;
        }

        public GenericQuestionApplicationViewModelBuilder withCompetitionName(String competitionName) {
            this.competitionName = competitionName;
            return this;
        }

        public GenericQuestionApplicationViewModelBuilder withQuestionName(String questionName) {
            this.questionName = questionName;
            return this;
        }

        public GenericQuestionApplicationViewModelBuilder withQuestionNumber(String questionNumber) {
            this.questionNumber = questionNumber;
            return this;
        }

        public GenericQuestionApplicationViewModelBuilder withQuestionSubtitle(String questionSubtitle) {
            this.questionSubtitle = questionSubtitle;
            return this;
        }

        public GenericQuestionApplicationViewModelBuilder withQuestionDescription(String questionDescription) {
            this.questionDescription = questionDescription;
            return this;
        }

        public GenericQuestionApplicationViewModelBuilder withQuestionGuidanceTitle(String questionGuidanceTitle) {
            this.questionGuidanceTitle = questionGuidanceTitle;
            return this;
        }

        public GenericQuestionApplicationViewModelBuilder withQuestionGuidance(String questionGuidance) {
            this.questionGuidance = questionGuidance;
            return this;
        }

        public GenericQuestionApplicationViewModelBuilder withQuestionType(QuestionSetupType questionType) {
            this.questionType = questionType;
            return this;
        }

        public GenericQuestionApplicationViewModelBuilder withWordCount(Integer wordCount) {
            this.wordCount = wordCount;
            return this;
        }

        public GenericQuestionApplicationViewModelBuilder withWordsLeft(Integer wordsLeft) {
            this.wordsLeft = wordsLeft;
            return this;
        }

        public GenericQuestionApplicationViewModelBuilder withAppendixFormInputId(Long appendixFormInputId) {
            this.appendixFormInputId = appendixFormInputId;
            return this;
        }

        public GenericQuestionApplicationViewModelBuilder withAppendixGuidance(String appendixGuidance) {
            this.appendixGuidance = appendixGuidance;
            return this;
        }

        public GenericQuestionApplicationViewModelBuilder withAppendixAllowedFileTypes(Set<FileTypeCategory> appendixAllowedFileTypes) {
            this.appendixAllowedFileTypes = appendixAllowedFileTypes;
            return this;
        }

        public GenericQuestionApplicationViewModelBuilder withAppendixFilename(String appendixFilename) {
            this.appendixFilename = appendixFilename;
            return this;
        }

        public GenericQuestionApplicationViewModelBuilder withTemplateDocumentFormInputId(Long templateDocumentFormInputId) {
            this.templateDocumentFormInputId = templateDocumentFormInputId;
            return this;
        }

        public GenericQuestionApplicationViewModelBuilder withTemplateDocumentTitle(String templateDocumentTitle) {
            this.templateDocumentTitle = templateDocumentTitle;
            return this;
        }

        public GenericQuestionApplicationViewModelBuilder withTemplateDocumentFilename(String templateDocumentFilename) {
            this.templateDocumentFilename = templateDocumentFilename;
            return this;
        }

        public GenericQuestionApplicationViewModelBuilder withTemplateDocumentResponseFilename(String templateDocumentResponseFilename) {
            this.templateDocumentResponseFilename = templateDocumentResponseFilename;
            return this;
        }

        public GenericQuestionApplicationViewModelBuilder withLastUpdated(ZonedDateTime lastUpdated) {
            this.lastUpdated = lastUpdated;
            return this;
        }

        public GenericQuestionApplicationViewModelBuilder withLastUpdatedByName(String lastUpdatedByName) {
            this.lastUpdatedByName = lastUpdatedByName;
            return this;
        }

        public GenericQuestionApplicationViewModelBuilder withLastUpdatedBy(Long lastUpdatedBy) {
            this.lastUpdatedBy = lastUpdatedBy;
            return this;
        }

        public GenericQuestionApplicationViewModelBuilder withOpen(boolean open) {
            this.open = open;
            return this;
        }

        public GenericQuestionApplicationViewModelBuilder withComplete(boolean complete) {
            this.complete = complete;
            return this;
        }

        public GenericQuestionApplicationViewModelBuilder withLeadApplicant(boolean leadApplicant) {
            this.leadApplicant = leadApplicant;
            return this;
        }

        public GenericQuestionApplicationViewModelBuilder withAssignButtonsViewModel(AssignButtonsViewModel assignButtonsViewModel) {
            this.assignButtonsViewModel = assignButtonsViewModel;
            return this;
        }

        public GenericQuestionApplicationViewModelBuilder withTextAreaFormInputId(Long textAreaFormInputId) {
            this.textAreaFormInputId = textAreaFormInputId;
            return this;
        }

        public GenericQuestionApplicationViewModelBuilder withMultipleChoiceFormInputId(Long multipleChoiceFormInputId) {
            this.multipleChoiceFormInputId = multipleChoiceFormInputId;
            return this;
        }

        public GenericQuestionApplicationViewModelBuilder withMultipleChoiceOptions(List<MultipleChoiceOptionResource> multipleChoiceOptions) {
            this.multipleChoiceOptions = multipleChoiceOptions;
            return this;
        }

        public GenericQuestionApplicationViewModelBuilder withMultipleChoiceFormInputText(String multipleChoiceFormInputText) {
            this.multipleChoiceFormInputText = multipleChoiceFormInputText;
            return this;
        }

        public GenericQuestionApplicationViewModel build() {
            return new GenericQuestionApplicationViewModel(applicationId, competitionName, questionId, currentUser,
                    applicationName, questionName, questionNumber, questionSubtitle, questionDescription, questionGuidanceTitle,
                    questionGuidance, questionType, textAreaFormInputId, wordCount, wordsLeft, appendixFormInputId, appendixGuidance,
                    appendixAllowedFileTypes, appendixFilename, templateDocumentFormInputId, templateDocumentTitle, templateDocumentFilename,
                    templateDocumentResponseFilename, lastUpdated, lastUpdatedByName, lastUpdatedBy, open, complete, leadApplicant,
                    assignButtonsViewModel, multipleChoiceFormInputId, multipleChoiceOptions, multipleChoiceFormInputText);
        }
    }
}
