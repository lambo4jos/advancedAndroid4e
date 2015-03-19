package com.advancedandroidbook.simpleservice;

import com.advancedandroidbook.simpleservice.GPXPoint;


interface IRemoteInterface {

    Location getLastLocation();
    GPXPoint getGPXPoint();
}
