# CodeTest

[![Platform](https://img.shields.io/badge/Android-4-brightgreen.svg)](http://developer.android.com)&nbsp;

CodeTest is a coding test sample `Android` Application

> The application has a library module that is testing a user against the possiblity of him having the Todd's Syndrome

  - Uses `Sqlite` for storing data
  - Uses separate UI for Tablets 
  - Uses a `ViewPager` for showing up the questions with custom animations
  - Has `Unit Tests`

### Application design

```
 - The questionaire is in a library and stored in a normalized Sqlite Database
 - Explanation of the Database Design is included on the DatabasHandler helper file
 - App has been designed so that it can easily be switched from a local database to a remote database with a REST API
 - Should we want to switch to a REST API, we only will need a few endpoints
```

### REST API design considerations:
- Rest Api Should only need a few endpoints
- Endpoint for getting all questions available and their answers with a single parameter to check the current version of the local questions database
- Endpoint for adding a new user (email) (a password will also be required for security reasons, password should be hashed)
- Endpoint for checking if a user already exists
- Endpoint for storing the users answers and getting as a response the result
- If we want to totally isolate the questionaire we can skip the local database and only store and retrieve data from the REST endpoint
- REST Api requests and responses should all be in JSON format

### Installation

The application should run to all devices running Android with SDK version 14 or later (Ice Cream Sandwich)
According to Google this covers 97.5% of all Android devices at this moment of writing

&copy; 2017 Lefteris Haritou
