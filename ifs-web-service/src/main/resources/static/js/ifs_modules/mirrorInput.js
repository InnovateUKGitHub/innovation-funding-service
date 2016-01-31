IFS.mirrorElements = (function(){
    "use strict";
    return {
        init : function(){
            jQuery('[data-mirror]').each(function(){
                var element = jQuery(this);
                var source = element.attr('data-mirror');

                IFS.mirrorElements.updateElement(element,source);
                IFS.mirrorElements.bindMirrorElement(element,source);
            });
        },
        bindMirrorElement : function(element,source){
            jQuery(document).on('change', source, function(){
                  IFS.mirrorElements.updateElement(element,this);
            });
        },
        updateElement : function(element,source){
            var sourceText = IFS.mirrorElements.getSourceText(source);
            if(element.is('input')){
              element.val(sourceText);
            }
            else {
              element.text(sourceText);
            }
        },
        getSourceText : function(element){
            var sourceEl = jQuery(element);
            if(sourceEl.length == 1){
              if(sourceEl.val().length){
                return sourceEl.val();
              }
              else if(sourceEl.text().length){
                return sourceEl.text();
              }
            }
            return '';
        }
    };
})();
