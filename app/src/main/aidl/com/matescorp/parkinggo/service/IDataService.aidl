// IDataService.aidl
package com.matescorp.parkinggo.service;

// Declare any non-default types here with import statements

import com.matescorp.parkinggo.service.IDataServiceCallback;

interface IDataService {

    boolean registerCallback(IDataServiceCallback callback);
    boolean unregisterCallback(IDataServiceCallback callback);
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);
}
