package com.worth.ifs.file.repository;

import com.worth.ifs.BaseRepositoryIntegrationTest;
import com.worth.ifs.file.domain.FileEntry;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;

import static org.junit.Assert.*;

/**
 * Repository Integration tests for Form Inputs.
 */
public class FileEntryRepositoryIntegrationTest extends BaseRepositoryIntegrationTest<FileEntryRepository> {

    @Autowired
    private FileEntryRepository repository;

    @Autowired
    private EntityManager em;

    @Override
    @Autowired
    protected void setRepository(FileEntryRepository repository) {
        this.repository = repository;
    }

    @Test
    public void test_crud() {

        //
        // Create
        //
        FileEntry fileEntry = new FileEntry(null, "originalfilename", "image/jpeg", 1234L);
        FileEntry saved = repository.save(fileEntry);
        assertTrue(saved == fileEntry);
        assertNotNull(saved.getId());
        em.flush();
        em.clear();

        //
        // Read
        //
        FileEntry retrieved = repository.findOne(fileEntry.getId());
        assertFalse(saved == retrieved);
        assertEquals(saved.getId(), retrieved.getId());
        assertEquals(saved.getMimeType(), retrieved.getMimeType());
        assertEquals(saved.getName(), retrieved.getName());
        assertEquals(saved.getFilesizeBytes(), retrieved.getFilesizeBytes());

        //
        // Update
        //
        retrieved.setName("updatedfilename");
        retrieved.setMimeType("image/png");
        retrieved.setFilesizeBytes(4321L);
        repository.save(retrieved);
        em.flush();
        em.clear();

        FileEntry retrievedAgain = repository.findOne(fileEntry.getId());
        assertEquals(retrieved.getId(), retrievedAgain.getId());
        assertEquals(retrieved.getMimeType(), retrievedAgain.getMimeType());
        assertEquals(retrieved.getName(), retrievedAgain.getName());
        assertEquals(retrieved.getFilesizeBytes(), retrievedAgain.getFilesizeBytes());

        //
        // Delete
        //
        repository.delete(retrievedAgain);
        em.flush();
        em.clear();

        assertNull(repository.findOne(fileEntry.getId()));
    }
}
