IFS.competitionManagement.setup = (function() {
  "use strict";
  var s;
  var disabledSections = {};
  return {
    init: function() {
      s = this.settings;
      IFS.competitionManagement.setup.handleCompetitionCode();

      jQuery("body.competition-management.competition-setup").on('change', '#competitionTypeId', function() {
        IFS.competitionManagement.setup.handleStateAid();
      });

      IFS.competitionManagement.setup.handleInnovationSector(true);
      jQuery("body.competition-management.competition-setup").on('change', '[name="innovationSectorCategoryId"]', function() {
        IFS.competitionManagement.setup.handleInnovationSector(false);
      });
      // jQuery(".competition-management.competition-setup").on('change', '[name^="innovationAreaCategoryIds"]', function() {
      //   IFS.competitionManagement.setup.changeInnovationAreas(this);
      // });
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
          url: url
        }).done(function(data) {
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
        });
        return false;
      });
    },
    // changeInnovationAreas : function(el) {
    //   var inst = jQuery(el);
    //   var name = inst.prop('name');
    //   var value = inst.val();
    //   disabledSections[name] = value;
    //
    //   disabledSections.each(function(){
    //
    //   });
    // },
    handleInnovationSector : function(pageLoad) {
      var sector = jQuery('[name="innovationSectorCategoryId"]').val();
      if(sector === null){
        var innovationCategory = jQuery('[name^="innovationAreaCategoryIds"]');
        innovationCategory.html('<option value="innovation sector" disabled="disabled" selected="selected">Please select an innovation sector first &hellip;</option>');
      }
      else {
        var url = window.location.protocol + "//" + window.location.host+'/management/competition/setup/getInnovationArea/'+sector;
        jQuery.ajaxProtected({
          type: "GET",
          url: url
        }).done(function(areas) {
          if(pageLoad){
            IFS.competitionManagement.setup.filterInnovationAreasPageLoad(areas);
          }
          else {
            IFS.competitionManagement.setup.fillInnovationAreas(areas);
            jQuery(innovationCategory).trigger('ifsValidate');
            IFS.core.autoSave.fieldChanged('[name="innovationAreaCategoryId"]');
          }
        });
      }
    },
    fillInnovationAreas : function(currentAreas) {
      var innovationAreasFields = jQuery('[name^="innovationAreaCategoryIds"]');
      jQuery.each(innovationAreasFields, function() {
        var innovationAreasField = jQuery(this);
        innovationAreasField.children().remove();
        innovationAreasField.append('<option value="" disabled="disabled" selected="selected">Please select &hellip;</option>');
        jQuery.each(currentAreas, function() {
          innovationAreasField.append('<option value="'+this.id+'">'+this.name+'</option>');
        });
        IFS.core.autoSave.fieldChanged(this);
      });
    },
    filterInnovationAreasPageLoad : function(currentAreas) {
      currentAreas = jQuery.map(currentAreas, function(area) {
        return '[value="'+area.id+'"]';
      });
      currentAreas.push('[value=""]');
      currentAreas = currentAreas.join(',');

      var innovationAreas = jQuery('[name^="innovationAreaCategoryIds"] option');
      innovationAreas.not(currentAreas).remove();
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
    }
  };
})();
