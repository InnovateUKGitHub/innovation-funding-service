package org.innovateuk.ifs.publiccontent.formpopulator;


import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.publiccontent.form.AbstractPublicContentForm;

/**
 * Abstract class for populating a public content form section.
 * @param <F> the form class.
 */
public abstract class AbstractPublicContentFormPopulator<F extends AbstractPublicContentForm> implements PublicContentFormPopulator<F> {

    public F populate(PublicContentResource publicContentResource) {
        F form = createInitial();
        populateSection(form, publicContentResource);
        return form;
    }

    protected abstract F createInitial();
    protected abstract void populateSection(F form, PublicContentResource publicContentResource);
    protected abstract PublicContentSectionType getType();
}
