package com.potrosuvaci.activty;

import static com.util.AppConstants.SHARED_PREFERENCES;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.potrosuvaci.R;

import java.util.Locale;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class AboutActivity extends AppCompatActivity {


    private TextView aboutUsTitle;

    private TextView aboutUsTextContent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_activity);
        Toolbar myToolbar = findViewById(R.id.toolbar);
        initViews();
        setSupportActionBar(myToolbar);
        setMenus();
    }

    private void initViews() {
        aboutUsTitle = findViewById(R.id.about_us_title);
        aboutUsTextContent = findViewById(R.id.about_us_text_content);
    }

    private void setMenus() {
        TextView languageMenu = findViewById(R.id.language_dropdown_menu);
        languageMenu.setText(getLanguage().toUpperCase());
        ImageView aboutMenu = findViewById(R.id.about_dropdown_menu);
        aboutMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(AboutActivity.this, AboutActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
                finish();
            }
        });

        languageMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(AboutActivity.this, languageMenu);
                popupMenu.getMenuInflater().inflate(R.menu.language_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        changeLanguage(item.getTitle().toString());
                        languageMenu.setText(item.getTitle().toString().toUpperCase());
                        reloadViews();
                        return true;
                    }
                });

                popupMenu.show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void reloadViews() {
        String title = getResources().getString(R.string.about_us_menu_item);
        String text = getResources().getString(R.string.about_us_text);
        aboutUsTitle.setText(title);
        aboutUsTextContent.setText(text);
    }

    private void changeLanguage(String value) {
        SharedPreferences sharedPreferences = getPreferences();
        String language = "MK".equalsIgnoreCase(value) ? "mk" : "sq";
        sharedPreferences.edit().putString("language", language).apply();
        setLocale(value);
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

    private String getLanguage() {
        return getPreferences().getString("language", "mk");
    }

    private SharedPreferences getPreferences() {
        return getSharedPreferences(SHARED_PREFERENCES,
                Context.MODE_PRIVATE);
    }
}
