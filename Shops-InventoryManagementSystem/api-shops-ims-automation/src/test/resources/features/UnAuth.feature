@UnAuth @AllTests

Feature: User UnAuthenticated

  Scenario: Fail to create product without authentication
    Given I generate a random product
    When I send the create product request without authentication
    Then the response status should be 401
    And the error message should be "No token, authorization denied"

  @GetProduct @401 @ignore_notInSwagger
  Scenario: Fail to retrieve product without authentication
    When I retrieve the product by ID without authentication
    Then the response status should be 401
    And the error message should be "Invalid token"

  @GetAllProducts @ignore_notInSwagger
  Scenario: Fail to retrieve all products without authentication
    When I retrieve all products without authentication
    Then the response status should be 401
    And the error message should be "Invalid token"

  @UpdateProduct @401
  Scenario: Fail to update a product without authentication
    When I update the product without authentication
    Then the response status should be 401
    And the error message should be "Invalid token"

  @401
  Scenario: Fail to delete product without authentication
    When I delete the product without authentication
    Then the response status should be 401
    And the error message should be "Invalid token"

  @Buy @401 @scenario1
  Scenario: Fail to place a buy order without authentication
    Given a new product is created
    When I place a buy order without authentication
    Then the response status should be 401
    And the error message should be "Invalid token"

  @stock @401
  Scenario: Fail to retrieve stock without authentication
    When I retrieve the stock level for that product without authentication
    Then the response status should be 401
    And the error message should be "Invalid token"