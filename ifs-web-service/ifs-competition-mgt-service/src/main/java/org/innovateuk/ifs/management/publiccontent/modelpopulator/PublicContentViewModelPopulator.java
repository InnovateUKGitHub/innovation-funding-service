package org.innovateuk.ifs.management.publiccontent.modelpopulator;


import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.management.publiccontent.viewmodel.AbstractPublicContentViewModel;

public interface PublicContentViewModelPopulator<M extends AbstractPublicContentViewModel> {

    M populate(PublicContentResource publicContentResources, boolean readOnly);
}
