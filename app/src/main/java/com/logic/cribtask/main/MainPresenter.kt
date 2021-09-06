package com.logic.cribtask.main

interface MainPresenter {
    fun checkMobileNumberEmpty(number: String?): Boolean
    fun validateMobileNumber(number: String?): Boolean
    fun checkNumberOfDaysEmpty(day: String?): Boolean
    fun fetchSMS(number: String?, day: String?)
    interface View {
        fun emptyMobileNumber(message:String)
        fun emptyNumberOfDays(message:String)
        fun invalidMobileNumber(message:String)
        fun onSuccessSMS(message:String)
        fun onFailureSMS(message:String)
    }
}