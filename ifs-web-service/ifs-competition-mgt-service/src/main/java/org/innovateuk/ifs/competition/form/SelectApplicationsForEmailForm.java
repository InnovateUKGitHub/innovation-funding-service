package org.innovateuk.ifs.competition.form;

import javax.validation.constraints.NotNull;
import java.util.List;

public class SelectApplicationsForEmailForm {

    @NotNull
    private List<String> ids;

    public List<String> getIds() {
        return ids;
    }

    public void setIds(List<String> ids) {
        this.ids = ids;
    }
}
