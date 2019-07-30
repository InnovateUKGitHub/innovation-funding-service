package org.innovateuk.ifs.management.publiccontent.formpopulator.section;


import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.management.publiccontent.form.section.ScopeForm;
import org.innovateuk.ifs.management.publiccontent.formpopulator.PublicContentFormPopulator;
import org.innovateuk.ifs.management.publiccontent.formpopulator.AbstractContentGroupFormPopulator;
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
