from robot.libraries.BuiltIn import BuiltIn

__version__='0.1.0'

def wait_for_autosave(formselector='css=[data-autosave]', completeselector='css=[data-save-status="done"]'):
    wait_until = BuiltIn().get_library_instance('WaitUntilLibrary')
    autosavedone =  wait_until.run_keyword_and_return_status_without_screenshots('Element Should Be Visible', formselector)
    if(autosavedone):
        wait_until.wait_until_page_contains_element_without_screenshots(completeselector)


def repeat_string(string='', multiplicity=0):
    return string * int(multiplicity);


# Performs a keyword on a paginated page, clicking through to the next pages if the keyword is not successful, until
# either the keyword succeeds or we run out of pages
def do_keyword_with_pagination(keyword, *args):

    wait_until = BuiltIn().get_library_instance('WaitUntilLibrary')

    keyword_succeeded = wait_until.run_keyword_and_return_status_without_screenshots(keyword, *args)

    if keyword_succeeded:

        return 'PASS'

    else:

        next_button_clicked = wait_until.run_keyword_and_return_status_without_screenshots('The user clicks the button/link', 'css=li.next a')

        if not next_button_clicked:
            return 'FAIL'
        else:
            return do_keyword_with_pagination(keyword, *args)


# same functionality as do_keyword_with_pagination() with the distinction that it will not cause a test failure if
# the keyword fails on every page
def do_keyword_with_pagination_and_ignore_error(keyword, *args):

    wait_until = BuiltIn().get_library_instance('WaitUntilLibrary')

    keyword_succeeded = wait_until.run_keyword_and_return_status_without_screenshots(keyword, *args)

    if keyword_succeeded:

        return True

    else:

        next_button_clicked = wait_until.run_keyword_and_return_status_without_screenshots('The user clicks the button/link', 'css=li.next a')

        if not next_button_clicked:
            return False
        else:
            return do_keyword_with_pagination_and_ignore_error(keyword, *args)


