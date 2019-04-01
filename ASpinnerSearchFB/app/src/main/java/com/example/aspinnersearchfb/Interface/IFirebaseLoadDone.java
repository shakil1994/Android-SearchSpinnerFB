package com.example.aspinnersearchfb.Interface;

import com.example.aspinnersearchfb.Model.Movie;

import java.util.List;

public interface IFirebaseLoadDone {
    void onFirebaseLoadSuccess(List<Movie> movieList);
    void onFirebaseLoadFailed(String message);
}
