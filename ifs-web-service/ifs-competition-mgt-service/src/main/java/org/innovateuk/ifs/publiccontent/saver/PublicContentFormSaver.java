package org.innovateuk.ifs.publiccontent.saver;


import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.publiccontent.form.AbstractPublicContentForm;

public interface PublicContentFormSaver<F extends AbstractPublicContentForm> {

    ServiceResult<Void> save(F form, PublicContentResource publicContentResource);
}
