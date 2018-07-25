from robot.libraries.BuiltIn import BuiltIn

se2lib = BuiltIn().get_library_instance('Selenium2Library')

def the_user_closes_the_last_opened_tab():
    browser = se2lib._current_browser()  # This is used because switch_to needs a webdriver instance, not a Selenium2Library instance.
    browser.switch_to.window(browser.window_handles[-1])  # Switches to the last opened tab.
    browser.close()
    browser.switch_to.window(browser.window_handles[0])   # Switches back to the main tab.