package org.innovateuk.ifs.publiccontent.saver;


import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.publiccontent.form.AbstractPublicContentForm;
import org.innovateuk.ifs.publiccontent.service.PublicContentService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.function.Supplier;

/**
 * Abstract class to save all public content section forms.
 * @param <F> the form class.
 */
public abstract class AbstractPublicContentFormSaver<F extends AbstractPublicContentForm> implements PublicContentFormSaver<F> {

    @Autowired
    private PublicContentService publicContentService;

    @Override
    public ServiceResult<Void> save(F form, PublicContentResource publicContentResource) {
        return saveInternal(form, publicContentResource,
                () -> publicContentService.updateSection(publicContentResource, getType()));
    }

    @Override
    public ServiceResult<Void> markAsComplete(F form, PublicContentResource publicContentResource) {
        return saveInternal(form, publicContentResource,
                () -> publicContentService.markSectionAsComplete(publicContentResource, getType()));
    }

    private ServiceResult<Void> saveInternal(F form, PublicContentResource publicContentResource, Supplier<ServiceResult<Void>> action) {
        List<Error> errors = populateResource(form, publicContentResource);
        if (!errors.isEmpty()) {
            return ServiceResult.serviceFailure(errors);
        } else {
            return action.get();
        }
    }

    protected abstract List<Error> populateResource(F form, PublicContentResource publicContentResource);
    protected abstract PublicContentSectionType getType();


}
