*** Keywords ***
Capture Large Screenshot If Test Failed
    Run Keyword Unless ${CURRENTLY_WAITING_UNTIL}      Set Window Size     1920    8000
    Run Keyword Unless ${CURRENTLY_WAITING_UNTIL}      Capture Page Screenshot
    Run Keyword Unless ${CURRENTLY_WAITING_UNTIL}      Set Window Size     1024    768