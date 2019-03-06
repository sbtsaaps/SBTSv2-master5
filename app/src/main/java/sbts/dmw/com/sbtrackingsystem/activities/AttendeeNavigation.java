package sbts.dmw.com.sbtrackingsystem.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import sbts.dmw.com.sbtrackingsystem.R;
import sbts.dmw.com.sbtrackingsystem.classes.SessionManager;
import sbts.dmw.com.sbtrackingsystem.classes.SingletonClass;
import sbts.dmw.com.sbtrackingsystem.fragments.AttendeeHome;
import sbts.dmw.com.sbtrackingsystem.fragments.StudentList;
import sbts.dmw.com.sbtrackingsystem.fragments.map;

public class AttendeeNavigation extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, LocationListener {

    private DrawerLayout drawerLayout;
    SessionManager sessionManager;
    Toolbar toolbar;
    NavigationView navigationView;
    LocationManager locationManager;
    ImageView imageView;
    String url;
    RequestOptions option;
    TextView name, email;
    StringRequest stringRequest;
    View header_view;
    private String User;
    final int PICK_CODE = 1;
    String[] str;
    Bundle bundle;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendee_navigation);
        option = new RequestOptions().centerCrop().placeholder(R.drawable.loading_shape).error(R.drawable.loading_shape);
        sharedPreferences = getSharedPreferences("LOGIN", Context.MODE_PRIVATE);
        navigationView = findViewById(R.id.attendeeNavigationView);
        header_view = navigationView.getHeaderView(0);
        imageView = header_view.findViewById(R.id.menu_photo);
        name = header_view.findViewById(R.id.menu_name);
        email = header_view.findViewById(R.id.menu_email);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        navigationView.setNavigationItemSelectedListener(this);
        sessionManager = new SessionManager(this);
        bundle = new Bundle();
        HashMap<String, String> user = sessionManager.getUserDetails();
        User = user.get(SessionManager.USERNAME);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.attendeeDrawer);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.


            return;
        }

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);


        if (savedInstanceState == null) {
            String url = "https://sbts2019.000webhostapp.com/studentList.php";
            bundle.putString("url", url);
            StudentList studentList = new StudentList();
            studentList.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.nav_frame, studentList).commit();
            navigationView.setCheckedItem(R.id.nav_studentsPresent);
            toolbar.setTitle("Student List");
        }
        getData();
    }


    @Override
    protected void onStart() {
        super.onStart();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 10, this);

    }

    @Override
    protected void onPause() {
        super.onPause();

        locationManager.removeUpdates(this);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_logout: {
                sessionManager.logout();
                break;
            }
            case R.id.nav_studentsPresent: {

                String url = "https://sbts2019.000webhostapp.com/studentList.php";
                bundle.putString("url", url);
                StudentList studentList = new StudentList();
                studentList.setArguments(bundle);

                getSupportFragmentManager().beginTransaction().replace(R.id.nav_frame, studentList).commit();
                toolbar.setTitle("Student List");
                break;
            }
            case R.id.nav_profile: {
                getSupportFragmentManager().beginTransaction().replace(R.id.nav_frame, new AttendeeHome()).commit();
                toolbar.setTitle("Profile");
                break;
            }
            case R.id.nav_map: {
                getSupportFragmentManager().beginTransaction().replace(R.id.nav_frame, new map()).commit();
                toolbar.setTitle("Map");
                break;
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onLocationChanged(final Location location) {
        String url = "https://sbts2019.000webhostapp.com/locationout.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (!response.trim().contains("success")) {
                            Toast.makeText(getApplicationContext(), "Failed to capture location.", Toast.LENGTH_LONG).show();
                        }
                        // Toast.makeText(getApplicationContext(), "Location Captured", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", User);
                params.put("latitude", String.valueOf(location.getLatitude()));
                params.put("longitude", String.valueOf(location.getLongitude()));
                return params;
            }
        };
        SingletonClass.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {


    }



    private void getData() {

        url = "https://sbts2019.000webhostapp.com/attendee.php";
        stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        editor = sharedPreferences.edit();
                        str = Pattern.compile(",").split(response);
                        editor.putString("Full_Name", str[0]);
                        editor.putString("Photo", str[1]);
                        editor.putString("Email", str[2]);
                        editor.putString("Mobile_No1", str[3]);
                        editor.putString("Bus_No", str[4]);
                        editor.putString("DOB", str[5]);
                        editor.putString("Address", str[6]);
                        editor.apply();

                        Glide.with(getApplicationContext()).load(sharedPreferences.getString("Photo", null)).apply(option).into(imageView);
                        name.setText(sharedPreferences.getString("Full_Name", null));
                        email.setText(sharedPreferences.getString("Email", null));

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", User);
                return params;
            }
        };
        SingletonClass.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);


    }

}
