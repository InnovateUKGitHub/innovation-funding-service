//Based upon Lea verou's SVG pie, adjusted with jquery for more legacy support
//http://www.smashingmagazine.com/2015/07/designing-simple-pie-charts-with-css/
IFS.application.invite_display = (function(){
    "use strict";
     var s; // private alias to settings

    return {
        settings : {
        },
        init : function(){
            s = this.settings;
            jQuery('[data-js-modal="modal-submit-remove-collaborator"]').click(function(){
                IFS.application.invite_display.assignInviteIdOnClickRemove(this);
            });
        },
        assignInviteIdOnClickRemove : function(element){
            var inviteId = jQuery(element).data('invite-id');
            jQuery('input[name="applicationInviteId"]').val(inviteId);
        }
    };
})();
