//very specific piece of code for having a organisation size change make adjustments to the max attribute for the funding level
//After that we re-evaluate that value with the validation
IFS.application.financeSpecifics = (function() {
  "use strict";
  var s;
  return {
    settings : {
      funderOrgFeedback: {
        funderOrgsizeInputs : '[name="financePosition-organisationSize"]',
        fundingLevelInput : '#cost-financegrantclaim',
        orgToFundingMapping :  {
          'SMALL' : 70,
          'MEDIUM' : 60,
          'LARGE' : 50
        }
      },
      administrationCostTotal : {
        adminSupportCosts : '[name*="overheads-type"]',
        supportCostInTotal : {
          'NONE' : '#section-total-10-labour-cost-none',
          'DEFAULT_PERCENTAGE' : '#section-total-10-default',
          'CUSTOM_RATE' : '#section-total-10-custom'
        },
        allTotals :  '[id*="section-total-10"]',
        totalCostInHeaders : '[data-mirror="#section-total-10"]'
      }
    },
    init : function() {
      s = this.settings;
      IFS.application.financeSpecifics.initFunderOrgSizeFeedback();
      IFS.application.financeSpecifics.initAdministrationCostTotal();
      IFS.application.financeSpecifics.initOtherFunding();
    },
    initAdministrationCostTotal : function() {
      //if the radio button % labour changes,
      //the total displayed in the header also changes.
      //All the other totals will get reset to 0 pounds.
      var el = jQuery(s.administrationCostTotal.adminSupportCosts+':checked');
      IFS.application.financeSpecifics.resetOtherAdministrationTotal(el);
      IFS.application.financeSpecifics.placeRightAdministrationTotalInHeader(el);

      jQuery(document).on('change', s.administrationCostTotal.adminSupportCosts, function() {
        IFS.application.financeSpecifics.resetOtherAdministrationTotal(this);
        IFS.application.financeSpecifics.placeRightAdministrationTotalInHeader(this);
      });
    },
    placeRightAdministrationTotalInHeader  : function(element) {
      var val = jQuery(element).val();
      var currentTotal = typeof(s.administrationCostTotal.supportCostInTotal[val]) !== 'undefined' ? s.administrationCostTotal.supportCostInTotal[val] : false;
      var mirrorElement = jQuery(s.administrationCostTotal.totalCostInHeaders);

      if(currentTotal && mirrorElement.length){
        IFS.core.mirrorElements.updateElement(mirrorElement, currentTotal);
        jQuery(document).off('change', s.administrationCostTotal.allTotals).on('change', currentTotal, function() {
          IFS.core.mirrorElements.updateElement(mirrorElement, this);
        });
      }
    },
    resetOtherAdministrationTotal : function(element) {
      //reset the totals for all other sections in administraton costs
      var radio = jQuery(element).closest('[data-target]');
      var section = jQuery('#'+jQuery(radio).attr('data-target'));
      var otherRadios = radio.prevAll('[data-target]').add(radio.nextAll('[data-target]'));

      otherRadios.each(function() {
        var section = jQuery('#'+jQuery(this).attr('data-target'));
        var totalField = section.find(s.administrationCostTotal.allTotals);
        var id = totalField.attr('id');

        totalField.attr('data-old-id', id).removeAttr('id').val('£ 0').attr('data-calculation-rawvalue', 0);
      });

      section.find('[data-old-id]').each(function() {
        var id = jQuery(this).attr('data-old-id');
        jQuery(this).attr('id', id).removeAttr('data-old-id');
        jQuery('#section-total-9').trigger('updateFinances');
      });
    },
    initFunderOrgSizeFeedback : function() {
      //If the organisation size changes, the max funding will also update.
      //We use the normal max field validation but do trigger a change
      jQuery('body').on('change', s.funderOrgFeedback.funderOrgsizeInputs, function() {
        var orgSize = jQuery(this).val();
        var funingLevelEl = jQuery(s.funderOrgFeedback.fundingLevelInput);
        if(funingLevelEl.length && (typeof(s.funderOrgFeedback.orgToFundingMapping[orgSize]) !== 'undefined')){
          funingLevelEl.attr('max', s.funderOrgFeedback.orgToFundingMapping[orgSize]).removeClass('field-error');
          var formGroup = funingLevelEl.closest('.form-group');
          formGroup.removeClass('error');
          formGroup.find('.error-message').remove();
          setTimeout(function() {
            // delay, so the orgSize is saved, before the funding level validation is executed.
            funingLevelEl.trigger('change');
          }, 1000);

        }
      });
    },
    initOtherFunding : function() {
      //make sure that the total gets updated on pressing yes, for more info INFUND-3196
      jQuery("#otherFundingShowHideToggle").on('change', '[data-target] input[type="radio"]', function() {
        setTimeout(function() {
          jQuery('[id*=fundingAmount]').trigger('updateFinances');
        }, 0);
      });
    }
  };
})();
