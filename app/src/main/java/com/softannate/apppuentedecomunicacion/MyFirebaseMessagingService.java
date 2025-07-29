package com.softannate.apppuentedecomunicacion;

import android.app.PendingIntent;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.softannate.apppuentedecomunicacion.ui.main.MainActivity;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
        @Override
        public void onMessageReceived(@NonNull RemoteMessage message) {
            String title = "";
            String body = "";

            if (message.getNotification() != null) {
                title = message.getNotification().getTitle();
                body = message.getNotification().getBody();
            }

            Intent intent = new Intent(this, MainActivity.class); // o ChatActivity
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "canal_id")
                    .setSmallIcon(R.drawable.puente)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent) // Se abre al tocar la notificación
                    .setAutoCancel(true); // cierra la notificación al tocar

            NotificationManagerCompat manager = NotificationManagerCompat.from(this);
            manager.notify(777, builder.build()); // ID de notificación
        }
    }


