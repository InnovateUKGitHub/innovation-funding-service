/* jshint strict: true, undef: true, unused: true */
/* globals  jQuery : false, setTimeout : false*/

//If there is javascript it becomes a modal, if there is not a links to the original page.
var ifs_modalLink = (function(){
    "use strict";
    var s; // private alias to settings 

    return {
        settings : {
            element: jQuery('[data-js-modal]')
        },
        init : function(){
            s = this.settings; 
            if(s.element.length) {
                s.element.each(function() {
                    ifs_modalLink.modalAttach();
                });
                this.modalCloseLink();
            }
        },
        modalAttach : function(){
            var link = jQuery(this);
            link.on('click',function(e){
              var modal = jQuery('.'+link.attr('data-js-modal'));
               if(modal.length){
                    e.preventDefault();
                    jQuery('.modal-overlay').removeClass('hidden');
                    modal.attr('aria-hidden','false');
                   
                    //vertical center,old browser support so no fancy css stuff :(
                    setTimeout(function(){
                        var height = modal.outerHeight();
                        modal.css({'margin-top':'-'+(height/2)+'px'});
                    },50);
               }
            });
        },
        modalCloseLink : function(){
            jQuery('body').on('click','.js-close',function(){
                jQuery('.modal-overlay').addClass('hidden');
                jQuery('[role="dialog"]').attr('aria-hidden','true');
            });
        }   
    };
})();
