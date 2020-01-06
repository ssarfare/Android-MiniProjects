package com.example.myfavoritemovies;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class SortingActivity extends AppCompatActivity {
    TextView tv_movieName,tv_title,tv_movieDescription,tv_genre,tv_rating,tv_year,tv_imdb;
    Button btn_finish;
    ImageView btn_showFirst,btn_showLast,btn_showNext,btn_showPrevious;
    ArrayList<Movie> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sorting);
        tv_title=findViewById(R.id.textView_sortByTitle);
        tv_movieName=findViewById(R.id.textView_movieName_SortActivity);
        tv_movieDescription=findViewById(R.id.editText_movieDescription_SortActivity);
        tv_genre=findViewById(R.id.textView_movieGenre_SortActivity);
        tv_rating=findViewById(R.id.textView_movieRating_SortActivity);
        tv_year=findViewById(R.id.textView_movieYear_SortActivity);
        tv_imdb=findViewById(R.id.textView_movieImdb_SortActivity);
        btn_finish=findViewById(R.id.button_finishSortActivity);
        btn_showFirst=findViewById(R.id.imageView_firstMovie_SortActivity);
        btn_showLast=findViewById(R.id.imageView_lastMovie_SortActivity);
        btn_showNext=findViewById(R.id.imageView_nextMovie_SortActivity);
        btn_showPrevious=findViewById(R.id.imageView_previousMovie_SortMovie);

        btn_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btn_showFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFirstMovie();
            }
        });

        btn_showLast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadLastMovie();
            }
        });

        btn_showNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadNextMovie();
            }
        });

        btn_showPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadPreviousMovie();
            }
        });

        if(getIntent().getExtras()!=null){
            setTitle(getIntent().getStringExtra("Title"));
            tv_title.setText(getIntent().getStringExtra("Title"));
            list= (ArrayList<Movie>) getIntent().getSerializableExtra("SortedList");
            loadFirstMovie();
        }
    }

    public void loadFirstMovie(){
        Movie m=list.get(0);
        setValues(m);
    }

    public void loadLastMovie(){
        Movie m=list.get(list.size()-1);
        setValues(m);
    }

    public void loadNextMovie() {
        int currentIndex = 0;
        Movie m;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).name.equals(tv_movieName.getText()))
                currentIndex = i;
        }
        currentIndex++;
        if (currentIndex < list.size()) {
            m = list.get(currentIndex);
            setValues(m);
        } else{
            Toast.makeText(this,"End of list reached",Toast.LENGTH_SHORT).show();
        }
    }

    public void loadPreviousMovie() {
        int currentIndex = 0;
        Movie m;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).name.equals(tv_movieName.getText()))
                currentIndex = i;
        }
        currentIndex--;
        if (currentIndex >= 0) {
            m = list.get(currentIndex);
            setValues(m);
        } else{
            Toast.makeText(this,"End of list reached",Toast.LENGTH_SHORT).show();
        }
    }



    public void setValues(Movie movie){
        tv_movieName.setText(movie.name);
        tv_movieDescription.setText(movie.description);
        tv_genre.setText(movie.genre);
        tv_rating.setText(String.valueOf(movie.rating));
        tv_year.setText(String.valueOf(movie.year));
        tv_imdb.setText(movie.imdbLink);
    }


}
