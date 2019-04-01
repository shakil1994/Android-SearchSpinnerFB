package com.example.kotlinspinnersearchfb.Interface

import com.example.kotlinspinnersearchfb.Model.Movie

interface IFirebaseLoadDone {
    fun onFirebaseLoadSuccess(movieList: List<Movie>)
    fun onFirebaseLoadFailed(message: String)
}