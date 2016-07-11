//If there is javascript it becomes a modal, if there is not a links to the original page.
IFS.core.modal = (function(){
    "use strict";
    var s; // private alias to settings

    return {
        settings : {
            element: '[data-js-modal]'
        },
        init : function(){
            s = this.settings;
            IFS.core.modal.initButtonRole();

            jQuery('body').on('click',s.element,function(e){
              IFS.core.modal.openModal(e,this);
            });
            jQuery('body').on('click','.js-close',function(){
              IFS.core.modal.closeModal();
            });
            //when submitting a form turns out invalid 
            jQuery('body').on('ifsInvalid',function(){
              IFS.modal.closeModal();
            });
            jQuery(document).keyup(function(e) {
              if (e.keyCode === 27){
                IFS.core.modal.closeModal();
              }
            });
        },
        initButtonRole : function(){
          //for a11y
          if(jQuery(s.element).is('a')){
            jQuery(s.element).attr({'role':'button','tabindex':'0'});
          }
        },
        openModal : function(event,el){
            var target = jQuery(event.target).attr('data-js-modal');
            target = jQuery('.'+target);
            if(target.length){
                event.preventDefault();
                if(jQuery(el).is('[aria-disabled="true"]') === false){
                  IFS.core.modal.disableTabPage();
                  target.add('.modal-overlay').attr('aria-hidden','false');
                  //vertical center,old browser support so no fancy css stuff :(
                  setTimeout(function(){
                      var height = target.outerHeight();
                      target.css({'margin-top':'-'+(height/2)+'px'});
                  },50);
                }
            }
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
        closeModal : function(){
            IFS.core.modal.enableTabPage();
            jQuery('[role="dialog"],.modal-overlay').attr('aria-hidden','true');
        }
    };
})();
