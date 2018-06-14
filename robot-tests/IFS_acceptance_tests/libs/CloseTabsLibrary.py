import selenium.webdriver as webdriver
from selenium.webdriver.common.keys import Keys
from robot.libraries.BuiltIn import BuiltIn

# The instance is created. This bit I'm not sure how or where 'browser' in our tests is referenced.
# main_window = s2l.current_window_handle
#
# def switch_to_then_close_tab():
#     driver = webdriver.Chrome()
#     main_window = driver.current_window_handle()
#     #main_window = browser.current_window_handle
#     # This is so we can switch back to it easier.
#
#     #s2l.find_element_by_tag_name("").send_keys(Keys.CONTROL + 'w')
#     driver.find_element_by_id("plugin").send_keys(Keys.CONTROL + 'w')
#     # Close current tab.
#
#     driver.switch_to_window(main_window)
# Back to the main tab.

def get_webdriver_instance():
    #driver = webdriver.Remote()
    se2lib = BuiltIn().get_library_instance('Selenium2Library')
    return se2lib.current_browser()

    se2lib.find_element_by_id("plugin").send_keys(Keys.CONTROL + 'w')
