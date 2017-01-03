package com.example.vcssolution.data2list;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import static android.R.attr.id;

public class MainActivity extends AppCompatActivity implements Runnable {
    EditText GetName,GetPhoneNumber,GetSubject ;
    Button Submit, ShowValues;
    SQLiteDatabase SQLITEDATABASE;
    String Name, PhoneNumber, Subject ;
    Boolean CheckEditTextEmpty ;
    String SQLiteQuery ;
    private APIHelpers apiHelper;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GetName = (EditText)findViewById(R.id.editText2);

        GetPhoneNumber = (EditText)findViewById(R.id.editText3);

        GetSubject = (EditText)findViewById(R.id.subject);

        Submit = (Button)findViewById(R.id.button);

        ShowValues = (Button)findViewById(R.id.button2);

        Submit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                DBCreate();

                SubmitData2SQLiteDB();

            }
        });

        ShowValues.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                Intent intent = new Intent(MainActivity.this, ListViewActivity.class);
                startActivity(intent);

            }
        });
    }

    public void DBCreate(){

        SQLITEDATABASE = openOrCreateDatabase("DemoDataBase", Context.MODE_PRIVATE, null);

        SQLITEDATABASE.execSQL("CREATE TABLE IF NOT EXISTS demoTable(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, name VARCHAR, phone_number VARCHAR, subject VARCHAR);");
    }

    public void SubmitData2SQLiteDB(){


        Name = GetName.getText().toString();

        PhoneNumber = GetPhoneNumber.getText().toString();

        Subject = GetSubject.getText().toString();

        CheckEditTextIsEmptyOrNot(Name,PhoneNumber,Subject);

        if(CheckEditTextEmpty == true)
        {

            SQLiteQuery = "INSERT INTO demoTable (name,phone_number,subject) VALUES('"+Name+"', '"+PhoneNumber+"', '"+Subject+"');";

            SQLITEDATABASE.execSQL(SQLiteQuery);

            Toast.makeText(MainActivity.this,"Data Submit Successfully", Toast.LENGTH_LONG).show();

            ClearEditTextAfterDoneTask();

        }
        else {

            Toast.makeText(MainActivity.this,"Please Fill All the Fields", Toast.LENGTH_LONG).show();
        }
    }

    public void CheckEditTextIsEmptyOrNot(String Name,String PhoneNumber, String subject ){

        if( TextUtils.isEmpty(Name) || TextUtils.isEmpty(PhoneNumber) || TextUtils.isEmpty(Subject)){

            CheckEditTextEmpty = false ;

        }
        else {
            CheckEditTextEmpty = true ;
        }
    }



    public void send (View view){
        Thread thread = new Thread(this);
        thread.start();
        progressDialog = ProgressDialog.show(MainActivity.this, "", "Please wait...", true);
        progressDialog.dismiss();
    }

    @Override
    public void run() {
//        String uid = Getid.getText().toString().trim();
        String name = GetName.getText().toString().trim();

        String phone = GetPhoneNumber.getText().toString().trim();

        String subject = GetSubject.getText().toString().trim();

        apiHelper = new APIHelpers();
        apiHelper.data(name,phone,subject , getApplicationContext(), handler);
    }
    private Handler handler = new Handler() {
        public String responseStatus;
        public String msgTitle;
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            responseStatus = bundle.getString("responseStatus");
            msgTitle = bundle.getString("msgTitle");
            if(msg.what == 1)
            {
                if(progressDialog.isShowing())
                    progressDialog.dismiss();
                AlertDialog.Builder alt_bld = new AlertDialog.Builder(MainActivity.this);
                alt_bld.setMessage(responseStatus).setCancelable(true);
                AlertDialog alert = alt_bld.create();
                alert.setButton(-1,"OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    } });
                alert.setTitle("Message");
                alert.show();
            }
            if(progressDialog.isShowing())
                progressDialog.dismiss();
            else
            {
                if (msg.what == 0)
                    if(progressDialog.isShowing())
                        progressDialog.dismiss();
                AlertDialog.Builder alt_bld = new AlertDialog.Builder(MainActivity.this);
                alt_bld.setMessage(responseStatus)
                        .setCancelable(true);
                AlertDialog alert = alt_bld.create();
                alert.setButton(-1,"OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    } });
                alert.setTitle(msgTitle);
                alert.show();
            }

        }
    };

    public void ClearEditTextAfterDoneTask() {
        GetName.getText().clear();
        GetPhoneNumber.getText().clear();
        GetSubject.getText().clear();
    }

}
