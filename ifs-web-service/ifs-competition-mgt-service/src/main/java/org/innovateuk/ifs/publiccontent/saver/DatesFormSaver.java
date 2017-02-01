package org.innovateuk.ifs.publiccontent.saver;


import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.publiccontent.form.DatesForm;
import org.springframework.stereotype.Service;

/**
 * Saver for the dates form.
 */
@Service
public class DatesFormSaver extends AbstractPublicContentFormSaver<DatesForm> implements PublicContentFormSaver<DatesForm> {

    @Override
    protected void populateResource(DatesForm form, PublicContentResource publicContentResource) {
    }

    @Override
    protected PublicContentSectionType getType() {
        return PublicContentSectionType.DATES;
    }
}
