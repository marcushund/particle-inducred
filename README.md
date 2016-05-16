# particle-inducred
Library to set the photons WiFi credentials through inductive coupling using your android phone and a relay coil.
Inducred stands for Inductive Credentials. It uses an inductive coupling between a loudspeaker of a smartphone and a relay coil. The coil is connected to an analog input of a Photon IOT building block of Particle.io. Smart makers can wire the relay so it can be used in the application as a relay too.
Setting WiFi credentials using this method gives you the opportunity to set SSID and password without being connected to the Particle Cloud. 
To make it work, create a new library on the Particle IDE with the following names: GetCredentials.cpp, GetCredentials.h
Copy the CPP and Header file to their respective files in the IDE and add the following lines to your application:


Download the java android app, build it and install it on your phone.
