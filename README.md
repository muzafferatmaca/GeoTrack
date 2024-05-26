# GeoTracker

GeoTracker is an Android application designed to provide seamless location tracking capabilities. It continuously monitors the user's location, adding markers on the map at every 100-meter interval of position change. The application is optimized to track location both in the foreground and background for extended periods.

## Objectives

The primary objective of GeoTracker is to offer robust location tracking functionality with the following features:

- Track user's position and add markers on the map at every 100-meter interval of position change.
- Ensure continuous location tracking in both foreground and background modes.
- Display address information for each marker by tapping on them.
- Enable users to start or stop location tracking at their discretion.
- Provide an option to reset the route, while preserving the current route until reset.

## Libraries Used

GeoTracker utilizes the following libraries to achieve its functionality:

- **Navigation**: androidx-navigation-fragment-ktx, androidx-navigation-ui-ktx
- **Dependency Injection**: hilt-android, hilt-compiler
- **Maps**: google-maps, google-location, google-maps-utils, google-maps-places
- **Data Persistence**: androidx-datastore
- **Lifecycle Management**: androidx-lifecycle-viewmodel, androidx-lifecycle-runtime, androidx-lifecycle-service
- **Animation**: airbnbLottie
- **Asynchronous Programming**: coroutine-core, coroutine-android, coroutine-playServices

## Project Structure

GeoTracker follows a modular structure with the main content encapsulated within the "GeoTracker" module. The application primarily focuses on location tracking and visualization on the map.

## Usage

To use GeoTracker, simply install the application on your Android device. Upon launching, grant necessary location permissions when prompted. The application will then start tracking your location automatically. You can view your route on the map with markers added at every 100-meter interval.

### Features

- **Continuous Location Tracking**: GeoTracker ensures uninterrupted location tracking, even when running in the background.
- **Address Information**: Tap on any marker to view detailed address information for the corresponding location.
- **Route Reset**: Optionally reset the route at any time to start afresh.

## Contributions

Contributions to GeoTracker are welcome! If you have any suggestions, enhancements, or bug fixes, feel free to open an issue or submit a pull request.

## License

GeoTracker is licensed under the MIT License.
