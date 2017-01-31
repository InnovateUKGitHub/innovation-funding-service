from logging import warn

from robot.libraries.BuiltIn import BuiltIn

__version__='0.1.0'

def wait_for_autosave(formselector='css=[data-autosave]', completeselector='css=[data-save-status="done"]'):
    wait_until = BuiltIn().get_library_instance('WaitUntilLibrary')
    autosavedone =  wait_until.run_keyword_and_return_status_without_screenshots('Element Should Be Visible', formselector)
    if(autosavedone):
        wait_until.wait_until_page_contains_element_without_screenshots(completeselector)


def repeat_string(string='', multiplicity=0):
    return string * int(multiplicity);
