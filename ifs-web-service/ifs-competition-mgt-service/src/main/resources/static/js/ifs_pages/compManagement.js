//TODO: divide over multiple files with purposes
IFS.competition_management.various = (function(){
    "use strict";
    var s;
    return {
        settings: {
          fundingDecisionSelects: '.funding-decision',
          assessorFeedbackButton: '#publish-assessor-feedback',
          noJsAssessorFeedbackButton: '#publish-assessor-feedback-no-js',
          submitFundingDecisionButton: '#publish-funding-decision',
          noJsSubmitFundingDecisionButton: '#no-js-notify-applicants',
          noJsSaveFundingDecisionButton: '#no-js-save-funding-decision',
          fundingDecisionForm: '#submit-funding-decision-form'
        },
        init: function(){
            s = this.settings;

            jQuery(document).on('change', s.fundingDecisionSelects, IFS.competition_management.various.handleFundingDecisionSelectChange);
            IFS.competition_management.various.handleFundingDecisionEnableOrDisable();
            IFS.competition_management.various.alterSubmitDecisionFormAction();
        },
        disableFundingDecisonButton : function(){
            var button = jQuery(s.submitFundingDecisionButton);
            var modal = button.attr('data-js-modal');
            //remove the modal action and add aria-disabled and disabled styling
            button.on('click',function(event){ event.preventDefault(); });
            button.removeAttr('data-js-modal').attr({'data-js-modal-disabled':modal, 'aria-disabled': 'true'}).addClass('disabled');
         },
         enableFundingDecisionButton : function(){
             var button = jQuery(s.submitFundingDecisionButton);
             var modal = button.attr('data-js-modal-disabled');
             button.off('click').removeAttr('data-js-modal-disabled aria-disabled').attr('data-js-modal',modal).removeClass('disabled');
         },
         alterSubmitDecisionFormAction: function(){
        	 var form = jQuery(s.fundingDecisionForm);
        	 var action = form.attr('action');
        	 form.attr('action', action + 'submit');
         },
         allSelectsDecided: function() {
        	 var selects = jQuery(s.fundingDecisionSelects);
        	 if(selects === null || selects === undefined || selects.length === 0) {
        		 return false;
        	 }

        	 var allDecided = true;
        	 selects.each(function() {
        		 var value = jQuery(this).val();
        		 var decided = value === 'Y' || value === 'N';
        		 if(!decided) {
        			 allDecided = false;
        		 }
        	 });

        	 return allDecided;
         },
        handleFundingDecisionSelectChange: function(){

        	IFS.competition_management.various.handleFundingDecisionEnableOrDisable();

        	var element = jQuery(this);
        	var applicationId = element.attr('name');
        	var competitionId = element.attr('competition');
        	var value = element.val();

        	IFS.competition_management.various.saveFundingDecision(competitionId, applicationId, value);
        },
        handleFundingDecisionEnableOrDisable: function() {
        	if(IFS.competition_management.various.allSelectsDecided()){
                IFS.competition_management.various.enableFundingDecisionButton();
        	} else {
                IFS.competition_management.various.disableFundingDecisonButton();
        	}
        },
        saveFundingDecision: function(competitionId, applicationId, value) {

        	var saveInfo = jQuery('#funding-decision-save-info-' + applicationId);

        	jQuery.ajaxProtected({
                 type: 'POST',
                 url: '/management/funding/' + competitionId,
                 data: {
                	 applicationId: applicationId,
                	 fundingDecision: value
                 },
                 dataType: "json",
                 beforeSend: function() {
                     saveInfo.html('Saving...');
                 }
             }).done(function(data){
            	 if(data.success == 'true') {
            		 saveInfo.html('Saved!');
            	 } else {
            		 saveInfo.html('Not saved.');
            	 }
             }).error(function(){
            	 saveInfo.html('Not saved.');
             });
        }
    };
})();
