/*****
 *  This is free software, use it as you like
 * (c) Marcus Hünd 2014 - 2015
 * 
 * */
#define DEBUG_GC

#define START 0
#define LISTENING 1
#define READY 2
#define CHECK_CREDENTIALS 3
#define CHECK_OK 4

class GetCredentials {
public:
    GetCredentials();
    void begin();
    bool listen();
    

private:
    int state;
    int puls;
    int lc; //linecounter used in print
    int charBreak;
    int watchDog;
    int threshold; //adc threshold value to determine a puls
    char credChar;
    char credBuf[128];
    char * ptrPassword;
    char * ptrSSID;
    int cci; //bit roll variable used to decode bit stream in to bytes
    int cbi; //index in credentials buffer
    int leadIn;
    int streep;
    int dot;
    int bitBreak;
    char password[32];
    char ssid[32];
    int analogPort;
    bool calcChecksum();
    bool watchDogOk();
    void resetWatchDog();
#ifdef DEBUG_GC
    void print(char vv);
    void print(int vv);
#endif
    void blink(bool color);
    int getPuls();
    bool addPulsToCredBuffer(int puls);
    void setDurations(int);
    bool setPointers();
    void setCredentials();
    void setThreshold(int port);
}; 
