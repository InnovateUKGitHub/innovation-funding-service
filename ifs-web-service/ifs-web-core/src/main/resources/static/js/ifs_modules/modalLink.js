//If there is javascript it becomes a modal, if there is not a links to the original page.
IFS.core.modal = (function(){
  "use strict";
  var s; // private alias to settings

  return {
    settings : {
      element: '[data-js-modal]',
      html5validationMode: {}
    },
    init : function(){
      s = this.settings;
      s.html5validationMode =  IFS.core.formValidation.checkHTML5validationMode();

      IFS.core.modal.initButtonRole();

      jQuery('body').on('click', s.element, function(e){
        IFS.core.modal.openModal(e, this);
      });
      jQuery('body').on('click', '.js-close', function(){
        IFS.core.modal.closeModal();
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
        jQuery(s.element).attr({'role':'button', 'tabindex':'0'});
      }
    },
    checkForInputErrors : function(button){
      var formValid = true;
      if((button.closest('form:not([novalidate])').length) && (s.html5validationMode)){
        var form = button.closest('form:not([novalidate])');
        formValid = form[0].checkValidity();
      }
      return formValid;
    },
    openModal : function(event, button){
      button = jQuery(button);
      var formValid = IFS.core.modal.checkForInputErrors(button);
      var target = jQuery(event.target).attr('data-js-modal');
      target = jQuery('.'+target);
      if(target.length){
        event.preventDefault();
        if((formValid) && (button.is('[aria-disabled="true"]') === false)){
          IFS.core.modal.disableTabPage();
          target.add('.modal-overlay').attr('aria-hidden', 'false');
          //vertical center,old browser support so no fancy css stuff :(
          setTimeout(function(){
            var height = target.outerHeight();
            target.css({'margin-top':'-'+(height/2)+'px'});
          }, 50);
        }
        else if (!formValid) {
          button.closest('form:not([novalidate])').find('input:invalid').first().focus();
        }
      }

      if(target.find('form').length){
        target.find('form').on('submit', function(event){
          /**
          * Validate the modal form fields before submitting
          * @requires: formValidation.js
          */
          var form = this,
          submitValid = true;

          // Validate each required field in the form
          jQuery('[required="required"]', form).each(function() {
            if(IFS.core.formValidation.checkRequired(jQuery(this), true) !== true) {
              submitValid = false;
            }
          });

          // If valid submit the form, otherwise prevent submission
          if(submitValid) {
            return true;
          } else {
            event.preventDefault();
            return false;
          }
        });
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
          el.prop('tabindex', '-1').attr('data-original-tabindex', tabindex);
        }
      });
    },
    enableTabPage : function(){
      jQuery('[data-original-tabindex]').each(function(){
        var el = jQuery(this);
        var orignalTabindex = el.attr('data-original-tabindex');
        el.prop('tabindex', orignalTabindex).removeAttr('data-original-tabindex');
      });
    },
    closeModal : function(){
      IFS.core.modal.enableTabPage();
      jQuery('[role="dialog"],.modal-overlay').attr('aria-hidden', 'true');
    }
  };
})();
