package com.example.kotlinspinnersearchfb

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetDialog
import android.support.design.widget.FloatingActionButton
import android.view.View
import android.widget.*
import com.example.kotlinspinnersearchfb.Interface.IFirebaseLoadDone
import com.example.kotlinspinnersearchfb.Model.Movie
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import java.util.ArrayList

class MainActivity : AppCompatActivity(), IFirebaseLoadDone {

    lateinit var moviesRef: DatabaseReference

    lateinit var iFirebaseLoadDone: IFirebaseLoadDone

    //Variable
    lateinit var bottomSheetDialog: BottomSheetDialog

    //View
    lateinit var movie_title: TextView
    lateinit var movie_description:TextView
    lateinit var movie_image: ImageView
    lateinit var btn_fav: FloatingActionButton

    internal var isFirstTimeClick = true

    internal var movies: List<Movie> = ArrayList<Movie>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Init bottom sheet
        bottomSheetDialog = BottomSheetDialog(this)
        val bottom_sheet_dialog = layoutInflater.inflate(R.layout.layout_movie, null)

        movie_title = bottom_sheet_dialog.findViewById(R.id.movie_title)
        movie_description = bottom_sheet_dialog.findViewById(R.id.movie_description)
        movie_image = bottom_sheet_dialog.findViewById(R.id.movie_image)
        btn_fav = bottom_sheet_dialog.findViewById(R.id.btn_fav)

        //Event
        btn_fav.setOnClickListener {
            //Close Dialog
            bottomSheetDialog.dismiss()
            Toast.makeText(this@MainActivity, "ADD FAV !!!", Toast.LENGTH_SHORT).show()
        }

        //Set Content
        bottomSheetDialog.setContentView(bottom_sheet_dialog)

        searchable_spinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                //Fixed First time click
                if (!isFirstTimeClick) {
                    val movie = movies.get(position)
                    movie_title.text = movie.name
                    movie_description.text = movie.description
                    Picasso.get().load(movie.image).into(movie_image)

                    bottomSheetDialog.show()
                } else {
                    isFirstTimeClick = false
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        })

        //Init Firebase
        iFirebaseLoadDone = this

        //Init DB
        moviesRef = FirebaseDatabase.getInstance().getReference("Movies")

        //Load Data
        moviesRef.addValueEventListener(object : ValueEventListener {

            val movieList:MutableList<Movie> = ArrayList<Movie>()
            override fun onCancelled(p0: DatabaseError) {
                iFirebaseLoadDone.onFirebaseLoadFailed(p0.message)
            }

            override fun onDataChange(p0: DataSnapshot) {
                for (movieSnapShot in p0.children) {
                    movieList.add(movieSnapShot.getValue<Movie>(Movie::class.java)!!)
                }
                iFirebaseLoadDone.onFirebaseLoadSuccess(movieList)
            }
        })
    }

    override fun onFirebaseLoadSuccess(movieList: List<Movie>) {

        this.movies = movieList

        //Get all name from list
        val movie_name_title = getMovieNameList(movieList)

        //Create Adapter and set for Spinner
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, movie_name_title)
        searchable_spinner.adapter = adapter
    }

    private fun getMovieNameList(movieList: List<Movie>): List<String> {
        val result = ArrayList<String>()
        for (movie in movieList){
            result.add(movie.name!!)
        }
        return result
    }

    override fun onFirebaseLoadFailed(message: String) {

    }
}
