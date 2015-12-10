//If there is javascript it becomes a modal, if there is not a links to the original page.
IFS.modal = (function(){
    "use strict";
    var s; // private alias to settings 

    return {
        settings : {
            element: '[data-js-modal]'
        },
        init : function(){
            s = this.settings;
            if(jQuery(s.element).length) {
                IFS.modal.initModals();
                IFS.modal.modalCloseLink();
            }
        },
        initModals : function(){
            jQuery('body').on('click',s.element,function(e){
                e.preventDefault();
                var target = jQuery(this).attr('data-js-modal');
                target = jQuery('.'+target);

                if(target.length){
                    e.preventDefault();

                    IFS.modal.disableTabPage();
                    target.add('.modal-overlay').attr('aria-hidden','false');
                    //vertical center,old browser support so no fancy css stuff :(
                    setTimeout(function(){
                        var height = target.outerHeight();
                        target.css({'margin-top':'-'+(height/2)+'px'});
                    },50);
                }
            });
        },
        disableTabPage : function(){
            jQuery(":tabbable").each(function(){
                var el = jQuery(this);
               
                if(el.closest('[role="dialog"]').length === 0){
                    var tabindex = 0;
                    if(el.prop('tabindex')){
                        tabindex = el.prop('tabindex');
                    }
                    el.prop('tabindex','-1').attr('data-original-tabindex',tabindex);
                }
            });
        },
        enableTabPage : function(){
            jQuery('[data-original-tabindex]').each(function(){
                var el = jQuery(this);
                var orignalTabindex = el.attr('data-original-tabindex');
                el.prop('tabindex',orignalTabindex).removeAttr('data-original-tabindex');
            });
        },
        modalCloseLink : function(){
            jQuery('body').on('click','.js-close',function(){
                IFS.modal.enableTabPage();
                jQuery('[role="dialog"],.modal-overlay').attr('aria-hidden','true');

            });
        }   
    };
})();
