//Innovation Funding Services javascript by Worth
var worthIFSFinance = {
    MathOperation : {
        '+': function (x, y) { return x + y },
        '-': function (x, y) { return x - y },
        '*': function (x, y) { return x * y },
        '/': function (x, y) { return x / y }
    },
    domReady : function(){
        if(worthIFS.isApplicationForm()){
            worthIFSFinance.preProcessRepeatedFieldsForTotalElements();
            worthIFSFinance.bindCalculationActionToFields();
            worthIFSFinance.initShowHideOtherCosts();
        }
    },
    preProcessRepeatedFieldsForTotalElements : function(){
        // TODO DW - upon a dynamic adding or removing of a cost row (i.e. without full page refresh), we're OK to allow
        // this behaviour to rerun and update the field ids on the "Total" field to let it know the latest set of fields
        // that are involved in its calculation - would be nicer to move to a delegate-based event listner approach though
        // whereby no behaviour needs rebinding after elements are added and removed
        if(jQuery('[data-calculation-repeating-total]').length){
            $('[data-calculation-repeating-total]').each(function(index,value){
                worthIFSFinance.preProcessRepeatedFieldsForTotalElement($(this));
            });
        }
    },
    preProcessRepeatedFieldsForTotalElement : function(totalElement) {
        // TODO DW - upon a dynamic adding or removing of a cost row (i.e. without full page refresh), we're OK to allow
        // this behaviour to rerun and update the field ids on the "Total" field to let it know the latest set of fields
        // that are involved in its calculation - would be nicer to move to a delegate-based event listner approach though
        // whereby no behaviour needs rebinding after elements are added and removed
        var fieldSelector = worthIFSFinance.getFieldSelector(totalElement);
        var repeatedFieldElements = worthIFSFinance.getFieldsBySelector(fieldSelector);
        worthIFSFinance.addRepeatedFieldIdsToTotalElement(totalElement, repeatedFieldElements);
    },
    rebindCalculationFieldsOnDynamicUpdate : function() {
        // TODO DW - currently having to bind / rebind change event listeners upon the dynamic adding or removing
        // of Cost rows (i.e. without a full page refresh).  Would be nicer to move to a delegate event handling
        // model whereby no rebinding is necessary when the page is changed
        worthIFSFinance.domReady();
    },

    getFieldsBySelector : function(fieldSelector) {
        var fields = [];

        $(fieldSelector).each(function(){
            fields.push('#'+$(this).attr('id'));
        });

        return fields;
    },
    getFieldSelector : function(totalElement) {
        return totalElement.attr("data-calculation-repeating-total");
    },
    addRepeatedFieldIdsToTotalElement : function(totalElement, repeatedFieldElements) {
        totalElement.attr('data-calculation-fields',repeatedFieldElements.join());
    },

    bindCalculationActionToFields : function(){
        $('[data-calculation-fields]').each(function(index,value){
                var inst = $(this);
                var fields = inst.attr("data-calculation-fields").split(',');

                _.each(fields, function(el){
                    var field = $(el);
                    //console.log('field',field);
                    // TODO DW - currently having to bind / rebind change event listeners upon the dynamic adding or removing
                    // of Cost rows (i.e. without a full page refresh).  Would be nicer to move to a delegate event handling
                    // model whereby no rebinding is necessary when the page is changed
                    field.off('change').on('change',function(){
                        worthIFSFinance.doMath(inst,fields);
                    });
                });
        });
    },
    initShowHideOtherCosts : function() {
        worthIFSFinance.triggerOtherCostsForm($('#otherCostsShowHideToggle'));
        worthIFSFinance.bindShowHideOtherCostsSelectTrigger();
    },
    bindShowHideOtherCostsSelectTrigger : function() {
        $('#otherCostsShowHideToggle').change(function() {
            var self = this;
            worthIFSFinance.triggerOtherCostsForm(self);
        });
    },
    triggerOtherCostsForm : function(element) {
        if($(element).val()==="No") {
            $('#otherCostsForm').hide();
        }
        else {
            $('#otherCostsForm').show();
        }
    },
    doMath : function(element,calcFields){
        var operation = element.attr('data-calculation-operations').split(',');
        var values = [];
        _.each(calcFields, function(field){
            values.push(parseFloat($(field).attr("data-calculation-rawvalue") || $(field).val() || 0));
        });

        if(values.length === 1) {
            var calculatedValue=values[0];
        }
        else {
            var calculatedValue = worthIFSFinance.MathOperation[operation[0]](values[0],values[1]);
        }
        //one operation and more values, all get the same operation

        if((operation.length == 1) && (values.length > 2)) {
            for (i = 2; i < values.length; i++) {
                //console.log('round:',i,typeof(operation[0]),operation[0],typeof(calculatedValue),calculatedValue,typeof(values[i]),values[i],values)
                calculatedValue = worthIFSFinance.MathOperation[operation[0]](calculatedValue,values[i]);
            };
        }
       //multiple operations and multiple values
        else if((operation.length > 1) && (values.length > 2)) {
            for (i = 1; i < operation.length; i++) {
                // console.log('round:',i,operation[i],calculatedValue,values[i+1])
                calculatedValue = worthIFSFinance.MathOperation[operation[i]](calculatedValue,values[i+1]);
            }
        }
        element.attr("data-calculation-rawvalue",calculatedValue);

        var formatted = worthIFSFinance.formatCurrency(Math.round(calculatedValue));
        if (element.is('span')) {
            element.text(formatted);
        } else {
            element.val(formatted);
        }
        element.trigger('change');
    },

    formatCurrency: function(total) {
        total = Math.abs(total);
        parsedFloat = parseFloat(total, 10);
        tofixed = parsedFloat.toFixed();
        replaced = tofixed.replace(/(\d)(?=(\d{3})+\b)/g, "$1,");
        tostring= replaced.toString();

        return tostring;
    }
}

//
// Bind change events to update calculations
//
$(document).ready(function(){
    worthIFSFinance.domReady();
});

//
// Set up the handlers for adding and removing Cost Category costs rows
//
$(function() {

    //
    // Replace the non-javascript add and remove functionality with single fragment question HTML responses
    //
    var generateFragmentUrl = function(originalLink) {
        var originalHref = originalLink.attr('href');
        var urlParamsParts = originalHref.split('?');
        var urlPart = urlParamsParts[0];
        var paramsPart = urlParamsParts.length == 2 ? ('&' + urlParamsParts[1]) : '';

        var questionToUpdate = originalLink.parents('[data-question-id]');
        var owningQuestionId = questionToUpdate.attr('data-question-id');
        var dynamicHref = urlPart + '/' + owningQuestionId + '?singleFragment=true' + paramsPart;
        return dynamicHref;
    };

    //
    // Add a new row from the HTML fragment being returned, and bind / rebind the autocalc and autosave behaviours again to ensure it behaves as
    // per other fields and that the repeating-total fields have a back-reference to any added fields
    //
    var addCostsRowHandler = function(e) {
        var amendRowsLink = $(this);
        var dynamicHref = generateFragmentUrl(amendRowsLink);

        $.get(dynamicHref, function(data) {
            var htmlReplacement = $('<div>' + data + '</div>');
            var tableSectionToUpdate = amendRowsLink.parents('[finance-subsection-table-container]');
            var tableSectionId = tableSectionToUpdate.attr('finance-subsection-table-container');
            var replacement = htmlReplacement.find('[finance-subsection-table-container=' + tableSectionId + ']');
            tableSectionToUpdate.replaceWith(replacement);
            worthIFS.initAllAutosaveElements(replacement);
            worthIFSFinance.rebindCalculationFieldsOnDynamicUpdate();
        })
        e.preventDefault();
        return false;
    };

    //
    // Remove a row from the HTML, but beforehand set all calculation fields being removed to zero so that the other running total fields not being
    // removed will be updated with the removal of these figures.  Then simply remove the HTML.
    //
    var removeCostsRowHandler = function(e) {
        var amendRowsLink = $(this);
        var dynamicHref = generateFragmentUrl(amendRowsLink);

        $.get(dynamicHref, function(data) {
            var costRowsId = amendRowsLink.attr('data-cost-row');
            var costRowsToDelete = $('[data-cost-row=' + costRowsId + ']');
            costRowsToDelete.find('[data-calculation-fields],[data-calculation-input]').val(0).attr('data-calculation-rawvalue',0).trigger('change');
            costRowsToDelete.remove();
            worthIFSFinance.rebindCalculationFieldsOnDynamicUpdate();
        })
        e.preventDefault();
        return false;
    };

    $(document).on('click', '[finance-subsection-table-container] .add-another-row', addCostsRowHandler);
    $(document).on('click', '[finance-subsection-table-container] .delete-row', removeCostsRowHandler);
});

//
// Bind input and output fields together to mirror values of the "input" fields in the "outputs"
//
$(function() {
    $('body').on('change', '[data-binding-input]', function(e) {
        var input = $(this);
        var bindingId = input.attr('data-binding-input');
        var bindingOutputs = $('[data-binding-output="' + bindingId + '"]');
        bindingOutputs.each(function() {
            var output = $(this);
            if (output.is('span')) {
                output.text(input.val());
            } else {
                output.val(input.val());
            }
        });
        e.preventDefault();
        return false;
    });
});

//
// Move section elements into the section header
//
$(function() {

    $('.place-in-header').each(function() {
        var elementToMove = $(this);
        var subsectionId = elementToMove.attr('data-subsection-id');
        var header = $('[data-subsection-header=' + subsectionId + ']');
        elementToMove.detach();
        header.append(elementToMove);
        elementToMove.show();
    });
});