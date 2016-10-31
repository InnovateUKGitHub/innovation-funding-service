IFS.core.disableSubmitUntilChecked = (function() {
  "use strict";
  var s;
  return {
    settings : {
      checkBoxesAttribute : 'data-disable-button-until-checked'
    },
    init : function() {
      s = this.settings;
      jQuery('body').on('change', '['+s.checkBoxesAttribute+']', function() {
        IFS.core.disableSubmitUntilChecked.checkButtonStates(this);
      });
      jQuery('['+s.checkBoxesAttribute+']').each(function() {
        IFS.core.disableSubmitUntilChecked.checkButtonStates(this);
      });
    },
    checkButtonStates : function(el) {
      var button = jQuery(el).attr(s.checkBoxesAttribute);
      if(jQuery(button).length){
        var allChecked = IFS.core.disableSubmitUntilChecked.checkAllChecked(button);
        IFS.core.disableSubmitUntilChecked.updateButton(button, allChecked);
      }
    },
    checkAllChecked : function(submitButton) {
      //we loop over all checkboxes which have the same attribute,
      //if all if them are checked it is true
      var allChecked = true;
      jQuery('['+s.checkBoxesAttribute+'="'+submitButton+'"]').each(function() {
        if(jQuery(this).prop('checked') === false){
          allChecked = false;
        }
      });
      return allChecked;
    },
    updateButton : function(button, state) {
      if(state === true){
        jQuery(button).removeAttr('aria-disabled').removeClass('disabled').prop("disabled", false);
      }
      else {
        jQuery(button).attr({'aria-disabled': 'true'}).addClass('disabled').prop("disabled", true);
      }
    }
  };
})();
