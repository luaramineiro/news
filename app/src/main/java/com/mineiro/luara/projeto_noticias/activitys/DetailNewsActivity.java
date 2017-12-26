package com.mineiro.luara.projeto_noticias.activitys;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mineiro.luara.projeto_noticias.R;
import com.mineiro.luara.projeto_noticias.daos.NewsDao;
import com.mineiro.luara.projeto_noticias.models.News;
import org.json.JSONException;
import java.util.Date;

public class DetailNewsActivity extends AppCompatActivity {
    private long news_id;
    private NewsDao newsDao;
    private TextView titleTextView;
    private TextView descriptionTextView;
    private TextView linkTextView;
    private TextView pubDateTextView;
    private ImageView imageView;
    private ImageView starView;
    private News item;

    private static final String TAG = "DetailNewsActivity";
    private static final String KEY_ID = "news_id";
    private static final String KEY_URL = "news_link";
    private static final String FAVORITES = "favorites";
    private static final String HISTORIC = "historic";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_news);

        titleTextView = (TextView)findViewById(R.id.title);
        descriptionTextView = (TextView)findViewById(R.id.description);
        linkTextView = (TextView)findViewById(R.id.link);
        pubDateTextView = (TextView)findViewById(R.id.pub_date);
        imageView = (ImageView)findViewById(R.id.image);

        linkTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNetworkAvaliable()) {
                    Uri uri = Uri.parse(getPreferencesNewsLink());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(DetailNewsActivity.this, "Por favor, restabeleça o acesso a internet para visitar o site!", Toast.LENGTH_LONG).show();
                }
            }
        });

        news_id = getPreferencesNewsId();
        if (news_id > 0) {
            newsDao = new NewsDao(this);
            newsDao.open();
            item = newsDao.getOne(news_id);
            if (item != null) {
                titleTextView.setText(item.title);
                descriptionTextView.setText(item.description);
                linkTextView.setText(item.link);
                pubDateTextView.setText(item.pub_date);
                if (item.image != null) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(item.image, 0, item.image.length);
                    imageView.setImageBitmap(bitmap);
                }
                else {
                    imageView.setImageResource(R.mipmap.ic_image_feed);
                }
            }
            else {
                Toast.makeText(this, "Desculpe, não achamos essa notícia para ser exibidas!", Toast.LENGTH_LONG).show();
            }
            newsDao.close();

            //Historic
            newsDao.updateHistoric(news_id, 1);
            item.setHistoric(1);
            Date data = new Date();
            savePreferencesHistoric();

            starView = (ImageView) findViewById(R.id.star);

            if (item.favorite == 1) {
                starView.setImageResource(android.R.drawable.btn_star_big_on);
            }
            else {
                starView.setImageResource(android.R.drawable.btn_star_big_off);
            }

            starView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Favotires
                    if (item.favorite == 0) {
                        Toast.makeText(view.getContext(), "Você Adicionou está notícia nos Favoritos!", Toast.LENGTH_LONG).show();
                        starView.setImageResource(android.R.drawable.btn_star_big_on);
                        newsDao.updateFavorite(news_id, 1);
                        item.setFavorite(1);
                        savePreferencesFavorites();

                    }
                    else {
                        Toast.makeText(view.getContext(), "Você Retirou está notícia dos Favoritos!", Toast.LENGTH_LONG).show();
                        starView.setImageResource(android.R.drawable.btn_star_big_off);
                        newsDao.updateFavorite(news_id, 0);
                        item.setFavorite(0);
                        savePreferencesFavorites();
                    }
                }
            });
        }
        else {
            Toast.makeText(this, "Desculpe, ocorreu um erro ao exibir esta notícia!", Toast.LENGTH_LONG).show();
        }
    }

    private long getPreferencesNewsId() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        return Long.parseLong(prefs.getString(KEY_ID, ""));
    }

    private String getPreferencesNewsLink() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        return prefs.getString(KEY_URL, "");
    }

    private void savePreferencesFavorites() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor edit = prefs.edit();
        try {
            String favorites = newsDao.getAllFavoritesJSON();
            //Log.d(TAG, favoritos);
            edit.putString(FAVORITES, favorites);
            edit.commit();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void savePreferencesHistoric() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor edit = prefs.edit();
        try {
            String historic = newsDao.getAllHistoricJSON();
            //Log.d(TAG, historic);
            edit.putString(HISTORIC, historic);
            edit.commit();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public boolean isNetworkAvaliable() {
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}
