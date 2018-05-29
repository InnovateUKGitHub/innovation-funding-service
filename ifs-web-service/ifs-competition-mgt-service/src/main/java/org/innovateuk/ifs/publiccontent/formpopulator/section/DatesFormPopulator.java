package org.innovateuk.ifs.publiccontent.formpopulator.section;


import org.innovateuk.ifs.competition.publiccontent.resource.ContentEventResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.publiccontent.form.section.DatesForm;
import org.innovateuk.ifs.publiccontent.form.section.subform.Date;
import org.innovateuk.ifs.publiccontent.formpopulator.AbstractPublicContentFormPopulator;
import org.innovateuk.ifs.publiccontent.formpopulator.PublicContentFormPopulator;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class DatesFormPopulator extends AbstractPublicContentFormPopulator<DatesForm> implements PublicContentFormPopulator<DatesForm> {

    @Override
    protected DatesForm createInitial() {
        return new DatesForm();
    }

    @Override
    protected void populateSection(DatesForm form, PublicContentResource publicContentResource) {
        List<Date> dateList = new ArrayList<>();

        publicContentResource.getContentEvents().forEach(event ->
            dateList.add(mapEventToDate(event))
        );

        form.setDates(dateList);
    }

    @Override
    protected PublicContentSectionType getType() {
        return PublicContentSectionType.DATES;
    }

    private Date mapEventToDate(ContentEventResource event) {
        Date date = new Date();

        date.setId(event.getId());
        date.setDay(event.getDate().getDayOfMonth());
        date.setMonth(event.getDate().getMonthValue());
        date.setYear(event.getDate().getYear());
        date.setContent(event.getContent());

        return date;
    }
}
