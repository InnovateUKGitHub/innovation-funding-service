package org.innovateuk.ifs.publiccontent.formpopulator;


import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.publiccontent.form.DatesForm;
import org.springframework.stereotype.Service;


@Service
public class DatesFormPopulator extends AbstractPublicContentFormPopulator<DatesForm> implements PublicContentFormPopulator<DatesForm> {

    @Override
    protected DatesForm createInitial() {
        return new DatesForm();
    }

    @Override
    protected void populateSection(DatesForm form, PublicContentResource publicContentResource) {

    }
}
