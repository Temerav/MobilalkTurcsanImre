package com.example.myapplication.activity;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.model.BillMenuItem;
import com.example.myapplication.adapter.BillMenuItemAdapter;
import com.example.myapplication.R;
import com.example.myapplication.util.AlarmReceiver;
import com.example.myapplication.util.NotificationHandler;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class BillMenuActivity extends AppCompatActivity {


    private static final String LOG_TAG = BillMenuActivity.class.getName();
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private RecyclerView recyclerView;
    private ArrayList<BillMenuItem> mItemList;
    private BillMenuItemAdapter menuItemAdapter;
    private NotificationHandler notificationHandler;
    private AlarmManager alarmManager;
    private int gridNumber = 1;
    private int favouriteItems = 0;
    private int queryLimit = 12;
    private boolean viewRow = true;
    private FrameLayout cyanCircle;
    private TextView countTextView;
    private FirebaseFirestore mFireStore;
    private CollectionReference mItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_menu);
        mAuth = FirebaseAuth.getInstance();
        // mAuth.signOut();

        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {

            Log.d(LOG_TAG, "Authenticated user!");

        } else {

            Log.d(LOG_TAG, "Authentication failed!");
            finish();

        }

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, gridNumber));
        mItemList = new ArrayList<>();

        menuItemAdapter = new BillMenuItemAdapter(this, mItemList);
        recyclerView.setAdapter(menuItemAdapter);

        mFireStore = FirebaseFirestore.getInstance();
        mItems = mFireStore.collection("Data");

        queryData();

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_POWER_CONNECTED);
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        this.registerReceiver(power, filter);

        notificationHandler = new NotificationHandler(this);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        setAlarmManager();

    }

    BroadcastReceiver power = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            if (action == null)
                return;

            switch (action) {
                case Intent.ACTION_POWER_CONNECTED:
                    queryLimit = 12;
                    break;
                case Intent.ACTION_POWER_DISCONNECTED:
                    queryLimit = 3;
                    break;

            }

            queryData();

        }

    };

    @Override
    protected void onDestroy() {

        super.onDestroy();
        unregisterReceiver(power);

    }

    private void queryData() {

        mItemList.clear();
        mItems.orderBy("favouritedCount", Query.Direction.ASCENDING).limit(queryLimit).get().addOnSuccessListener(queryDocumentSnapshots -> {

            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                BillMenuItem item = documentSnapshot.toObject(BillMenuItem.class);
                item.setId(documentSnapshot.getId());
                mItemList.add(item);

            }

            if (mItemList.size() == 0) {

                initializeData();
                queryData();

            }

            menuItemAdapter.notifyDataSetChanged();

        });
    }


    public void deleteItem(BillMenuItem item){

        DocumentReference ref = mItems.document(item._getId());
        ref.delete().addOnSuccessListener(success ->{

            Log.d(LOG_TAG, "Item succesfully deleted " + item._getId());

        }).addOnFailureListener(failure ->{

            Toast.makeText(this, "Item " + item._getId() + " cannot be deleted", Toast.LENGTH_LONG).show();

        });

        queryData();
        notificationHandler.cancel();

    }

    public void updateAlertIcon(BillMenuItem item) {

        favouriteItems = (favouriteItems + 1);

        if (0 < favouriteItems) {

            countTextView.setText(String.valueOf(favouriteItems));

        } else {

            countTextView.setText("");

        }

        cyanCircle.setVisibility((favouriteItems > 0) ? VISIBLE : GONE);

        mItems.document(item._getId()).update("favouritedCount", item.getFavouritedCount() + 1)
                .addOnFailureListener(failure -> {
                    Toast.makeText(this, "Item " + item._getId() + " cannot be updated", Toast.LENGTH_LONG).show();
                });

        notificationHandler.send(item.getName());
        queryData();

    }

    private void initializeData() {


        String[] itemsList = getResources().getStringArray(R.array.menu_item_list);
        String[] itemsInfo = getResources().getStringArray(R.array.menu_item_desc);
        String[] itemsPlace = getResources().getStringArray(R.array.menu_item_place);
        TypedArray itemsImageResource = getResources().obtainTypedArray(R.array.menu_item_images);
        TypedArray itemsRate = getResources().obtainTypedArray(R.array.menu_item_rates);



        for (int i = 0; i < itemsList.length; i++) {

            mItems.add(new BillMenuItem(
                    itemsList[i],
                    itemsInfo[i],
                    itemsPlace[i],
                    itemsRate.getFloat(i, 0),
                    itemsImageResource.getResourceId(i, 0), 0));

        }

        itemsImageResource.recycle();

    }

    public void dictationStart(View view) {

        Intent intent = new Intent(this, BillCalculatorActivity.class);
        startActivity(intent);

    }

    private void setAlarmManager(){

        long repeatInterval = 120000;
        long triggerTime = SystemClock.elapsedRealtime() + repeatInterval;

        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        alarmManager.setInexactRepeating(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                triggerTime,
                repeatInterval,
                pendingIntent);


        //alarmManager.cancel(pendingIntent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.bill_list_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.search_bar);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {


            @Override
            public boolean onQueryTextSubmit(String s) {

                return false;

            }

            @Override
            public boolean onQueryTextChange(String s) {

                Log.d(LOG_TAG, s);
                menuItemAdapter.getFilter().filter(s);
                return false;

            }

        });

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.log_out_button) {

            Log.d(LOG_TAG, "Logout clicked!");
            FirebaseAuth.getInstance().signOut();
            finish();
            return true;

        } else if (item.getItemId() ==  R.id.favourite) {

            Log.d(LOG_TAG, "Favourite clicked!");
            return true;

        } else if (item.getItemId() ==  R.id.setting_button) {

            Log.d(LOG_TAG, "Setting clicked!");
            FirebaseAuth.getInstance().signOut();
            finish();
            return true;

        } else if (item.getItemId() == R.id.view_selector) {

                if (viewRow) {

                    changeSpanCount(item, R.drawable.view_grid, 1);

                } else {

                    changeSpanCount(item, R.drawable.view_row, 2);

                }

                return true;

        } else {

            return super.onOptionsItemSelected(item);

        }

    }

    private void changeSpanCount(MenuItem item, int drawableId, int spanCount) {


        viewRow = !viewRow;
        item.setIcon(drawableId);
        GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
        layoutManager.setSpanCount(spanCount);

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {


        final MenuItem alertMenuItem = menu.findItem(R.id.favourite);
        FrameLayout rootView = (FrameLayout) alertMenuItem.getActionView();

        cyanCircle = (FrameLayout) rootView.findViewById(R.id.view_alert_cyan_circle);
        countTextView = (TextView) rootView.findViewById(R.id.view_alert_count_textview);

        rootView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                onOptionsItemSelected(alertMenuItem);

            }

        });

        return super.onPrepareOptionsMenu(menu);

    }

}