package org.innovateuk.ifs.management.publiccontent.formpopulator.section;


import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.management.publiccontent.formpopulator.PublicContentFormPopulator;
import org.innovateuk.ifs.management.publiccontent.form.section.SupportingInformationForm;
import org.innovateuk.ifs.management.publiccontent.formpopulator.AbstractContentGroupFormPopulator;
import org.springframework.stereotype.Service;


@Service
public class SupportingInformationFormPopulator extends AbstractContentGroupFormPopulator<SupportingInformationForm> implements PublicContentFormPopulator<SupportingInformationForm> {

    @Override
    protected SupportingInformationForm createInitial() {
        return new SupportingInformationForm();
    }

    @Override
    protected PublicContentSectionType getType() {
        return PublicContentSectionType.SUPPORTING_INFORMATION;
    }
}
