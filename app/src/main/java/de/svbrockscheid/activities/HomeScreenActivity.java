package de.svbrockscheid.activities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;

import de.svbrockscheid.APIClient;
import de.svbrockscheid.R;
import de.svbrockscheid.fragments.AboutFragment;
import de.svbrockscheid.fragments.InfoFragment;
import de.svbrockscheid.fragments.MenuFragment;
import de.svbrockscheid.fragments.SpielplanFragment;
import de.svbrockscheid.fragments.UebersichtsFragment;
import de.svbrockscheid.sync.SyncAdapter;

public class HomeScreenActivity extends AppCompatActivity
        implements MenuFragment.NavigationDrawerCallbacks {

    public static final String NEUE_NACHRICHTEN = "de.svbrockscheid.NEUE_NACHRICHTEN";
    private static final String UEBERSICHT = "übersicht";
    private static final String SPIELPLAN = "spielplan";
    private static final String ABOUT = "about";
    private static final String INFO = "info";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        //actionbar aufsetzen
        Toolbar toolbar = (Toolbar) findViewById(R.id.svb_toolbar);
        setSupportActionBar(toolbar);

//        //check if the menu should be displayed or not
//        if(findViewById(R.id.container2) != null) {
//            // alle views auf einmal anzeigen
//            getSupportFragmentManager().beginTransaction().replace(R.id.container, new InfoFragment()).replace(R.id.container2, new UebersichtsFragment()).replace(R.id.container3, new SpielplanFragment()).commit();
//        } else {
        //menü anzeigen
        MenuFragment mMenuFragment;
        if (savedInstanceState == null) {
            mMenuFragment = new MenuFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.navigation_drawer, mMenuFragment).commit();
        } else {
            mMenuFragment = (MenuFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        }

        // Set up the drawer.
        mMenuFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout),
                this);
        APIClient.registerCGM(this);
        //sync aufsetzen
        // Create the account type and default account
        Account newAccount = new Account(getString(R.string.account_name), APIClient.ACCOUNT_TYPE);
        // Get an instance of the Android account manager
        AccountManager accountManager = (AccountManager) getSystemService(ACCOUNT_SERVICE);
        if (accountManager.addAccountExplicitly(newAccount, "", new Bundle())) {
            ContentResolver.setIsSyncable(newAccount, APIClient.AUTHORITY, 1);
            ContentResolver.setSyncAutomatically(newAccount, APIClient.AUTHORITY, true);
            //set a periodic sync
            ContentResolver.addPeriodicSync(newAccount, APIClient.AUTHORITY, new Bundle(), DateUtils.HOUR_IN_MILLIS);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (NEUE_NACHRICHTEN.equals(intent.getAction())) {
            //die Nachrichten auswählen
            ((MenuFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer)).selectItem(MenuFragment.INFO_POSITION);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                //notification entfernen
                notificationManager.cancel(SyncAdapter.NOTIFICATION_ID_NEUE_NACHRICHTEN);
            }
        }
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment selectedFragment;
        String backstackName;
        switch (position) {
            case MenuFragment.INFO_POSITION:
                selectedFragment = new InfoFragment();
                backstackName = INFO;
                break;
            case MenuFragment.UEBERSICHT_POSITION:
                selectedFragment = new UebersichtsFragment();
                backstackName = UEBERSICHT;
                break;
            case MenuFragment.SPIELPLAN_POSITION:
                selectedFragment = new SpielplanFragment();
                backstackName = SPIELPLAN;
                break;
            case MenuFragment.ABOUT_POSITION:
                selectedFragment = new AboutFragment();
                backstackName = ABOUT;
                break;
            default:
                selectedFragment = new UebersichtsFragment();
                backstackName = INFO;
        }
        if (fragmentManager.getBackStackEntryCount() == 0 || !fragmentManager.popBackStackImmediate(backstackName, 0)) {
            Fragment fragmentById = fragmentManager.findFragmentById(R.id.container);
            if (fragmentById != null && fragmentById.getClass() == selectedFragment.getClass()) {
                //nichts tun!
                return;
            }
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.container, selectedFragment);
            if (fragmentById != null) {
                //ab in den back stack!
                transaction.addToBackStack(backstackName);
            }
            transaction.commit();
        }
    }
}
