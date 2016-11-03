// Toggle the required attribute on fields depending on the user input
IFS.assessment.conditionallyRequired = (function() {
  'use strict';

  return {
    init: function() {
      jQuery('body').on('click', '[data-conditionally-add-required]', function() {

        IFS.assessment.conditionallyRequired.addRequired(this);
      });
      jQuery('body').on('click', '[data-conditionally-remove-required]', function() {

        IFS.assessment.conditionallyRequired.removeRequired(this);
      });
    },
    addRequired : function(el) {
      var targets = jQuery(el).attr('data-conditionally-add-required');

      jQuery(targets).each(function() {
        jQuery(this).attr('required', 'required');
      });
    },
    removeRequired : function(el) {
      var targets = jQuery(el).attr('data-conditionally-remove-required');

      jQuery(targets).each(function() {
        var inst = jQuery(this);
        var parentGroup = inst.closest('.form-group');

        inst.removeProp('required').removeClass('field-error');

        //remove any existing error messages and classes
        parentGroup.removeClass('error');
        parentGroup.find('.error-message').remove();
      });
    }
  };
})();
