@auth @AllTests

#  Test designed and developed by Idris Odulami -ID number: 13768192 #

Feature: User Authentication

  Scenario: Successful login with valid credentials
    Given I have a valid username and password
    When I send a login request
    Then I should receive a token

  Scenario: Successfully login with valid credentials
    Given I have a valid username and password
    When I send a login request
    Then the response status should be 200
    And the response should contain a non-empty token

  Scenario: Login with invalid credentials
    Given I have invalid login details
    When I send a login request
    Then I should receive a 400 status code with message "Invalid credentials"

  Scenario: Login with malformed request
    Given I have a malformed login payload
    Then I should receive a 500 status code with message "Illegal arguments: undefined, string"