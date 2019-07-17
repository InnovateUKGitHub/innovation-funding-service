package org.innovateuk.ifs.management.publiccontent.saver;


import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.management.publiccontent.form.AbstractPublicContentForm;

public interface PublicContentFormSaver<F extends AbstractPublicContentForm> {

    ServiceResult<Void> markAsComplete(F form, PublicContentResource publicContentResource);
    ServiceResult<Void> save(F form, PublicContentResource publicContentResource);
}
