// Handlers for single section refreshing when assigning questions to users on the Application Overview
// dependency on ifs_collpasible
IFS.application_summary = (function(){
    "use strict";
    
    return {
        init: function(){
        	jQuery(document).find("#agree-to-terms-container").show();
        	jQuery(document).find('#submit-application-button').hide();
        	jQuery(document).find('#disabled-submit-application-button').show();
        	jQuery(document).find('#agree-terms').attr('checked', false);
        	jQuery(document).on('change', '#agree-terms', IFS.application_summary.handleChangeAgreeTermsCheckbox);
        },
        handleChangeAgreeTermsCheckbox: function() {
        	var checkbox = jQuery(this);
        	var submitButton = jQuery(document).find('#submit-application-button');
        	var dummyButton = jQuery(document).find('#disabled-submit-application-button');
        	if(checkbox.is(":checked")) {
        		submitButton.show();
        		dummyButton.hide();
        	} else {
        		submitButton.hide();
        		dummyButton.show();
        	}
        }
    };
})();


