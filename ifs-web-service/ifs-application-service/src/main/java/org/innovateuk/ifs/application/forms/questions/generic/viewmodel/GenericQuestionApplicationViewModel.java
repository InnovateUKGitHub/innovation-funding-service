package org.innovateuk.ifs.application.forms.questions.generic.viewmodel;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private final String leadOrganisationName;
    private final String leadOrganisationCompaniesHouseNumber;

    private final String applicationName;
    private final String questionName;
    private final String questionNumber;
    private final String questionSubtitle;
    private final String questionDescription;
    private final String questionDescription2;
    private final String questionGuidanceTitle;
    private final String questionGuidance;
    private final QuestionSetupType questionType;
    private final boolean questionHasMultipleStatuses;

    private final Long textAreaFormInputId;
    private final Integer wordCount;
    private final Integer wordsLeft;

    private final Long appendixFormInputId;
    private final String appendixGuidance;
    private final Set<FileTypeCategory> appendixAllowedFileTypes;
    private final List<GenericQuestionAppendix> appendices;
    private final Integer maximumAppendices;

    private final Long templateDocumentFormInputId;
    private final String templateDocumentTitle;
    private final String templateDocumentFilename;
    private final String templateDocumentResponseFilename;
    private final Long templateDocumentResponseFileEntryId;

    private final ZonedDateTime lastUpdated;
    private final String lastUpdatedByName;
    private final Long lastUpdatedBy;

    private final boolean open;
    private final boolean complete;
    private final boolean leadApplicant;

    private final AssignButtonsViewModel assignButtonsViewModel;

    private final Long multipleChoiceFormInputId;
    private final List<MultipleChoiceOptionResource> multipleChoiceOptions;
    private final MultipleChoiceOptionResource selectedMultipleChoiceOption;
    private final boolean loansPartBEnabled;
    private final String salesForceURL;

    public GenericQuestionApplicationViewModel(long applicationId, String competitionName ,long questionId, long currentUser,
                                               String applicationName, String questionName, String questionNumber, String questionSubtitle,
                                               String questionDescription, String questionDescription2, String questionGuidanceTitle, String questionGuidance,
                                               QuestionSetupType questionType, boolean questionHasMultipleStatuses, Long textAreaFormInputId,
                                               Integer wordCount, Integer wordsLeft, Long appendixFormInputId, String appendixGuidance,
                                               Set<FileTypeCategory> appendixAllowedFileTypes, List<GenericQuestionAppendix> appendices,
                                               Integer maximumAppendices, Long templateDocumentFormInputId, String templateDocumentTitle,
                                               String templateDocumentFilename, String templateDocumentResponseFilename, Long templateDocumentResponseFileEntryId,
                                               ZonedDateTime lastUpdated, String lastUpdatedByName, Long lastUpdatedBy, boolean open,
                                               boolean complete, boolean leadApplicant, AssignButtonsViewModel assignButtonsViewModel,
                                               Long multipleChoiceFormInputId, List<MultipleChoiceOptionResource> multipleChoiceOptions, MultipleChoiceOptionResource selectedMultipleChoiceOption,
                                               String leadOrganisationName, String leadOrganisationCompaniesHouseNumber, boolean loansPartBEnabled, String salesForceURL) {
        this.applicationId = applicationId;
        this.competitionName = competitionName;
        this.questionId = questionId;
        this.currentUser = currentUser;
        this.applicationName = applicationName;
        this.questionName = questionName;
        this.questionNumber = questionNumber;
        this.questionSubtitle = questionSubtitle;
        this.questionDescription = questionDescription;
        this.questionDescription2 = questionDescription2;
        this.questionGuidanceTitle = questionGuidanceTitle;
        this.questionGuidance = questionGuidance;
        this.questionType = questionType;
        this.questionHasMultipleStatuses = questionHasMultipleStatuses;
        this.textAreaFormInputId = textAreaFormInputId;
        this.wordCount = wordCount;
        this.wordsLeft = wordsLeft;
        this.appendixFormInputId = appendixFormInputId;
        this.appendixGuidance = appendixGuidance;
        this.appendixAllowedFileTypes = appendixAllowedFileTypes;
        this.appendices = appendices;
        this.maximumAppendices = maximumAppendices;
        this.templateDocumentFormInputId = templateDocumentFormInputId;
        this.templateDocumentTitle = templateDocumentTitle;
        this.templateDocumentFilename = templateDocumentFilename;
        this.templateDocumentResponseFilename = templateDocumentResponseFilename;
        this.templateDocumentResponseFileEntryId = templateDocumentResponseFileEntryId;
        this.lastUpdated = lastUpdated;
        this.lastUpdatedByName = lastUpdatedByName;
        this.lastUpdatedBy = lastUpdatedBy;
        this.open = open;
        this.complete = complete;
        this.leadApplicant = leadApplicant;
        this.assignButtonsViewModel = assignButtonsViewModel;
        this.multipleChoiceFormInputId = multipleChoiceFormInputId;
        this.multipleChoiceOptions = multipleChoiceOptions;
        this.selectedMultipleChoiceOption = selectedMultipleChoiceOption;
        this.leadOrganisationName = leadOrganisationName;
        this.leadOrganisationCompaniesHouseNumber = leadOrganisationCompaniesHouseNumber;
        this.loansPartBEnabled = loansPartBEnabled;
        this.salesForceURL = salesForceURL;
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

    public String getQuestionDescription2() {
        return questionDescription2;
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

    public boolean isQuestionHasMultipleStatuses() {
        return questionHasMultipleStatuses;
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

    public List<GenericQuestionAppendix> getAppendices() {
        return appendices;
    }

    public Integer getMaximumAppendices() {
        return maximumAppendices;
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

    public Long getTemplateDocumentResponseFileEntryId() {
        return templateDocumentResponseFileEntryId;
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

    public MultipleChoiceOptionResource getSelectedMultipleChoiceOption() {
        return selectedMultipleChoiceOption;
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

    public String getLeadOrganisationName() {
        return leadOrganisationName;
    }

    public String getLeadOrganisationCompaniesHouseNumber() {
        return leadOrganisationCompaniesHouseNumber;
    }

    @JsonIgnore
    public QuestionSetupType getLoansBusinessAndFinancialInformation() {
        return QuestionSetupType.LOAN_BUSINESS_AND_FINANCIAL_INFORMATION;
    }

    public String getSalesForceURL() {
        return salesForceURL;
    }

    public boolean isLoansPartBEnabled() {
        return loansPartBEnabled;
    }

    @JsonIgnore
    public String getLoansQuestionsFormSalesForceURL() {
        return salesForceURL + "/?" + "(" + "CompanyName= " + leadOrganisationName + "," +
            "CompanyNumber= " + leadOrganisationCompaniesHouseNumber + "," +
            "IFSApplicationNumber= " + applicationId + ")" ;
    }

    public static final class GenericQuestionApplicationViewModelBuilder {
        private long questionId;
        private long currentUser;
        private long applicationId;
        private String competitionName;
        private String applicationName;
        private String questionName;
        private String questionNumber;
        private String questionSubtitle;
        private String questionDescription;
        private String questionDescription2;
        private String questionGuidanceTitle;
        private String questionGuidance;
        private QuestionSetupType questionType;
        private boolean questionHasMultipleStatuses;
        private Long textAreaFormInputId;
        private Integer wordCount;
        private Integer wordsLeft;
        private Long appendixFormInputId;
        private String appendixGuidance;
        private Set<FileTypeCategory> appendixAllowedFileTypes;
        private List<GenericQuestionAppendix> appendices;
        private Integer maximumAppendices;
        private Long templateDocumentFormInputId;
        private String templateDocumentTitle;
        private String templateDocumentFilename;
        private String templateDocumentResponseFilename;
        private Long templateDocumentResponseFileEntryId;
        private ZonedDateTime lastUpdated;
        private String lastUpdatedByName;
        private Long lastUpdatedBy;
        private boolean open;
        private boolean complete;
        private boolean leadApplicant;
        private AssignButtonsViewModel assignButtonsViewModel;
        private Long multipleChoiceFormInputId;
        private List<MultipleChoiceOptionResource> multipleChoiceOptions;
        private MultipleChoiceOptionResource selectedMultipleChoiceOption;
        private String leadOrganisationName;
        private String leadOrganisationCompaniesHouseNumber;
        private boolean loansPartBEnabled;
        private String salesForceURL;

        private GenericQuestionApplicationViewModelBuilder() {
        }

        public static GenericQuestionApplicationViewModelBuilder aGenericQuestionApplicationViewModel() {
            return new GenericQuestionApplicationViewModelBuilder();
        }

        public GenericQuestionApplicationViewModelBuilder withQuestionId(long questionId) {
            this.questionId = questionId;
            return this;
        }

        public GenericQuestionApplicationViewModelBuilder withCurrentUser(long currentUser) {
            this.currentUser = currentUser;
            return this;
        }

        public GenericQuestionApplicationViewModelBuilder withApplicationId(long applicationId) {
            this.applicationId = applicationId;
            return this;
        }

        public GenericQuestionApplicationViewModelBuilder withCompetitionName(String competitionName) {
            this.competitionName = competitionName;
            return this;
        }

        public GenericQuestionApplicationViewModelBuilder withApplicationName(String applicationName) {
            this.applicationName = applicationName;
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

        public GenericQuestionApplicationViewModelBuilder withQuestionDescription2(String questionDescription2) {
            this.questionDescription2 = questionDescription2;
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

        public GenericQuestionApplicationViewModelBuilder withQuestionHasMultipleStatus(boolean questionHasMultipleStatuses) {
            this.questionHasMultipleStatuses = questionHasMultipleStatuses;
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

        public GenericQuestionApplicationViewModelBuilder withAppendices(List<GenericQuestionAppendix> appendices) {
            this.appendices = appendices;
            return this;
        }

        public GenericQuestionApplicationViewModelBuilder withMaximumAppendices(Integer maximumAppendices) {
            this.maximumAppendices = maximumAppendices;
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

        public GenericQuestionApplicationViewModelBuilder withTemplateDocumentResponseFileEntryId(Long templateDocumentResponseFileEntryId) {
            this.templateDocumentResponseFileEntryId = templateDocumentResponseFileEntryId;
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

        public GenericQuestionApplicationViewModelBuilder withSelectedMultipleChoiceOption(MultipleChoiceOptionResource selectedMultipleChoiceOption) {
            this.selectedMultipleChoiceOption = selectedMultipleChoiceOption;
            return this;
        }

        public GenericQuestionApplicationViewModelBuilder withLeadOrganisationName(String leadOrganisationName){
            this.leadOrganisationName = leadOrganisationName;
            return this;
        }

        public GenericQuestionApplicationViewModelBuilder withLeadOrganisationCompaniesHouseNumber(String leadOrganisationCompaniesHouseNumber){
            this.leadOrganisationCompaniesHouseNumber = leadOrganisationCompaniesHouseNumber;
            return this;
        }

        public GenericQuestionApplicationViewModelBuilder withLoansPartBEnabled(boolean loansPartBEnabled){
            this.loansPartBEnabled = loansPartBEnabled;
            return this;
        }

        public GenericQuestionApplicationViewModelBuilder withLoansFormQuestionsSalesForceURL(String salesForceURL){
            this.salesForceURL = salesForceURL;
            return this;
        }

        public GenericQuestionApplicationViewModel build() {
            return new GenericQuestionApplicationViewModel(applicationId, competitionName, questionId, currentUser, applicationName,
                    questionName, questionNumber, questionSubtitle, questionDescription, questionDescription2, questionGuidanceTitle, questionGuidance,
                    questionType, questionHasMultipleStatuses, textAreaFormInputId, wordCount, wordsLeft, appendixFormInputId, appendixGuidance, appendixAllowedFileTypes,
                    appendices, maximumAppendices, templateDocumentFormInputId, templateDocumentTitle, templateDocumentFilename,
                    templateDocumentResponseFilename, templateDocumentResponseFileEntryId, lastUpdated, lastUpdatedByName, lastUpdatedBy,
                    open, complete, leadApplicant, assignButtonsViewModel, multipleChoiceFormInputId, multipleChoiceOptions, selectedMultipleChoiceOption,
                    leadOrganisationName, leadOrganisationCompaniesHouseNumber, loansPartBEnabled, salesForceURL);
        }

    }
}
