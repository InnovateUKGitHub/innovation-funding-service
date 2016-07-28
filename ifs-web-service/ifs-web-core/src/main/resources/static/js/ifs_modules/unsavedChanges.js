// save the current form state, so we can warn the user if he leaves the page without saving.
IFS.core.unsavedChanges = (function(){
    "use strict";
    var s;
    return {
        settings : {
          formelement : jQuery('.form-serialize-js')
        },
        init : function(){
            s = this.settings;
            IFS.core.unsavedChanges.initUnsavedChangesWarning();
            IFS.core.unsavedChanges.updateSerializedFormState();

            jQuery('body').on('updateSerializedFormState',function(){
                IFS.core.unsavedChanges.updateSerializedFormState();
            });
        },
        updateSerializedFormState : function(){
            s.formelement.data('serializedFormState',jQuery('.form-serialize-js').serialize());
        },
        initUnsavedChangesWarning : function(){
            // don't show the warning when the user is submitting the form.
            var formSubmit = false;
            s.formelement.on('submit', function(){
                formSubmit = true;
            });

             jQuery(window).bind('beforeunload', function(e){
                 var acceptanceTest = s.formelement.attr('data-test');
                 if (typeof acceptanceTest !== typeof undefined && acceptanceTest !== false) {
                     acceptanceTest = true;
                 }
                 else {
                     acceptanceTest = false;
                 }
                var serializedState =  jQuery('.form-serialize-js').serialize()==jQuery('.form-serialize-js').data('serializedFormState');
                if(formSubmit === false && serializedState === false  && acceptanceTest === false){
                    return "Are you sure you want to leave this page? There are some unsaved changes...";
                } else{
                    e=null;
                }
            });
        }
    };
})();
