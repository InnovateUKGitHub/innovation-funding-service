package org.innovateuk.ifs.thread.domain;

import org.innovateuk.ifs.threads.domain.Post;
import org.innovateuk.ifs.threads.domain.Query;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.domain.OrganisationType;
import org.innovateuk.threads.resource.FinanceChecksSectionType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class QueryTest {

    private Query query;
    private Long id;
    private Long classPk;
    private String className;
    private List<Post> posts;
    private FinanceChecksSectionType section;
    private String title;
    private LocalDateTime createdOn;

    @Before
    public void setUp() throws Exception {
        id = 0L;
        classPk = 22L;
        className = "org.innovateuk.ifs.class";
        posts = new ArrayList<>();
        section = FinanceChecksSectionType.VIABILITY;
        title = "Test Query Title";
        createdOn = LocalDateTime.now();

        query = new Query(1L, classPk, className, posts, section, title, createdOn);
    }

    @Test
    public void organisationShouldReturnCorrectAttributeValues() throws Exception {
        Assert.assertEquals(query.id(), id);
        Assert.assertEquals(query.contextClassPk(), classPk);
        Assert.assertEquals(query.contextClassName(), className);
        Assert.assertEquals(query.posts(), posts);
        Assert.assertEquals(query.section(), section);
        Assert.assertEquals(query.title(), title);
        Assert.assertEquals(query.createdOn(), createdOn);
    }

}
