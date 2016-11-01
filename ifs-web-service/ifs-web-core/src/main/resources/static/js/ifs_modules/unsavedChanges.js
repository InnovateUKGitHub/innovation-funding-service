// save the current form state, so we can warn the user if he leaves the page without saving.
IFS.core.unsavedChanges = (function() {
  "use strict";
  var s;
  return {
    settings : {
      formelement : '[data-autosave]'
    },
    init : function() {
      s = this.settings;
      if(jQuery(s.formelement).length){
        IFS.core.unsavedChanges.initUnsavedChangesWarning();
        IFS.core.unsavedChanges.updateSerializedFormState();

        jQuery('body').on('updateSerializedFormState', function() {
          IFS.core.unsavedChanges.updateSerializedFormState();
        });
      }
    },
    updateSerializedFormState : function() {
      var FormEl = jQuery(s.formelement);
      FormEl.data('serializedFormState', FormEl.serialize());
    },
    initUnsavedChangesWarning : function() {
      // don't show the warning when the user is submitting the form.
      var formSubmit = false;
      jQuery('body').on(s.formelement, 'submit', function() {
        formSubmit = true;
      });

      jQuery(window).bind('beforeunload', function(e) {
        var formEl = jQuery(s.formelement);
        var acceptanceTest = formEl.attr('data-test');
        if (typeof acceptanceTest !== typeof undefined && acceptanceTest !== false) {
          acceptanceTest = true;
        }
        else {
          acceptanceTest = false;
        }
        var serializedState =  formEl.serialize()==formEl.data('serializedFormState');
        if(formSubmit === false && serializedState === false  && acceptanceTest === false){
          return "Are you sure you want to leave this page? There are some unsaved changes...";
        } else{
          e=null;
        }
      });
    }
  };
})();
