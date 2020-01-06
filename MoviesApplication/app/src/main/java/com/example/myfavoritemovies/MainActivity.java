package com.example.myfavoritemovies;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    static int ADD_REQUEST =100;
    static int EDIT_REQUEST=101;
    static HashMap<String,String> movieList= new HashMap<String, String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button addMovie=findViewById(R.id.button_addMovie);
        Button editMovie=findViewById(R.id.button_editMovie);
        final Button deleteMovie=findViewById(R.id.button_deleteMovie);
        Button showListByYear=findViewById(R.id.button_showListByYear);
        Button showListByRating=findViewById(R.id.button_showListByRating);

        addMovie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i= new Intent(MainActivity.this,AddMovie.class);
                startActivityForResult(i,ADD_REQUEST);

            }
        });

        editMovie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getData(new iGetMovieData() {
                    @Override
                    public void getMovies(HashMap<String, String> movies) {
                        movieList=movies;
                        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        final String movieNameList []= movieList.values().toArray(new String[0]);
                        builder.setItems(movieNameList, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String id=movieList.keySet().toArray(new String[0])[i];
                                //Movie m=movieList.get(key);
                                Intent editIntent= new Intent(MainActivity.this,AddMovie.class);
                                editIntent.putExtra("title",movieNameList[i]);
                                editIntent.putExtra("documentId",id);
                                startActivityForResult(editIntent,EDIT_REQUEST);
                            }
                        });
                        final AlertDialog dialog=builder.create();
                        if(movieList.size()>0)
                            dialog.show();
                        else{
                            Toast.makeText(MainActivity.this, "No movies to edit", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void getMoviesDataList(ArrayList<Movie> movies) {

                    }
                });
            }
        });

        deleteMovie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getData(new iGetMovieData() {
                    @Override
                    public void getMovies(HashMap<String, String> movies) {
                        movieList=movies;
                        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        final String movieNameList []= movieList.values().toArray(new String[0]);
                        builder.setItems(movieNameList, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String id=movieList.keySet().toArray(new String[0])[i];
                                //Movie m=movieList.get(key);
                                deleteMovieById(id, new iDeleteMovie() {
                                            @Override
                                            public void deleteMovieById(boolean res) {
                                                if(res){
                                                    Toast.makeText(MainActivity.this,"Movie deleted successfully",Toast.LENGTH_SHORT).show();

                                                }
                                            }
                                        }
                                );


                            }
                        });
                        final AlertDialog dialog=builder.create();
                        if(movieList.size()>0)
                            dialog.show();
                        else{
                            Toast.makeText(MainActivity.this, "No movies to delete", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void getMoviesDataList(ArrayList<Movie> movies) {

                    }
                });

            }
        });

        showListByYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getData(new iGetMovieData() {
                    @Override
                    public void getMovies(HashMap<String, String> movies) {

                    }

                    @Override
                    public void getMoviesDataList(ArrayList<Movie> movies) {

                        if(movies.size()>0){
                            Collections.sort(movies, new Comparator<Movie>() {
                                @Override
                                public int compare(Movie movie1, Movie movie2) {
                                    return movie1.year-movie2.year;
                                }
                            });
                            Intent i= new Intent("com.example.myfavoritemovies.intent.action.VIEW");
                            i.addCategory(Intent.CATEGORY_DEFAULT);
                            i.putExtra("Title","Movies by year");
                            i.putExtra("SortedList",movies);
                            startActivity(i);
                        }
                        else
                            Toast.makeText(MainActivity.this, "No movies to view", Toast.LENGTH_SHORT).show();


                    }
                });

                /*if(movieList.size()>0){
                    Movie movieListToSort[]=movieList.values().toArray(new Movie[0]);


                }else
                {
                    Toast.makeText(MainActivity.this, "No movies to view", Toast.LENGTH_SHORT).show();
                }
                */
            }
        });


        showListByRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getData(new iGetMovieData() {
                    @Override
                    public void getMovies(HashMap<String, String> movies) {

                    }

                    @Override
                    public void getMoviesDataList(ArrayList<Movie> movies) {
                        if(movies.size()>0){
                            Collections.sort(movies, new Comparator<Movie>() {
                                @Override
                                public int compare(Movie movie1, Movie movie2) {
                                    return movie2.rating-movie1.rating;
                                }
                            });
                            Intent i= new Intent("com.example.myfavoritemovies.intent.action.VIEW");
                            i.addCategory(Intent.CATEGORY_DEFAULT);
                            i.putExtra("Title","Movies by rating");
                            i.putExtra("SortedList",movies);
                            startActivity(i);
                        }else
                            Toast.makeText(MainActivity.this, "No movies to view", Toast.LENGTH_SHORT).show();
                    }
                });

                /*if(movieList.size()>0){
                    Movie movieListToSort[]=movieList.values().toArray(new Movie[0]);
                    Collections.sort(Arrays.asList(movieListToSort), new Comparator<Movie>() {
                        @Override
                        public int compare(Movie movie1, Movie movie2) {
                            return movie2.rating-movie1.rating;
                        }
                    });

                }else
                {

                }
                */


            }
        });
    }

public void deleteMovieById(String movieId, final iDeleteMovie deleteMovie){
    FirebaseFirestore db= FirebaseFirestore.getInstance();
    db.collection("Movies").document(movieId)
            .delete()
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    deleteMovie.deleteMovieById(true);
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainActivity.this,"Something went wrong!",Toast.LENGTH_SHORT).show();
                }
            });
}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==ADD_REQUEST || requestCode==EDIT_REQUEST) {
            if (resultCode == RESULT_OK) {
                if (requestCode == ADD_REQUEST)
                    Toast.makeText(this, "Movie added successfully", Toast.LENGTH_SHORT).show();

                else if (requestCode == EDIT_REQUEST) {
                    Toast.makeText(this, "Saved changes successfully", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void getData(final iGetMovieData movieData){
        FirebaseFirestore db= FirebaseFirestore.getInstance();
        db.collection("Movies").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            HashMap<String,String> data= new HashMap<String,String>();
                            ArrayList<Movie> moviesDataList=new ArrayList<Movie>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Movie m= document.toObject(Movie.class);
                                data.put(document.getId(),m.name);
                                moviesDataList.add(m);
                            }
                            movieData.getMovies(data);
                            movieData.getMoviesDataList(moviesDataList);
                        }
                    }
                });
    }

    public interface iGetMovieData{
       public void getMovies(HashMap<String,String> movies);
       public void getMoviesDataList(ArrayList<Movie> movies);
    }

    public interface iDeleteMovie{
        public void deleteMovieById(boolean res);
    }


}
