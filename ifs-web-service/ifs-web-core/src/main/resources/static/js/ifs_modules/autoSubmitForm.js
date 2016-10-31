IFS.core.autoSubmitForm = (function() {
  "use strict";
  var s; // private alias to settings
  return {
    settings : {
      submitElements : '.js-auto-submit'
    },
    init : function() {
      s = this.settings;
      //hide non-js button
      jQuery(s.submitElements).next('button').addClass('visuallyhidden'); //keep available for screenreaders
      jQuery(document).on('change', s.submitElements, function() {
        IFS.core.autoSubmitForm.submitForm(this);
      });
    },
    submitForm : function(el) {
      jQuery(el).closest('form').submit();
    }
  };
})();
