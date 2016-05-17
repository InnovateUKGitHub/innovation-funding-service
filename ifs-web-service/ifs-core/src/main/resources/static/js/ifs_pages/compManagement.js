//Brent: Did some investigation what this does as it wasn't clear to me
//Does Hide and show textarea and some basic data validation
//Hide and show of form elements are already within the gov.uk js and client side validation is nice but we should cover that more generic.
IFS.competition_management = (function(){
    "use strict";
    var calculatedValues = {};
    var resizeTimer;
    var s;
    return {
        settings: {
          menu : jQuery('.info-area'),
          container : '.competition-data form',
          breakpoint : 1200, //px
          fundingDecisionSelects: '.funding-decision',
          submitFundingDecisionButton: '#publish-funding-decision',
          noJsSubmitFundingDecisionButton: '#no-js-notify-applicants',
          noJsSaveFundingDecisionButton: '#no-js-save-funding-decision',
          fundingDecisionForm: '#submit-funding-decision-form'
        },
        init: function(){
            s = this.settings;
            IFS.competition_management.getWindowWidth();
            IFS.competition_management.getMenuHeight();
            IFS.competition_management.getContainerHeight();

            jQuery(document).on('change', s.fundingDecisionSelects, IFS.competition_management.handleFundingDecisionSelectChange);

            IFS.competition_management.handleFundingDecisionEnableOrDisable();
            IFS.competition_management.handleFundingDecisionButtons();
            IFS.competition_management.alterSubmitDecisionFormAction();

            if(IFS.competition_management.stickyEnabled()){
                IFS.competition_management.getMenuWidth();
                IFS.competition_management.getMenuOffset();
                IFS.competition_management.getContainerOffset();
                IFS.competition_management.menuPxToPercentage();
            }

            jQuery(window).resize(function(){
              clearTimeout(resizeTimer);
              resizeTimer = setTimeout(function(){
                IFS.competition_management.getMenuHeight();
                IFS.competition_management.getMenuWidth();
                IFS.competition_management.getContainerHeight();
                IFS.competition_management.getWindowWidth();
                IFS.competition_management.menuPxToPercentage();
                IFS.competition_management.stickyScroll();
              },250);
            });
            jQuery(document).scroll(function(){
                IFS.competition_management.stickyScroll();
            });
        },
        getWindowWidth: function(){
           calculatedValues.windowWidth = jQuery(window).width();
        },
        getMenuOffset : function(){
          calculatedValues.menuOffsetTop = s.menu.offset().top;
        },
        getMenuWidth: function(){
          calculatedValues.menuWidth = s.menu.width();
        },
        getMenuHeight : function(){
          calculatedValues.menuHeight = s.menu.outerHeight();
        },
        getContainerHeight : function(){
          calculatedValues.containerHeight = jQuery(s.container).outerHeight(true);
        },
        getContainerOffset : function(){
          calculatedValues.containerOffsetTop = jQuery(s.container).offset().top;
        },
        stickyEnabled: function(){
          //not responsively disabled or higher menu than container
          if((s.breakpoint < calculatedValues.windowWidth) && (calculatedValues.menuHeight < calculatedValues.containerHeight)){
            return true;
          }
          return false;
        },
        menuPxToPercentage : function(){
          calculatedValues.menuPercentage =  parseFloat((calculatedValues.menuWidth/calculatedValues.windowWidth)*100).toFixed(2);
        },
        stickyScroll : function(){
          if(IFS.competition_management.stickyEnabled()){
            var scroll = jQuery(document).scrollTop();
            if((calculatedValues.menuHeight+scroll) > (calculatedValues.containerHeight+calculatedValues.containerOffsetTop)){
                var top = (calculatedValues.containerHeight+calculatedValues.containerOffsetTop)-calculatedValues.menuHeight;
                s.menu.addClass('bottom').removeClass('sticky').css({'top':top+'px','width':calculatedValues.menuPercentage+'%'});
            }
            else if(calculatedValues.menuOffsetTop < scroll) {
               s.menu.addClass('sticky').css({'top':'0','width':calculatedValues.menuPercentage+'%'}).removeClass('bottom');
            }
            else {
              s.menu.removeClass('sticky bottom').removeAttr('style');
            }
          }
          else {
            s.menu.removeClass('sticky bottom').removeAttr('style');
          }
        },
        handleFundingDecisionButtons: function(){
        	var button = jQuery(s.submitFundingDecisionButton);
        	var noJsButton = jQuery(s.noJsSubmitFundingDecisionButton);
        	var noJsSaveButton = jQuery(s.noJsSaveFundingDecisionButton);
        	noJsButton.hide();
        	noJsSaveButton.hide();
        	button.show();
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
        	
        	IFS.competition_management.handleFundingDecisionEnableOrDisable();
        	
        	var element = jQuery(this);
        	var applicationId = element.attr('name');
        	var competitionId = element.attr('competition');
        	var value = element.val();
        	
        	IFS.competition_management.saveFundingDecision(competitionId, applicationId, value);
        },
        handleFundingDecisionEnableOrDisable: function() {
        	if(IFS.competition_management.allSelectsDecided()){
                IFS.competition_management.enableFundingDecisionButton();
        	} else {
                IFS.competition_management.disableFundingDecisonButton();
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
