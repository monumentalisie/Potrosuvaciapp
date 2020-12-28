package com.potrosuvaci.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.potrosuvaci.R;
import com.potrosuvaci.activty.DetailActivity;

import org.json.JSONArray;
import org.json.JSONException;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainItemAdapter extends RecyclerView.Adapter<MainItemAdapter.ViewHolder> {

    private JSONArray mJSONArray;
    private Context mContext;

    public MainItemAdapter(JSONArray JSONArray, Context context) {
        mJSONArray = JSONArray;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_list_item,
                parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            String name = mJSONArray.getJSONObject(position).get("name").toString();
            holder.testMenuText.setText(name);
            holder.mRecyclerView.setAdapter(null);
            holder.mainMenuImage.setImageResource(getMainImageView(position));
            JSONArray titlesAndSubtitles = (JSONArray) mJSONArray.getJSONObject(position).get(
                    "info");
            String usefullInformations = mJSONArray.getJSONObject(position).get(
                    "korisni_informacii_celtext").toString();
            JSONArray hyperlinks = (JSONArray) mJSONArray.getJSONObject(position).get("links");
            String imageLocation = mJSONArray.getJSONObject(position).get("vodic_image").toString();
            String imageUrl = mJSONArray.getJSONObject(position).get("vodic_url").toString();
            SubtitleItemAdapter subtitleItemAdapter = new SubtitleItemAdapter(titlesAndSubtitles,
                    hyperlinks, usefullInformations, imageLocation, imageUrl, mContext);
            holder.mRecyclerView.setAdapter(subtitleItemAdapter);
            holder.mRecyclerView.invalidate();
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

    @Override
    public int getItemCount() {
        return mJSONArray.length();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView testMenuText;
        private ImageView mainMenuImage;
        private RecyclerView mRecyclerView;
        private Context mContext;

        ViewHolder(View itemView) {
            super(itemView);
            testMenuText = itemView.findViewById(R.id.main_menu_text_detail_view);
            mainMenuImage = itemView.findViewById(R.id.main_menu_detail_view_picture);
            mRecyclerView = itemView.findViewById(R.id.test_recycler_view_1);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
            mContext = itemView.getContext();
            this.itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            final Intent intent = new Intent(mContext, DetailActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("position", getAdapterPosition());
            mContext.startActivity(intent);
          /*  if (mRecyclerView.getVisibility() == View.GONE) {
                mRecyclerView.setVisibility(View.VISIBLE);
            } else {
                mRecyclerView.setVisibility(View.GONE);
            }*/
        }
    }
}
