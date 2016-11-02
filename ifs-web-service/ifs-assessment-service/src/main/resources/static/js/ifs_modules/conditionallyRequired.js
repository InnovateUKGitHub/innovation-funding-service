// Toggle the required attribute on fields depending on the user input
IFS.assessment.conditionallyRequired = (function() {
  'use strict';

  return {
    init: function() {
      jQuery('body').on('click', '[data-conditionally-add-required]', function(e) {

        IFS.assessment.conditionallyRequired.addRequired(this, e);
      });
      jQuery('body').on('click', '[data-conditionally-remove-required]', function(e) {

        IFS.assessment.conditionallyRequired.removeRequired(this, e);
      });
    },
    addRequired : function(el) {
      //allow for multiple targets to be given in comma separated string
      var targets = jQuery(el).attr('data-conditionally-add-required').split(', ');

      $(targets).each(function() {
        jQuery(this).attr('required', 'required');
      });
    },
    removeRequired : function(el) {
      //allow for multiple targets to be given in comma separated string
      var targets = jQuery(el).attr('data-conditionally-remove-required').split(', ');

      $(targets).each(function() {
        var parentGroup = jQuery(this).closest('.form-group');

        jQuery(this).removeAttr('required').removeClass('field-error');

        //remove any existing error messages and classes
        jQuery(parentGroup).find('.error-message').remove();
        jQuery(parentGroup).removeClass('error');
      });
    }
  };
})();
