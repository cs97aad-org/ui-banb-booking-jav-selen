@product @AllTests
Feature: Product Management

  Background:
    Given I am an authenticated user

  @CreateProduct
  Scenario: Create random product
    Given I generate a random product
    When I send the create product request
    Then the response status should be 201
    And the response should include the created product details

  @CreateProduct
  Scenario Outline: Fail to create product with invalid input
    When I create a product with name "<name>", price <price>, type "<type>", and quantity <quantity>
    Then the response status should be <status>
    And the error message should be "<message>"

    Examples:
      | name        | price | type    | quantity | status | message                                        |
      | product0001 | 0     | games   | 10       | 400    | Price must be greater than 0                   |
      | product0002 | 10.0  | games   | -5       | 400    | Validation failed                              |
      | product0003 | 15.99 | laptops | 5        | 400    | Product with this name and type already exists |
      | product0004 | 10.0  | car     | 10       | 400    | Validation failed                              |

  @GetProduct
  Scenario: Get the created product by ID
    Given a product has been created
    When I retrieve the product by ID
    Then the response status should be 200
    And the retrieved product details should match the created product

  @GetProduct
  Scenario: Fail to retrieve product with invalid ID
    When I retrieve a product using an invalid ID
    Then the response status should be 404
    And the error message should be "Product not found"

  @GetAllProducts
  Scenario: Retrieve all products
    Given a product has been created
    When I retrieve all products
    Then the response status should be 200
    And the created product should be listed in the response array

  @UpdateProduct
  Scenario: Update a created product successfully
    Given a product has been created
    When I update the product with a new name, price and quantity
    Then the response status should be 200
    And the response should reflect the updated product details

  @UpdateProduct
  Scenario: Fail to update a product with invalid ID
    Given I prepare valid product update data
    When I update a product using an invalid ID
    Then the response status should be 404
    And the error message should be "Product not found"

  @DeleteProduct
  Scenario: Successfully delete a product
    Given a product has been created
    When I delete the product by ID
    Then the response status should be 200
    And the delete response message should be "Product removed"

  @DeleteProduct
  Scenario: Fail to delete product with invalid ID
    When I delete a product using an invalid ID
    Then the response status should be 404
    And the error message should be "Product not found"