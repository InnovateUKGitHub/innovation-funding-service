//TODO: divide over multiple files with purposes
IFS.competition_management.various = (function(){
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
          assessorFeedbackButton: '#publish-assessor-feedback',
          noJsAssessorFeedbackButton: '#publish-assessor-feedback-no-js',
          submitFundingDecisionButton: '#publish-funding-decision',
          noJsSubmitFundingDecisionButton: '#no-js-notify-applicants',
          noJsSaveFundingDecisionButton: '#no-js-save-funding-decision',
          fundingDecisionForm: '#submit-funding-decision-form'
        },
        init: function(){
            s = this.settings;
            IFS.competition_management.various.getWindowWidth();
            IFS.competition_management.various.getMenuHeight();
            IFS.competition_management.various.getContainerHeight();
            IFS.competition_management.various.handleCompetitionCode();

            jQuery("body.competition-management.competition-setup").on('change','#competitionTypeId',function(){
              IFS.competition_management.various.handleStateAid();
          });
            jQuery("body.competition-management.competition-setup").on('change','[name="innovationSectorCategoryId"]',function(){
              IFS.competition_management.various.handleInnovationSector();
            });

            jQuery(document).on('change', s.fundingDecisionSelects, IFS.competition_management.various.handleFundingDecisionSelectChange);

            IFS.competition_management.various.handleFundingDecisionEnableOrDisable();
            IFS.competition_management.various.alterSubmitDecisionFormAction();

            if(IFS.competition_management.various.stickyEnabled()){
                IFS.competition_management.various.getMenuWidth();
                IFS.competition_management.various.getMenuOffset();
                IFS.competition_management.various.getContainerOffset();
                IFS.competition_management.various.menuPxToPercentage();
            }

            jQuery(window).resize(function(){
              clearTimeout(resizeTimer);
              resizeTimer = setTimeout(function(){
                IFS.competition_management.various.getMenuHeight();
                IFS.competition_management.various.getMenuWidth();
                IFS.competition_management.various.getContainerHeight();
                IFS.competition_management.various.getWindowWidth();
                IFS.competition_management.various.menuPxToPercentage();
                IFS.competition_management.various.stickyScroll();
              },250);
            });
            jQuery(document).scroll(function(){
                IFS.competition_management.various.stickyScroll();
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
          if(IFS.competition_management.various.stickyEnabled()){
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
        },
        handleCompetitionCode : function(){
            jQuery(document).on('click','#generate-code',function(){
                var inst = jQuery(this);
                var day = jQuery('#openingDate .day input').val();
                var month = jQuery('#openingDate .month input').val();
                var year = jQuery('#openingDate .year input').val();
                var formGroup = inst.closest('.form-group');

                if((jQuery('#openingDate').hasClass('error') === false) && day.length && month.length && year.length){
                    formGroup.removeClass('error');
                    formGroup.find("label .error-message").remove();

                    var competitionId = inst.val();
                    var url = window.location.protocol + "//" + window.location.host+'/management/competition/setup/'+competitionId+'/generateCompetitionCode?day='+day+'&month='+month+'&year='+year;
                    //todo ajax failure
                    jQuery.ajaxProtected({
                      type: "GET",
                      url: url,
                      success: function(data) {
                           data = data.replace(/"/g,"");
                          inst.closest('.form-group').find('input').val(data);
                          inst.remove();
                      }
                    });
                }
                else {
                  formGroup.addClass('error');
                  if(formGroup.find('.error-message.correct-date-error-message').length === 0){
                    formGroup.find("label").append('<span class="error-message correct-date-error-message">Please fill in a correct date before generating the competition code</span>');
                  }
                }
            });
        },
        handleInnovationSector : function(){
              var sector = jQuery('[name="innovationSectorCategoryId"]').val();
              if(typeof(sector) !=='undefined'){
                var url = window.location.protocol + "//" + window.location.host+'/management/competition/setup/getInnovationArea/'+sector;
                jQuery.ajaxProtected({
                  type: "GET",
                  url: url,
                  success: function(data) {
                      var innovationCategory = jQuery('[name="innovationAreaCategoryId"]');
                      innovationCategory.children().remove();
                      jQuery.each(data,function(){
                          innovationCategory.append('<option value="'+this.id+'">'+this.name+'</option>');
                      });
                      innovationCategory.trigger('change');
                  }
              });
              }

        },
        handleStateAid : function(){
           var stateAid =  jQuery('#competitionTypeId').find('[value="'+jQuery('#competitionTypeId').val()+'"]').attr('data-stateaid');
           if(stateAid == 'true'){
             stateAid = 'yes';
           }
           else {
             stateAid = 'no';
           }
           jQuery('#stateAid').attr('aria-hidden','false').find('p').html('<span class="'+stateAid+'">'+stateAid+'</span>');
        }
    };
})();
