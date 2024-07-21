import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.phone.SmsRetriever

class SmsBroadcastReceiver(private val onSmsReceived: (String?) -> Unit) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION == intent.action) {
            val sms = intent.getParcelableExtra<Intent>(SmsRetriever.EXTRA_SMS_MESSAGE)
            val message = sms?.getStringExtra("sms_message")
            val otp = extractOtp(message)
            onSmsReceived(otp)
        }
    }

    private fun extractOtp(message: String?): String? {
        // Extract the OTP from the message
        return message?.substringAfter("Your verification code is: ")?.take(6)
    }
}
