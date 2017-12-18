package org.innovateuk.ifs.file.transactional;

import org.apache.commons.lang3.StringUtils;
import org.innovateuk.ifs.file.resource.FileTypeCategories;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.transactional.FormInputService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.innovateuk.ifs.util.CollectionFunctions.flattenLists;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * Generates a set of MediaTypes based on a FormInput (and its "allowedFileTypes" field)
 */
@Component
public class ByFormInputMediaTypesGenerator implements MediaTypesGenerator<Long> {

    private ByMediaTypeStringsMediaTypesGenerator byStringGenerator = new ByMediaTypeStringsMediaTypesGenerator();

    @Autowired
    private FormInputService formInputService;

    @Override
    public List<MediaType> apply(Long formInputId) {

        FormInputResource formInput = formInputService.findFormInput(formInputId).getSuccessObjectOrThrowException();

        List<FileTypeCategories> fileTypeCategories = simpleMap(formInput.getAllowedFileTypes().split(","), FileTypeCategories::valueOf);
        List<String> mediaTypesStrings = flattenLists(simpleMap(fileTypeCategories, this::getMediaTypesListFromCategory));
        return byStringGenerator.apply(mediaTypesStrings);
    }

    private List<String> getMediaTypesListFromCategory(FileTypeCategories cat) {
        String[] individualMediaTypes = cat.getMediaTypeString().split(",");
        return simpleMap(individualMediaTypes, StringUtils::trim);
    }
}
