# ResQSync
ResQSync is a powerful Android application designed to provide users with a comprehensive solution for ensuring the safety and security of their family members. With its innovative real-time tracking feature, ResQSync empowers users to effortlessly monitor the whereabouts of their loved ones. Whether it's keeping an eye on children, elderly relatives, or even friends, ResQSync offers peace of mind by enabling users to stay connected and informed at all times.

## Development Setup Guide

1. Download [Android Studio](https://developer.android.com/studio?gclid=Cj0KCQjw4bipBhCyARIsAFsieCz3A1EeRIG97crAXWMC6_PYfbh25cP9Yvi3x1wZXnp3YyIG6-qtVPkaAmAgEALw_wcB&gclsrc=aw.ds)
2. Create a [Firebase](https://firebase.google.com/) account
    - Register for new Android application and follow the steps you would get your `google-service.json` file paste it in your root project file.
3. Register for Google Developers console and get your API key (required for Google Maps API) as you won't be able to run the setup without it.
   - To get the API, follow the directions [here](https://developers.google.com/maps/documentation/android-sdk/get-api-key):
   - Once you have your API key (it starts with "AIza"), define a new property in your project's local.properties file (e.g. MAPS_API_KEY=Aiza...), and replace the "YOUR_API_KEY" string in this file with "${MAPS_API_KEY}".


