IFS.competitionManagement.setup = (function() {
  "use strict";
  var s;
  return {
    settings: {
      milestonesForm : '[data-section="milestones"]'
    },
    init: function() {
      s = this.settings;
      IFS.competitionManagement.setup.handleCompetitionCode();

      jQuery("body.competition-management.competition-setup").on('change', '#competitionTypeId', function() {
        IFS.competitionManagement.setup.handleStateAid();
      });
      jQuery("body.competition-management.competition-setup").on('change', '[name="innovationSectorCategoryId"]', function() {
        IFS.competitionManagement.setup.handleInnovationSector(false);
      });
      IFS.competitionManagement.setup.innovationSectorOnPageLoad();

      jQuery(s.milestonesForm).on('change', 'input[data-date]', function() {
        IFS.competitionManagement.setup.milestonesExtraValidation();
        IFS.competitionManagement.setup.milestonesSetFutureDate(jQuery(this));
      });
      IFS.competitionManagement.setup.mileStoneValidateOnPageLoad();

    },
    handleCompetitionCode : function() {
      jQuery(document).on('click', '#generate-code', function() {
        var button = jQuery(this);
        var competitionId = button.val();
        var field = button.closest('.form-group').find('input');
        var url = window.location.protocol + "//" + window.location.host+'/management/competition/setup/'+competitionId+'/generateCompetitionCode';
        //todo ajax failure
        jQuery.ajaxProtected({
          type: "GET",
          url: url,
          success: function(data) {
            if(typeof(data) !== 'undefined'){
              if(data.success === "true"){
                IFS.core.formValidation.setValid(field, IFS.core.formValidation.getErrorMessage(field, 'required'));
                field.val(data.message);
                jQuery('body').trigger('updateSerializedFormState');
              }
              else {
                IFS.core.formValidation.setInvalid(field, data.message);
              }
            }
          }
        });
        return false;
      });
    },
    handleInnovationSector : function(pageLoad) {
      var sector = jQuery('[name="innovationSectorCategoryId"]').val();
      var innovationCategorySelected = jQuery('[name="innovationAreaCategoryId"]').val();
      if(typeof(sector) !=='undefined'){
        var url = window.location.protocol + "//" + window.location.host+'/management/competition/setup/getInnovationArea/'+sector;
        jQuery.ajaxProtected({
          type: "GET",
          url: url,
          success: function(data) {
            var innovationCategory = jQuery('[name="innovationAreaCategoryId"]');
            innovationCategory.children().remove();
            jQuery.each(data, function() {
              if(this.id == innovationCategorySelected) {
                innovationCategory.append('<option selected="selected" value="'+this.id+'">'+this.name+'</option>');
              } else {
                innovationCategory.append('<option value="'+this.id+'">'+this.name+'</option>');
              }
            });
            if(!pageLoad) {
              innovationCategory.prepend('<option value="" disabled="disabled" selected="disabled">Please select...</option>');
              jQuery(innovationCategory).trigger('ifsValidate');
              IFS.core.autoSave.fieldChanged('[name="innovationSectorCategoryId"]');
              IFS.core.autoSave.fieldChanged('[name="innovationAreaCategoryId"]');
            }
          }
        });
      }
    },
    innovationSectorOnPageLoad : function() {
      var sectorInput = jQuery('[name="innovationSectorCategoryId"]');
      var sector = sectorInput.val();
      if (sectorInput.length) {
        if (!sector) {
          var innovationCategory = jQuery('[name="innovationAreaCategoryId"]');
          innovationCategory.children().remove();
          innovationCategory.append('<option value="innovation sector" disabled="disabled" selected="selected">Please select an innovation sector first &hellip;</option>');
        } else {
          IFS.competitionManagement.setup.handleInnovationSector(true);
        }
      }
    },
    handleStateAid : function() {
      var stateAid =  jQuery('#competitionTypeId').find('[value="'+jQuery('#competitionTypeId').val()+'"]').attr('data-stateaid');
      if(stateAid == 'true'){
        stateAid = 'yes';
      }
      else {
        stateAid = 'no';
      }
      jQuery('#stateAid').attr('aria-hidden', 'false').find('p').html('<span class="'+stateAid+'">'+stateAid+'</span>');
    },
    milestonesExtraValidation : function() {
      //some extra javascript to hide the server side messages when the field is valid
      var fieldErrors = jQuery(s.milestonesForm+' .field-error');
      var emptyInputs = jQuery(s.milestonesForm+' input').filter(function() { return !this.value; });
      if(fieldErrors.length === 0 && emptyInputs.length === 0){
        jQuery(s.milestonesForm+' .error-summary').attr('aria-hidden', 'true');
      }
    },
    mileStoneValidateOnPageLoad : function() {
      jQuery(s.milestonesForm+' .day input').each(function(index, value) {
        var field = jQuery(value);
        if(index===0){
          IFS.core.formValidation.checkDate(field, true);
        }
        IFS.competitionManagement.setup.milestonesSetFutureDate(field);
      });
    },
    milestonesSetFutureDate : function(field) {
      setTimeout(function() {
        var nextRow = field.closest('tr').next('tr');
        var date = field.attr('data-date');
        if(nextRow.length){
          nextRow.attr({'data-future-date':date});
          if(jQuery.trim(date.length) !== 0){
            var input = nextRow.find('.day input');
            IFS.core.formValidation.checkDate(input, true);
          }
        }
      }, 0);
    }
  };
})();
