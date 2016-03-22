IFS.application_summary = (function(){
    "use strict";
    var s;
    return {
        settings : {
            submitButton : '#submit-application-button',
            checkbox : '#agree-terms-page'
        },
        init: function(){
          s =this.settings;
          IFS.application_summary.handleChangeAgreeTermsCheckbox();
          jQuery(document).on('change', s.checkbox, IFS.application_summary.handleChangeAgreeTermsCheckbox);
          //if the submitbutton is disabled we prevent the default href action
          jQuery('body').on('click',s.submitButton+'[aria-disabled]',function(e){ e.preventDefault();});
        },
        disableButton : function(){
           var button = jQuery(s.submitButton);
           var modal = button.attr('data-js-modal');
           //remove the modal action and add aria-disabled and disabled styling
           button.removeAttr('data-js-modal').attr({'data-js-modal-disabled':modal, 'aria-disabled': 'true'}).addClass('disabled');
        },
        enableButton : function(){
            var button = jQuery(s.submitButton);
            var modal = button.attr('data-js-modal-disabled');
            button.removeAttr('data-js-modal-disabled aria-disabled').attr('data-js-modal',modal).removeClass('disabled');
        },
        handleChangeAgreeTermsCheckbox: function() {
        	if(jQuery(s.checkbox).prop("checked")) {
            IFS.application_summary.enableButton();
        	} else {
            IFS.application_summary.disableButton();
        	}
        }
    };
})();
