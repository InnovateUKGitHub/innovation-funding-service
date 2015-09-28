// Innovation Funding Services control methods
var IFS = {
    collapsibleEl : '.collapsible > h2, .assign-container .assign-button',
    pieEl : '.pie',
    domReady : function(){
        IFS.collapsible();
        IFS.pieChart();
        IFS.modalLink();
        IFS.closeAlertHide();

        if(IFS.isApplicationForm()){
            IFS.initAllAutosaveElements();
            IFS.initUnsavedChangesWarning();
            IFS.initWordCount();
        }
    },
    isApplicationForm : function(){
        if(jQuery('body.app-form').size()==1){
            return true;
        }
        return false;
    },
    collapsible : function() {
        /*  Progressive collapsibles written by @Heydonworks altered by Worth Systems
         -----------------------------------------------------------------------------
         */
        IFS.collapsibleWithinScope($(document));
    },
    collapsibleWithinScope : function($scope) {

        var existingCollapsibles = $scope.find('[data-collapsible-id]');

        var maxId = 0;

        existingCollapsibles.each(function(index, element) {
            var id = element.attr('data-collapsible-id');
            maxId = Math.max(maxId, parseInt(id));
        });

        $scope.find(IFS.collapsibleEl).each(function(index,value) {
            var inst = $(this);
            IFS.addCollapsibleBehaviourToElement(inst, index, maxId + 1);
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
              var container =  jQuery(this).closest('.collapsible')
              container.find('[aria-expanded]').attr('aria-expanded','false');
              container.find('[aria-hidden]').attr('aria-hidden','true');
          }

          //toggle the current
          jQuery(this).attr('aria-expanded', state);
          panel.attr('aria-hidden', !state);
        });
    },
    pieChart : function() {
        /* 
        Lea verou's SVG pie, adjusted with jquery and modernizr for more legacy support
        */
        if(Modernizr.svg && Modernizr.inlinesvg){
           jQuery(IFS.pieEl).each(function(index,pie) {
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
    initAllAutosaveElements : function($scope){

        var $targetScope = $scope || $(document);

        var inputFields = jQuery('.form-serialize-js input').not('[type="button"],[readonly="readonly"]');
        var textareas = jQuery('.form-serialize-js textarea').not('[readonly="readonly"]'); 
        var fields = inputFields.add(textareas);

        IFS.initAutosaveElements(fields);
    },
    initAutosaveElements : function(fields){

        var options = {
            callback: function (value) { IFS.fieldChanged(this);  },
            wait: 500,
            highlight: false,
            captureLength: 1
        }

        fields.typeWatch(options);
        fields.off('change').on('change', function(e) {
            IFS.fieldChanged(e.target);
        });

    },
    fieldChanged : function (element){

        var field = $(element);
        var fieldId = field.attr('id');

        var jsonObj = {
                value: element.value,
                questionId: fieldId,
                fieldName: field.attr('name'),
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
             field.removeClass('error');
             var formGroup = field.closest('.form-group');
             formGroup.removeClass('error');
             formGroup.find('span.error-message').remove();

         }).fail(function(data) {

             var formGroup = field.closest('.form-group');
             if (formGroup.length) {
                 formGroup.addClass('error');

                 var label = formGroup.find('label').first();
                 if (label.length) {
                     label.find('span.error-message').remove();
                     var errorMessage = data.responseJSON.errorMessage;
                     label.append('<span class="error-message" id="error-message-' + fieldId + '">' + errorMessage + '</span>');
                 }

             } else {
                 field.addClass('error');
             }
         });
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
    initWordCount : function(){
        var options = {
            callback: function (value) { updateWordCount(this);  },
            wait: 500,
            highlight: false,
            captureLength: 1
        }
        jQuery(".word-count textarea").typeWatch( options );
        jQuery(".word-count textarea").each(function(){
            updateWordCount(this);
        });

        function updateWordCount(element){
            delta = element.dataset.max_words - element.value.split(" ").length

            count = element.value.length;
            jQuery(element).parent(".word-count").find(".count-down").html(delta);
            if(delta < 0 ){
                jQuery(element).parent(".word-count").addClass("word-count-reached");
            }else{
                jQuery(element).parent(".word-count").removeClass("word-count-reached");
            }
        }

    },
    closeAlertHide : function(){
        setTimeout(function(){
            jQuery('.is-open').removeClass('is-open');
        },3000);
    },
    modalLink : function(){
        var modalLinks = jQuery('[data-js-modal]');
        if(modalLinks.length) {
            modalLinks.each(function() {
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
                })
            });
            IFS.modalCloseLink();
        }
    },
    modalCloseLink : function(){
        jQuery('body').on('click','.js-close',function(){
            jQuery('.modal-overlay').addClass('hidden');
            jQuery('[role="dialog"]').attr('aria-hidden','true');
        });
    },
    log : function(message){
       if (window.console) {
           switch(arguments.length){
               case 1:
                    window.console.log(arguments[0]);
                    break;
               case 2:
                   window.console.log(arguments[0],arguments[1]);
                   break;
               case 3:
                   window.console.log(arguments[0],arguments[1],arguments[2]);
                   break;
               default:
           }
            return
       }
       return false;
    }
} 

jQuery(document).ready(function(){
  IFS.domReady();
});



