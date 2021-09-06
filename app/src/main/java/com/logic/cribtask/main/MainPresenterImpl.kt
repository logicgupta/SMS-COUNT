package com.logic.cribtask.main

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.Telephony
import com.logic.cribtask.R
import java.text.SimpleDateFormat
import java.util.*

class MainPresenterImpl(private val mContext: Context, private val view: MainPresenter.View) :
    MainPresenter {
    override fun checkMobileNumberEmpty(number: String?): Boolean {
        return number.isNullOrBlank()
    }

    override fun validateMobileNumber(number: String?): Boolean {
        if (number!!.length < 10) {
            return true
        }
        return false
    }

    override fun checkNumberOfDaysEmpty(day: String?): Boolean {
        return day.isNullOrBlank()
    }

    override fun fetchSMS(number: String?, day: String?) {
        when {
            checkMobileNumberEmpty(number) -> {
                view.emptyMobileNumber(mContext.getString(R.string.error_empty_number))
                return
            }
            validateMobileNumber(number) -> {
                view.invalidMobileNumber(mContext.getString(R.string.error_invalid_number))
                return
            }
            checkNumberOfDaysEmpty(day) -> {
                view.emptyNumberOfDays(mContext.getString(R.string.error_empty_day))
                return
            }
            else -> {
                var count = 0
                val filter =
                    arrayOf("+91" + number!!.trim(), getStartDate(day)!!.time.toString().trim())
                val inboxURI: Uri = Uri.parse("content://sms")
                val cursor: Cursor? = mContext.contentResolver.query(
                    inboxURI,
                    arrayOf("_id", "thread_id", "address", "person", "date", "body", "type"),
                    Telephony.Sms.ADDRESS + "=? AND " + Telephony.Sms.DATE + ">=?",
                    filter,
                    null
                )
                while (cursor!!.moveToNext()) {
                    count += 1
                }
                cursor.close()
                if (count > 0)
                    view.onSuccessSMS("$count " + mContext.getString(R.string.sms_found))
                else
                    view.onFailureSMS(mContext.getString(R.string.no_sms_found))

            }
        }

    }

    private fun getStartDate(day: String?): Date? {
        val cal: Calendar = Calendar.getInstance()
        cal.time = Date()
        cal.add(Calendar.DATE, -(day!!.trim().toInt()))
        val formatter = SimpleDateFormat("dd-MM-yyyy'T'hh:mm:ss")
        val selectedDate = getDate(cal.timeInMillis) + "T00:00:00"
        return formatter.parse(selectedDate)
    }

    private fun getDate(timeInMilli: Long): String {
        val formatter = SimpleDateFormat("dd-MM-yyyy")
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeInMilli
        return formatter.format(calendar.time)
    }

}