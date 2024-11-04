package com.example.sharetheride;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.ui.AppBarConfiguration;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        DrawerLayout.DrawerListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private AppBarConfiguration appBarConfiguration;


    //Login/Register/Authentication
    FirebaseAuth auth;
    FirebaseUser user;

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //etWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        //getActionBar().hide();

        //Login/Register/Authentication
        auth = FirebaseAuth.getInstance();
        /*
        linearLayout = findViewById(R.id.info_context);
        button_logout = findViewById(R.id.logout);
        button_update_profile = findViewById(R.id.update_profile);
        button_show_hide_prof = findViewById(R.id.button_prof_info);
        textView = findViewById(R.id.user_details);
        */
        //User
        user = auth.getCurrentUser();
        /*
        name_text_input = findViewById(R.id.user_name);
        surname_text_input = findViewById(R.id.user_surname);
        company_name_text_input = findViewById(R.id.user_comp_name);
        phone_text_input = findViewById(R.id.user_phone);
        email_text_input = findViewById(R.id.user_email);
         */
        //auth.signOut();
        db = FirebaseFirestore.getInstance();

        String test;

        //if (user == null){
        //    Intent intent = new Intent(getApplicationContext(), Login.class);
        //    startActivity(intent);
        //    finish();
        //}else {
        //    //email_text_input.setText(user.getEmail());
        //    //linearLayout.setVisibility(View.GONE);
        //}
        //auth.signOut();


        setContentView(R.layout.activity_home);
        setupToolbar();
        setupDrawer();

        // Get the menu from the NavigationView
        Menu menu = navigationView.getMenu();

        // Find the login/register menu item
        MenuItem loginMenuItem = menu.findItem(R.id.nav_log_reg);

        // Update the title based on login status
        if (user == null) {
            // User is not logged in, set to "Login/Register"
            loginMenuItem.setTitle(R.string.menu_login_register);
            loginMenuItem.setIcon(R.drawable.account);  // Set the icon accordingly

            //navigationView.getMenu().findItem(R.id.group_profile).setVisible(false);
            navigationView.getMenu().setGroupVisible(R.id.group_profile, false);
        } else {
            // User is logged in, set to "Logout"
            loginMenuItem.setTitle(R.string.menu_logout);
            loginMenuItem.setIcon(R.drawable.logout);  // Update the icon if needed

            //navigationView.getMenu().findItem(R.id.group_profile).setVisible(true);
            navigationView.getMenu().setGroupVisible(R.id.group_profile, true);
        }
    }

    private void setupToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(findViewById(R.id.toolbar));
    }

    private void setupDrawer() {
        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        drawerLayout.addDrawerListener(this);

        setupNavigationView();

        ActionBarDrawerToggle drawerToggle;
        drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
    }

    private void setupNavigationView() {
        navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        // Access the Menu and hide the specific item
        //if (user == null) {
        //    navigationView.getMenu().findItem(R.id.group_profile).setVisible(false);
        //    //navigationView.getMenu().findItem(R.id.nav_profile).setVisible(false);
        //    //navigationView.getMenu().setGroupVisible(R.id.group_profile, false);
        //} else {
        //    navigationView.getMenu().findItem(R.id.group_profile).setVisible(true);
        //    //navigationView.getMenu().setGroupVisible(R.id.group_profile, true);
        //}
        setDefaultMenuItem();
        setupHeader();
    }

    private void setDefaultMenuItem() {
        MenuItem menuItem = navigationView.getMenu().getItem(0);
        onNavigationItemSelected(menuItem);
        menuItem.setChecked(true);
    }

    private void setupHeader() {
        View header = navigationView.getHeaderView(0);
        header.findViewById(R.id.header_title).setOnClickListener(view -> Toast.makeText(
                MainActivity.this,
                getString(R.string.title_click),
                Toast.LENGTH_SHORT).show());
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
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int title = getTitle(menuItem);
        showFragment(title, menuItem);
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private int getTitle(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_trips:
                return R.string.menu_trips;
            case R.id.nav_create_trip:
                return R.string.menu_create_trip;
            case R.id.nav_profile:
                return R.string.menu_profile;
            //case R.id.nav_share:
            case R.id.nav_log_reg:
                if (user == null) {
                    return R.string.menu_login_register;
                } else {
                    return R.string.menu_logout;
                }
            case R.id.nav_send:
                return R.string.menu_send;
            case R.id.nav_menu_show_my_trips:
                return R.string.menu_show_my_trips;
            default:
                throw new IllegalArgumentException("menu option not implemented!!");
        }
    }

    private void showFragment(@StringRes int titleId, MenuItem menuItem) {

        // Get the menu from the NavigationView
        Menu menu = navigationView.getMenu();

        // Find the login/register menu item
        MenuItem loginMenuItem = menu.findItem(R.id.nav_log_reg);

        Fragment fragment = new Fragment();

        switch (menuItem.getItemId()) {
            case R.id.nav_trips:
                fragment = DisplayTipsFragment.newInstance(titleId);
                break;
            case R.id.nav_create_trip:
                fragment = CreateTripFragment.newInstance(titleId);
                break;
            case R.id.nav_profile:
                fragment = ProfileFragment.newInstance(titleId);
                break;
            //case R.id.nav_share:
            case R.id.nav_log_reg:
                if (menuItem.toString().contentEquals(getString(R.string.menu_logout))) {

                    //navigationView.getMenu().findItem(R.id.group_profile).setVisible(false);
                    navigationView.getMenu().setGroupVisible(R.id.group_profile, false);
                    loginMenuItem.setTitle(R.string.menu_login_register);
                    loginMenuItem.setIcon(R.drawable.login);  // Set the icon accordingly

                    fragment = LoginFragment.newInstance(R.string.menu_login_register);
                    titleId = R.string.menu_login_register;
                    //fragment = LoginFragment.newInstance(R.string.menu_login_register);
                    auth.signOut();
                    FirebaseAuth.getInstance().signOut();
                    setUser(null);
                } else {
                    fragment = LoginFragment.newInstance(titleId);
                }
                break;
            case R.id.nav_send:
                fragment = HomeContentFragment.newInstance(titleId);
                break;
            case R.id.nav_menu_show_my_trips:
                fragment = MyTripsFragment.newInstance(titleId);
                break;
        }

        //Fragment fragment = HomeContentFragment.newInstance(titleId);
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.nav_enter, R.anim.nav_exit)
                .replace(R.id.home_content, fragment)
                .commit();

        setTitle(getString(titleId));
    }

    @Override
    public void onDrawerSlide(@NonNull View view, float v) {
        //cambio en la posición del drawer
    }

    @Override
    public void onDrawerOpened(@NonNull View view) {
        //el drawer se ha abierto completamente
        Toast.makeText(this, getString(R.string.navigation_drawer_open),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDrawerClosed(@NonNull View view) {
        //el drawer se ha cerrado completamente
    }

    @Override
    public void onDrawerStateChanged(int i) {
        //cambio de estado, puede ser STATE_IDLE, STATE_DRAGGING or STATE_SETTLING
    }
    /*
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }*/

    // Method to update the Login/Register menu item based on login status
    public void updateLoginMenuItem() {
        if (navigationView == null) {
            return;  // Guard clause in case navigationView is null
        }

        Fragment fragment = new Fragment();

        // Get the menu from the NavigationView
        Menu menu = navigationView.getMenu();
        MenuItem loginMenuItem = menu.findItem(R.id.nav_log_reg);

        //loginMenuItem = navigationView.getMenu().getItem(2); //User 0 for people to go to profile page
        //onNavigationItemSelected(menuItem);
        // Check login status (this could be from SharedPreferences, a ViewModel, etc.)
        //SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        //boolean isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false);

        if (user == null) {
            loginMenuItem.setTitle(R.string.menu_login);
            loginMenuItem.setIcon(R.drawable.login);

            //navigationView.getMenu().findItem(R.id.group_profile).setVisible(false);
            navigationView.getMenu().setGroupVisible(R.id.group_profile, false);
        } else {
            //navigationView.getMenu().findItem(R.id.group_profile).setVisible(true);
            navigationView.getMenu().setGroupVisible(R.id.group_profile, true);

            loginMenuItem.setTitle(R.string.menu_logout); //Text on menu
            loginMenuItem.setIcon(R.drawable.logout);

            //onNavigationItemSelected(loginMenuItem);
            fragment = ProfileFragment.newInstance(R.string.menu_profile); //Text inside the fragment

            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.nav_enter, R.anim.nav_exit)
                    .replace(R.id.home_content, fragment)
                    .commit();

            loginMenuItem = navigationView.getMenu().getItem(1); //Use 1 for people to go to profile page
            loginMenuItem.setChecked(true);
            setTitle(getString(R.string.menu_profile)); //Set title on top of fragment

            //fragment = LoginFragment.newInstance(titleId);
        }
    }

    public void setUser(FirebaseUser user) {
        this.user = user;
        //updateLoginMenuItem();  // Update the menu items
    }

    public FirebaseUser getUser() {
        return this.user;
    }
    public FirebaseFirestore getDb() {
        return this.db;
    }

    public NavigationView getMenu() {
        return this.navigationView;
    }

}