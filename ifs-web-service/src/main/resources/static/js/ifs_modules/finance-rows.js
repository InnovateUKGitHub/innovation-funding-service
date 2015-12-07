// Set up the handlers for adding and removing Cost Category costs rows
IFS.financeRows = (function() {
    "use strict";

    return {
        init: function(){
            jQuery(document).on('click', '[data-finance-subsection-table-container] .add-another-row', IFS.financeRows.addCostsRowHandler);
            jQuery(document).on('click', '[data-finance-subsection-table-container] .delete-row', IFS.financeRows.removeCostsRowHandler);
        },
        generateFragmentUrl : function(originalLink) {
            // Replace the non-javascript add and remove functionality with single fragment question HTML responses

            var originalHref = originalLink.attr('href');
            var urlParamsParts = originalHref.split('?');
            var urlPart = urlParamsParts[0];
            var paramsPart = urlParamsParts.length == 2 ? ('&' + urlParamsParts[1]) : '';

            var questionToUpdate = originalLink.parents('[data-question-id]');
            var owningQuestionId = questionToUpdate.attr('data-question-id');
            var dynamicHref = urlPart + '/' + owningQuestionId + '?singleFragment=true' + paramsPart;
            return dynamicHref;
        },
        addCostsRowHandler : function(e) {
            // Add a new row from the HTML fragment being returned, and bind / rebind the autocalc and autosave behaviours again to ensure it behaves as
            // per other fields and that the repeating-total fields have a back-reference to any added fields

            var amendRowsLink = jQuery(this);
            var dynamicHref = IFS.financeRows.generateFragmentUrl(amendRowsLink);

            jQuery.get(dynamicHref, function(data) {
                var htmlReplacement = jQuery('<div>' + data + '</div>');
                var tableSectionToUpdate = amendRowsLink.parents('[data-finance-subsection-table-container]');
                var tableSectionId = tableSectionToUpdate.attr('data-finance-subsection-table-container');
                var replacement = htmlReplacement.find('[data-finance-subsection-table-container=' + tableSectionId + ']');
                tableSectionToUpdate.replaceWith(replacement);
                IFS.autoSave.init();
            });
            e.preventDefault();
            return false;
        },
        removeCostsRowHandler : function(e) {
            var amendRowsLink = jQuery(this);
            var dynamicHref = IFS.financeRows.generateFragmentUrl(amendRowsLink);

            jQuery.get(dynamicHref, function() {
                var costRowsId = amendRowsLink.attr('data-cost-row');
                var costRowsToDelete = jQuery('[data-cost-row=' + costRowsId + ']');
                costRowsToDelete.find('[data-calculation-fields],[data-calculation-input]').val(0).attr('data-calculation-rawvalue',0).trigger('change');
                costRowsToDelete.remove();
            });
            e.preventDefault();
            return false;
        }        
    };
})();