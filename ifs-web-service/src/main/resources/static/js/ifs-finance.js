//Innovation Funding Services javascript for calculating the finance fields
var IFSFinance = {
    MathOperation : {
        '+': function (x, y) { return x + y },
        '-': function (x, y) { return x - y },
        '*': function (x, y) { return x * y },
        '/': function (x, y) {
            if(y === 0) {
                return 0;
            }
            return x / y;
        }
    },
    domReady : function(){
        if(IFS.isApplicationForm()){
            IFSFinance.initShowHideOtherCosts();
        }
    },
    bindCalculationActionToFields : function(){

        var updateBasedOnDataCalculationFieldsIfNecessary = function(dependantField, input) {

            var dependencySelectors = dependantField.attr("data-calculation-fields").split(',');

            var matchingDependenciesInArrays = dependencySelectors.map(function(selector) {
                return $(selector);
            });

            var matchingDependencies = matchingDependenciesInArrays.reduce(function(combined, currentJqueryObject) {
                return $.merge(combined, currentJqueryObject);
            });

            var idFn = function(element) {
              if (element instanceof jQuery) {
                  return element.attr('id');
              }
              return $(element).attr('id');
            };

            if (matchingDependencies.toArray().map(idFn).indexOf(idFn(input)) !== -1) {
                IFSFinance.doMath(dependantField, matchingDependencies);
            }
        };

        $('body').on('change', 'input', function() {

            var input = $(this);

            var fieldsDependantOnOthers = $('[data-calculation-fields]');

            fieldsDependantOnOthers.each(function(i, element) {
                var dependantField = $(element);
                updateBasedOnDataCalculationFieldsIfNecessary(dependantField, input);
            });

        });
    },
    initShowHideOtherCosts : function() {
        IFSFinance.triggerOtherCostsForm($('#otherCostsShowHideToggle'));
        IFSFinance.bindShowHideOtherCostsSelectTrigger();
    },
    bindShowHideOtherCostsSelectTrigger : function() {
        $('#otherCostsShowHideToggle').change(function() {
            var self = this;
            IFSFinance.triggerOtherCostsForm(self);
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

            var input = $(field);
            var rawValue = input.attr("data-calculation-rawvalue");

            // TODO DW - would be better to force all fields to have a raw value at the start rather than these fallback cases
            if (typeof rawValue !== 'undefined') {
                values.push(parseFloat(rawValue));
            } else {
                var displayValue = input.val();
                if (typeof displayValue !== 'undefined' && displayValue.length > 0) {
                    var parsed = displayValue.indexOf('£ ') == 0 ? displayValue.substring(2) : displayValue;
                    values.push(parseFloat(parsed));
                } else {
                    values.push(parseFloat(0));
                }
            }
        });

        if(values.length === 1) {
            var calculatedValue=values[0];
        }
        else {
            var calculatedValue = IFSFinance.MathOperation[operation[0]](values[0],values[1]);
        }
        //one operation and more values, all get the same operation

        if((operation.length == 1) && (values.length > 2)) {
            for (i = 2; i < values.length; i++) {
                //console.log('round:',i,typeof(operation[0]),operation[0],typeof(calculatedValue),calculatedValue,typeof(values[i]),values[i],values)
                calculatedValue = IFSFinance.MathOperation[operation[0]](calculatedValue,values[i]);
            };
        }
       //multiple operations and multiple values
        else if((operation.length > 1) && (values.length > 2)) {
            for (i = 1; i < operation.length; i++) {
                // console.log('round:',i,operation[i],calculatedValue,values[i+1])
                calculatedValue = IFSFinance.MathOperation[operation[i]](calculatedValue,values[i+1]);
            }
        }
        element.attr("data-calculation-rawvalue",calculatedValue);

        var formatted = IFSFinance.formatCurrency(Math.round(calculatedValue));
        if (element.is('span')) {
            element.text(formatted);
        } else {
            element.val(formatted);
        }
        element.trigger('change');
    },

    formatCurrency: function(total) {
        var absTotal = Math.abs(total);
        var parsedFloat = parseFloat(absTotal, 10);
        var toFixed = parsedFloat.toFixed();
        var replaced = toFixed.replace(/(\d)(?=(\d{3})+\b)/g, "$1,");
        return '£ ' + replaced.toString();
    }
}

//
// Bind calculations
//
$(function() {

    IFSFinance.bindCalculationActionToFields();
});

//
// Bind Other Costs events
//
$(function() {
    IFSFinance.domReady();
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
            var tableSectionToUpdate = amendRowsLink.parents('[data-finance-subsection-table-container]');
            var tableSectionId = tableSectionToUpdate.attr('data-finance-subsection-table-container');
            var replacement = htmlReplacement.find('[data-finance-subsection-table-container=' + tableSectionId + ']');
            tableSectionToUpdate.replaceWith(replacement);
            IFS.initAllAutosaveElements(replacement);
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
        })
        e.preventDefault();
        return false;
    };

    $(document).on('click', '[data-finance-subsection-table-container] .add-another-row', addCostsRowHandler);
    $(document).on('click', '[data-finance-subsection-table-container] .delete-row', removeCostsRowHandler);
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