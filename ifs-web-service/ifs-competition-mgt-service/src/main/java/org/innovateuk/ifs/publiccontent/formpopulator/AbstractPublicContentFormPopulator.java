package org.innovateuk.ifs.publiccontent.formpopulator;


import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.publiccontent.form.AbstractPublicContentForm;

public abstract class AbstractPublicContentFormPopulator<F extends AbstractPublicContentForm> implements PublicContentFormPopulator<F> {

    public F populate(PublicContentResource publicContentResource) {
        F form = createInitial();
        populateSection(form, publicContentResource);
        return form;
    }

    protected abstract F createInitial();
    protected abstract void populateSection(F form, PublicContentResource publicContentResource);
}
