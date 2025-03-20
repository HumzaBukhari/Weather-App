This is a simple weather app I made in my software design class. It uses the National Weather Services API to get data on weather conditions throughout the US.

# Wireframe
Since it was my first time making one, I opted to do it by hand. In the future, I'll probably try to use Figma or something similar.
![Project 2 Wireframe-1](https://github.com/user-attachments/assets/dc33744c-ef4a-4fbc-ae59-1e49c1793ea4)

# Final Product Images
## Main Scene
![Weather App Main Scene](https://github.com/user-attachments/assets/87205ae3-9b1f-4c7e-ab69-c215d16d13b3)
## Three Day Forecast Scene
![Weather App Three Day Scene](https://github.com/user-attachments/assets/6ce06caa-1aa5-4139-8c0f-3236e4a09252)
## Change Locations Scene
![Weather App Change Locations Scene](https://github.com/user-attachments/assets/b4d96add-6dce-4ba4-a903-7716ed73904c)

# Final Comments
- The main forecast contains wind speed and direction and both short and detailed descriptions. It is extra information for the user to have.
- A Text Flow FX object is used to wrap the detailed description around the window so longer descriptions are fully shown without needing a super long window.
- The background color changes whether it is daytime or nighttime. The three-day forecast will also change colors depending on whether it is a daytime or nighttime forecast, a simple UI change that makes the program look neater.
- I wanted to make custom graphics for different weather conditions and times of day based on the short description. Unfortunately, I soon discovered that the short description that the National Weather Service API provides doesn’t have a set number of outputs, and there could be hundreds of possible combinations. Thankfully, the API already provides a graphic representing the weather and time of day. I added that graphic to the verbal description to add a visual element to my app.
- There is a change location scene that fully works by taking latitude and longitude coordinates and putting them through the api.weather.gov/points, which will convert them into grid coordinates and ID. I was given a class that would take these grid coordinates and IDs and return the forecast for those areas. The ability to change locations greatly expands the app's functionality to provide forecasts throughout the US.
- After a user inputs latitude and longitude coordinates, the program will attempt to find the grid coordinates. If successful, the enter button will display “Success”; if it is unable to find the coordinates, the enter button will display “Failure.” A timer object displays this information for 3 seconds before returning to the main scene. This feedback will ensure that the user will know whether their change went through or not, smoothing out the process.
