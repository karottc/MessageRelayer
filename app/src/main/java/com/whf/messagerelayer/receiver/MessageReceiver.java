package com.whf.messagerelayer.receiver;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.whf.messagerelayer.confing.Constant;
import com.whf.messagerelayer.service.SmsService;
import com.whf.messagerelayer.utils.FormatMobile;
import com.whf.messagerelayer.utils.NativeDataManager;

public class MessageReceiver extends BroadcastReceiver {

    private NativeDataManager mNativeDataManager;
    public MessageReceiver() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        this.mNativeDataManager = new NativeDataManager(context);
        if(mNativeDataManager.getReceiver()){
            Bundle bundle = intent.getExtras();
            if(bundle!=null){
                Object[] pdus = (Object[]) bundle.get("pdus");
                for(int i = 0;i<pdus.length;i++){
                    SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    startSmsService(context, sms);
                }
            }
        }
    }

    private ComponentName startSmsService(Context context, SmsMessage sms) {
        String mobile = sms.getOriginatingAddress();//发送短信的手机号码

        if(FormatMobile.hasPrefix(mobile)){
            mobile = FormatMobile.formatMobile(mobile);
        }
        String content = sms.getMessageBody();//短信内容

        Date date = new Date(sms.getTimestampMillis());// 得到发送短信的具体时间
        // 2009-10-12 12:21:23
        SimpleDateFormat format = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.US);// 为时间设置格式
        String sendtime = format.format(date);

        content += "[from: " + mobile + " ,at:" + sendtime + "]";

        Intent serviceIntent = new Intent(context, SmsService.class);
        serviceIntent.putExtra(Constant.EXTRA_MESSAGE_CONTENT,content);
        serviceIntent.putExtra(Constant.EXTRA_MESSAGE_MOBILE,mobile);
        return context.startService(serviceIntent);
    }


}
