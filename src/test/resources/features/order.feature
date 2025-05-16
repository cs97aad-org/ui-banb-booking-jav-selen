@order @AllTests
Feature: Order Management - Buy Product

  Background:
    Given I am an authenticated user

  @buy
  Scenario: Successfully create a buy order for a product
    Given a new product is created
    When I place a buy order with quantity 1
    Then the response status should be 201
    And the buy order response should include the updated stock

  @buy
  Scenario: Fail to place a buy order with an invalid order type
    Given a new product is created
    When I place a buy order with invalid order type "purchase"
    Then the response status should be 400
    And the error message should be "Invalid order type. Must be \"buy\" or \"sell\""

  @buy
  Scenario: Fail to place a buy order with an invalid product ID
    When I place a buy order for invalid productId "1234555555"
    Then the response status should be 404
    And the error message should be "Product not found"

  @sell
  Scenario: Successfully create a sell order for a product
    Given a product is created and stocked with quantity 3
    When I place a sell order with quantity 2
    Then the response status should be 201
    And the sell order response should include the updated stock

  @sell
  Scenario: Fail to sell more than the available stock
    Given a product is created and stocked with quantity 1
    When I place a sell order with quantity 2
    Then the response status should be 400
    And the error message should be "Insufficient stock for sale"

  @stock
  Scenario: Retrieve current stock level after buy and sell
    Given a product is created and stocked with quantity 3
    When I place a sell order with quantity 2
    Then the response status should be 201
    When I retrieve the stock level for that product
    Then the stock level response should show 3 buys, 2 sells and 1 in stock

  @stock @new
  Scenario: Retrieve stock after buy and sell
    Given a product is created and stocked with quantity 3
    When I place a sell order with quantity 2
    Then the response status should be 201
    When I retrieve the stock level for that product
    Then the response should contain stock summary with 3 buys, 2 sells, and 1 in stock

  @stock
  Scenario: Retrieve stock for product with no transactions
    Given a new product is created without any transactions
    When I retrieve the stock level for that product
    Then the response status should be 404
    And the error message should be "No orders found for this product"
