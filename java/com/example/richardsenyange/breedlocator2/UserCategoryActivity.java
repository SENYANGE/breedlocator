package com.example.richardsenyange.breedlocator2;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

public class UserCategoryActivity extends AppCompatActivity {
    RadioGroup userType;
    Button done;
   // String usercategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userType = findViewById(R.id.userType);
        done = findViewById(R.id.done);


        done.setOnClickListener(new View.OnClickListener(){
            @Override
                    public void onClick(View v){
                if(userType.getCheckedRadioButtonId()==R.id.subscriber){

                   String usercategory="farmer";
                    //startActivity(new Intent(UserCategoryActivity.this, VictimActivity.class));
                    Intent i=new Intent(getApplicationContext(),LoginActivity.class);
                    i.putExtra("usercategory",usercategory);
                    startActivity(i);


                }
                else if(userType.getCheckedRadioButtonId()==R.id.volunteer)
                {
                   // startActivity(new Intent(UserCategoryActivity.this, LoginActivity.class));
                  String  usercategory="veterinary doctor";
                    Intent i=new Intent(getApplicationContext(),LoginActivity.class);
                    i.putExtra("usercategory",usercategory);
                    startActivity(i);

                }
                else{
                    Toast.makeText(UserCategoryActivity.this,"Please select an option",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}
