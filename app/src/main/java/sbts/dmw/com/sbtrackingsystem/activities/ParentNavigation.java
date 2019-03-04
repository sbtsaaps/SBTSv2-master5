package sbts.dmw.com.sbtrackingsystem.activities;

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import sbts.dmw.com.sbtrackingsystem.R;
import sbts.dmw.com.sbtrackingsystem.classes.SessionManager;
import sbts.dmw.com.sbtrackingsystem.fragments.ParentHome;
import sbts.dmw.com.sbtrackingsystem.fragments.map;

public class ParentNavigation extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    SessionManager sessionManager;
    Toolbar toolbar;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_navigation);

        navigationView = findViewById(R.id.parentNavigationView);
        navigationView.setNavigationItemSelectedListener(this);

        sessionManager = new SessionManager(this);

        toolbar = findViewById(R.id.parent_toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.parentDrawer);
        ActionBarDrawerToggle toogle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toogle);
        toogle.syncState();

        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.parent_nav_frame,new map()).commit();
            navigationView.setCheckedItem(R.id.nav_bus_location);
            toolbar.setTitle("Map");
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.nav_logout_parent:{
                sessionManager.logout();
                break;
            }
            case R.id.nav_bus_location:{
                getSupportFragmentManager().beginTransaction().replace(R.id.parent_nav_frame,new map()).commit();
                toolbar.setTitle("Map");
                break;
            }
            case R.id.nav_parent_profile:{
                getSupportFragmentManager().beginTransaction().replace(R.id.parent_nav_frame, new ParentHome()).commit();
                toolbar.setTitle("Profile");
                break;
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }
    }
}
