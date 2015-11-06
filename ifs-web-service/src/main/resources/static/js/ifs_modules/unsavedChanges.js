// save the current form state, so we can warn the user if he leaves the page without saving.
var ifs_unsavedChanges = (function(){
    "use strict";
    var s; 

    return {
        settings : {
          formelement : jQuery('.form-serialize-js')
        },
        init : function(){
            s = this.settings; 
            this.initUnsavedChangesWarning();
        },
        initUnsavedChangesWarning : function(){
            s.formelement.data('serializedFormState',$('.form-serialize-js').serialize());

            // don't show the warning when the user is submitting the form.
            var formSubmit = false;
            s.formelement.on('submit', function(e){
                formSubmit = true;
            });

             $(window).bind('beforeunload', function(e){
                if(formSubmit === false && jQuery('.form-serialize-js').serialize()!=$('.form-serialize-js').data('serializedFormState')){
                    return "Are you sure you want to leave this page? There are some unsaved changes...";
                } else{
                    e=null;
                }
            });
        }
    };
})();
