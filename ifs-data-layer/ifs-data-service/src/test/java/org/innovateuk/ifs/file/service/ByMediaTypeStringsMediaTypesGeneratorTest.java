package org.innovateuk.ifs.file.service;

import org.junit.Test;
import org.springframework.http.MediaType;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertArrayEquals;

public class ByMediaTypeStringsMediaTypesGeneratorTest {

    @Test
    public void testApply() {

        ByMediaTypeStringsMediaTypesGenerator generator = new ByMediaTypeStringsMediaTypesGenerator();

        List<MediaType> mediaTypes = generator.apply(asList("application/pdf", "application/json"));

        assertArrayEquals(new MediaType[] {MediaType.APPLICATION_PDF, MediaType.APPLICATION_JSON}, mediaTypes.toArray());
    }
}
