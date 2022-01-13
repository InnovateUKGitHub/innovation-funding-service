package org.innovateuk.ifs.question.resource;

import static org.innovateuk.ifs.question.resource.QuestionImplementationType.FORM_INPUTS;
import static org.innovateuk.ifs.question.resource.QuestionImplementationType.QUESTIONNAIRE;

public enum QuestionSetupType {

    ASSESSED_QUESTION("", FORM_INPUTS),
    SCOPE("Scope", FORM_INPUTS),
    PROJECT_SUMMARY("Project summary", FORM_INPUTS),
    PUBLIC_DESCRIPTION("Public description", FORM_INPUTS),
    APPLICATION_DETAILS("Application details"),
    RESEARCH_CATEGORY("Research category"),
    APPLICATION_TEAM("Application team"),
    TERMS_AND_CONDITIONS("T&C"),
    EQUALITY_DIVERSITY_INCLUSION("Equality, diversity & inclusion", FORM_INPUTS),
    SUBSIDY_BASIS("Subsidy basis", QUESTIONNAIRE),
    NORTHERN_IRELAND_DECLARATION("Northern Ireland declaration", FORM_INPUTS),
    /* h2020 */
    GRANT_TRANSFER_DETAILS("Application details"),
    GRANT_AGREEMENT("Horizon 2020 grant agreement"),
    /* KTP */
    KTP_ASSESSMENT("Ktp Assessment", FORM_INPUTS),
    /* Loan */
    LOAN_BUSINESS_AND_FINANCIAL_INFORMATION("Business and financial information", FORM_INPUTS);


    private String shortName;
    private QuestionImplementationType implementationType;

    QuestionSetupType(String shortName) {
        this.shortName = shortName;
    }

    QuestionSetupType(String shortName, QuestionImplementationType implementationType) {
        this.shortName = shortName;
        this.implementationType = implementationType;
    }

    public String getShortName() {
        return this.shortName;
    }

    public boolean hasFormInputResponses() {
        return implementationType == FORM_INPUTS;
    }
    public boolean isQuestionnaire() {
        return implementationType == QUESTIONNAIRE;
    }
}
