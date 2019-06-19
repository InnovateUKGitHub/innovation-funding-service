package org.innovateuk.ifs.application.readonly.viewmodel;

public interface ApplicationQuestionReadOnlyViewModel {

    String getName();
    String getFragment();
    boolean isComplete();
    boolean shouldDisplayActions();
    boolean shouldDisplayMarkAsComplete();
    boolean isLead();
    default boolean isDisplayCompleteStatus() {
        return true;
    }
}