# weatherfm

Coding Test

Service https://openweathermap.org/ provides an open API to retrieve weather forecast.
You should register to obtain an API key to have an ability to send requests.

The task is to create an application that accepts a city name as a parameter and shows a list with 5 day weather forecast.
The application should display the following information about each item:
- date
- weather description
- temperature in degrees celsius
The app should start an activity and add fragment where the result will be displayed.
Orientation changes should work as expected, and the data shall not be reloaded when screen orientation changes.
There also should be an ability to find current city by GPS, so that user doesn't need to type it in.

Task requirements
Feel free to spend as much or as little time on the exercise as you like, as long as the following requirements have been met:
- Code must compile and run in one step
- Feel free to use whatever frameworks / libraries / packages you like.
- You can use Java, Kotlin or even combine them

Acceptance criteria
- For the known city name Kyiv forecast is returned
- The date, weather description and temperature in degrees celsius are dispayed
