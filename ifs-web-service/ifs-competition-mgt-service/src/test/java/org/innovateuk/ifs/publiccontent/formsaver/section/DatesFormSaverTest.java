package org.innovateuk.ifs.publiccontent.formsaver.section;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.publiccontent.form.section.DatesForm;
import org.innovateuk.ifs.publiccontent.form.section.subform.Date;
import org.innovateuk.ifs.publiccontent.saver.section.DatesFormSaver;
import org.innovateuk.ifs.publiccontent.service.ContentEventService;
import org.innovateuk.ifs.publiccontent.service.PublicContentService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentResourceBuilder.newPublicContentResource;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DatesFormSaverTest {

    @InjectMocks
    private DatesFormSaver target;

    @Mock
    private PublicContentService publicContentService;

    @Mock
    private ContentEventService contentEventService;

    @Test
    public void testMarkAsComplete() {
        final Long publicContentId = 1L;

        DatesForm form = new DatesForm();
        Date date = new Date();
        date.setDay(1);
        date.setMonth(2);
        date.setYear(2017);
        date.setContent("Content");
        form.setDates(asList(date));
        PublicContentResource resource = newPublicContentResource()
                .with(publicContentResource -> publicContentResource.setId(publicContentId))
                .build();

        ServiceResult<Void> result = target.markAsComplete(form, resource);

        verify(contentEventService).resetAndSaveEvents(any(PublicContentResource.class), anyList());
    }
}
