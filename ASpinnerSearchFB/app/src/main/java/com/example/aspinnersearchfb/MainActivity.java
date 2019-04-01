package com.example.aspinnersearchfb;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aspinnersearchfb.Interface.IFirebaseLoadDone;
import com.example.aspinnersearchfb.Model.Movie;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements IFirebaseLoadDone {

    SearchableSpinner searchableSpinner;

    DatabaseReference moviesRef;

    IFirebaseLoadDone iFirebaseLoadDone;

    List<Movie> movies;

    BottomSheetDialog bottomSheetDialog;

    //View
    TextView movie_title, movie_description;
    ImageView movie_image;
    FloatingActionButton btn_fav;

    boolean isFirstTimeClick = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Init Dialog
        bottomSheetDialog = new BottomSheetDialog(this);
        View bottom_sheet_dialog = getLayoutInflater().inflate(R.layout.layout_movie, null);

        movie_title = bottom_sheet_dialog.findViewById(R.id.movie_title);
        movie_description = bottom_sheet_dialog.findViewById(R.id.movie_description);
        movie_image = bottom_sheet_dialog.findViewById(R.id.movie_image);
        btn_fav = bottom_sheet_dialog.findViewById(R.id.btn_fav);

        //Event
        btn_fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Close Dialog
                bottomSheetDialog.dismiss();
                Toast.makeText(MainActivity.this, "ADD FAV !!!", Toast.LENGTH_SHORT).show();
            }
        });

        //Set Content
        bottomSheetDialog.setContentView(bottom_sheet_dialog);

        searchableSpinner = findViewById(R.id.searchable_spinner);
        searchableSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Fixed First time click
                if (!isFirstTimeClick){
                    Movie movie = movies.get(position);
                    movie_title.setText(movie.getName());
                    movie_description.setText(movie.getDescription());
                    Picasso.get().load(movie.getImage()).into(movie_image);

                    bottomSheetDialog.show();
                }
                else {
                    isFirstTimeClick = false;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Init DB
        moviesRef = FirebaseDatabase.getInstance().getReference("Movies");

        //Init Firebase
        iFirebaseLoadDone = this;

        //Get Data
        moviesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Movie> movies = new ArrayList<>();
                for (DataSnapshot movieSnapShot : dataSnapshot.getChildren()){
                    movies.add(movieSnapShot.getValue(Movie.class));
                }
                iFirebaseLoadDone.onFirebaseLoadSuccess(movies);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                iFirebaseLoadDone.onFirebaseLoadFailed(databaseError.getMessage());
            }
        });


    }

    @Override
    public void onFirebaseLoadSuccess(List<Movie> movieList) {
        movies = movieList;

        //Get all name
        List<String> name_list = new ArrayList<>();
        for (Movie movie : movieList){
            name_list.add(movie.getName());
        }

        //Create Adapter and set for Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, name_list);
        searchableSpinner.setAdapter(adapter);
    }

    @Override
    public void onFirebaseLoadFailed(String message) {

    }
}
