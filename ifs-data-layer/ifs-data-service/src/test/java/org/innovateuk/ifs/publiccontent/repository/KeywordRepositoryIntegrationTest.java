package org.innovateuk.ifs.publiccontent.repository;

import org.innovateuk.ifs.BaseRepositoryIntegrationTest;
import org.innovateuk.ifs.publiccontent.domain.Keyword;
import org.innovateuk.ifs.publiccontent.domain.PublicContent;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Set;

import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.publiccontent.builder.KeywordBuilder.newKeyword;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentBuilder.newPublicContent;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class KeywordRepositoryIntegrationTest extends BaseRepositoryIntegrationTest<KeywordRepository> {

    @Autowired
    @Override
    protected void setRepository(KeywordRepository repository) {
        this.repository = repository;
    }

    @Autowired
    private PublicContentRepository publicContentRepository;

    @Before
    public void setUp() throws Exception {
        PublicContent publicContent = publicContentRepository.save(newPublicContent()
                .with(id(null))
                .withCompetitionId(1L)
                .build());

        repository.save(newKeyword()
                .with(id(null))
                .withKeyword("keyword1", "keyword 2")
                .withPublicContent(publicContent)
                .build(2));
    }

    @Test
    public void test_findByKeywordLike() throws Exception {
        assertEquals(2, repository.findByKeywordLike("%word%").size());
    }

    @Test
    public void test_findByKeywordLikeMultiple() throws Exception {
        Set<Keyword> keywords = new HashSet<>(repository.findByKeywordLike("%key%"));
        keywords.addAll(repository.findByKeywordLike("%word%"));
        keywords.addAll(repository.findByKeywordLike("%rd%"));
        keywords.addAll(repository.findByKeywordLike("%wo%"));
        keywords.addAll(repository.findByKeywordLike("%d%"));

        assertEquals(2, keywords.size());
    }

    @Test
    public void test_findByKeywordLikeEmpty() throws Exception {
        assertTrue(repository.findByKeywordLike("%keywording%").isEmpty());
    }
}
