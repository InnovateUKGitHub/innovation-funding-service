from robot.libraries.BuiltIn import BuiltIn

se2lib = BuiltIn().get_library_instance('Selenium2Library')

def get_webdriver_instance():
    return se2lib._current_browser()  # This is used because switch_to needs a webdriver instance, not a Selenium2Library instance.

def the_user_closes_the_last_opened_tab():
    get_webdriver_instance().switch_to.window(get_webdriver_instance().window_handles[-1])  # Switches to the last opened tab.
    get_webdriver_instance().close()
    get_webdriver_instance().switch_to.window(get_webdriver_instance().window_handles[0])   # Switches back to the main tab.
