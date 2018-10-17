IoT Storytelling - Frontend
==========================
This project is meant to explore the field of Internet of Things combined with digital storytelling to show 
the interaction between user and devices over multiple communication channels.

The project was supervised by Andrew Perkis and Asim Hameed at the NTNU and implemented by Øyvind Klungre and Lukas Bernhard.

Project Description
--------------------------
The project contains two layers, an interactive and technological one. The Android App is mostly implementing the interactive part.
If you want to read more about the interactive part of the project, please visit [backend](https://github.com/itsStRaNge/iot_storytelling_backend.git).

The interactive layer contains everything the user can recognize. Multiple devices across a room with three communication channels 
each. Every device can react to events with displaying text, images, playing sounds or any possible combination. An event is triggered
by an interaction from the user with his environment. The participant has a device that serves as interface between physical and 
digital space. Possible user interactions can be scanning an QR-code, which is representing the position of the participant in 
physical space, or moving a virtual object on the device. This device also used to react to the events as described. 

Project Overview
--------------------------
**Configuration** At `Configuration.java` you can configure the Apps behaviour as followed:

* The App has two versions. It can be used as *actuator* that receives events and react to them, or as
*sensor* that triggers events based on the user input.

* You can set the App to Develop or Productive mode. This mode specifies the node that is being used
at the firebase realtime database. This enables no corruption of the productive system while developing
new features.

* If the firebase realtime database structure changes, then in the Configuration file the key values
can be adjusted.

**MainActivity** setups all necessary services like layout (depending on App mode), firebase service 
and loading screen

**FirebaseManager** Handles communication and notification of the firebase realtime database

**DownloadManager** Downloads all media files, received from the firebase realtime database, from the
http backend server.

**FileManager** Stores all media files in the internal storage of the device. It also loads audio, text 
and images to be used at the *MainActivity*.

**Sensor - Sensor Utilities** This handles all functions for the sensor mode. That contains the drag
and drop function of the bird, the starting of the qr code scanner and permission checking of the camera.

**Sensor - QRScanActivity** This is a wrapper for a third party library to scan qr codes.

**Sensor - UploadManager & UploadInterface** This provides a smooth handling of sending TCP packages
to the backend system.

Project Setup
-------------------------
The project was created with [Android Studio](https://developer.android.com/studio/). Use it to edit
the source code and to install the apk on the devices.