package org.innovateuk.ifs.application.readonly.viewmodel;

import org.innovateuk.ifs.question.resource.QuestionSetupType;

public interface ApplicationQuestionReadOnlyViewModel {

    default long getQuestionId() {return 1;};
    String getName();
    String getFragment();
    boolean isComplete();
    boolean isLead();
    default boolean shouldDisplayActions()  { return true; }
    default boolean shouldDisplayMarkAsComplete()  {
        return true;
    }
    default boolean isDisplayCompleteStatus() {
        return true;
    }
    default boolean hasScore() { return false; }
    default String getQuestionSetupTypeName() { return QuestionSetupType.APPLICATION_DETAILS.getShortName(); }
}