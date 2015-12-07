// save the current form state, so we can warn the user if he leaves the page without saving.
IFS.unsavedChanges = (function(){
    "use strict";
    var s; 
    return {
        settings : {
          formelement : jQuery('.form-serialize-js')
        },
        init : function(){
            s = this.settings; 
            IFS.unsavedChanges.initUnsavedChangesWarning();
        },
        initUnsavedChangesWarning : function(){
            s.formelement.data('serializedFormState',jQuery('.form-serialize-js').serialize());

            // don't show the warning when the user is submitting the form.
            var formSubmit = false;
            s.formelement.on('submit', function(){
                formSubmit = true;
            });

             jQuery(window).bind('beforeunload', function(e){
                if(formSubmit === false && jQuery('.form-serialize-js').serialize()!=jQuery('.form-serialize-js').data('serializedFormState')){
                    return "Are you sure you want to leave this page? There are some unsaved changes...";
                } else{
                    e=null;
                }
            });
        }
    };
})();
