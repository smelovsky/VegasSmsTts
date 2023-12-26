package com.example.app.vegastts.sms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import com.example.app.vegastts.mainViewModel

class SMSMonitor : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            for (message in messages) {
                val sender = message.originatingAddress
                if (sender.equals(mainViewModel.getSmsFrom(), ignoreCase = true)) {
                    mainViewModel.smsBody.value = message.messageBody
                    break
                }
                abortBroadcast()
            }
        }
    }

    companion object {
        private const val ACTION = "android.provider.Telephony.SMS_RECEIVED"
    }
}
