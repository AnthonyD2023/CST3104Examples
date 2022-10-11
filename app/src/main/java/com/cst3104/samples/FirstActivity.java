package com.cst3104.samples;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


//This makes it a page in your application
public class FirstActivity extends AppCompatActivity {
    //Create an OpenHelper to store data:
    private SQLiteDatabase theDatabase;

    //need onCreate:
    @Override
    public void onCreate(Bundle p){
        super.onCreate(p);
        //load XML:
        setContentView(R.layout.activity_main);

        MyOpenHelper myOpener;
        Button submit;
        EditText edit;
        ListView lView;
        ArrayAdapter<Message> theAdapter;
        ArrayList<Message> messages = new ArrayList<>();

        //initialize it in onCreate
        myOpener = new MyOpenHelper( this );
        //open the database:
        theDatabase = myOpener.getWritableDatabase();

        //load from the database:
        Cursor results = theDatabase.rawQuery( "Select * from " + MyOpenHelper.TABLE_NAME + ";", null );//no arguments to the query

        //Convert column names to indices:
        int idIndex = results.getColumnIndex( MyOpenHelper.COL_ID );
        int  messageIndex = results.getColumnIndex( MyOpenHelper.COL_MESSAGE);
        int sOrRIndex = results.getColumnIndex( MyOpenHelper.COL_SEND_RECEIVE);
        int timeIndex = results.getColumnIndex( MyOpenHelper.COL_TIME_SENT );

        //cursor is pointing to row -1
        while( results.moveToNext() ) //returns false if no more data
        { //pointing to row 2
            int id = results.getInt(idIndex);
            String message = results.getString( messageIndex );
            String time = results.getString( timeIndex);

            //add to arrayList:
            messages.add( new Message( message, time, id ));
        }

        results.close();

        submit = findViewById(R.id.submitButton);
        edit = findViewById(R.id.editText);
        lView = findViewById(R.id.myListView);

        theAdapter = new ArrayAdapter<>( this, android.R.layout.simple_list_item_1, messages );
        lView.setAdapter( theAdapter ) ;

        submit.setOnClickListener( click ->{
            String whatIsTyped = edit.getText().toString();
            Date timeNow = new Date(); //when was this code run

            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm:ss", Locale.getDefault());

            String currentDateandTime = sdf.format( timeNow ); //convert date to String
            //insert into database:
            ContentValues newRow = new ContentValues();// like intent or Bundle

            //Message column:
            newRow.put( MyOpenHelper.COL_MESSAGE , whatIsTyped  );

            //Send or receive column:
            newRow.put(MyOpenHelper.COL_SEND_RECEIVE, 1);

            //TimeSent column:
            newRow.put( MyOpenHelper.COL_TIME_SENT, currentDateandTime );

            //now that columns are full, you insert:

            long id = theDatabase.insert( MyOpenHelper.TABLE_NAME, null, newRow ); //returns the id

            Message cm = new Message(whatIsTyped, currentDateandTime, id) ;

            //adding a new message to our history:
            messages.add( cm ); //what is the database id?

            edit.setText("");//clear the text

            //notify that new data was added at a row:
            theAdapter.notifyDataSetChanged();

        });

        lView.setOnItemClickListener( (click, view, position, id) ->  {
            //which row was clicked.
            Message whatWasClicked = messages.get(position);

            AlertDialog.Builder builder = new AlertDialog.Builder( FirstActivity.this );

            builder.setTitle("Question:")
                .setMessage("Do you want to delete this:" + whatWasClicked.getMessageTyped())
                .setNegativeButton("Negative", (dialog, click1)->{ })
                .setPositiveButton("Positive", (dialog, click2)->{
                    //actually delete something:
                    messages.remove(position);
                    theAdapter.notifyDataSetChanged();
                    Snackbar.make(submit, "You removed item # " + position, Snackbar.LENGTH_LONG)
                        .setAction("Undo", (click4)-> {
                            messages.add(position, whatWasClicked);
                            theAdapter.notifyDataSetChanged();
                            //reinsert into the database
                            theDatabase.execSQL( String.format(Locale.CANADA, " Insert into %s values (\"%d\", \"%s\", \"%d\", \"%s\" );",
                                    MyOpenHelper.TABLE_NAME      , whatWasClicked.getId()  , whatWasClicked.getMessageTyped() , 1  , whatWasClicked.getTimeSent()));

                        })
                        .show();
                    //delete from database:, returns number of rows deleted
                    theDatabase.delete(MyOpenHelper.TABLE_NAME,
                            MyOpenHelper.COL_ID +" = ?", new String[] { Long.toString( whatWasClicked.getId() )  });
                }).create().show();
        });
    }

}
