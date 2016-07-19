IFS.competition_management.setup = (function(){
  "use strict";
  var s;
  return {
    settings: {

    },
    init: function(){
        s = this.settings;
        IFS.competition_management.setup.handleCompetitionCode();

        jQuery("body.competition-management.competition-setup").on('change','#competitionTypeId',function(){
          IFS.competition_management.setup.handleStateAid();
        });
        jQuery("body.competition-management.competition-setup").on('change','[name="innovationSectorCategoryId"]',function(){
          IFS.competition_management.setup.handleInnovationSector();
        });
    },
    handleCompetitionCode : function(){
        jQuery(document).on('click','#generate-code',function(){
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
                    IFS.core.formValidation.setValid(field,data.message);
                    field.val(data);
                  }
                  else {
                    IFS.core.formValidation.setInvalid(field,data.message);
                  }
                }
              }
            });
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
