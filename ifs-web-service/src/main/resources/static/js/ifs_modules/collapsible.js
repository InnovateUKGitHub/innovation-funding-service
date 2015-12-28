//  Progressive collapsibles original code by @Heydonworks altered by Worth Systems
//-----------------------------------------------------------------------------
IFS.collapsible = (function(){
  "use strict";
  var s; // private alias to settings 

  return {
      settings: {
        collapsibleEl : '.collapsible > h2, .collapsible > h3, .assign-container .assign-button'
      },
      init : function() {
            s = this.settings;
            IFS.collapsible.collapsibleWithinScope(jQuery(document));
      },
      collapsibleWithinScope : function($scope) {

          var existingCollapsibles = $scope.find('[data-collapsible-id]');
          var maxId = 0;

          existingCollapsibles.each(function(index, element) {
              var id = element.attr('data-collapsible-id');
              maxId = Math.max(maxId, parseInt(id));
          });

          $scope.find(s.collapsibleEl).each(function(index,value) {
              var inst = jQuery(value);
              IFS.collapsible.addCollapsibleBehaviourToElement(inst, index, maxId + 1);
          });
      },
      addCollapsibleBehaviourToElement : function(inst, index, idOffset) {

          var id = 'collapsible-' + (index + idOffset);   // create unique id for a11y relationship
          var loadstate = inst.hasClass('open');

          var closeAll = false;
           if(inst.closest('.collapsible').hasClass('js-close-others')){
              closeAll = true;
           }
          // wrap the content and make it focusable
          inst.nextUntil('h2').wrapAll('<div id="'+ id +'" aria-hidden="'+!loadstate+'">');
          var panel = inst.next();

          // Add the button inside the <h2> so both the heading and button semanics are read
          inst.wrapInner('<button aria-expanded="'+loadstate+'" aria-controls="'+ id +'" type="button">');
          var button = inst.children('button');

          // Toggle the state properties
          // TODO DW - direct event handling placed on button difficult to maintain when allowing partial page updates via ajax
          // Consider moving to event delegation-based handling wherever possible
          button.off('click').on('click', function() {
            var state = jQuery(this).attr('aria-expanded') === 'false' ? true : false;

            //close all other buttons on click, defined by the js-close-others class on the container element
            if(closeAll){
                jQuery('.collapsible [aria-expanded]').attr('aria-expanded','false');
                jQuery('.collapsible [aria-hidden]').attr('aria-hidden','true');
            }
            
            //toggle the current
            jQuery(this).attr('aria-expanded', state);
            panel.attr('aria-hidden', !state);
          });
      }
  };
 })();