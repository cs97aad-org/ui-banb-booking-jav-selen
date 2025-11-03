
Feature: Check Availability
As a potential guest
I want to check the availability of rooms
So that I can book a stay at Shady Meadows B&B

  Scenario: Verify user can check room availability
    Given User is on the home page
    Then Page title should be "Restful-booker-platform demo"
    And Header text should be "Welcome to Shady Meadows B&B"

    And User scrolls to the "Check Availability & Book Your Stay" section
    And Booking section title should be "Check Availability & Book Your Stay"
    And the check-in date is defaulted to today’s date, and checkout is defaulted to tomorrow’s date

    When User enters a valid check-in date "7" days from today, and a check-out of "1" nights
    And User clicks on Check Availability
    Then User should see "Our Rooms" section

    And User should see available rooms for the selected dates -  room type, Single, Double and Suite rooms; price and the Book Now button.





