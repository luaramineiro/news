package com.mineiro.luara.projeto_noticias.daos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.util.Log;
import com.mineiro.luara.projeto_noticias.models.News;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

/**
 * Created by luara on 29/11/17.
 * DAO for table NEWS
 */

public class NewsDao {
    private static final String TAG = "NewsDao";

    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    public NewsDao(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() {
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
        db = null;
    }

    public List<News> getAll() {
        List<News> lista = new ArrayList<>();
        this.open();
        Cursor cursor = db.query(false, "news", null, null, null, null, null, "title", null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            long id = cursor.getInt(0);
            String title = cursor.getString(1);
            String description = cursor.getString(2);
            String link = cursor.getString(3);
            String pub_date = cursor.getString(4);
            byte[] image = cursor.getBlob(5);
            String url_image = cursor.getString(6);
            int favorite = cursor.getInt(7);
            int historic = cursor.getInt(8);
            String date_historic = cursor.getString(9);
            News item = new News(title, link, description, pub_date, url_image, image, favorite, historic, date_historic);
            item.setId(id);
            lista.add(item);
            cursor.moveToNext();
        }
        this.close();
        return lista;
    }

    public News getOne(long id) {
        //Log.d(TAG, "getOne = " + id);
        Cursor cursor = db.rawQuery("SELECT * from news WHERE _id = ?", new String[]{String.valueOf(id)});
        if (cursor != null && cursor.moveToFirst()) {
            String title = cursor.getString(1);
            String description = cursor.getString(2);
            String link = cursor.getString(3);
            String pub_date = cursor.getString(4);
            byte[] image = cursor.getBlob(5);
            String url_image = cursor.getString(6);
            int favorite = cursor.getInt(7);
            int historic = cursor.getInt(8);
            String date_historic = cursor.getString(9);
            News item = new News(title, link, description, pub_date, url_image, image, favorite, historic, date_historic);
            item.setId(id);
            cursor.close();
            return item;
        }
        cursor.close();

        return null;
    }

    public String getAllFavoritesJSON() throws JSONException {
        this.open();
        JSONArray jsonArray = new JSONArray();
        Cursor cursor = db.rawQuery("SELECT * from news WHERE favorite = 1", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            JSONObject formDetailsJson = new JSONObject();
            formDetailsJson.put("id", String.valueOf(cursor.getInt(0)));
            //formDetailsJson.put("title", cursor.getString(1));
            //formDetailsJson.put("description", cursor.getString(2));
            //formDetailsJson.put("link", cursor.getString(3));
            //formDetailsJson.put("pub_date", cursor.getString(4));
            //formDetailsJson.put("url_image", cursor.getString(6));
            //formDetailsJson.put("favorite", cursor.getString(7));
            //formDetailsJson.put("historic", cursor.getString(8));
            jsonArray.put(formDetailsJson);
            cursor.moveToNext();
        }
        this.close();
        return String.valueOf(jsonArray);
    }

    public String getAllHistoricJSON() throws JSONException {
        this.open();
        JSONArray jsonArray = new JSONArray();
        Cursor cursor = db.rawQuery("SELECT * from news WHERE historic = 1", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            JSONObject formDetailsJson = new JSONObject();
            formDetailsJson.put("id", String.valueOf(cursor.getInt(0)));
            jsonArray.put(formDetailsJson);
            cursor.moveToNext();
        }
        this.close();
        return String.valueOf(jsonArray);
    }

    public int getNewsCount() {
        String countQuery = "SELECT * FROM news";
        Cursor cursor = db.rawQuery(countQuery, null);
        return cursor.getCount();
    }

    public long insert(News news) {
        this.open();
        ContentValues values = new ContentValues();
        values.put("title", news.title);
        values.put("description", news.description);
        values.put("link", news.link);
        values.put("pub_date", news.pub_date);
        values.put("image", news.image);
        values.put("url_image", news.url_image);
        values.put("favorite", news.favorite);
        values.put("historic", news.historic);
        long id = db.insert("news", null, values);
        Log.d(TAG, "insert new _id = " + id);
        this.close();
        return id;
    }

    public void updateImage(long id, byte[] image) {
        this.open();
        Cursor cursor = db.rawQuery("SELECT _id from news WHERE _id = ?", new String[]{String.valueOf(id)});
        cursor.moveToFirst();

        if ((image != null) && (cursor != null)) {
            ContentValues values = new ContentValues();
            values.put("image", image);
            db.update("news", values, "_id = ?", new String[]{String.valueOf(id)});
            Log.d(TAG, "update image _id = " + id);
        }

        this.close();
    }

    public  boolean validateLink(String link) {
        //Log.d(TAG, "Link = " + link);
        this.open();
        Cursor cursor = db.rawQuery("SELECT * from news WHERE link like ?", new String[]{String.valueOf(link)});
        //Log.d(TAG, "COUNT DB = " + cursor.getCount());
        if (cursor.getCount() <= 0) {
            return true;
        }
        return false;
    }

    public boolean removeAll() {
        this.open();
        db.execSQL("DELETE FROM news WHERE favorite = 0 and historic = 0"); //
        this.close();
        return true;
    }

    public void updateFavorite(long id, int favorite) {
        this.open();
        Cursor cursor = db.rawQuery("SELECT _id from news WHERE _id = ?", new String[]{String.valueOf(id)});
        cursor.moveToFirst();

        if (((favorite == 0) || (favorite == 1)) && (cursor != null)) {
            ContentValues values = new ContentValues();
            values.put("favorite", favorite);
            db.update("news", values, "_id = ?", new String[]{String.valueOf(id)});
            //Log.d(TAG, "update favorite _id = " + id);
        }

        this.close();
    }

    public void updateHistoric(long id, int historic) {
        this.open();
        Cursor cursor = db.rawQuery("SELECT _id from news WHERE _id = ?", new String[]{String.valueOf(id)});
        cursor.moveToFirst();

        if (((historic == 0) || (historic == 1)) && (cursor != null)) {
            ContentValues values = new ContentValues();
            Date date = new Date();
            SimpleDateFormat formatador = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            values.put("historic", historic);
            values.put("date_historic", formatador.format(date).toString());
            db.update("news", values, "_id = ?", new String[]{String.valueOf(id)});
            //Log.d(TAG, "update historic _id = " + id);
        }

        this.close();
    }
}
