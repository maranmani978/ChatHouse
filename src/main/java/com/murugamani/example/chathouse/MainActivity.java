package com.murugamani.example.chathouse;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.murugamani.example.chathouse.Fragments.ChatFragment;
import com.murugamani.example.chathouse.Fragments.FriendsFragment;
import com.murugamani.example.chathouse.Fragments.PeopleFragment;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout mDrawer;
    private ActionBarDrawerToggle mToggle;
    private NavigationView navigationView;

    private int menuItem;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener stateListener;
    private FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawer = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.nav_view);

        mToggle = new ActionBarDrawerToggle(this,mDrawer,R.string.open,R.string.close);
        mDrawer.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigationView.bringToFront();

        navigationView.getMenu().findItem(R.id.chats).setChecked(true);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                navigationView.getMenu().findItem(menuItem).setChecked(false);
                navigationView.getMenu().findItem(item.getItemId()).setChecked(true);
                Fragment fragment = null;
                Class fragmentClass= null;
                switch (item.getItemId()){
                    case R.id.people:
                        fragmentClass = PeopleFragment.class;
                        break;
                    case R.id.friends:
                        fragmentClass = FriendsFragment.class;
                        break;
                    case R.id.chats:
                        fragmentClass = ChatFragment.class;
                        break;
                }

                menuItem = item.getItemId();

                try{
                    fragment = (Fragment)fragmentClass.newInstance();
                }catch (Exception e){
                    e.printStackTrace();
                }

                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction().replace(R.id.fragment,fragment);
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                fragmentTransaction.commit();
                mDrawer.closeDrawer(GravityCompat.START);

                return false;
            }
        });
        chatFragment();


        mFirebaseAuth = FirebaseAuth.getInstance();


        stateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user!=null){

                }else{
                    Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                    startActivityForResult(intent,1);
                }
            }
        };

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1){
            if (resultCode == RESULT_CANCELED){
                finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else if(menuItem == R.id.chats) {
            finish();
        }else if (menuItem != R.id.chats){
            navigationView.getMenu().findItem(R.id.chats).setChecked(true);
            navigationView.getMenu().findItem(menuItem).setChecked(false);
            menuItem = R.id.chats;
            chatFragment();
        }else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mToggle.onOptionsItemSelected(item)){
            return true;
        }else if(item.getItemId() == R.id.log_out){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are You want to quit Chat??");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    navigationView.getMenu().findItem(menuItem).setChecked(false);
                    navigationView.getMenu().findItem(R.id.chats).setChecked(true);
                    menuItem = R.id.chats;
                    chatFragment();
                    AuthUI.getInstance().signOut(MainActivity.this);
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (dialogInterface != null){
                        dialogInterface.dismiss();
                    }
                }
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();

            return  true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void chatFragment(){
        menuItem = R.id.chats;
        Fragment fragment = new ChatFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction().replace(R.id.fragment, fragment);
        fragmentTransaction.commit();
    }


    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(stateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (stateListener!=null){
            mFirebaseAuth.removeAuthStateListener(stateListener);
        }
    }
}
