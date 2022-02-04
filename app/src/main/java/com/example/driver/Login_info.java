package com.example.driver;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.rilixtech.widget.countrycodepicker.Country;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;


public class Login_info extends AppCompatActivity {


    EditText mobile_number;
    EditText name;
    EditText last_name;
    TextInputLayout first_name_layout;
    TextInputLayout last_name_layout;

    CountryCodePicker countryCodePicker;
    TextInputLayout mobile_textLayout;
    String selected_code="91";
    Button continue_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_info);

        countryCodePicker=findViewById(R.id.county_code_picker);
        mobile_textLayout=findViewById(R.id.mobileText_layout);

        mobile_textLayout.setPrefixText("+"+selected_code);
        first_name_layout=findViewById(R.id.first_namelayout);
        last_name_layout=findViewById(R.id.last_namelayout);
        name=first_name_layout.getEditText();
        last_name=last_name_layout.getEditText();
        mobile_number=mobile_textLayout.getEditText();
        continue_btn=findViewById(R.id.get_otp);
        ActionBar actionBar=getSupportActionBar();
        actionBar.hide();

        countryCodePicker.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected(Country selectedCountry) {
                selected_code=selectedCountry.getPhoneCode();
                mobile_textLayout.setPrefixText("+"+selected_code);
            }
        });

        continue_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String mobile_no=mobile_number.getText().toString();
               String mname=name.getText().toString();
               String mlast=last_name.getText().toString();
               if(mobile_no.length()<10){
                   mobile_textLayout.setError("Enter 10 Digit No");
                   return;
               }
               if (mname.equals("")){
                   first_name_layout.setError("Enter Name");
                   return;
               }
                if (last_name.equals("")){
                    first_name_layout.setError("Enter Last Name");
                    return;
                }
                mobile_no="+"+selected_code+mobile_no;

                Intent intent=new Intent(Login_info.this, LoginActivity.class);
                intent.putExtra("Name",mname);
                intent.putExtra("lastname",mlast);
                intent.putExtra("Mobile_No",mobile_no);
                startActivity(intent);

            }
        });

    }

    @Override
    protected void onStart() {
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser!=null){
            Intent intent=new Intent(Login_info.this,MapsActivity.class);
            startActivity(intent);
            finish();
        }
        super.onStart();
    }
}