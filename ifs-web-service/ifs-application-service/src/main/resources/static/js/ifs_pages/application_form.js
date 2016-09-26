IFS.application.application_form = (function(){
    "use strict";
    var s;
    return {
        settings : {
            markAllAsCompleteButton : '#mark-all-as-complete',
            termsCheckbox : '#agree-terms-page',
            stateAidCheckbox : '#agree-state-aid-page'
        },
        init: function(){
          s =this.settings;
          IFS.application.application_form.handleChangeAgreeTermsCheckbox();
          jQuery(document).on('change', s.checkbox, IFS.application.application_form.handleChangeAgreeTermsCheckbox);
        },
        disableButton : function(){
          //add aria-disabled for the modal button and disabled styling
          jQuery(s.markAllAsCompleteButton).attr({'aria-disabled': 'true'}).addClass('disabled');
          jQuery(s.markAllAsCompleteButton).prop("disabled", true);
        },
        enableButton : function(){
          jQuery(s.markAllAsCompleteButton).removeAttr('aria-disabled').removeClass('disabled');
          jQuery(s.markAllAsCompleteButton).prop("disabled", false);
        },
        handleChangeAgreeTermsCheckbox: function() {
        	if(jQuery(s.termsCheckbox).prop("checked") && jQuery(s.stateAidCheckbox).prop("checked")) {
            IFS.application.application_form.enableButton();
        	} else {
            IFS.application.application_form.disableButton();
        	}
        }
    };
})();
