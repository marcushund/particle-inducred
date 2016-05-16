// This #include statement was automatically added by the Spark IDE.
#include "GetCredentials.h"


GetCredentials gc;

void setup() {
    Serial1.begin(115200);
 
    gc.begin();
    Serial1.println("start");
}

 
//  char password[64];
//  char SSID[64];
 
 
void loop() {
     
    if (gc.listen()){
   //     Spark.publish("SSID",WiFi.SSID());
        Serial1.println("OK");
    }
 
}




