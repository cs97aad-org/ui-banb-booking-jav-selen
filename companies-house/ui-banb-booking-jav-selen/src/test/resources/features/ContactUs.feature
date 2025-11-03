Feature: Contact Us
    As a visitor
    I want to send a message via the Contact form
    So that I can get in touch with the site owners

  Scenario: Send a message from the Contact form
    Given User is on the home page
    When User clicks on the Contact link on the navbar
    Then User is navigated to the Send Us a Message section
    And User sees the Name, Email, Phone, Subject and Message fields
    When User completes the Name, Email, Phone, Subject and Message fields
    And User clicks the Submit button
    Then User is shown a message "Thanks for getting in touch" with the senders Name and Subject entered
