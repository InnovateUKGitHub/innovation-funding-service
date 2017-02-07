package org.innovateuk.ifs.publiccontent.formpopulator.section;


import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.publiccontent.form.section.SupportingInformationForm;
import org.innovateuk.ifs.publiccontent.formpopulator.AbstractContentGroupFormPopulator;
import org.innovateuk.ifs.publiccontent.formpopulator.PublicContentFormPopulator;
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
