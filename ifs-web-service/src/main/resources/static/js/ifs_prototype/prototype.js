jQuery(document).ready(function(){

	jQuery('body').on('click','.js-add-partner',function(e){
		e.preventDefault();
        addPartner();
	});

	jQuery('body').on('click','.add-another-row',function(e){
        addTableRow(this);
	});

	jQuery('body').on('keyup','.js-organisation-name',function(e){
        organisationNameHeader(this);
	});

	jQuery('body').on('click','.delete-row',function(){
        deleteTableRow(this);
	});

    jQuery('.boxed-list li > input').trigger('keyup');

    
    function addPartner(){
        var html =  '<li>\
            <h2 class="heading-medium">Partner Organisation <span></span></h2>\
            <p>The lead organisation will be the lead partner throughout the application and project.Add members of your own organisation to contribute to this application.</p>\
          <label class="form-label" for="full-name-f1">Organisation name</label>\
          <input class="form-control js-organisation-name" placeholder="Your partner company Ltd."  id="full-name-f1" type="text">\
           <table>\
                    <thead>\
                    <tr>\
                        <th>Name</th>\
                        <th>E-mail</th>\
                        <th>&nbsp;</th>\
                    </tr>\
                    </thead>\
                    <tbody>\
                    <tr>\
                            <td>\
                                <input type="text" placeholder="name" name="name-item-1" id="name-item-1" value=""  />\
                            </td>\
                            <td>\
                                <input type="e-mail" placeholder="name@name.com" name="mail-item-1" id="mail-item-1" value="" />\
                            </td>\
                            <td class="alignright remove"> \
                                <a class="delete-row" data-cost-row="12" href="javascript:void(0)">Remove</a>\
                            </td>\
                        </tr>\
                    </tbody>\
                </table>\
                <p class="alignright">\
                    <a class="add-another-row" href="javascript:void(0)">Add Person</a>\
                </p>\
        </li>';

        jQuery('.boxed-list li:nth-last-child(2)').after(html);
    }




    function addTableRow(el){
        el = jQuery(el);
        var html = '<tr>\
                    <td>\
                        <input type="text" value="" placeholder="name" id="name-item-1" name="name-item-1">\
                    </td>\
                    <td>\
                        <input type="e-mail"  value="" placeholder="name@name.com" id="mail-item-1" name="mail-item-1">\
                    </td>\
                    <td class="alignright remove">\
                        <a class="delete-row" data-cost-row="12" href="javascript:void(0);">Remove</a>\
                    </td>\
                </tr>';
       el.closest('li').find('tbody').append(html);
    }


    function deleteTableRow(el){
        el = jQuery(el);
        if(el.closest('table').find('tr').length == 2){
            el.closest('li').remove();
        }
        else {
            el.closest('tr').remove();
        }
    }

    function organisationNameHeader(el){
        el = jQuery(el);
        if(el.val().length > 0){
            el.closest('li').find('h2 span').html('"'+el.val()+'"');
        }
        else {
            el.closest('li').find('h2 span').html('');
        }
    }


    //---- COMP ADMIN ASSIGN ASSESSORS -----//


    function addRow(assessor, skills, type, applications){
        //alert(assessor+' '+skills+' '+type+' '+applications);
        jQuery('#assessor-assigned').append(
            "<tr><th>"+ assessor +"</th>" +
            "<td>"+ skills +"</td>" +
            "<td>"+ type +"</td>" +
            "<td>"+ applications +"</td>" +
            "<td><a href='#' class='view-assessor'>View</a>" +
            "<td class='alignright'><a href='#' class='undo-assessor'>Undo</a>" +
            "</tr>"
            );
    };

    function numAvailable(count){
        var available = jQuery('.available').text();
        var available = parseInt(available);
        if (count == 'subtract'){
            jQuery('.available').html(available -1);
        }
        if (count == 'add'){
            jQuery('.available').html(available +1);
        }
        return available;
    }

    var counter = 0;

    jQuery('.assign-assessor').on('click',function(e){ 
        e.preventDefault();
        counter ++;

        var assessor = jQuery(this).parent().parent().find('th').text();
        var skills = jQuery(this).parent().parent().find('td:eq(0)').text();
        var type = jQuery(this).parent().parent().find('td:eq(1)').text();
        var applications = jQuery(this).parent().parent().find('td:eq(2)').text();

        jQuery(this).parent().parent().hide();
        jQuery('.assigned-count').html('('+counter+')');

        numAvailable('subtract');

        if(counter >= 1){
            jQuery('#no-assessors').hide();
        }

        addRow(assessor, skills, type, applications);
        //alert(skills);
    });

    jQuery('body').on('click','.undo-assessor',function(e){
        e.preventDefault();
        counter --;

        var assessor = jQuery(this).parent().parent().find('th').text();

        jQuery('#assessor-list tbody tr').each(function(){
            if(jQuery(this).find('th').eq(0).text() == assessor){
                jQuery(this).show();
                //alert('found');
            }
        });

        //alert(assessor);

        jQuery(this).parent().parent().hide();

        jQuery('.assigned-count').html('('+counter+')');

        numAvailable('add');

        if(counter <= 0){
            jQuery('#no-assessors').show();
        }

    });

    jQuery('body').on('click','.view-assessor',function(e){
        e.preventDefault();

        var url = "/prototypes/1383-application-allocate-applications?assessorName="
        var assessor = jQuery(this).parent().parent().find('th').text();
        var type = jQuery(this).parent().parent().find('td:eq(1)').text();
        var applications = jQuery(this).parent().parent().find('td:eq(2)').text();

        window.location.href = url+assessor+"&assessorType="+type+"&assessorApplications="+applications;
    });


});