// IDataServiceCallback.aidl
package com.matescorp.parkinggo.service;

interface IDataServiceCallback {
	oneway void valueChanged(long value);
}
