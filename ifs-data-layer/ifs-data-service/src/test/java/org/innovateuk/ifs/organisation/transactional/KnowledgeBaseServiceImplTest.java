package org.innovateuk.ifs.organisation.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.organisation.domain.KnowledgeBase;
import org.innovateuk.ifs.organisation.repository.KnowledgeBaseRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;

import static java.util.Collections.singleton;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class KnowledgeBaseServiceImplTest extends BaseServiceUnitTest<KnowledgeBaseService> {

    @Mock
    private KnowledgeBaseRepository knowledgeBaseRepository;

    private KnowledgeBase knowledgeBase;

    protected KnowledgeBaseService supplyServiceUnderTest() {
        return new KnowledgeBaseServiceImpl();
    }

    @Before
    public void setup() {
        knowledgeBase = new KnowledgeBase("KnowledgeBase 1");
        knowledgeBase.setId(1L);
    }

    @Test
    public void getKnowledegeBase() {
        when(knowledgeBaseRepository.findById(1L)).thenReturn(Optional.of(knowledgeBase));

        ServiceResult<String> result = service.getKnowledegeBase(1L);

        assertTrue(result.isSuccess());
        assertEquals(knowledgeBase.getName(), result.getSuccess());
    }

    @Test
    public void getKnowledegeBases() {
        when(knowledgeBaseRepository.findAll()).thenReturn(singleton(knowledgeBase));

        ServiceResult<List<String>> result = service.getKnowledegeBases();

        assertTrue(result.isSuccess());
        assertEquals(knowledgeBase.getName(), result.getSuccess().get(0));
    }

    @Test
    public void createKnowledgeBase() {
        when(knowledgeBaseRepository.save(any())).thenReturn(knowledgeBase);

        ServiceResult<Long> result = service.createKnowledgeBase(knowledgeBase.getName());

        assertTrue(result.isSuccess());
        assertEquals(knowledgeBase.getId(), result.getSuccess());
    }
}