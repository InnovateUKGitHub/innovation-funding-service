IFS.application.application_summary = (function(){
    "use strict";
    var s;
    return {
        settings : {
            submitButton : '#submit-application-button',
            checkbox : '#agree-terms-page'
        },
        init: function(){
          s =this.settings;
          IFS.application.application_summary.handleChangeAgreeTermsCheckbox();
          jQuery(document).on('change', s.checkbox, IFS.application.application_summary.handleChangeAgreeTermsCheckbox);
        },
        disableButton : function(){
          //add aria-disabled for the modal button and disabled styling
          jQuery(s.submitButton).attr({'aria-disabled': 'true'}).addClass('disabled');
        },
        enableButton : function(){
          jQuery(s.submitButton).removeAttr('aria-disabled').removeClass('disabled');
        },
        handleChangeAgreeTermsCheckbox: function() {
        	if(jQuery(s.checkbox).prop("checked")) {
                IFS.application.application_summary.enableButton();
        	} else {
                IFS.application.application_summary.disableButton();
        	}
        }
    };
})();
