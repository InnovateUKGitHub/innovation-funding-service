package org.innovateuk.ifs.eu.form;

import org.innovateuk.ifs.eugrant.EuContactResource;

import java.util.List;

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
