package com.example.bookreview

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.widget.Adapter
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.bookreview.adapter.BookAdapter
import com.example.bookreview.adapter.HistoryAdapter
import com.example.bookreview.api.BookService
import com.example.bookreview.databinding.ActivityMainBinding
import com.example.bookreview.model.BestSellerDTO
import com.example.bookreview.model.History
import com.example.bookreview.model.SearchBookDTO
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter : BookAdapter
    private lateinit var historyAdapter : HistoryAdapter
    private lateinit var bookService: BookService

    private lateinit var db : AppDatabase

        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initBookRecyclerView()
        initHistoryRecyclerView()
        initSearchEditText()
        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "BookSearchDB"
        ).build()

        val retrofit = Retrofit.Builder()
            .baseUrl("http://book.interpark.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        bookService = retrofit.create(BookService::class.java)

        bookService.getBestSellerBooks(getString(R.string.interparkAPIKey)).enqueue(object :Callback<BestSellerDTO>{
            override fun onResponse(call: Call<BestSellerDTO>, response: Response<BestSellerDTO>) {
                //todo 성공처리
                if(response.isSuccessful.not()){
                    Log.e(TAG, "Not Success")
                    return
                }
                response.body()?.let {
                    Log.d(TAG, it.toString())
                    it.books.forEach{
                        book -> Log.d(TAG, book.toString())
                    }
                    adapter.submitList(it.books)
                }
            }

            override fun onFailure(call: Call<BestSellerDTO>, t: Throwable) {
                //todo 실패처리
                Log.e(TAG, t.toString())
            }
        })

    }

    private fun search(keyword : String){
        bookService.getBookByName(getString(R.string.interparkAPIKey), keyword).enqueue(object : Callback<SearchBookDTO>{
            override fun onResponse(call: Call<SearchBookDTO>, response: Response<SearchBookDTO>) {
                //todo 성공처리
                hideHistoryView()
                saveSearchKeyword(keyword)
                if(response.isSuccessful.not()){
                    Log.e(TAG, "Not Success")
                    return
                }
                adapter.submitList(response.body()?.books.orEmpty())
            }

            override fun onFailure(call: Call<SearchBookDTO>, t: Throwable) {
                //todo 실패처리
                hideHistoryView()
                Log.e(TAG, t.toString())
            }

        })
    }

    fun initBookRecyclerView(){
        adapter = BookAdapter {
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("bookModel", it)
            startActivity(intent)
        }
        binding.bookRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.bookRecyclerView.adapter = adapter
    }

    fun initHistoryRecyclerView(){
        historyAdapter = HistoryAdapter {
            deleteSearchKeyword(it)
        }
        binding.historyRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.historyRecyclerView.adapter = historyAdapter
    }
    private fun showHistoryView(){
        Thread{
            val keywords = db.historyDao().getAll().reversed()
            runOnUiThread {
                binding.historyRecyclerView.isVisible = true
                historyAdapter.submitList(keywords.orEmpty())
            }
        }.start()
        binding.historyRecyclerView.isVisible = true
    }

    private fun initSearchEditText(){
        binding.searchEditText.setOnKeyListener { v, keyCode, event ->
            if(keyCode == KeyEvent.KEYCODE_ENTER && event.action == MotionEvent.ACTION_DOWN){
                search(binding.searchEditText.text.toString())
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }
        binding.searchEditText.setOnTouchListener { v, event ->
            if(event.action == MotionEvent.ACTION_DOWN){
                showHistoryView()
            }
            return@setOnTouchListener false
        }
    }

    private fun hideHistoryView(){
        binding.historyRecyclerView.isVisible = false
    }
    fun saveSearchKeyword(keyword: String){
        Thread{
            db.historyDao().insertHistory(History(null, keyword))
        }.start()

    }
    private fun deleteSearchKeyword(keyword: String){
        Thread{
            db.historyDao().delete(keyword)
            //todo 리스트 갱신
            showHistoryView()
        }.start()
    }
    companion object{
        private const val TAG = "MainActivity"

    }
}