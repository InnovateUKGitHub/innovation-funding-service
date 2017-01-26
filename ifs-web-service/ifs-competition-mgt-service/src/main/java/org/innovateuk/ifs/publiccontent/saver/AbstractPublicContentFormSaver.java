package org.innovateuk.ifs.publiccontent.saver;


import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSection;
import org.innovateuk.ifs.publiccontent.form.AbstractPublicContentForm;
import org.innovateuk.ifs.publiccontent.service.PublicContentService;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractPublicContentFormSaver<F extends AbstractPublicContentForm> implements PublicContentFormSaver<F> {

    @Autowired
    private PublicContentService publicContentService;

    @Override
    public ServiceResult<Void> save(F form, PublicContentResource publicContentResource) {
        populateResource(form, publicContentResource);
        return publicContentService.updateSection(publicContentResource, getType());
    }

    protected abstract void populateResource(F form, PublicContentResource publicContentResource);
    protected abstract PublicContentSection getType();


}
