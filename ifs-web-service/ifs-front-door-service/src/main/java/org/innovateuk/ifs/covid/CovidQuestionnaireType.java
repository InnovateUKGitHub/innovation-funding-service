package org.innovateuk.ifs.covid;

import org.apache.commons.beanutils.PropertyUtils;
import org.innovateuk.ifs.commons.exception.IFSRuntimeException;
import org.innovateuk.ifs.covid.form.CovidQuestionaireForm;

import java.lang.reflect.InvocationTargetException;

public enum CovidQuestionnaireType {
    BUSINESS("business", "business", "Are you a business?"),
    AWARD_RECIPIENT("award-recipient", "awardRecipient", "Are you an Innovate UK award recipient?"),
    CHALLENGE_TIMING("challenge-timing", "challengeTiming", "Is your challenge in the timing of your project activity?", "For example, do you need to extend the blah?"),
    CHALLENGE_CASHFLOW("challenge-cashflow", "challengeCashflow", "Is your challenge in managing your cashflow, so that you can meet your project costs to continue your project activity up to £250,000 in the next [x] quarters? "),
    CHALLENGE_LARGE_FUNDING_GAP("challenge-cashflow", "challengeLargeFundingGap", "Is the challenge in meeting a larger funding gap (up to somthi???n"),
    CHALLENGE_SIGNIFICANT_FUNDING_GAP("challenge-cashflow", "challengeSignificantFundingGap", "Is the challnege in meeting a significant one?");

    private String url;
    private String field;
    private String question;
    private String hint;

    CovidQuestionnaireType(String url, String field, String question, String hint) {
        this.url = url;
        this.field = field;
        this.question = question;
        this.hint = hint;
    }

    CovidQuestionnaireType(String url, String field, String question) {
        this(url, field, question, null);
    }

    public String getUrl() {
        return url;
    }

    public String getField() {
        return field;
    }

    public String getQuestion() {
        return question;
    }

    public String getHint() {
        return hint;
    }

    public void reset(CovidQuestionaireForm form) {
        setValue(form, null);
    }

    public Boolean getValue(CovidQuestionaireForm form) {
        try {
            return (Boolean) PropertyUtils.getSimpleProperty(form, field);
        } catch (IllegalAccessException  | InvocationTargetException  | NoSuchMethodException e) {
            throw new IFSRuntimeException(e);
        }
    }

    public void setValue(CovidQuestionaireForm form, Boolean value) {
        try {
            PropertyUtils.setSimpleProperty(form, field, value);
        } catch (IllegalAccessException  | InvocationTargetException  | NoSuchMethodException e) {
            throw new IFSRuntimeException(e);
        }

    }

}
