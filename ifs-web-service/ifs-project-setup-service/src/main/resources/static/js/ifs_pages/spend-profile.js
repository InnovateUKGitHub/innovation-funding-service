IFS.projectSetup.spendProfile = (function() {
  "use strict";
  var s; // private alias to settings
  return {
    settings : {
      totalElement : 'input[id*=row-total-],#spend-profile-total-total',
      message : 'Your total costs are higher than your eligible costs'
    },
    init : function() {
      s = this.settings;
      jQuery('body').on('change', s.totalElement, function() {
        IFS.projectSetup.spendProfile.checkSpend(this);
      });
    },
    checkSpend : function(el) {
      var inst = jQuery(el);

      var totalTd = inst.closest('td');
      var currentTotal = parseInt(inst.attr('data-calculation-rawvalue'), 10);
      var eligibleTotal = parseInt(totalTd.next().find('input').attr('data-calculation-rawvalue'), 10);
      if(currentTotal > eligibleTotal){
        totalTd.addClass('cell-error');
        IFS.core.formValidation.setInvalid(inst, s.message);
      }
      else {
        totalTd.removeClass('cell-error');
        IFS.core.formValidation.setValid(inst, s.message);
      }
    }
  };
})();
