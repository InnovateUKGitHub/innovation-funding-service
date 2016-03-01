IFS.upload = (function(){
    "use strict";
     var s; // private alias to settings
    return {
        settings : {
            uploadEl : '[type="file"][class="inputfile"]'
        },
        init : function(){
            s = this.settings;
            jQuery('body').on('change',s.uploadEl,function(){IFS.upload.updateLabelName(this);});
        },
        updateLabelName : function(el){
            var inst = jQuery(el);
            var label = inst.val().split('\\').pop();
            if(label.length){
              inst.next().html(label);
            }
        }
    };
})();
