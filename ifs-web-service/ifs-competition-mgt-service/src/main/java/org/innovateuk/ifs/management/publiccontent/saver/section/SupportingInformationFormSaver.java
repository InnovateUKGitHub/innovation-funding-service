package org.innovateuk.ifs.management.publiccontent.saver.section;


import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.management.publiccontent.saver.AbstractContentGroupFormSaver;
import org.innovateuk.ifs.management.publiccontent.saver.PublicContentFormSaver;
import org.innovateuk.ifs.management.publiccontent.form.section.SupportingInformationForm;
import org.springframework.stereotype.Service;

/**
 * Saver for the SupportingInformationForm form.
 */
@Service
public class SupportingInformationFormSaver extends AbstractContentGroupFormSaver<SupportingInformationForm> implements PublicContentFormSaver<SupportingInformationForm> {

    @Override
    protected PublicContentSectionType getType() {
        return PublicContentSectionType.SUPPORTING_INFORMATION;
    }

}
