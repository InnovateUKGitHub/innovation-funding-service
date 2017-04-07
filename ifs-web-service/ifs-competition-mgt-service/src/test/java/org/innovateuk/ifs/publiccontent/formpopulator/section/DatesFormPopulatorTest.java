package org.innovateuk.ifs.publiccontent.formpopulator.section;

import org.innovateuk.ifs.competition.publiccontent.resource.ContentEventResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.publiccontent.form.section.DatesForm;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static org.innovateuk.ifs.publiccontent.builder.ContentEventResourceBuilder.newContentEventResource;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentResourceBuilder.newPublicContentResource;
import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class DatesFormPopulatorTest {
    
    @InjectMocks
    private DatesFormPopulator target;

    @Test
    public void testPopulate() {
        List<ContentEventResource> contentEvents = newContentEventResource()
                .withId(1L)
                .withPublicContent(2L)
                .withDate(ZonedDateTime.of(2017,1,1,0,0,0,0,ZoneId.systemDefault()))
                .withContent("Some Content")
                .build(2);

        PublicContentResource resource = newPublicContentResource()
                .withContentEvents(contentEvents)
                .build();

        DatesForm form = target.populate(resource);

        form.getDates().forEach(
                date -> {
                    assertEquals(Long.valueOf(1L), date.getId());
                    assertEquals("Some Content", date.getContent());
                    assertEquals(Integer.valueOf(1), date.getDay());
                    assertEquals(Integer.valueOf(1), date.getMonth());
                    assertEquals(Integer.valueOf(2017), date.getYear());
                });
    }
}
