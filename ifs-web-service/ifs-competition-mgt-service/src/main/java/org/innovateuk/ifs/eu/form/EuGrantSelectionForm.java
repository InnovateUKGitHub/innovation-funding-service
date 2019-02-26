package org.innovateuk.ifs.eu.form;

import java.util.List;
import java.util.UUID;

/**
 * Form for the selection of eu contacts to invite onto the IFS platform
 */
public class EuGrantSelectionForm {

    public EuGrantSelectionForm() {
    }

    private List<UUID> euGrantIds;

    public List<UUID> getEuGrantIds() {
        return euGrantIds;
    }

    public void setEuGrantIds(List<UUID> euGrantIds) {
        this.euGrantIds = euGrantIds;
    }
}
