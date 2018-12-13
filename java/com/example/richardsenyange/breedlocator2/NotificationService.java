package com.example.richardsenyange.breedlocator2;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.RemoteInput;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import static com.example.richardsenyange.breedlocator2.UtilityFunctions.decodeFromFirebaseBase64;
import static com.example.richardsenyange.breedlocator2.UtilityFunctions.getCircleBitmap;
import static com.example.richardsenyange.breedlocator2.UtilityFunctions.getResizedBitmap;

public class NotificationService extends FirebaseMessagingService {
    SharedPreferences sharedPreferences;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String LOCATION = "location";
        String KEY_REPLY = "key_reply";
        String LABEL = "Enter your reply here";

        sharedPreferences = getSharedPreferences(LOCATION, Context.MODE_PRIVATE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT_WATCH) {
            RemoteInput remoteInput = new RemoteInput.Builder(KEY_REPLY)
                                        .setLabel(LABEL)
                                        .build();
        }

        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        Drawable drawable = ContextCompat.getDrawable(getApplicationContext(),R.drawable.notification_logo);
//        Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
//        Bitmap circularBitmap = getCircleBitmap(bitmap);
        Intent resultIntent = new Intent(this, ReplyActivity.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        String NOTIFICATION_WITHOUT_PIC = "notificationWithoutPic";
        String REPLY_NOTIFICATION = "reply_notification";
        int NOTIFICATION_ID = 123;

        //notification without image, to volunteers
        String NOTIFICATION_WITH_PIC = "notificationWithPic";


        if(remoteMessage.getData().get("notificationType").equals(NOTIFICATION_WITHOUT_PIC)){
            String content = remoteMessage.getData().get("title");
        NotificationCompat.Action replyAction = new NotificationCompat.Action.Builder(R.drawable.ic_reply_black_24dp, Html.fromHtml(getString(R.string.action_reply)), pendingIntent)
                .build();


        @SuppressWarnings("deprecation") NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setContentTitle("Breed Request Reply")
                .setContentText(content)
                .setSound(sound)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(content))
                .setSmallIcon(R.drawable.notification_logo);
                //.setLargeIcon(circularBitmap);

        builder.addAction(replyAction);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.cancel(NOTIFICATION_ID);
                notificationManager.notify(NOTIFICATION_ID, builder.build());
            }
    }

    //notification showing reply from farmer
            else if (remoteMessage.getData().get("notificationType").equals(REPLY_NOTIFICATION)) {
                final SharedPreferences.Editor editor = sharedPreferences.edit();

                FirebaseDatabase.getInstance().getReference().child("replies").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        editor.putString("repName",dataSnapshot.child("name").getValue().toString());
                        editor.putString("repCont",dataSnapshot.child("contact").getValue().toString());
                        if(dataSnapshot.child("image").exists())
                        editor.putString("repIm",dataSnapshot.child("image").getValue().toString());

                        editor.apply();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                String reply = remoteMessage.getData().get("reply");
                String name = sharedPreferences.getString("repName", "Anonymous");
                String image = sharedPreferences.getString("repIm", "");
                String contact = sharedPreferences.getString("repCont","0700000000");

                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + contact));

                if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }

                PendingIntent pIntent = PendingIntent.getActivity(this,0, callIntent, PendingIntent.FLAG_UPDATE_CURRENT);


                NotificationCompat.Action callAction = new NotificationCompat.Action.Builder(R.drawable.ic_call_black_24dp, Html.fromHtml(getString(R.string.action_call)), pIntent)
                        .build();

                @SuppressWarnings("deprecation") NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.notification_logo)
                        .setContentTitle("Reply from " + name)
                        .setContentText(reply)
                        .addAction(callAction)
                        .setSound(sound)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(reply));

                if(!image.equals("")){
                    Bitmap dp = getResizedBitmap(decodeFromFirebaseBase64(image), 500, 500);
                    Bitmap circularDp = getCircleBitmap(dp);
                    builder.setLargeIcon(circularDp);
                }
                else {
                    //builder.setLargeIcon(circularBitmap);
                }

                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                if (notificationManager != null) {
                    notificationManager.cancel(NOTIFICATION_ID);
                    notificationManager.notify(NOTIFICATION_ID, builder.build());
                }
            }

            //notification with image, to farmer
                else if(remoteMessage.getData().get("notificationType").equals(NOTIFICATION_WITH_PIC)) {
                    final SharedPreferences.Editor editor = sharedPreferences.edit();

                    NotificationCompat.Action replyAction = new NotificationCompat.Action.Builder(R.drawable.ic_reply_black_24dp, Html.fromHtml(getString(R.string.action_reply)), pendingIntent)
                            .build();

                    FirebaseDatabase.getInstance().getReference().child("imageNotifications").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String imageUrl = dataSnapshot.child("imageUrl").getValue().toString();
                            editor.putString("BreedPhoto", imageUrl);
                            editor.apply();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    String image = sharedPreferences.getString("BreedPhoto","");
                    Bitmap accidentBitmap = decodeFromFirebaseBase64(image);
                    @SuppressWarnings("deprecation") NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.notification_logo)
                            .setContentTitle("Breed wanted...")
                            .setContentText("Expand to view")
                            .setSound(sound)
                            .setPriority(NotificationCompat.PRIORITY_MAX)
                            //.setLargeIcon(circularBitmap)
                            .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(accidentBitmap).bigLargeIcon(null));
                    builder.addAction(replyAction);

                    NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
                    if (notificationManager != null) {
                        notificationManager.cancel(NOTIFICATION_ID);
                        notificationManager.notify(NOTIFICATION_ID,builder.build());
                    }
            }
    }
}
