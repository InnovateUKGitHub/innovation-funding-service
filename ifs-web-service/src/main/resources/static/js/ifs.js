//Innovation Funding Services javascript by Worth
var worthIFS = {
    collapsibleEl : '.collapsible',
    pieEl : '.pie',
    domReady : function(){
        worthIFS.collapsible();
        worthIFS.pieChart();

        if(jQuery('body.app-form').size()==1){
            worthIFS.initAutosaveElement();
            worthIFS.initUnsavedChangesWarning();
            worthIFS.closeAlertHide();
            worthIFS.initCharacterCount();
        }
    },
    collapsible : function(){
      /*  Progressive collapsibles written by @Heydonworks altered by Worth Systems
      -----------------------------------------------------------------------------
      */
      jQuery(worthIFS.collapsibleEl+' h2').each(function() {
        var inst = jQuery(this);
        var id = 'collapsible-' + inst.index();   // create unique id for a11y relationship
        var loadstate = inst.hasClass('open');

        // wrap the content and make it focusable
        inst.nextUntil('h2').wrapAll('<div id="'+ id +'" aria-hidden="'+!loadstate+'">');
        var panel = inst.next();

        // Add the button inside the <h2> so both the heading and button semanics are read  
        inst.wrapInner('<button aria-expanded="'+loadstate+'" aria-controls="'+ id +'" type="button">');
        var button = inst.children('button');

        // Toggle the state properties  
        button.on('click', function() {
          var state = jQuery(this).attr('aria-expanded') === 'false' ? true : false;

          //close all others
          jQuery('.collapsible [aria-expanded]').attr('aria-expanded','false');
          jQuery('.collapsible [aria-hidden]').attr('aria-hidden','true');

          //toggle the current
          jQuery(this).attr('aria-expanded', state);
          panel.attr('aria-hidden', !state);
        });
      });
    },
    pieChart : function() {
        /* 
        Lea verou's SVG pie, adjusted with jquery and modernizr for more legacy support
        */
        if(Modernizr.svg && Modernizr.inlinesvg){
           jQuery(worthIFS.pieEl).each(function(index,pie) {
            var p = parseFloat(pie.textContent);
            var NS = "http://www.w3.org/2000/svg";
            var svg = document.createElementNS(NS, "svg");
            var circle = document.createElementNS(NS, "circle");
            var title = document.createElementNS(NS, "title");
            
            circle.setAttribute("r", 16);
            circle.setAttribute("cx", 16);
            circle.setAttribute("cy", 16);
            circle.setAttribute("stroke-dasharray", p + " 100");
            
            svg.setAttribute("viewBox", "0 0 32 32");
            title.textContent = pie.textContent;
            pie.textContent = '';
            svg.appendChild(title);
            svg.appendChild(circle);
            pie.appendChild(svg);
          });
        }
    },
    initAutosaveElement : function(){
        var options = {
            callback: function (value) { fieldChanged(this);  },
            wait: 2500,
            highlight: false,
            captureLength: 1
        }

        jQuery(".form-serialize-js input, .form-serialize-js textarea").typeWatch( options );
        jQuery(".form-serialize-js input, .form-serialize-js textarea").change(function(e) {
            fieldChanged(e.target);
        });

        function fieldChanged(element){
            var jsonObj = {
                    value:element.value,
                    questionId: jQuery(element).data("question_id"),
                    applicationId: jQuery(".form-serialize-js #application_id").val()
             };


             var formState = $('.form-serialize-js').serialize();
             jQuery.ajax({
                 type: 'POST',
                 url: "/application-form/saveFormElement",
                 data: jsonObj,
                 dataType: "json"
             }).done(function(){
                // set the form-saved-state
                $('.form-serialize-js').data('serializedFormState',formState);
             }).fail(function(){
                // ajax save failed.
             });
        }
    },
    initUnsavedChangesWarning : function(){
        // save the current form state, so we can warn the user if he leaves the page without saving.
        jQuery('.form-serialize-js').data('serializedFormState',$('.form-serialize-js').serialize());

        // don't show the warning when the user is submitting the form.
        formSubmit = false;
        jQuery('.form-serialize-js').on('submit', function(e){
            formSubmit = true;
        });

         $(window).bind('beforeunload', function(e){
                if(formSubmit == false && jQuery('.form-serialize-js').serialize()!=$('.form-serialize-js').data('serializedFormState')){
                    return "Are you sure you want to leave this page? There are some unsaved changes...";
                }else{
                 e=null;
                }
        });

    },
    initCharacterCount : function(){
        var options = {
            callback: function (value) { updateCharacterCount(this);  },
            wait: 500,
            highlight: false,
            captureLength: 1
        }
        jQuery(".character-count textarea").typeWatch( options );
        jQuery(".character-count textarea").each(function(){
            updateCharacterCount(this);
        });

        function updateCharacterCount(element){
            delta = element.dataset.max_characters - element.value.length

            count = element.value.length;
            jQuery(element).parent(".character-count").find(".count-down").html(delta);
            if(delta < 0 ){
                jQuery(element).parent(".character-count").addClass("character-count-reached");
            }else{
                jQuery(element).parent(".character-count").removeClass("character-count-reached");
            }
        }

    },
    closeAlertHide : function(){
        setTimeout(function(){
            jQuery('.is-open').removeClass('is-open');
        },5000);
    }
} 

jQuery(document).ready(function(){
  worthIFS.domReady();
});



