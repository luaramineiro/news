package com.mineiro.luara.projeto_noticias.activitys;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import com.mineiro.luara.projeto_noticias.R;
import com.mineiro.luara.projeto_noticias.models.Sites;
import java.util.ArrayList;
import java.util.List;


public class InfoActivity extends AppCompatActivity {

    private RecyclerView list_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
    }
}
