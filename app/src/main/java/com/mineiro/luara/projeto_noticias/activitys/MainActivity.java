package com.mineiro.luara.projeto_noticias.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.mineiro.luara.projeto_noticias.R;
import com.mineiro.luara.projeto_noticias.activitys.InfoActivity;
import com.mineiro.luara.projeto_noticias.activitys.ListNewsActivity;

/**
 * Created by luara on 28/11/17.
 * Activity for First Screen
 */

public class MainActivity extends AppCompatActivity {

    protected Button button_list;
    protected Button button_favorites;
    protected Button button_historic;
    protected Button button_info;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        button_list = (Button)findViewById(R.id.button_list);
        button_favorites = (Button)findViewById(R.id.button_favorites);
        button_historic = (Button)findViewById(R.id.button_historic);
        button_info = (Button)findViewById(R.id.button_info);

        button_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openListNews();
            }
        });

        button_favorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFavorites();
            }
        });

        button_historic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openHistoric();
            }
        });

        button_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openInfo();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    protected void openListNews() {
        Intent intent = new Intent(this, ListNewsActivity.class);
        startActivity(intent);
    }

    protected void openFavorites() {
        Intent intent = new Intent(this, FavoritesActivity.class);
        startActivity(intent);
    }

    protected void openHistoric() {
        Intent intent = new Intent(this, HistoricActivity.class);
        startActivity(intent);
    }

    protected void openInfo() {
        Intent intent = new Intent(this, InfoActivity.class);
        startActivity(intent);
    }
}
