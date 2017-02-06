package org.innovateuk.ifs.publiccontent.saver;


import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentEventResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.publiccontent.form.DatesForm;
import org.innovateuk.ifs.publiccontent.form.subform.Date;
import org.innovateuk.ifs.publiccontent.service.PublicContentEventRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Saver for the dates form.
 */
@Service
public class DatesFormSaver extends AbstractPublicContentFormSaver<DatesForm> implements PublicContentFormSaver<DatesForm> {

    @Autowired
    private PublicContentEventRestService publicContentEventRestService;

    @Override
    protected void populateResource(DatesForm form, PublicContentResource publicContentResource) {
        publicContentEventRestService.resetAndSaveEvents(publicContentResource.getId(), mapDateToEventResource(publicContentResource.getId(), form.getDates()));
    }

    @Override
    public ServiceResult<Void> save(DatesForm form, PublicContentResource publicContentResource) {
        return ServiceResult.serviceSuccess();
    }

    private List<PublicContentEventResource> mapDateToEventResource(Long publicContentId, List<Date> dates) {
        List<PublicContentEventResource> publicContentEventResources = new ArrayList<>();

        dates.forEach(date -> {
            PublicContentEventResource eventResource = new PublicContentEventResource();
            eventResource.setContent(date.getContent());
            eventResource.setId(date.getId());
            eventResource.setDate(LocalDateTime.of(date.getYear(), date.getMonth(), date.getDay(), 0, 0));
            eventResource.setPublicContent(publicContentId);
            publicContentEventResources.add(eventResource);
        });

        return publicContentEventResources;
    }



    @Override
    protected PublicContentSectionType getType() {
        return PublicContentSectionType.DATES;
    }
}
