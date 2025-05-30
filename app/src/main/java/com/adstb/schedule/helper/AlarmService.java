package com.adstb.schedule.helper;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.adstb.schedule.R;
import com.adstb.schedule.activity.MainActivity;

/**
 * ForegroundService 类用于实现前台 Service，使应用在退出后依然持续运行任务。
 * 例如，这里可用于持续播放音乐、定位或者执行其他长时间后台任务。
 */
public class AlarmService extends Service {

    // 定义通知渠道 ID 和名称，适用于 Android 8.0 及以上
    private static final String CHANNEL_ID = "ForegroundServiceChannel";
    private static final String CHANNEL_NAME = "后台服务通知";

    /**
     * onCreate 方法用于 Service 的初始化操作
     */
    @Override
    public void onCreate() {
        super.onCreate();

        Toast.makeText(this, "哈哈哈测试测试", Toast.LENGTH_SHORT).show();
        // 可在此处初始化资源，例如媒体播放器、定位模块等
    }

    /**
     * onStartCommand 方法在 Service 每次启动时调用
     * 通过 startForeground() 方法启动前台服务，保证 Service 的持续运行
     *
     * @param intent  启动服务时传递的 Intent
     * @param flags   启动标志
     * @param startId 唯一标识
     * @return 返回 Service 的启动模式（START_STICKY 表示服务异常被终止后重新启动）
     */
    @SuppressLint("ForegroundServiceType")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 建立通知渠道（Android 8.0 及以上必须）
        createNotificationChannel();

        // 创建一个用于点击通知后回到应用的 PendingIntent
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        // 创建前台通知，通知中展示当前后台任务信息（此处以“后台运行中”作为示例）
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("后台服务")
                .setContentText("应用在后台持续运行")
                .setSmallIcon(R.drawable.setting_alarm)
                .setContentIntent(pendingIntent)
                .build();

        // 启动前台 Service，通知 ID 为 1
        startForeground(2, notification);

        // 此处加入业务逻辑代码，例如开启线程、执行任务等
        new Thread(() -> {
            // 模拟一个后台任务（例如持续执行某项操作）
            while (true) {
                try {
                    // 模拟耗时任务，等待 1 秒
                    Thread.sleep(1000);
                    // 此处可加入实际需要执行的后台操作逻辑
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }).start();

        // 返回 START_STICKY，确保服务在被系统杀死后能自动重启
        return START_STICKY;
    }

    /**
     * createNotificationChannel 方法用于为前台通知创建通知渠道
     * 仅针对 Android 8.0 及以上系统有效
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 获取 NotificationManager 服务
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            // 设置通知渠道的属性，如名称、重要级别（IMPORTANCE_LOW 让通知更不打扰用户）
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW);
            // 可选：设置渠道描述、振动模式等
            channel.setDescription("用于显示后台服务状态");
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    /**
     * onBind 方法用于处理绑定操作，此处返回 null 表示不支持绑定
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * onDestroy 方法用于 Service 被销毁时的回调
     * 需要在此方法中清理相关资源，停止后台任务
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        // 停止后台任务，清理资源，例如停止线程、释放定位对象等
    }
}