package com.example.freedom.myapplication.Defect;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.freedom.myapplication.Helper.FragmentNavigationManager;
import com.example.freedom.myapplication.Helper.NavigationManager;
import com.example.freedom.myapplication.Helper.RecyclerViewAdapterDefectLog;

import com.example.freedom.myapplication.Helper.customExpandableListAdapter;
import com.example.freedom.myapplication.Project.Project_activity;
import com.example.freedom.myapplication.R;
import com.example.freedom.myapplication.Report.Fragment.PieSeverity;
import com.example.freedom.myapplication.Report.PriorityActivity;
import com.example.freedom.myapplication.Report.Severity;
import com.example.freedom.myapplication.Report.StatusDefect;
import com.example.freedom.myapplication.Report.Typedefect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Defect_log extends AppCompatActivity {

    private LinearLayoutManager layoutManager;
    private List<API_Defect> apiDefects;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private String mActivityTitle;
    private String[] items;

    private ExpandableListView expandableListView;
    private ExpandableListAdapter adapter;
    private List<String> lstTitle;
    private Map<String, List<String>> lstChild;
    private NavigationManager navigationManager;
    private static FragmentManager fragmentManager;
    private Dialog myDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_defect_log);

        // show ActionBar
        getSupportActionBar().show();

        //popup set
        myDialog = new Dialog(this);
        View v = null;

        showpopup(v);


        //init View

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();
        expandableListView = (ExpandableListView) findViewById(R.id.navList);
        navigationManager = FragmentNavigationManager.getmInstance(this);

        initItem();


        View listHeaderView = getLayoutInflater().inflate(R.layout.header, null, false);
        expandableListView.addHeaderView(listHeaderView);

        genData();

        addDrawerItem();
        setupDrawer();

        if (savedInstanceState == null)
            selectFirstItemAsDefult();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Defect_Log");
        getUserNameNav();
//
//
        String df_pj_id = getIntent().getStringExtra("pj_id");

        get_Defect_log(Integer.valueOf(df_pj_id));


    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    private void selectFirstItemAsDefult() {
        if (navigationManager != null) {

            String firstItem = lstTitle.get(0);
            navigationManager.showFragment(firstItem);
            getSupportActionBar().setTitle(firstItem);

        }
    }

    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                //  getSupportActionBar().setTitle("STM");
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                //   getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu();
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void addDrawerItem() {
        adapter = new customExpandableListAdapter(this, lstTitle, lstChild);
        expandableListView.setAdapter(adapter);
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                getSupportActionBar().setTitle(lstTitle.get(groupPosition).toString()); //set title for Toobar when open menu
            }
        });
        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {
                getSupportActionBar().setTitle("STM");
            }
        });

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                // change fragment when click on item
                String selectedItem = ((List) (lstChild.get(lstTitle.get(groupPosition))))
                        .get(childPosition).toString();
                getSupportActionBar().setTitle(selectedItem);
                Log.d("ss", selectedItem);

                if (items[0].equals(lstTitle.get(groupPosition))) {
                    // throw new IllegalArgumentException("Not supported fragment ");
                    navigationManager.showFragment(selectedItem);
                    if (selectedItem == "Severity") {  // condition for report
                        Intent intent = new Intent(Defect_log.this, Severity.class);

                        String df_pj_id = getIntent().getStringExtra("df_pj_id");
                        String Username = getIntent().getStringExtra("User_login");
                        String department = getIntent().getStringExtra("Department");

                        String pj_id = getIntent().getStringExtra("pj_id");
                        String NameRe = selectedItem;
                        intent.putExtra("df_pj_id", df_pj_id);
                        intent.putExtra("NameRe", NameRe);
                        intent.putExtra("User_login", Username);
                        intent.putExtra("Department", department);
                        intent.putExtra("pj_id", pj_id);

                        startActivity(intent);

                    } else if (selectedItem == "Priority") {
                        Intent intent = new Intent(Defect_log.this, PriorityActivity.class);

                        String df_pj_id = getIntent().getStringExtra("df_pj_id");
                        String Username = getIntent().getStringExtra("User_login");
                        String department = getIntent().getStringExtra("Department");

                        String pj_id = getIntent().getStringExtra("pj_id");
                        String NameRe = selectedItem;
                        intent.putExtra("df_pj_id", df_pj_id);
                        intent.putExtra("NameRe", NameRe);
                        intent.putExtra("User_login", Username);
                        intent.putExtra("Department", department);
                        intent.putExtra("pj_id", pj_id);
                        startActivity(intent);

                    } else if (selectedItem == "SummaryDefect") {
                        Intent intent = new Intent(Defect_log.this, Typedefect.class);

                        String df_pj_id = getIntent().getStringExtra("df_pj_id");
                        String Username = getIntent().getStringExtra("User_login");
                        String department = getIntent().getStringExtra("Department");

                        String pj_id = getIntent().getStringExtra("pj_id");
                        String NameRe = selectedItem;
                        intent.putExtra("df_pj_id", df_pj_id);
                        intent.putExtra("NameRe", NameRe);
                        intent.putExtra("User_login", Username);
                        intent.putExtra("Department", department);
                        intent.putExtra("pj_id", pj_id);
                        startActivity(intent);


                    } else if (selectedItem == "SummaryReport") {
                        Intent intent = new Intent(Defect_log.this, StatusDefect.class);

                        String df_pj_id = getIntent().getStringExtra("df_pj_id");
                        String Username = getIntent().getStringExtra("User_login");
                        String department = getIntent().getStringExtra("Department");

                        String pj_id = getIntent().getStringExtra("pj_id");
                        String NameRe = selectedItem;
                        intent.putExtra("df_pj_id", df_pj_id);
                        intent.putExtra("NameRe", NameRe);
                        intent.putExtra("User_login", Username);
                        intent.putExtra("Department", department);
                        intent.putExtra("pj_id", pj_id);
                        startActivity(intent);
                    } else
                        Toast.makeText(getApplicationContext(), " No DATA !!!",
                                Toast.LENGTH_SHORT).show();

                } else if (items[1].equals(lstTitle.get(groupPosition))) {
                    navigationManager.showFragment(selectedItem);
                    if (selectedItem == "Project") {
                        Intent intent = new Intent(Defect_log.this, Project_activity.class);
                        String Username = getIntent().getStringExtra("User_login");
                        String department = getIntent().getStringExtra("Department");
                        intent.putExtra("User_login", Username);
                        intent.putExtra("Department", department);
                        startActivity(intent);
                    }

                } else
                    //throw new IllegalArgumentException("Not supported fragment ");
                    Toast.makeText(getApplicationContext(), " IM Here !!!",
                            Toast.LENGTH_SHORT).show();

                mDrawerLayout.closeDrawer(GravityCompat.START);
                return false;
            }
        });
    }

    private void genData() {

        List<String> title = Arrays.asList("Report", "Project", "Defect", "Home");

        List<String> Report = Arrays.asList("Severity", "Priority", "SummaryDefect", "SummaryReport");
        List<String> project = Arrays.asList("Project", "", "", "");
        List<String> Defect_log = Arrays.asList("Defect_log", "", "", "");
        List<String> Empty1 = Arrays.asList("", "", "", "");

        lstChild = new TreeMap<>();
        lstChild.put(title.get(0), Report);
        lstChild.put(title.get(1), project);
        lstChild.put(title.get(2), Defect_log);
        lstChild.put(title.get(3), Empty1);


        lstTitle = new ArrayList<>(lstChild.keySet());
        lstTitle.remove(1);

    }

    private void initItem() {

        items = new String[]{"Report", "Project"};
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        item.getItemId();
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void get_Defect_log(Integer df_pj_id) {

        Service_Defect_log service = ApiClientDefectLog.getRetrofit().create(Service_Defect_log.class);
        Log.d("tt", String.valueOf(service));
        Call<List<API_Defect>> call = service.ConnectDefect(df_pj_id);
        call.enqueue(new Callback<List<API_Defect>>() {
            @Override
            public void onResponse(Call<List<API_Defect>> call, Response<List<API_Defect>> response) {
                if (response.isSuccessful()) {
                    apiDefects = response.body();
//                    layoutManager = new LinearLayoutManager(Defect_log.this);
//                    layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//                    RecyclerView recyclerView = (RecyclerView) findViewById(R.id.card_recycler_view);
//                    recyclerView.setLayoutManager(layoutManager);
//                    RecyclerViewAdapterDefectLog recyclerViewAdapterDefectLog = new RecyclerViewAdapterDefectLog(apiDefects);
//                    recyclerView.setAdapter(recyclerViewAdapterDefectLog);


                }
            }

            @Override
            public void onFailure(Call<List<API_Defect>> call, Throwable t) {
                Log.d("Error", t.getMessage());
            }

        });

    }

    public void getUserNameNav() {

        TextView User = findViewById(R.id.name_header);
        TextView project_id = findViewById(R.id.Project_id);
        TextView dpname = findViewById(R.id.Department);

        String Username, pj_id, department;
        Intent intent = getIntent();

        Username = intent.getStringExtra("User_login");
        department = intent.getStringExtra("Department");
        pj_id = intent.getStringExtra("pj_id");

        Log.d("wq", Username);
        Log.d("wq", department);
        Log.d("wq", pj_id);

        User.setText(Username);
        project_id.setText("Project_ID : " + pj_id);
        dpname.setText(department);
    }

    private void showpopup(View v) {
        final Button btnpie, btnbar, btntable;
        myDialog.setContentView(R.layout.custompopup);
        btnpie = myDialog.findViewById(R.id.btnpie);



        //myDialog.show();
        //click Pie chart
//Auto click 5s
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                btnpie.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Fragment selectedFragment = null;
                        selectedFragment = Fragment_defect_log.newInstance();

                        Bundle bundleSP = new Bundle();
                        //Pass data Severity
                        String Project_id = getIntent().getStringExtra("pj_id");

                        bundleSP.putString("pj_id",Project_id );
//


                        selectedFragment.setArguments(bundleSP);
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, selectedFragment).commit();

                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.container, selectedFragment);
                        transaction.commit();

                        myDialog.hide();
                    }

                });

                btnpie.performClick();

            }
        }, 1000);
    }




}
