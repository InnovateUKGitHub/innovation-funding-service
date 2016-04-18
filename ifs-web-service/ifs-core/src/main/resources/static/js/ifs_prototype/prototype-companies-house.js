jQuery(document).ready(function(){

    jQuery('#org-name').on('keydown',function(e){ 
        if (this.value.length>0){
            jQuery("#org-results").show();
            jQuery("#manual-company-input").hide();
        }else{
            jQuery("#org-results").hide();
        }

    });

    jQuery('#manual-company-trigger').on('click',function(e){ 
        e.preventDefault();
        jQuery("#manual-company-input").show();
        jQuery("#org-results").hide();

    });

    jQuery('#postcode-lookup').on('click',function(e){ 
        e.preventDefault();
        jQuery("#select-address-block").show();

    });

    jQuery('#select-address').on('change',function(e){ 
        e.preventDefault();
        jQuery("#address-details").show();
        jQuery("#street").val("King William House");
        jQuery("#street-2").val("13 Queens Square");
        jQuery("#town").val("Bristol");
        jQuery("#postcode").val("BS1 4NT");

    });

    jQuery('#enter-address-manually').on('click',function(e){ 
        e.preventDefault();
        jQuery("#address-details").show();
        jQuery("#select-address-block").hide();
    });

});