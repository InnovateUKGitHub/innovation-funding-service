package org.innovateuk.ifs.publiccontent.formpopulator.section;


import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.publiccontent.form.section.ScopeForm;
import org.innovateuk.ifs.publiccontent.formpopulator.AbstractContentGroupFormPopulator;
import org.innovateuk.ifs.publiccontent.formpopulator.PublicContentFormPopulator;
import org.springframework.stereotype.Service;


@Service
public class ScopeFormPopulator extends AbstractContentGroupFormPopulator<ScopeForm> implements PublicContentFormPopulator<ScopeForm> {

    @Override
    protected ScopeForm createInitial() {
        return new ScopeForm();
    }

    @Override
    protected PublicContentSectionType getType() {
        return PublicContentSectionType.SCOPE;
    }
}
