package org.innovateuk.ifs.publiccontent.formpopulator;


import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.publiccontent.form.AbstractPublicContentForm;

public interface PublicContentFormPopulator<F extends AbstractPublicContentForm> {

    F populate(PublicContentResource publicContentResource);
}
