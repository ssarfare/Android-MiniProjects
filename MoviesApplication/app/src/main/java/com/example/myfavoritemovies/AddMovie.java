package com.example.myfavoritemovies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Instrumentation;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewDebug;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.GenericArrayType;
import java.util.ArrayList;

import javax.xml.transform.Result;

public class AddMovie extends AppCompatActivity {
    public static ArrayList<String> GENRE_LIST= new ArrayList<String>();

    EditText et_movieName,et_description,et_imdb,et_year;
    TextView tv_seekBarValue;
    Button btn_addMovie;
    Spinner spr_genre;
    SeekBar sb_rating;
    public static String ADDED_MOVIE="Movie Added";
    public static String EDITED_MOVIE="Movie Edited";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_movie);
        if(GENRE_LIST.size()==0)
        {
            GENRE_LIST.add("Select");
            GENRE_LIST.add("Action");
            GENRE_LIST.add("Animation");
            GENRE_LIST.add("Comedy");
            GENRE_LIST.add("Documentary");
            GENRE_LIST.add("Family");
            GENRE_LIST.add("Horror");
            GENRE_LIST.add("Crime");
            GENRE_LIST.add("Others");
        }

        setTitle("Add Movie");
        initialiseActivity();
        try{
            if(getIntent().getExtras()!=null){
                setTitle(getIntent().getStringExtra("title"));
                String movieId= getIntent().getStringExtra("documentId");
                // initialiseActivity();

                getMovieByDocumentId(movieId, new iGetMovieById() {
                    @Override
                    public void getMovieByDocumentId(Movie movieToEdit) {
                        if(movieToEdit!=null){
                            et_movieName.setText(movieToEdit.name);
                            et_description.setText(movieToEdit.description);
                            et_year.setText(String.valueOf(movieToEdit.year));
                            et_imdb.setText(movieToEdit.imdbLink);

                            int index=GENRE_LIST.indexOf(movieToEdit.genre);
                            spr_genre.setSelection(index);
                            sb_rating.setProgress(movieToEdit.rating);
                            btn_addMovie.setText("Save Changes");
                        }
                    }
                });
            }
        }
        catch(Exception e){
            Log.e("Exception", "onCreate: ",e );
        }

        btn_addMovie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String movieId=null;
                if(getIntent().getStringExtra("documentId")!=null)
                    movieId=getIntent().getStringExtra("documentId");
                final Movie movie =validateMovie();
                if(movie.isValid){
                    checkIfMovieWithSameNameExists(movie.name, movieId, new iMovieExistsCallBack() {
                        @Override
                        public void movieAlreadyExists(final boolean result, String movieId) {

                            if(movieId==null){
                                if(movie.isValid && result) {
                                    Toast.makeText(AddMovie.this, "Movie with same name already exists", Toast.LENGTH_SHORT).show();
                                    movie.isValid=false;
                                }
                                else {
                                    FirebaseFirestore db= FirebaseFirestore.getInstance();
                                    db.collection("Movies").add(movie)
                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {
                                                    //Toast.makeText(AddMovie.this,"Successful",Toast.LENGTH_SHORT).show();
                                                    Intent intent=new Intent();
                                                    setResult(RESULT_OK);
                                                    finish();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(AddMovie.this,"Something went wrong!",Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            }
                            else {
                                if(movie.isValid && result) {
                                    Toast.makeText(AddMovie.this, "Movie with same name already exists", Toast.LENGTH_SHORT).show();
                                    movie.isValid=false;
                                }
                                else {
                                    FirebaseFirestore db= FirebaseFirestore.getInstance();
                                    db.collection("Movies").document(movieId).set(movie)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Intent intent=new Intent();
                                                    setResult(RESULT_OK);
                                                    finish();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(AddMovie.this,"Something went wrong!",Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        }
                    });
                }
            }
        });

    }


    public void getMovieByDocumentId(String documentId, final iGetMovieById listener){

        FirebaseFirestore db= FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("Movies").document(documentId);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Movie m = documentSnapshot.toObject(Movie.class);
                listener.getMovieByDocumentId(m);
            }
        });

    }

    public interface iGetMovieById{
        public void getMovieByDocumentId(Movie m);
    }



    public Movie validateMovie(){
        final Movie mobject=new Movie("","","",null,null,"");

        boolean result=true;
        String ErrorName="Please enter valid";

        try{
            String name=et_movieName.getText().toString();
            if(name.equals("")) {
                result = false;
                ErrorName = ErrorName + " Movie Name,";
                et_movieName.setError("Enter valid name");
            }else {
                if (name.toCharArray().length > 50) {
                    ErrorName = ErrorName + " Movie Name ";
                    et_movieName.setError("Movie name cannot be greater than 50 characters");
                    result=false;
                }else
                    mobject.name=name;
            }

            String desc=et_description.getText().toString();
            if(desc.equals("")){
                result = false;
                ErrorName = ErrorName + " Description,";
                et_description.setError("Enter valid description");
            }else{
                if (desc.toCharArray().length > 1000) {
                    ErrorName = ErrorName + " Description,";
                    et_description.setError("Description cannot be greater than 1000 characters");
                    result=false;
                }else
                    mobject.description=desc;
            }

            String gen=(String)spr_genre.getSelectedItem();
            if(gen.equals("Select")){
                result = false;
                ErrorName = ErrorName + " Genre,";
            }
            else{
                mobject.genre=gen;
            }

            if(!et_year.getText().toString().equals(""))
            {
                int year=Integer.parseInt(et_year.getText().toString());
                if(year<=1800 || year>2019){
                    result = false;
                    ErrorName = ErrorName + " Year,";
                    et_year.setError("Enter valid year");
                }else{
                    mobject.year=year;
                }
            }
            else
            {
                result = false;
                ErrorName = ErrorName + " Year,";
                et_year.setError("Enter valid year");
            }

            String imdbLink=et_imdb.getText().toString();
            if(imdbLink.equals("")){
                result = false;
                ErrorName = ErrorName + " IMDB link,";
                et_imdb.setError("Enter valid IMDB link");
            }else{
                mobject.imdbLink=imdbLink;
            }

            int rating=sb_rating.getProgress();
            mobject.rating=rating;
            mobject.isValid=result;

            if(!mobject.isValid)
                Toast.makeText(this,ErrorName,Toast.LENGTH_SHORT).show();
        }
        catch (Exception e){
            Toast.makeText(this,"Please enter valid input",Toast.LENGTH_SHORT).show();
        }
        return mobject;
    }
    public void checkIfMovieWithSameNameExists(String name, final String movieId,final iMovieExistsCallBack callBack){
        //final boolean[] result = {false};
        FirebaseFirestore db= FirebaseFirestore.getInstance();
        db.collection("Movies")
                .whereEqualTo("name", name)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            if(movieId==null && task.getResult().size()>=1)
                                callBack.movieAlreadyExists(true,null);
                            else if(movieId!=null && task.getResult().size()>1)
                                callBack.movieAlreadyExists(true,movieId);
                                else if(task.getResult().size()==1){
                                for (QueryDocumentSnapshot doc:task.getResult()) {
                                    if(!doc.getId().equals(movieId))
                                        callBack.movieAlreadyExists(true,movieId);
                                    else callBack.movieAlreadyExists(false,movieId);
                                }
                            }
                                else callBack.movieAlreadyExists(false,movieId);
                        }
                    }
                });
    }
    public void initialiseActivity(){
        et_movieName=findViewById(R.id.editText_movieName);
        et_description=findViewById(R.id.editText_movieDescription);
        et_year=findViewById(R.id.editText_year);
        et_imdb=findViewById(R.id.editText_imdb);
        spr_genre=findViewById(R.id.spinner_genre);
        sb_rating=findViewById(R.id.seekBar_rating);
        btn_addMovie=findViewById(R.id.button_addMovie_AddMovieActivity);
        tv_seekBarValue=findViewById(R.id.textView_seekbarValue);
        sb_rating.setMax(5);

        ArrayAdapter<String> adapter= new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line
                ,GENRE_LIST);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spr_genre.setAdapter(adapter);

        sb_rating.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                tv_seekBarValue.setText(String.valueOf(i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });



    }

    public interface iMovieExistsCallBack{
        public void movieAlreadyExists(boolean result,String movieId);
    }



}
