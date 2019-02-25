package org.innovateuk.ifs.eu.form;

import java.util.List;

/**
 * Form for the selection of eu contacts to invite onto the IFS platform
 */
public class EuContactSelectionForm {

    public EuContactSelectionForm() {
    }

    private List<Long> euContactIds;

    public List<Long> getEuContactIds() {
        return euContactIds;
    }

    public void setEuContactIds(List<Long> euContactIds) {
        this.euContactIds = euContactIds;
    }
}
