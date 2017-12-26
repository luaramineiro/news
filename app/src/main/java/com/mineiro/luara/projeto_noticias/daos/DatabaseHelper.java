package com.mineiro.luara.projeto_noticias.daos;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by luara on 29/11/17.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int versao = 6;

    public DatabaseHelper(Context context) {
        super(context, "banco.db", null, versao);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE news(" +
                "  _id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  title TEXT," +
                "  description TEXT," +
                "  link TEXT," +
                "  pub_date TEXT," +
                "  image BLOB," +
                "  url_image TEXT," +
                "  favorite INTEGER," +
                "  historic INTEGER, " +
                "  date_historic TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS news");
        // Create tables again
        onCreate(db);
    }
}
