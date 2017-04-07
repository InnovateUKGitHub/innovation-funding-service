package org.innovateuk.ifs.publiccontent.saver.section;


import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.competition.publiccontent.resource.ContentEventResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.publiccontent.form.section.DatesForm;
import org.innovateuk.ifs.publiccontent.form.section.subform.Date;
import org.innovateuk.ifs.publiccontent.saver.AbstractPublicContentFormSaver;
import org.innovateuk.ifs.publiccontent.saver.PublicContentFormSaver;
import org.innovateuk.ifs.util.TimeZoneUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;

/**
 * Saver for the dates form.
 */
@Service
public class DatesFormSaver extends AbstractPublicContentFormSaver<DatesForm> implements PublicContentFormSaver<DatesForm> {

    @Override
    protected List<Error> populateResource(DatesForm form, PublicContentResource publicContentResource) {
        publicContentResource.setContentEvents(mapDateToEventResource(publicContentResource.getId(), form.getDates()));
        return Collections.emptyList();
    }

    private List<ContentEventResource> mapDateToEventResource(Long publicContentId, List<Date> dates) {
        List<ContentEventResource> contentEventResources = new ArrayList<>();

        if(null != dates) {
            dates.forEach(date -> {
                ContentEventResource eventResource = new ContentEventResource();
                eventResource.setContent(date.getContent());
                eventResource.setId(date.getId());
                eventResource.setDate(TimeZoneUtil.fromUkTimeZone(date.getYear(), date.getMonth(), date.getDay()));
                eventResource.setPublicContent(publicContentId);
                contentEventResources.add(eventResource);
            });
        }

        return contentEventResources;
    }



    @Override
    protected PublicContentSectionType getType() {
        return PublicContentSectionType.DATES;
    }
}
