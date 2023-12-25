package com.example.app.vegastts.sms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsMessage
import com.example.app.vegastts.mainViewModel

class SMSMonitor : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        if (intent != null && intent.action != null && ACTION.compareTo(intent.action!!) === 0) {

            val pduArray = intent.extras!!["pdus"] as Array<Any>?
            val messages: Array<SmsMessage?> = arrayOfNulls<SmsMessage>(pduArray!!.size)
            for (i in pduArray!!.indices) {
                messages[i] = SmsMessage.createFromPdu(pduArray[i] as ByteArray)
            }

            val sms_from: String = messages.get(0)?.getDisplayOriginatingAddress() ?: ""

            if (sms_from.equals(mainViewModel.sender, ignoreCase = true)) {
                val bodyText = StringBuilder()
                for (i in 0 until messages.size) {
                    bodyText.append(messages.get(i)?.getMessageBody() ?: "")
                }
                val body = bodyText.toString()

                mainViewModel.smsBody.value = body

                abortBroadcast()
            }
        }

    }

    companion object {
        private const val ACTION = "android.provider.Telephony.SMS_RECEIVED"
    }
}
