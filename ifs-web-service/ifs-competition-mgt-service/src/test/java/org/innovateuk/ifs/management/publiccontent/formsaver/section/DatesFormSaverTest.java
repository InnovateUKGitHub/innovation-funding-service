package org.innovateuk.ifs.management.publiccontent.formsaver.section;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.management.publiccontent.form.section.DatesForm;
import org.innovateuk.ifs.management.publiccontent.form.section.subform.Date;
import org.innovateuk.ifs.management.publiccontent.saver.section.DatesFormSaver;
import org.innovateuk.ifs.management.publiccontent.service.PublicContentService;
import org.innovateuk.ifs.util.TimeZoneUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentResourceBuilder.newPublicContentResource;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class DatesFormSaverTest {

    @InjectMocks
    private DatesFormSaver target;

    @Mock
    private PublicContentService publicContentService;

    @Test
    public void testMarkAsComplete() {
        final Long publicContentId = 1L;
        ZonedDateTime localDateTime = ZonedDateTime.now();
        String content = "content";
        DatesForm form = new DatesForm();
        Date date = new Date();
        date.setDay(localDateTime.getDayOfMonth());
        date.setMonth(localDateTime.getMonthValue());
        date.setYear(localDateTime.getYear());
        date.setContent(content);
        form.setDates(asList(date));
        PublicContentResource resource = newPublicContentResource()
                .with(publicContentResource -> publicContentResource.setId(publicContentId))
                .build();

        when(publicContentService.markSectionAsComplete(resource, PublicContentSectionType.DATES)).thenReturn(ServiceResult.serviceSuccess());

        ServiceResult<Void> result = target.markAsComplete(form, resource);

        assertThat(result.isSuccess(), equalTo(true));

        verify(publicContentService).markSectionAsComplete(resource, PublicContentSectionType.DATES);

        assertThat(resource.getContentEvents().size(), equalTo(1));
        assertThat(resource.getContentEvents().get(0).getDate(), equalTo(LocalDateTime.of(localDateTime.toLocalDate(), LocalTime.MIDNIGHT).atZone(TimeZoneUtil.UK_TIME_ZONE)));
        assertThat(resource.getContentEvents().get(0).getContent(), equalTo(content));
    }
}
