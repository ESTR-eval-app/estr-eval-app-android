# Eval n Android Application

This application provides a native Android app for completing evaluations, and consists predominantly of a web view which encapsulates the mobile first responsive web app.

For information about process of selecting and completing evaluations, see the readme in the mobile web app project.

## Google play
The current application can be downloaded from Google Play here: https://play.google.com/store/apps/details?id=ca.stevenlyall.evaln

## App version
Each time the app starts, it connects to the internet to determine whether the current version of the Android app is the most recent, and directs for the application to be updated before it can be used.

## Ensuring network connectivity
Since the application's content relies entirely on the web application, it cannot be used without an internet connection. The application checks to ensure that the device is connected before attempting to retrieve content from the server.
