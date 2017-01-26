from robot.libraries.BuiltIn import BuiltIn

__version__='0.1.0'

def wait_for_autosave(formselector='css=[data-autosave]', completeselector='css=[data-save-status="done"]'):
    waitUntil = BuiltIn().get_library_instance('WaitUntilLibrary')
    autosavedone =  waitUntil.ifs_run_keyword_and_return_status('Element Should Be Visible', formselector)
    if(autosavedone):
        waitUntil.ifs_wait_until_page_contains_element(completeselector)

def repeat_string(string='', multiplicity=0):
    return string * int(multiplicity);
