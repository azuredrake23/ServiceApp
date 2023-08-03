package com.example.serviceapp.utils

object Constants {

    //-------------------------------------- UI start ----------------------------------------
    const val MAX_BAR_VALUE = 300
//    const val FREQ_MIN = 1.5
//    const val FREQ_MAX = 6000.0
//    const val STEP_MIN = 1
//    const val STEP_MAX = 1000
//    const val JUMP_FREQ_MIN = 1
//    const val JUMP_FREQ_MAX = 1000
//    const val DURATION_MIN = 0
//    const val DURATION_MAX = Int.MAX_VALUE
//    const val OUT_OF_RANGE_ERROR = R.string.out_of_range_error
//    const val VALUE_REQUIRED_ERROR = R.string.value_required_error

    //-------------------------------------- USB start ----------------------------------------
    const val INTENT_ACTION_GRANT_USB: String = com.hoho.android.usbserial.BuildConfig.LIBRARY_PACKAGE_NAME + ".GRANT_USB"
    const val INTENT_ACTION_DISCONNECT: String = com.hoho.android.usbserial.BuildConfig.LIBRARY_PACKAGE_NAME + ".Disconnect"
    const val ACTION_USB_PERMISSION = com.hoho.android.usbserial.BuildConfig.LIBRARY_PACKAGE_NAME + ".USB_PERMISSION"

    //-------------------------------------- Settings enter ----------------------------------------
    const val loginCheck = "admin"
    const val passwordCheck = "12345"

    //-------------------------------ServicePath start------------------------------
       const val BASE="https://10.101.25.91:446/"

    //-------------------------------------- Others ----------------------------------------
    const val ThresholdMin = 0f
    const val ThresholdMax = 100f
}