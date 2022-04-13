package org.innovateuk.ifs.application.forms.questions.horizon;

import org.innovateuk.ifs.application.forms.questions.horizon.model.HorizonWorkProgrammeSelectionData;
import org.innovateuk.ifs.util.EncryptedCookieService;
import org.innovateuk.ifs.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

import static org.innovateuk.ifs.util.JsonUtil.getObjectFromJson;

@Service
public class HorizonWorkProgrammeCookieService {

    public static final String HORIZON_SELECTION_DATA = "horizonSelectionData";

    @Autowired
    private EncryptedCookieService encryptedCookieService;

    public void saveWorkProgrammeSelectionData(HorizonWorkProgrammeSelectionData data, HttpServletResponse response){
        encryptedCookieService.saveToCookie(response, HORIZON_SELECTION_DATA, JsonUtil.getSerializedObject(data));
    }

    public Optional<HorizonWorkProgrammeSelectionData> getHorizonWorkProgrammeSelectionData(HttpServletRequest request){
        return Optional.ofNullable(getObjectFromJson(encryptedCookieService.getCookieValue(request, HORIZON_SELECTION_DATA), HorizonWorkProgrammeSelectionData.class));
    }

    public void deleteWorkProgrammeSelectionData(HttpServletResponse response){
        encryptedCookieService.removeCookie(response, HORIZON_SELECTION_DATA);
    }
}
