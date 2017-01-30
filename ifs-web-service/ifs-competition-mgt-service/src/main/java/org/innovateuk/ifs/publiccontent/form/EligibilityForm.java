package org.innovateuk.ifs.publiccontent.form;

import java.util.List;

/**
 * Form for the Eligibility page on public content setup.
 */
public class EligibilityForm extends AbstractPublicContentForm {

    List<ContentGroupForm> groups;

    public List<ContentGroupForm> getGroups() {
        return groups;
    }

    public void setGroups(List<ContentGroupForm> groups) {
        this.groups = groups;
    }
}
