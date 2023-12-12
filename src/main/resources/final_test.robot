Test-login-1
   Open Browser   https://demoqa.com/login   Edge
   Input Text   xpath=//input[@id='firstName' and @placeholder='First Name']   NOTa1
   Click Element   NOTxpath=//input[@id='lastName' and @placeholder='Last Name']
   Should Go To   https://demoqa.com/profile
Test-login-2
   Open Browser   https://demoqa.com/login   Edge
   Input Text   xpath=//input[@id='firstName' and @placeholder='First Name']   NOTa1
   Click Element   xpath=//input[@id='lastName' and @placeholder='Last Name']
   Should Go To   https://demoqa.com/profile
Test-login-3
   Open Browser   https://demoqa.com/login   Edge
   Input Text   xpath=//input[@id='firstName' and @placeholder='First Name']   a1
   Click Element   NOTxpath=//input[@id='lastName' and @placeholder='Last Name']
   Should Go To   https://demoqa.com/profile
Test-login-4
   Open Browser   https://demoqa.com/login   Edge
   Input Text   xpath=//input[@id='firstName' and @placeholder='First Name']   a2
   Click Element   NOTxpath=//input[@id='lastName' and @placeholder='Last Name']
   Should Go To   https://demoqa.com/profile
