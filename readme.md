# About
- Do media streaming with skip ahead on GAE.
- Work on all major Browsers, OSs, and Devices.

# Run/Dev/Test
mvn appengine:devserver -Pdevserver

- upload video
  [localhost:8080/upload](localhost:8080/upload)

NOTE: see src/test/data folder for test files.

# Clean db
mvn clean

# Deployment
mvn clean appengine:update -Pupdate

# Demo
[http://wow-stream.appspot.com](http://wow-stream.appspot.com)
