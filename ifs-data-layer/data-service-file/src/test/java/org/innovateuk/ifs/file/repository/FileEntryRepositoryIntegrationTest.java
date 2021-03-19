package org.innovateuk.ifs.file.repository;

import org.innovateuk.ifs.file.domain.FileEntry;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;

import static org.junit.Assert.*;
import static org.springframework.http.MediaType.parseMediaType;

/**
 * Repository Integration tests for Form Inputs.
 */
@RunWith(SpringRunner.class)
@DataJpaTest
@EnableAutoConfiguration
@EntityScan(basePackages = "org.innovateuk.ifs.file.domain")
public class FileEntryRepositoryIntegrationTest  {

    @Autowired
    private FileEntryRepository repository;

    @Autowired
    private EntityManager em;


    @Test
    public void test_crud() {

        //
        // Create
        //
        FileEntry fileEntry = new FileEntry(null, "originalfilename", parseMediaType("image/jpeg"), 1234L);
        FileEntry saved = repository.save(fileEntry);
        assertTrue(saved == fileEntry);
        assertNotNull(saved.getId());
        flushAndClearSession();

        //
        // Read
        //
        FileEntry retrieved = repository.findById(fileEntry.getId()).get();
        assertFalse(saved == retrieved);
        assertEquals(saved.getId(), retrieved.getId());
        assertEquals(saved.getMediaType(), retrieved.getMediaType());
        assertEquals(saved.getName(), retrieved.getName());
        assertEquals(saved.getFilesizeBytes(), retrieved.getFilesizeBytes());

        //
        // Update
        //
        retrieved.setName("updatedfilename");
        retrieved.setMediaType("image/png");
        retrieved.setFilesizeBytes(4321L);
        repository.save(retrieved);
        flushAndClearSession();

        FileEntry retrievedAgain = repository.findById(fileEntry.getId()).get();
        assertEquals(retrieved.getId(), retrievedAgain.getId());
        assertEquals(retrieved.getMediaType(), retrievedAgain.getMediaType());
        assertEquals(retrieved.getName(), retrievedAgain.getName());
        assertEquals(retrieved.getFilesizeBytes(), retrievedAgain.getFilesizeBytes());

        //
        // Delete
        //
        repository.delete(retrievedAgain);
        flushAndClearSession();

        assertFalse(repository.findById(fileEntry.getId()).isPresent());
    }

    private  void flushAndClearSession() {
        em.flush();
        em.clear();
    }

    @Configuration
    static class ContextConfiguration {
    }

}
