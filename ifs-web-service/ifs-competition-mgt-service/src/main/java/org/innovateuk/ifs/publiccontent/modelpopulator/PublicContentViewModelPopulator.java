package org.innovateuk.ifs.publiccontent.modelpopulator;


import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.publiccontent.viewmodel.AbstractPublicContentViewModel;

public interface PublicContentViewModelPopulator<M extends AbstractPublicContentViewModel> {

    M populate(PublicContentResource publicContentResources, boolean readOnly);
}
