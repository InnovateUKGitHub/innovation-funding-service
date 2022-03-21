package org.innovateuk.ifs.application.forms.questions.heukar;

import org.innovateuk.ifs.application.forms.questions.heukar.model.HeukarProjectLocationSelectionData;
import org.innovateuk.ifs.util.EncryptedCookieService;
import org.innovateuk.ifs.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

import static org.innovateuk.ifs.util.JsonUtil.getObjectFromJson;

@Service
public class HeukarProjectLocationCookieService {

    public static final String APPLICATION_ID = "applicationId";
    public static final String PARENT_LOCATIONS = "parentLocations";
    public static final String ENGLAND_LOCATIONS = "englandLocations";
    public static final String OVERSEAS_LOCATIONS = "overseasLocations";
    public static final String CROWN_DEPENDENCY_LOCATIONS = "crownDependencyLocations";
    public static final String HEUKAR_SELECTION_DATA = "heukarSelectionData";

    @Autowired
    private EncryptedCookieService encryptedCookieService;

    public void saveProjectLocationSelectionData(HeukarProjectLocationSelectionData data, HttpServletResponse response){
        encryptedCookieService.saveToCookie(response, HEUKAR_SELECTION_DATA, JsonUtil.getSerializedObject(data));
    }

    public Optional<HeukarProjectLocationSelectionData> getProjectLocationSelectionData(HttpServletRequest request){
        return Optional.ofNullable(getObjectFromJson(encryptedCookieService.getCookieValue(request, HEUKAR_SELECTION_DATA), HeukarProjectLocationSelectionData.class));
    }

    public void deleteProjectLocationSelectionData(HttpServletResponse response){
        encryptedCookieService.removeCookie(response, HEUKAR_SELECTION_DATA);
    }
}
