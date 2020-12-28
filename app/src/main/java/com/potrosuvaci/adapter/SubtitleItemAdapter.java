package com.potrosuvaci.adapter;

import static com.util.StringUtils.isEmpty;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.potrosuvaci.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SubtitleItemAdapter extends RecyclerView.Adapter<SubtitleItemAdapter.ViewHolder> {

    private static final String TAG = SubtitleItemAdapter.class.getSimpleName();

    private final JSONArray titlesAndSubtitles;
    private final JSONArray hyperlinks;
    private final String usefullInformations;
    private final String vodicImage;
    private final String vodicUrl;
    private final Context mContext;

    public SubtitleItemAdapter(JSONArray titlesAndSubtitles, JSONArray hyperlinks,
            String usefullInformations, String vodicImage, String vodicUrl, Context context) {
        this.titlesAndSubtitles = titlesAndSubtitles;
        this.hyperlinks = hyperlinks;
        this.usefullInformations = usefullInformations;
        this.vodicImage = vodicImage;
        this.vodicUrl = vodicUrl;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.subtitle_list_item,
                parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            if (position == titlesAndSubtitles.length()) {
                String title = holder.mContext.getResources().getString(R.string.vodic);
                holder.subtitle.setText(title);

                int imageResource = holder.mContext.getResources().getIdentifier(vodicImage,
                        "drawable",
                        holder.mContext.getPackageName());
                holder.vodicImage.setImageResource(imageResource);
                holder.vodicImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(vodicUrl));
                        holder.mContext.startActivity(browserIntent);

                    }
                });
            } else if (position == titlesAndSubtitles.length() - 1) {
                String title = holder.mContext.getResources().getString(
                        R.string.korisni_informacii);
                holder.subtitle.setText(title);
                SpannableString ss = setHyperlinks();
                holder.content.setText(ss);
                holder.content.setMovementMethod(LinkMovementMethod.getInstance());
            } else {
                String title = titlesAndSubtitles.getJSONObject(position).get(
                        "question").toString();
                boolean shouldExpand = Boolean.parseBoolean(titlesAndSubtitles.getJSONObject(
                        position).get("cellTypeExpand").toString());
                String content = titlesAndSubtitles.getJSONObject(position).get("info").toString();
                holder.subtitle.setText(title);
                holder.content.setText(content);
                if (shouldExpand) {
                    holder.itemView.performClick();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private SpannableString setHyperlinks() throws JSONException {
        SpannableString ss = new SpannableString(usefullInformations);
        for (int i = 0; i < hyperlinks.length(); i++) {
            JSONObject jsonObject = hyperlinks.getJSONObject(i);
            String hyperlinkText = jsonObject.get("hyperlink").toString();
            String url = jsonObject.get("url").toString();
            String lat = null;
            String lon = null;
            try {
                lat = jsonObject.get("latitude").toString();
                lon = jsonObject.get("longitude").toString();
            } catch (Exception e) {
                Log.e(TAG, "No latitude or longitude");
            }
            final String latitude = lat;
            final String longitude = lon;
            int startIndex = usefullInformations.indexOf(hyperlinkText);
            int endIndex = usefullInformations.lastIndexOf(hyperlinkText);

            Pattern word = Pattern.compile(hyperlinkText);
            Matcher match = word.matcher(usefullInformations);

            while (match.find()) {
                startIndex = match.start();
                endIndex = match.end();
            }
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(@NonNull View view) {
                    if (!isEmpty(latitude) && !isEmpty(longitude)) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        final View customLayout = LayoutInflater.from(mContext).inflate(
                                R.layout.open_link_dialog_layout, null);
                        TextView textView = customLayout.findViewById(R.id.select_web_page);
                        builder.setView(customLayout);

                        AlertDialog dialog = builder.create();
                        Window window = dialog.getWindow();
                        WindowManager.LayoutParams wlp = window.getAttributes();

                        wlp.gravity = Gravity.BOTTOM;
                        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                        window.setAttributes(wlp);
                        dialog.show();
                        textView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                startBrowser(url);
                                dialog.dismiss();
                            }
                        });

                        TextView openGoogleMapsView = customLayout.findViewById(
                                R.id.select_open_address);
                        openGoogleMapsView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                openGoogleMaps(latitude, longitude);
                                dialog.dismiss();
                            }
                        });
                    } else {
                        startBrowser(url);
                    }
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    ds.setUnderlineText(true);
                }
            };
            ss.setSpan(clickableSpan, startIndex, endIndex,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return ss;
    }

    private void startBrowser(String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(browserIntent);

    }

    private void openGoogleMaps(String latitude, String longitude) {
        Uri gmmIntentUri = Uri.parse(
                "http://maps.google.com/maps?saddr=Your+location&daddr=" + latitude
                        + "," + longitude);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mapIntent.setPackage("com.google.android.apps.maps");
        mContext.startActivity(mapIntent);
    }

    @Override
    public int getItemCount() {
        return titlesAndSubtitles.length() + 1;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView subtitle;
        private final TextView content;
        private final ImageView arrow;
        private final ImageView vodicImage;
        private final Context mContext;

        ViewHolder(View itemView) {
            super(itemView);
            subtitle = itemView.findViewById(R.id.subtitle_menu_title);
            content = itemView.findViewById(R.id.subtitle_menu_content);
            arrow = itemView.findViewById(R.id.item_arrow);
            vodicImage = itemView.findViewById(R.id.vodic_image);
            mContext = itemView.getContext();
            this.itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            String vodicText = mContext.getResources().getString(R.string.vodic);
            if (subtitle.getText().equals(vodicText)) {
                content.setVisibility(View.GONE);
                if (vodicImage.getVisibility() == View.GONE) {
                    vodicImage.setVisibility(View.VISIBLE);
                    arrow.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24);
                } else {
                    vodicImage.setVisibility(View.GONE);
                    arrow.setImageResource(R.drawable.ic_baseline_keyboard_arrow_right_24);
                }


            } else {
                handleTextClicked();
            }
        }

        private void handleTextClicked() {
            if (content.getVisibility() == View.GONE) {
                content.setVisibility(View.VISIBLE);
                arrow.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24);
            } else {
                content.setVisibility(View.GONE);
                arrow.setImageResource(R.drawable.ic_baseline_keyboard_arrow_right_24);
            }
        }
    }
}
