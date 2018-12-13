package com.example.richardsenyange.breedlocator2;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import java.util.ArrayList;

import static com.example.richardsenyange.breedlocator2.UtilityFunctions.decodeFromFirebaseBase64;
import static com.example.richardsenyange.breedlocator2.UtilityFunctions.getResizedBitmap;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyHolder> {

    //this is the activity which will call the RecyclerAdapter
    private Context c;

    //this is the source of the data
    private ArrayList<RecyclerviewUser> arrayList;


    //constructor for adapter //used to initiliaze the Context and ArrayList
    RecyclerAdapter(Context c, ArrayList<RecyclerviewUser> arrayList){
        Log.i("adapter", "constructor");
        this.c = c;
        this.arrayList = arrayList;
    }


    /*
    INITIALIZE VIEWHOLDER
     */


    //this method will create the layout for each row
    @NonNull
    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.i("oncreateview",parent.toString());
        View v= LayoutInflater.from(c).inflate(R.layout.recylerview_row,parent,false);
        return new MyHolder(v);
    }

    /*
    BIND
     */

    //this method will attach the corresponding data to each View
    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        Log.i("onBind",arrayList.get(position).getContact());
        RecyclerviewUser user = arrayList.get(position);

        //decode image-url in firebase from string to bitmap
        if(user.imageExists()) {
            Bitmap imageBitmap = decodeFromFirebaseBase64(user.getImage());
            holder.img.setImageBitmap(getResizedBitmap(imageBitmap, 200, 220));
        }

        final String phoneNum = user.getContact();
        holder.nameTxt.setText(user.getName());
        holder.phoneTxt.setText(phoneNum);

        holder.phoneTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + phoneNum));
                if (ActivityCompat.checkSelfPermission(view.getContext(), android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                view.getContext().startActivity(callIntent);
            }
        });

        holder.ratingBar.setRating(user.getRating());
    }


    //this function returns the number of items in the recycler view
    @Override
    public int getItemCount() {
        if(arrayList != null)
        return arrayList.size();
        else
            return 0;
    }


    //this inner class will hold the Views which will be filled later on
    /*
    VIEW HOLDER CLASS
     */
    class MyHolder extends RecyclerView.ViewHolder
    {
        TextView nameTxt;
        TextView phoneTxt;
        ImageView img;
        SimpleRatingBar ratingBar;

        MyHolder(View userDetails) {
            super(userDetails);
            nameTxt =  userDetails.findViewById(R.id.nameTxt);
            phoneTxt = userDetails.findViewById(R.id.phoneTxt);
            img =  userDetails.findViewById(R.id.profile_picture);
            ratingBar =  userDetails.findViewById(R.id.ratingBarID);
        }
    }

}
