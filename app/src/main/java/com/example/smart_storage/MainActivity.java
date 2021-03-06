package com.example.smart_storage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    static int active = 0; //0 = All - 1 = Pantry - 2 = Freezer - 3 = Fridge
    //arrays for user inputted items/dates, BIG NOOB, but it works xd
    static String[] item = {"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""};
    static String[] date = {"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""};
    static String[] storageType = {"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""};
    static long[] miliseconds = new long[item.length]; //used to make the sort method work

    static int itemAmount = 0;//keeps count of how many items we have
    int toggle = 1; //has the same button been pressed three times in a row? if so, toggle background colour to that button's colour
    Button pantryButton, freezerButton, fridgeButton, addFoodButton;
    RecyclerView recyclerView;
    View constraintLayout;

    public static void setItem(String addItem) {//used to set the items to the array
        item[itemAmount] = addItem;
    }

    public static void setDate(String addDate) {
        date[itemAmount] = addDate;
        try {
            String dateString = addDate;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            Date date = sdf.parse(dateString);

            miliseconds[itemAmount] = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public static void setStorageType(String addStorageType) {
        storageType[itemAmount] = addStorageType;
        itemAmount++;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        constraintLayout = findViewById(R.id.constraint_Layout);
        pantryButton = findViewById(R.id.pantry_Button);
        freezerButton = findViewById(R.id.freezer_Button);
        fridgeButton = findViewById(R.id.fridge_Button);
        addFoodButton = findViewById(R.id.add_item_button);
        recyclerView = findViewById(R.id.myRecyclerView);

        updateRecycler();

        pantryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pantry();//display the items in the pantry
            }
        });

        freezerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                freezer();//displays the items in the freezer
            }
        });

        fridgeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fridge();//displays the items in the fridge
            }
        });

        addFoodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddFoodActivity();
            }
        });
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        sortItems();

        if (itemAmount != 0) {
            if (active == 1) pantry();
            else if (active == 2) freezer();
            else if (active == 3) fridge();
        }
    }

    private void sortItems() {
        for (int i = 0; i < itemAmount - 1; i++) {
            if (miliseconds[i + 1] < miliseconds[i]) {
                String temp = date[i];
                date[i] = date[i + 1];
                date[i + 1] = temp;
                temp = item[i];
                item[i] = item[i + 1];
                item[i + 1] = temp;
                temp = storageType[i];
                storageType[i] = storageType[i + 1];
                storageType[i + 1] = temp;
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        updateRecycler();

        saveArray(item, "Item", this);//when the app is set to onpause the arrays are stored in sharedpreferences
        saveArray(date, "date", this);
        saveArray(storageType, "storeType", this);
    }

    public void pantry() {
        if (active == 1 && toggle == 0) { //set back to all foods list
            active = 0;
            toggle = 1;
            constraintLayout.setBackgroundColor(Color.parseColor("#dcffd6"));
            pantryButton.setTypeface(null, Typeface.NORMAL);
            updateRecycler();
        } else { //set to pantry food list
            active = 1;
            toggle = 0;
            constraintLayout.setBackgroundColor(Color.parseColor("#bf9573"));
            pantryButton.setTypeface(null, Typeface.BOLD_ITALIC);
            freezerButton.setTypeface(null, Typeface.NORMAL);
            fridgeButton.setTypeface(null, Typeface.NORMAL);
            updateRecycler();
        }
    }

    public void freezer() {
        if (active == 2 && toggle == 0) { //set back to all foods list
            active = 0;
            toggle = 1;
            constraintLayout.setBackgroundColor(Color.parseColor("#dcffd6"));
            freezerButton.setTypeface(null, Typeface.NORMAL);
            updateRecycler();
        } else { //set to freezer food list
            active = 2;
            toggle = 0;
            constraintLayout.setBackgroundColor(Color.parseColor("#0077ff"));
            freezerButton.setTypeface(null, Typeface.BOLD_ITALIC);
            pantryButton.setTypeface(null, Typeface.NORMAL);
            fridgeButton.setTypeface(null, Typeface.NORMAL);
            updateRecycler();
        }
    }

    public void fridge() {
        if (active == 3 && toggle == 0) { //set back to all foods list
            active = 0;
            toggle = 1;
            constraintLayout.setBackgroundColor(Color.parseColor("#dcffd6"));
            fridgeButton.setTypeface(null, Typeface.NORMAL);
            updateRecycler();
        } else { //set to fridge food list
            active = 3;
            toggle = 0;
            constraintLayout.setBackgroundColor(Color.parseColor("#abb6ff"));
            fridgeButton.setTypeface(null, Typeface.BOLD_ITALIC);
            pantryButton.setTypeface(null, Typeface.NORMAL);
            freezerButton.setTypeface(null, Typeface.NORMAL);
            updateRecycler();
        }
    }

    private void updateRecycler() {//updates the recycler to show the current
        MyAdapter myAdapter = new MyAdapter(this, item, date, storageType);
        recyclerView.setAdapter(myAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void openAddFoodActivity() {
        Intent intent = new Intent(this, add_food_activity.class);
        startActivity(intent);
    }

    public boolean saveArray(String[] array, String Name, Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences("prefrencename", 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(Name + "_size", array.length);
        editor.putInt("itemCount", itemAmount);
        for (int i = 0; i < array.length; i++)
            editor.putString(Name + " " + i, array[i]);
        return editor.commit();
    }

    public String[] loadArray(String arrayName, Context mContext) {//creates a new array that grabs the items that were stored before;
        SharedPreferences prefs = mContext.getSharedPreferences("prefrencename", 0);
        int size = prefs.getInt(arrayName + "_size", 0);
        String[] array = new String[size];
        itemAmount = prefs.getInt("itemCount", 0);
        for (int i = 0; i < size; i++)
            array[i] = prefs.getString(arrayName + " " + i, null);
        return array;
    }
}