package org.innovateuk.ifs.publiccontent.repository;

import org.innovateuk.ifs.BaseRepositoryIntegrationTest;
import org.innovateuk.ifs.publiccontent.domain.Keyword;
import org.innovateuk.ifs.publiccontent.domain.PublicContent;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.innovateuk.ifs.publiccontent.builder.PublicContentBuilder.newPublicContent;
import static org.junit.Assert.assertEquals;

public class KeywordRepositoryIntegrationTest extends BaseRepositoryIntegrationTest<KeywordRepository> {

    @Autowired
    @Override
    protected void setRepository(KeywordRepository repository) {
        this.repository = repository;
    }

    @Autowired
    private PublicContentRepository publicContentRepository;

    @Test
    @Rollback
    public void test_findByKeywordLike() throws Exception {
        setupTestSet();

        List<Keyword> found = repository.findByKeywordLike("%word%");

        assertEquals(2, found.size());
    }

    @Test
    @Rollback
    public void test_findByKeywordLikeMultiple() throws Exception {
        setupTestSet();

        Set<Keyword> keywords = new HashSet<>();

        List<Keyword> found1 = repository.findByKeywordLike("%key%");
        keywords.addAll(found1);

        List<Keyword> found2 = repository.findByKeywordLike("%word%");
        keywords.addAll(found2);

        List<Keyword> found3 = repository.findByKeywordLike("%rd%");
        keywords.addAll(found3);

        List<Keyword> found4 = repository.findByKeywordLike("%wo%");
        keywords.addAll(found4);

        List<Keyword> found5 = repository.findByKeywordLike("%d%");
        keywords.addAll(found5);

        assertEquals(2, keywords.size());
    }

    @Test
    @Rollback
    public void test_findByKeywordLikeEmpty() throws Exception {
        setupTestSet();

        List<Keyword> found = repository.findByKeywordLike("%keywording%");

        assertEquals(0, found.size());
    }


    private void setupTestSet() {

        PublicContent publicContentLink = newPublicContent().with(publicContent -> {
            publicContent.setCompetitionId(1L);
        }).build();

        PublicContent publicContentSaved = publicContentRepository.save(publicContentLink);

        Keyword keyword1 = new Keyword();
        keyword1.setKeyword("keyword");
        keyword1.setPublicContent(publicContentSaved);

        Keyword keyword2 = new Keyword();
        keyword2.setKeyword("keywords");
        keyword2.setPublicContent(publicContentSaved);

        repository.save(keyword1);
        repository.save(keyword2);
    }

}
