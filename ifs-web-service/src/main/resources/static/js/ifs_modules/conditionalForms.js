//Conditional questions based on answers see: govuk-elements.herokuapp.com/form-elements/#form-toggle-content 
//
//This code is a replacement for the GDS application.js code that was overly complex for what it did and didn't work on pageload
//All behaviours and html are the same as the GDS html so no need to refactor html
//Original logic: https://raw.githubusercontent.com/alphagov/govuk_elements/master/public/javascripts/application.js
IFS.conditionalForms = (function(){
    "use strict";
    return {
        init : function(){
            jQuery('label[data-target]').each(function(){
                var label = jQuery(this);
                var dataTarget = label.attr('data-target');
                var dataTargetEl = jQuery('#'+dataTarget);
                var inputEl = label.find('input[type="radio"],input[type="checkbox"]');

                if(inputEl && dataTarget && dataTargetEl){
                    var groupName = inputEl.attr('name');
                    inputEl.attr('aria-controls',dataTarget);
                    //execute on pageload
                    IFS.conditionalForms.toggleVisibility(inputEl,dataTargetEl); 

                    //execute on click
                    jQuery('input[name="'+groupName+'"]').on('click',function(){
                        IFS.conditionalForms.toggleVisibility(inputEl,dataTargetEl); 
                    });
                }
            });
        },
        toggleVisibility : function(input,target){
            if(input.is(':checked')){
              input.attr('aria-expanded','true');
              target.attr('aria-hidden','false').removeClass('js-hidden');
            }
            else {
              input.attr('aria-expanded','false');  
              target.attr('aria-hidden','true');
            }
        }
    };
})();



