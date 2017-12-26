package com.mineiro.luara.projeto_noticias.activitys;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.mineiro.luara.projeto_noticias.R;
import com.mineiro.luara.projeto_noticias.adapters.ListNewsAdapter;
import com.mineiro.luara.projeto_noticias.daos.NewsDao;
import com.mineiro.luara.projeto_noticias.models.News;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HistoricActivity extends AppCompatActivity {
    private static final String TAG = "HistoricActivity";
    private static final String HISTORIC = "historic";

    private String historic;
    private NewsDao newsDao;
    private List<News> feedModelList;
    private RecyclerView list_view;
    private SwipeRefreshLayout mSwipeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);

        historic = getPreferencesHistoric();
        //Log.d(TAG, favorites);
        newsDao = new NewsDao(this);
        feedModelList = listNews(historic);

        if (feedModelList.size() <= 0) {
            Toast.makeText(HistoricActivity.this, "Ainda não existe nenhuma notícia no Histórico!",Toast.LENGTH_LONG).show();
        }

        list_view = (RecyclerView)findViewById(R.id.list_view);
        list_view.setLayoutManager(new LinearLayoutManager(this));
        list_view.setAdapter(new ListNewsAdapter(feedModelList, newsDao, false));

        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);

        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                feedModelList = listNews(historic);
                if (feedModelList.size() <= 0) {
                    Toast.makeText(HistoricActivity.this, "Ainda não existe nenhuma notícia no Histórico!",Toast.LENGTH_LONG).show();
                }
                list_view.setAdapter(new ListNewsAdapter(feedModelList, newsDao, false));
                mSwipeLayout.setRefreshing(false);
            }
        });
    }

    private String getPreferencesHistoric() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        return prefs.getString(HISTORIC, "");
    }

    private List<News> listNews(String string) {
        try {
            JSONArray jsonArray = new JSONArray(string);
            int count = 0;
            List<News> items = new ArrayList<>();
            newsDao.open();
            while (count < jsonArray.length()) {
                JSONObject detailsJson = jsonArray.getJSONObject(count);
                long id = Long.parseLong(detailsJson.getString("id"));
                //Log.d(TAG, "PEGOU = " + id);
                News item = newsDao.getOne(id);
                if (item != null) {
                    items.add(item);
                }
                count++;
            }
            newsDao.close();
            return orderByDate(items);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<News> orderByDate(List<News> items) {
        Collections.sort(items, new Comparator<News>() {
            @Override
            public int compare(News new1, News new2)
            {
                //Log.d(TAG, "new1 = " + new1.date_historic);
                //Log.d(TAG, "new2 = " + new2.date_historic);
                return  new2.date_historic.compareTo(new1.date_historic);
            }
        });
        return items;
    }
}
