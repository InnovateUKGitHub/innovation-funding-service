package org.innovateuk.ifs.application.viewmodel;

/**
 * Generic ViewModel for common fields in LeadOnlyViewModels
 */
public abstract class AbstractLeadOnlyViewModel {

    private boolean closed;
    private boolean complete;
    private boolean canMarkAsComplete;

    public boolean isCanMarkAsComplete() {
        return canMarkAsComplete;
    }

    public void setCanMarkAsComplete(boolean canMarkAsComplete) {
        this.canMarkAsComplete = canMarkAsComplete;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }
}
