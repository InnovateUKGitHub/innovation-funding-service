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

      IFS.competitionManagement.setup.handleAddCoFunder();
      IFS.competitionManagement.setup.handleRemoveCoFunder();

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
    handleAddCoFunder: function() {
      jQuery(document).on('click', '#add-cofunder', function() {
        var count = parseInt(jQuery('#co-funder-count').val(), 10);
        jQuery('<div class="grid-row funder-row" id="funder-row-'+ count +'"><div class="column-half"><div class="form-group"><input type="text" maxlength="255" data-maxlength-errormessage="Funders has a maximum length of 255 characters" class="form-control width-x-large" id="' + count +'-funder" name="funders['+ count +'].funder" value=""></div></div>' +
        '<div class="column-half"><div class="form-group"><input type="number" min="0" class="form-control width-x-large" id="' + count +'-funderBudget" name="funders['+ count +'].funderBudget" value=""><input required="required" type="hidden" id="' + count +'-coFunder" name="funders['+ count +'].coFunder" value="true">' +
        '<button class="buttonlink remove-funder" name="remove-funder" value="'+ count +'" id="remove-funder-'+ count +'">Remove</button></div></div></div>')
        .insertBefore('#dynamic-row-pointer');

        jQuery('#co-funder-count').val(count + 1);
        return false;
      });
    },
    handleRemoveCoFunder: function() {
      jQuery(document).on('click', '.remove-funder', function() {
        var $this = jQuery(this),
        index = $this.val(),
        funderRow = $this.closest('.funder-row'),
        count = parseInt(jQuery('#co-funder-count').val(), 10);

        jQuery('[name="removeFunder"]').val(index);
        IFS.core.autoSave.fieldChanged('[name="removeFunder"]');
        funderRow.remove();
        jQuery('#co-funder-count').val(count - 1);
        IFS.competitionManagement.setup.reindexFunderRows();
        //Force recalculation of the total.
        jQuery('body').trigger('recalculateAllFinances');
        return false;
      });
    },
    reindexFunderRows: function() {
      jQuery('[name*="funders"]').each(function() {
        var $this = jQuery(this),
        thisIndex = $this.closest('.funder-row').index('.funder-row'),
        oldAttr = $this.attr('name'),
        newAttr = oldAttr.replace(/funders\[\d\]/, 'funders[' +thisIndex+ ']');

        $this.attr('name', newAttr);
      });
      jQuery('button.remove-funder').each(function() {
        var $this = jQuery(this),
        thisIndex = $this.closest('.funder-row').index('.funder-row');

        $this.val(thisIndex);
      });
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
