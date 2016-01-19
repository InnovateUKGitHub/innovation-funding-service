//very specific piece of code for having a organisation size change make adjustments to the max attribute for the funding level
//After that we re-evaluate that value with the validation
IFS.orgsizeToFunding = (function(){
    "use strict";
    var s;
    return {
      settings : {
          funderOrgsizeInputs : '[name="financePosition-organisationSize"]',
          fundingLevelInput : '#cost-financegrantclaim',
          orgToFundingMapping :  {
              'SMALL' : 70,
              'MEDIUM' : 60,
              'LARGE' : 50
          }
      },
      init : function(){
          s = this.settings;
          jQuery('body').on('change',s.funderOrgsizeInputs, function(){
              var orgSize = jQuery(this).val();
              var funingLevelEl = jQuery(s.fundingLevelInput);
              if(funingLevelEl.length && (typeof(s.orgToFundingMapping[orgSize]) !== 'undefined')){
                  funingLevelEl.attr('max',s.orgToFundingMapping[orgSize]).removeClass('field-error');

                  var formGroup = funingLevelEl.closest('.form-group');
                  formGroup.removeClass('error');
                  formGroup.find('.error-message').remove();
                  funingLevelEl.trigger('change');
              }
          });
      }
    };
})();
