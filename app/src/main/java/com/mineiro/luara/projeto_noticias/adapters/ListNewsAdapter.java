package com.mineiro.luara.projeto_noticias.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mineiro.luara.projeto_noticias.activitys.DetailNewsActivity;
import com.mineiro.luara.projeto_noticias.daos.NewsDao;
import com.mineiro.luara.projeto_noticias.models.News;
import com.mineiro.luara.projeto_noticias.R;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

/**
 * Created by luara on 28/11/17.
 * Adpapter for List News
 */

public class ListNewsAdapter extends RecyclerView.Adapter<ListNewsAdapter.FeedModelViewHolder> {

    private static final String TAG = "ListNewsAdapter";

    private List<News> feedModelList;
    private NewsDao newsDao;
    boolean isConnect;

    public static class FeedModelViewHolder extends RecyclerView.ViewHolder {
        private static final String KEY_ID = "news_id";
        private static final String KEY_URL = "news_link";

        private View rssFeedView;
        private CardView cardView;
        private TextView idText;
        private TextView linkText;
        //private TextView titleText;

        public FeedModelViewHolder(View v) {
            super(v);
            rssFeedView = v;
            cardView = (CardView)v.findViewById(R.id.card_view);
            //titleText = (TextView)v.findViewById(R.id.title);
            idText = (TextView)v.findViewById(R.id.id);
            linkText = (TextView)v.findViewById(R.id.link);
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), DetailNewsActivity.class);
                    Context context = v.getContext();
                    savePreferencesIdUrl((String) idText.getText(), (String) linkText.getText(), context);
                    context.startActivity(intent);
                    //Toast.makeText(context, "Card_Id = "+idText.getText()+" Titulo ="+titleText.getText()+"!", Toast.LENGTH_LONG).show();
                }
            });
        }

        private void savePreferencesIdUrl(String id, String link, Context context) {
            //Log.d(TAG, "savePreferencesId = "+id);
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor edit = prefs.edit();
            edit.putString(KEY_ID, id);
            edit.putString(KEY_URL, link);
            edit.commit();
        }
    }

    public ListNewsAdapter(List<News> rssFeedModels, NewsDao newsDao, boolean isConnect) {
        this.feedModelList = rssFeedModels;
        this.newsDao = newsDao;
        this.isConnect = isConnect;
    }

    @Override
    public FeedModelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_new, parent, false);
        FeedModelViewHolder holder = new FeedModelViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(FeedModelViewHolder holder, int position) {
        final News rssFeedModel = feedModelList.get(position);
        //url = "http://www.rrpdesign.com.br/c/UXmobile/front/img/icons/icon-news.png";

        if (isConnect && (rssFeedModel.image == null)) {
            new DownloadImageTask((ImageView)holder.rssFeedView.findViewById(R.id.image), rssFeedModel.getId(), position)
                    .execute(rssFeedModel.url_image);
        }
        else {
            if (rssFeedModel.image != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(rssFeedModel.image, 0, rssFeedModel.image.length);
                ((ImageView) holder.rssFeedView.findViewById(R.id.image)).setImageBitmap(bitmap);
            }
            //colocar aqui a imagem default quando não tiver uma imagem no banco
            else {
                ((ImageView) holder.rssFeedView.findViewById(R.id.image)).setImageResource(R.mipmap.ic_image_feed);
            }
        }

        ((TextView)holder.rssFeedView.findViewById(R.id.title)).setText(rssFeedModel.title);
        ((TextView)holder.rssFeedView.findViewById(R.id.pub_date)).setText(rssFeedModel.pub_date);
        ((TextView)holder.rssFeedView.findViewById(R.id.id)).setText(Long.toString(rssFeedModel.getId()));
        ((TextView)holder.rssFeedView.findViewById(R.id.link)).setText(rssFeedModel.link);
    }

    @Override
    public int getItemCount() {
        if (feedModelList == null) {
            return 0;
        } else {
            return feedModelList.size();
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        private ImageView bmImage;
        private long bmId;
        int position;

        public DownloadImageTask(ImageView bmImage, long bmId, int position) {
            this.bmImage = bmImage;
            this.bmId = bmId;
            this.position = position;
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
            if (result != null) {
                byte[] img = getBitmapAsByteArray(result);
                newsDao.updateImage(bmId, img);
                feedModelList.get(position).setImage(img);
            }
        }

        public byte[] getBitmapAsByteArray(Bitmap bitmap) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            return outputStream.toByteArray();
        }
    }
}
