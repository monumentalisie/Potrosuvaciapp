package com.potrosuvaci.activty;

import static com.util.AppConstants.SHARED_PREFERENCES;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.potrosuvaci.R;
import com.potrosuvaci.adapter.MainItemAdapter;
import com.util.AssetReader;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Locale;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();


    private JSONArray jsonContent;
    private RecyclerView mRecyclerView;
    private TextView languageMenu;
    private ImageView aboutMenuImageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        initViews();
        reloadRecyclerViewData();
        setMenus();
        updateLocale();
    }

    private void updateLocale() {
        String languageStoredInSharedPreferences = getLanguageStoredInSharedPreferences();
        String currentLocale = getCurrentLocale();
        if (languageStoredInSharedPreferences.equalsIgnoreCase("MK")) {
            if (!currentLocale.equalsIgnoreCase("EN-US")) {
                changeLanguage(languageStoredInSharedPreferences);
            }
        } else {
            if (!currentLocale.equalsIgnoreCase("sq")) {
                changeLanguage(languageStoredInSharedPreferences);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        reloadRecyclerViewData();
        languageMenu.setText(getLanguageStoredInSharedPreferences().toUpperCase());

    }

    private void setMenus() {
        languageMenu = findViewById(R.id.language_dropdown_menu);
        languageMenu.setText(getLanguageStoredInSharedPreferences().toUpperCase());
        aboutMenuImageView = findViewById(R.id.about_dropdown_menu);
        aboutMenuImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
            }
        });

        languageMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(MainActivity.this, languageMenu);
                popupMenu.getMenuInflater().inflate(R.menu.language_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        changeLanguage(item.getTitle().toString());
                        reloadRecyclerViewData();
                        languageMenu.setText(item.getTitle().toString().toUpperCase());
                        return true;
                    }
                });

                popupMenu.show();
            }
        });
    }

    private void reloadRecyclerViewData() {
        jsonContent = setJsonContent();
        mRecyclerView.setAdapter(null);
        MainItemAdapter mainItemAdapter = new MainItemAdapter(jsonContent, this);
        mRecyclerView.setAdapter(mainItemAdapter);
        mRecyclerView.invalidate();
    }


    private void initViews() {
        mRecyclerView = findViewById(R.id.main_menu_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));


    }

    private JSONArray setJsonContent() {
        String jsonFile = getLanguageStoredInSharedPreferences().equalsIgnoreCase("mk") ?
                "informacii_mk.json" : "informacii_sq.json";
        final String content = AssetReader.loadJSONFromAsset(this.getAssets(),
                jsonFile);
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(content);
        } catch (JSONException e) {
            Log.e(TAG, "Failed to read json content", e);
        }
        return jsonArray;
    }

    private String getLanguageStoredInSharedPreferences() {
        return getPreferences().getString("language", "mk");
    }

    private void changeLanguage(String value) {
        SharedPreferences sharedPreferences = getPreferences();
        String language = "MK".equalsIgnoreCase(value) ? "mk" : "sq";
        sharedPreferences.edit().putString("language", language).apply();
        setLocale(value);
    }

    private String getCurrentLocale() {
        return getResources().getConfiguration().locale.toString();
    }

    private void setLocale(String local) {
        final Resources resources = getResources();
        final Configuration configuration = resources.getConfiguration();
        final Locale locale = getLocale(local);
        if (!configuration.locale.equals(locale)) {
            configuration.setLocale(locale);
            resources.updateConfiguration(configuration, null);
        }
    }

    public static Locale getLocale(String lang) {

        if ("AL".equalsIgnoreCase(lang) || "SQ".equalsIgnoreCase(lang)) {
            return new Locale("sq");
        }
        return Locale.getDefault();
    }

    private SharedPreferences getPreferences() {
        return getSharedPreferences(SHARED_PREFERENCES,
                Context.MODE_PRIVATE);
    }

}
