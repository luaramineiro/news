package com.mineiro.luara.projeto_noticias.activitys;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.widget.Toast;

import com.mineiro.luara.projeto_noticias.adapters.ListNewsAdapter;
import com.mineiro.luara.projeto_noticias.daos.NewsDao;
import com.mineiro.luara.projeto_noticias.models.News;
import com.mineiro.luara.projeto_noticias.R;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by luara on 28/11/17.
 * Activity for List News
 */

public class ListNewsActivity extends AppCompatActivity {
    private static final String TAG = "ListNewsActivity";

    private RecyclerView list_view;
    private SwipeRefreshLayout mSwipeLayout;
    private NewsDao newsDao;
    private List<News> feedModelList;
    private String feed_title;
    private String feed_link;
    private String feed_description;
    private String feed_pub_date;
    private String feed_url_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);

        newsDao = new NewsDao(this);
        newsDao.open();

        list_view = (RecyclerView)findViewById(R.id.list_view);
        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);

        list_view.setLayoutManager(new LinearLayoutManager(this));

        if (newsDao.getNewsCount() > 0) {
            feedModelList = newsDao.getAll();
            list_view.setAdapter(new ListNewsAdapter(feedModelList, newsDao, isNetworkAvaliable()));
        }
        else {
            refreshListNews();
        }
        newsDao.close();

        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isNetworkAvaliable()) {
                    refreshListNews();
                }
                else {
                    mSwipeLayout.setRefreshing(false);
                    Toast.makeText(ListNewsActivity.this, "Desculpe! Reinicie a sua conexão com a internet para atualizar o seu feed!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public boolean parseFeed(InputStream inputStream) throws XmlPullParserException, IOException {
        String title = null;
        String link = null;
        String description = null;
        String pub_date = null;
        String url_image = null;
        boolean isItem = false;
        List<News> items = new ArrayList<>();

        try {
            newsDao = new NewsDao(this);
            //Remove all news from database for get news from internet
            newsDao.removeAll();

            XmlPullParser xmlPullParser = Xml.newPullParser();
            xmlPullParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            xmlPullParser.setInput(inputStream, null);

            xmlPullParser.nextTag();

            while ((xmlPullParser.next() != XmlPullParser.END_DOCUMENT)) {
                int eventType = xmlPullParser.getEventType();

                String name = xmlPullParser.getName();
                if(name == null)
                    continue;

                if(eventType == XmlPullParser.END_TAG) {
                    if(name.equalsIgnoreCase("item")) {
                        isItem = false;
                    }
                    continue;
                }

                if (eventType == XmlPullParser.START_TAG) {
                    if(name.equalsIgnoreCase("item")) {
                        isItem = true;
                        continue;
                    }
                }

                //Log.d(TAG, "Parsing name ==> " + name);

                String result = "";

                if (name.equalsIgnoreCase("media:thumbnail")) {
                    result = xmlPullParser.getAttributeValue(null, "url");
                }

                //Log.d(TAG, "IMAGE URL ==> " + result);

                if (xmlPullParser.next() == XmlPullParser.TEXT){
                    if (!name.equalsIgnoreCase("media:thumbnail")) {
                        result = xmlPullParser.getText();
                    }
                    xmlPullParser.nextTag();
                }

                if (name.equalsIgnoreCase("title")) {
                    title = result;
                } else if (name.equalsIgnoreCase("link")) {
                    link = result;
                } else if (name.equalsIgnoreCase("description")) {
                    description = result;
                } else if (name.equalsIgnoreCase("pubDate")) {
                    pub_date = result;
                } else if (name.equalsIgnoreCase("media:thumbnail")) {
                    url_image = result;
                }

                if (title != null && link != null && description != null && pub_date != null && url_image != null) {
                    if(isItem) {
                        //para não inserir noticias repetidas
                        if (newsDao.validateLink(link)) {
                            pub_date = pub_date.substring(5,7)+"/"+dayOfmonth(pub_date.substring(8,11))+"/"+pub_date.substring(12, 16)+" as "+pub_date.substring(17, 25);
                            //Log.d(TAG, "pub_date =>>>>>> " + pub_date);
                            /*
                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
                            Date date;
                            try {
                                date = dateFormat.parse(pub_date);

                                dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                                pub_date = dateFormat.format(date);

                                //Log.d(TAG, "DATA =>>>>>> " + pub_date);

                            } catch (ParseException e) {
                                e.printStackTrace();
                                //Log.d(TAG, "ERRO =>>>>> " +e.getMessage());
                            }
                            */

                            News item = new News(title, link, description, pub_date, url_image, null, 0, 0, null);
                            long id = newsDao.insert(item);
                            item.setId(id);
                            items.add(item);
                        }
                    }
                    else {
                        feed_title = title;
                        feed_link = link;
                        feed_description = description;
                        feed_pub_date = pub_date;
                        feed_url_image = url_image;
                    }

                    title = null;
                    link = null;
                    description = null;
                    pub_date = null;
                    url_image = null;
                    isItem = false;
                }
            }
        } finally {
            inputStream.close();
            newsDao.close();
        }

        return true;
    }

    public boolean isNetworkAvaliable() {
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public void refreshListNews() {
        //Check if has connection with internet and get news informations
        if (isNetworkAvaliable()) {
            new FetchFeedTask().execute((Void) null);
        }
        //if can't have connect with internet get informations from database
        else {
            feedModelList = newsDao.getAll();
            Toast.makeText(ListNewsActivity.this, "Por favor reinicie a sua conexão com a internet!", Toast.LENGTH_LONG).show();
        }

        list_view.setAdapter(new ListNewsAdapter(feedModelList, newsDao, isNetworkAvaliable()));
    }

    public String dayOfmonth(String month) {
        switch (month.toLowerCase()) {
            case "jan" : return "01";
            case "feb" : return "02";
            case "mar" : return "03";
            case "apr" : return "04";
            case "may" : return "05";
            case "june" : return "06";
            case "july" : return "07";
            case "aug" : return "08";
            case "sept" : return "09";
            case "oct" : return "10";
            case "nov" : return "11";
            case "dec" : return "12";
        }
        return null;
    }

    private class FetchFeedTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            mSwipeLayout.setRefreshing(true);
            feed_title = null;
            feed_link = null;
            feed_description = null;
            feed_pub_date = null;
            feed_url_image = null;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                if (isNetworkAvaliable()) {
                    String urlLink = "http://feeds.bbci.co.uk/portuguese/rss.xml";
                    URL url = new URL(urlLink);
                    InputStream inputStream = url.openConnection().getInputStream();
                    if (!parseFeed(inputStream)) {
                        return false;
                    }
                }
                else {
                    Toast.makeText(ListNewsActivity.this, "Desculpe! Reinicie a sua conexão com a internet para atualizar o seu feed!", Toast.LENGTH_LONG).show();
                }
                feedModelList = newsDao.getAll();
                return true;
            } catch (IOException e) {
                Log.e(TAG, "Error", e);
            } catch (XmlPullParserException e) {
                Log.e(TAG, "Error", e);
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            mSwipeLayout.setRefreshing(false);

            if (success) {
                list_view.setAdapter(new ListNewsAdapter(feedModelList, newsDao, isNetworkAvaliable()));
            } else {
                Toast.makeText(ListNewsActivity.this, "Desculpe! Não foi possível atualizar o seu feed!", Toast.LENGTH_LONG).show();
            }
        }
    }
}
