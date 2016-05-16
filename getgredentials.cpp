/*****
 *  This is free software, use it as you like
 * (c) Marcus Hünd 2014 - 2015
 * 
 * */
 
#include "GetCredentials.h"
#include "application.h"

GetCredentials::GetCredentials() {

}

void GetCredentials::begin() {
    state = START;
    lc = 0;
    analogPort = A3;   // change if you need to another analog input
    setThreshold(analogPort);
    
    setDurations(40000);
    pinMode(D7, OUTPUT);

    pinMode(A0, INPUT_PULLDOWN);
    resetWatchDog();
}

void GetCredentials::setDurations(int lead) {
    leadIn = lead - lead / 10;
    streep = lead / 5;
    dot = lead / 8 - lead / 10;
    bitBreak = dot;
    charBreak = lead / 24;
    threshold = 250;
    //  print('*');
    //  print(streep);
    //  print(dot);
    //  Serial1.println();
    memset(credBuf, 0, sizeof (credBuf));
    cbi = 0;
    credChar = 0;
    cci = 0;

}

bool GetCredentials::watchDogOk() {

    if (state == START || (millis() - watchDog) < 2000)
        return true;
    else {
        blink(false);
        state = START;
        resetWatchDog();

        return false;
    }
}

void GetCredentials::resetWatchDog() {
    watchDog = millis();
    //   print('.');
}

bool GetCredentials::listen() {
    int result = false;
    if (watchDogOk()) {

        if (getPuls()) {
            resetWatchDog();
            switch (state) {
                case START:
                    if (puls > streep) {
                        state = LISTENING;
                        setDurations(puls);
                    }
                    break;
                case LISTENING:
                    int bit = 0;
                    if (puls < dot)
                        state = START;
                    else
                        if (puls < streep)
                        bit = 0;
                    else
                        bit = 1;
#ifdef DEBUG_GC 
                    print(bit);
#endif   
                    result = addPulsToCredBuffer(bit);
                    break;
            }
        }
    }
    return result;
}

bool GetCredentials::setPointers() {
    bool result = false;
    for (int ii = 0; ii < cbi - 3; ii++)
        if (credBuf[ii] == ',') {
            credBuf[ii] = 0;
            ptrPassword = credBuf;
            ptrSSID = credBuf + ii + 1;
            result = true;
            break;
        }
    return result;
}

int GetCredentials::getPuls() {
    long start = millis();

    digitalWrite(D7, LOW);
    long t1 = start;
    long t2 = micros();
    for (puls = 0; (millis() - start) < 50000 && millis() - t1 < 5;) {
        int vv = analogRead(analogPort);
        if (vv > threshold) {
            print ('>');print(vv);print(threshold);
            digitalWrite(D7, HIGH);
            t1 = millis();
        }
    }
    puls = (micros() - t2);
    if (puls < 6000)
        puls = 0;
    return puls;
}

bool GetCredentials::addPulsToCredBuffer(int bit) {
    bool result = false;
    credChar |= (bit << (7 - cci++));
    if (cci > 7) {
        cci = 0;
        if (credChar == '|') {
            state = START; //CHECK_CREDENTIALS;
            if ((result = calcChecksum())) {
                state = START;
                blink(true);
                setCredentials();
            } else
                blink(false);
        } else {
            credBuf[cbi++] = credChar;
#ifdef DEBUG_GC    
            Serial1.println(credChar);
#endif    
            credChar = 0;
        }
    }
    return result;
}

void GetCredentials::blink(bool color) {
    RGB.control(true);
    for (int ii = 0; ii < 3; ii++) {
        if (color == true)
            RGB.color(255, 70, 0);
        else
            RGB.color(128, 0, 0);
        delay(200);
        RGB.color(0, 0, 0);
        delay(200);
    }
    RGB.control(false);
}

bool GetCredentials::calcChecksum() {
    byte cnt = 0;
    int ndx = cbi - 3;

    credBuf[cbi] = 0;

    byte chkSum = atoi(credBuf + ndx);
#ifdef DEBUG_GC
    Serial1.println("\nc:");
    print(chkSum);
    print('|');
#endif
    for (int ii = 0; ii < ndx; ii++) {
        cnt += (byte) credBuf[ii];
#ifdef DEBUG_GC
        print(credBuf[ii]);
#endif
    }
#ifdef DEBUG_GC
    print('|');
    Serial1.print("\nchecksums: ");
    print(chkSum);
    print(cnt);
#endif
    credBuf[ndx] = 0;
#ifdef DEBUG_GC
    Serial1.println(credBuf);
#endif
    if (cnt == chkSum && setPointers()) {
        return true;
    } else
        return false;
}
#ifdef DEBUG_GC

void GetCredentials::print(char vv) {

    lc++;
    Serial1.print(vv);
    Serial1.print(" ");
    if (lc > 80) {
        //    Serial1.println();
        lc = 0;
    }
}

void GetCredentials::print(int vv) {
    lc++;
    Serial1.print(vv);
    Serial1.print(" ");
    if (lc > 20) {
        //    Serial1.println();
        lc = 0;
    }
}
#endif 

void GetCredentials::setCredentials() {
    memcpy((char *) password, ptrPassword, 122);
    memcpy((char *) ssid, ptrSSID, 122);
    WiFi.setCredentials(ssid, password);
    NVIC_SystemReset();
}

void GetCredentials::setThreshold(int analogPort) {
    int lowest;
    int highest;
    int buf[32];
    int val;
    
    for (int ii = 0; ii < 32; ii++) {
        buf[ii] = analogRead(analogPort);
        delay(10);
    }
    lowest = buf[0];
    highest = buf[0];
    for (int ii = 1; ii < 32; ii++) {
        val = buf[ii];
        if (lowest > val)
            lowest = val;
        if (highest < val)
            highest = val;
    }
    int delta = highest - lowest;
    Serial1.println(lowest);
    Serial1.println(highest);
    
    threshold = lowest + (delta / 2) + (lowest * 0.3);
    Serial1.print("th:");
    Serial1.println(threshold);

}
