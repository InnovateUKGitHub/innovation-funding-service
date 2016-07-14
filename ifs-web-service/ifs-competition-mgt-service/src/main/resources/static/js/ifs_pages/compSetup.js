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
