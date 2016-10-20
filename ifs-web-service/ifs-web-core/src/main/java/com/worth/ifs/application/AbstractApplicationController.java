package com.worth.ifs.application;

import com.worth.ifs.BaseController;
import com.worth.ifs.application.finance.view.FinanceHandler;
import com.worth.ifs.application.finance.view.FinanceOverviewModelManager;
import com.worth.ifs.application.form.ApplicationForm;
import com.worth.ifs.application.form.Form;
import com.worth.ifs.application.model.ApplicationModelPopulator;
import com.worth.ifs.application.model.ApplicationPrintPopulator;
import com.worth.ifs.application.resource.*;
import com.worth.ifs.application.service.*;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.security.UserAuthenticationService;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.filter.CookieFlashMessageFilter;
import com.worth.ifs.form.resource.FormInputResource;
import com.worth.ifs.form.resource.FormInputResponseResource;
import com.worth.ifs.form.service.FormInputResponseService;
import com.worth.ifs.form.service.FormInputService;
import com.worth.ifs.invite.constant.InviteStatus;
import com.worth.ifs.invite.resource.ApplicationInviteResource;
import com.worth.ifs.invite.resource.InviteOrganisationResource;
import com.worth.ifs.invite.service.InviteRestService;
import com.worth.ifs.model.OrganisationDetailsModelPopulator;
import com.worth.ifs.user.resource.OrganisationResource;
import com.worth.ifs.user.resource.ProcessRoleResource;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.service.OrganisationRestService;
import com.worth.ifs.user.service.ProcessRoleService;
import com.worth.ifs.user.service.UserService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.worth.ifs.util.CollectionFunctions.simpleFilter;

/**
 * This object contains shared methods for all the Controllers related to the {@link ApplicationResource} data.
 */
public abstract class AbstractApplicationController extends BaseController {
    public static final String MARK_AS_COMPLETE = "mark_as_complete";
    public static final String MARK_SECTION_AS_COMPLETE = "mark_section_as_complete";
    public static final String SUBMIT_SECTION = "submit-section";
    public static final String MARK_SECTION_AS_INCOMPLETE = "mark_section_as_incomplete";
    public static final String MARK_AS_INCOMPLETE = "mark_as_incomplete";
    public static final String UPLOAD_FILE = "upload_file";
    public static final String REMOVE_UPLOADED_FILE = "remove_uploaded_file";
    public static final String ADD_COST = "add_cost";
    public static final String REMOVE_COST = "remove_cost";
    public static final String EDIT_QUESTION = "edit_question";
    public static final String APPLICATION_FORM = "application-form";
    public static final String MODEL_ATTRIBUTE_FORM = "form";
    public static final String QUESTION_ID = "questionId";
    public static final String APPLICATION_ID = "applicationId";
    public static final String APPLICATION_BASE_URL = "/application/";

    public static final String ASSIGN_QUESTION_PARAM = "assign_question";
    public static final String FORM_MODEL_ATTRIBUTE = "form";
    public static final String APPLICATION_START_DATE = "application.startDate";
    public static final String QUESTION_URL = "/question/";
    public static final String SECTION_URL = "/section/";

    public static final String TERMS_AGREED_KEY = "termsAgreed";
    public static final String STATE_AID_AGREED_KEY = "stateAidAgreed";


    @Autowired
    protected UserAuthenticationService userAuthenticationService;

    @Autowired
    protected QuestionService questionService;

    @Autowired
    protected ProcessRoleService processRoleService;

    @Autowired
    protected ApplicationService applicationService;

    @Autowired
    protected CompetitionService competitionService;

    @Autowired
    protected SectionService sectionService;

    @Autowired
    protected ApplicationModelPopulator applicationModelPopulator;

    @Autowired
    protected FormInputResponseService formInputResponseService;

    @Autowired
    protected CookieFlashMessageFilter cookieFlashMessageFilter;

    @Autowired
    protected OrganisationDetailsModelPopulator organisationDetailsModelPopulator;

    @Autowired
    protected ApplicationPrintPopulator applicationPrintPopulator;

    @Autowired
    protected FormInputService formInputService;

}
