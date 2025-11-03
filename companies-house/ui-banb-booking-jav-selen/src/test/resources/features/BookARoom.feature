
Feature: Book a Room on the Hotel Booking Website
As a potential guest
I want to book a room
So that I can stay at Shady Meadows B&B

  Background:
    Given User is on the home page
    And User scrolls to the "Check Availability & Book Your Stay" section
    And the check-in date is defaulted to today’s date, and checkout is defaulted to tomorrow’s date
    When User enters a valid check-in date "10" days from today, and a check-out of "1" nights
    And User clicks on Check Availability
    Then User should see "Our Rooms" section
    And User should see available rooms for the selected dates -  room type, Single, Double and Suite rooms; price and the Book Now button.
    And Click on the Book Now button.

    And User is navigated to the booking page showing the Room type selected
    And The page also shows the "Room Description" section
    And the booking page shows the header "Book This Room" and the price per night
    And The selected date matches the check-in and check-out date with offset "10" and nights "1"
    And The calendar highlights the selected range for offset "10" and nights "1"
    And The price summary section is shown

    And User clicks on the Reserve Now button
    And User sees booking form -  First Name, Last Name, Email and Phone number fields


  Scenario: Book a room successfully
    And User enters First Name, Last Name, Email and Phone Number
    And Click on the Reserve Now button

    And Booking Confirmed is displayed, showing the selected check-in date and check-out date.
    And the Return Home page button is shown.
    And User click on Return home button
    And User is navigated to the Home page showing the header "Welcome to Shady Meadows B&B"


  Scenario: User attempts to click Reserve Now without entering booking details
    And User clicks on the Reserve Now button without entering any data
    Then User sees validation messages for First Name, Last Name, Email and Phone Number fields
      | must not be empty              |
      | Lastname should not be blank   |
      | Firstname should not be blank  |
      | size must be between 11 and 21 |
      | must not be empty              |
      | size must be between 3 and 30  |

    Scenario: User cancels the booking process
      And Click on Cancel Booking button
      And The calendar highlights the selected range for offset "10" and nights "1"




