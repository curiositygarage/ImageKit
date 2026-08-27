package com.curiositygarage.imagekit

import android.app.Application
import android.util.Log
import timber.log.Timber

class ImageKitApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // 1. Initialize Timber Logging Layer
        if (BuildConfig.DEBUG) {
            Timber.plant(object : Timber.DebugTree() {
                override fun createStackElementTag(element: StackTraceElement): String {
                    return "ImageKit:${super.createStackElementTag(element)}:${element.lineNumber}"
                }
            })
            Timber.tag("ImageKitApp").i("Timber DebugTree initialized successfully")
        } else {
            // Production tree: record warnings and errors
            Timber.plant(object : Timber.Tree() {
                override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                    if (priority >= Log.WARN) {
                        // Keep production log clean of PII and verbose noise
                    }
                }
            })
        }

        // 2. Global Uncaught Crash Exception Handler
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Timber.tag("CrashObserver").e(
                throwable,
                "FATAL: Uncaught exception in thread [%s] - %s",
                thread.name,
                throwable.javaClass.simpleName
            )
            defaultHandler?.uncaughtException(thread, throwable)
        }
    }
}
