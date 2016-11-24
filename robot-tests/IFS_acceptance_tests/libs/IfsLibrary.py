from robot.libraries.BuiltIn import BuiltIn

__version__='0.1.0'

def wait_for_autosave(formselector='css=[data-autosave]', completeselector='css=[data-save-status="done"]'):
    s2l = BuiltIn().get_library_instance('Selenium2Library')
    autosavedone =  BuiltIn().run_keyword_and_return_status('Element Should Be Visible', formselector)
    if(autosavedone):
        s2l.wait_until_page_contains_element(completeselector)

def repeat_string(string='', multiplicity=0):
    return string * int(multiplicity);
