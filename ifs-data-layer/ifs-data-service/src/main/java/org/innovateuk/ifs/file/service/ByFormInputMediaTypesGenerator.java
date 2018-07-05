package org.innovateuk.ifs.file.service;

import org.innovateuk.ifs.file.resource.FileTypeCategory;
import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.transactional.FormInputService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

import static org.innovateuk.ifs.util.CollectionFunctions.flattenLists;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * Generates a set of {@link MediaType}s based on {@link FormInput#allowedFileTypes}.
 */
@Component
public class ByFormInputMediaTypesGenerator implements MediaTypesGenerator<Long> {

    private ByMediaTypeStringsMediaTypesGenerator byStringGenerator = new ByMediaTypeStringsMediaTypesGenerator();

    @Autowired
    private FormInputService formInputService;

    @Override
    public List<MediaType> apply(Long formInputId) {
        FormInputResource formInput = formInputService.findFormInput(formInputId).getSuccess();

        Set<FileTypeCategory> fileTypeCategories = formInput.getAllowedFileTypes();

        List<String> mediaTypesStrings = flattenLists(simpleMap(fileTypeCategories, FileTypeCategory::getMediaTypes));

        return byStringGenerator.apply(mediaTypesStrings);
    }
}
