IFS.upload = (function(){
    "use strict";
     var s; // private alias to settings
    return {
        settings : {
            uploadEl : '[type="file"][class="inputfile"]'
        },
        init : function(){
            s = this.settings;
            jQuery('body').on('change',s.uploadEl,function(){
                jQuery('[name="upload_file"]').click();
            });
        }
    };
})();
