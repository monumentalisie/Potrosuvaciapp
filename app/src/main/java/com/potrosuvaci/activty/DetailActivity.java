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
import com.potrosuvaci.adapter.SubtitleItemAdapter;
import com.util.AssetReader;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Locale;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class DetailActivity extends AppCompatActivity {

    private static final String TAG = DetailActivity.class.getSimpleName();

    private JSONArray jsonContent;
    private ImageView mainItemPicture;
    private TextView mainMenuTextView;
    private RecyclerView mRecyclerView;
    private TextView languageMenu;
    private int index = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        index = getIntent().getIntExtra("position", 0);

        initViews();
        setViewsContent(index);
        setMenus();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setViewsContent(index);
        languageMenu.setText(getLanguage().toUpperCase());
    }

    private void setMenus() {
        languageMenu = findViewById(R.id.language_dropdown_menu);
        languageMenu.setText(getLanguage().toUpperCase());
        ImageView aboutMenu = findViewById(R.id.about_dropdown_menu);
        aboutMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(DetailActivity.this, AboutActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
            }
        });

        languageMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(DetailActivity.this, languageMenu);
                popupMenu.getMenuInflater().inflate(R.menu.language_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        changeLanguage(item.getTitle().toString());
                        setViewsContent(index);
                        languageMenu.setText(item.getTitle().toString().toUpperCase());
                        return true;
                    }
                });

                popupMenu.show();
            }
        });
    }


    private void setViewsContent(int index) {
        try {
            jsonContent = setJsonContent();
            String name = jsonContent.getJSONObject(index).get("name").toString();
            mainMenuTextView.setText(name);
            mRecyclerView.setAdapter(null);
            mainItemPicture.setImageResource(getMainImageView(index));
            JSONArray titlesAndSubtitles = (JSONArray) jsonContent.getJSONObject(index).get(
                    "info");
            String usefullInformations = jsonContent.getJSONObject(index).get(
                    "korisni_informacii_celtext").toString();
            JSONArray hyperlinks = (JSONArray) jsonContent.getJSONObject(index).get("links");
            String imageLocation = jsonContent.getJSONObject(index).get("vodic_image").toString();
            String imageUrl = jsonContent.getJSONObject(index).get("vodic_url").toString();
            SubtitleItemAdapter subtitleItemAdapter = new SubtitleItemAdapter(titlesAndSubtitles,
                    hyperlinks, usefullInformations,imageLocation,imageUrl, DetailActivity.this);
            mRecyclerView.setAdapter(subtitleItemAdapter);
            mRecyclerView.invalidate();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private int getMainImageView(int index) {
        switch (index) {
            case 1:
                return R.drawable.umbrella_icon;
            case 2:
                return R.drawable.wallet_icon;
            case 3:
                return R.drawable.cellphone_and_shopping_cart_icon;
            case 4:
                return R.drawable.house_icon;
            case 5:
                return R.drawable.windmill_icon;
            default:
                return R.drawable.shopping_cart_icon;
        }
    }

    private void initViews() {
        mainItemPicture = findViewById(R.id.main_menu_detail_view_picture);
        mainMenuTextView = findViewById(R.id.main_menu_text_detail_view);
        mRecyclerView = findViewById(R.id.recycler_view_with_subtexts);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private JSONArray setJsonContent() {
        String jsonFile = getLanguage().equalsIgnoreCase("mk") ?
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

    private String getLanguage() {
        return getPreferences().getString("language", "mk");
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

    private SharedPreferences getPreferences() {
        return getSharedPreferences(SHARED_PREFERENCES,
                Context.MODE_PRIVATE);
    }
}
