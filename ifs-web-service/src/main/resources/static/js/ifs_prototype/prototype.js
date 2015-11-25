jQuery(document).ready(function(){
	jQuery('body').on('click','.js-add-partner',function(e){

		e.preventDefault();
var html = '<li>\
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
                                <a class="delete-row" data-cost-row="12" href="javascript:void()">Remove</a>\
                            </td>\
                        </tr>\
                    </tbody>\
                </table>\
                <p class="alignright">\
                    <a class="add-another-row" href="javascript:void(0)">Add contributor</a>\
                </p>';

		jQuery('.boxed-list li:nth-last-child(2)').after(html);
	});

	jQuery('body').on('click','.add-another-row',function(e){
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
               jQuery(this).closest('li').find('tbody').append(html);
	});

	jQuery('body').on('keyup','.js-organisation-name',function(e){
			if(jQuery(this).val() > 0){
				jQuery(this).closest('li').find('h2 span').html('"'+jQuery(this).val()+'"');
			}
			else {
				jQuery(this).closest('li').find('h2 span').html('');
			}
	});

	jQuery('body').on('click','.delete-row',function(){
			if(jQuery(this).closest('table').find('tr') == 1){
				jQuery(this).closest('li').remove();
			}
			else {
				jQuery(this).closest('tr').remove();

			}
	});
});