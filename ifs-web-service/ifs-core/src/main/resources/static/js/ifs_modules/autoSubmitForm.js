IFS.autoSubmitForm = (function(){
    "use strict";
     var s; // private alias to settings
    return {
        settings : {
            submitElements : '.js-auto-submit'
        },
        init : function(){
            s = this.settings;
            //hide non-js button
            jQuery(s.submitElements).next('button').attr('aria-hidden','true');
            jQuery(document).on('change',s.submitElements,function(){
              IFS.autoSubmitForm.submitForm(this);
            });
        },
        submitForm : function(el){
          jQuery(el).closest('form').submit();
        }
    };
})();
