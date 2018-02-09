package org.innovateuk.ifs.file.service;

import org.apache.commons.lang3.StringUtils;
import org.innovateuk.ifs.file.resource.FileTypeCategories;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.transactional.FormInputService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.util.CollectionFunctions.flattenLists;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * Generates a set of MediaTypes based on a {@link org.innovateuk.ifs.form.domain.FormInput} id (and its
 * "allowedFileTypes" field)
 */
@Component
public class ByFormInputMediaTypesGenerator implements MediaTypesGenerator<Long> {

    private ByMediaTypeStringsMediaTypesGenerator byStringGenerator = new ByMediaTypeStringsMediaTypesGenerator();

    @Autowired
    private FormInputService formInputService;

    @Override
    public List<MediaType> apply(Long formInputId) {

        FormInputResource formInput = formInputService.findFormInput(formInputId).getSuccess();

        List<FileTypeCategories> fileTypeCategories = getAllowableFileTypeCategoriesFromFormInput(formInput);

        List<String> mediaTypesStrings = flattenLists(simpleMap(fileTypeCategories, FileTypeCategories::getMediaTypes));
        return byStringGenerator.apply(mediaTypesStrings);
    }

    private List<FileTypeCategories> getAllowableFileTypeCategoriesFromFormInput(FormInputResource formInput) {

        if (StringUtils.isEmpty(formInput.getAllowedFileTypes())) {
            return emptyList();
        } else {
            return simpleMap(formInput.getAllowedFileTypes().split(","), FileTypeCategories::fromDisplayName);
        }
    }
}
