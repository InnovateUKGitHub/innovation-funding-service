package com.worth.ifs.application.repository;

import com.worth.ifs.BaseRepositoryIntegrationTest;
import com.worth.ifs.application.domain.Response;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;

public class ResponseRepositoryIntegrationTest extends BaseRepositoryIntegrationTest<ResponseRepository> {

    @Override
    @Autowired
    protected void setRepository(ResponseRepository repository) {
        this.repository = repository;
    }

    @Test
    public void test_getSingleResponse() {

        Response response = repository.findOne(1L);
        assertEquals(Long.valueOf(1), response.getId());
    }
}
