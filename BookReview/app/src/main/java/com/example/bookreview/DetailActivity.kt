package com.example.bookreview
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.room.Room
import com.bumptech.glide.Glide
import com.example.bookreview.databinding.ActivityDetailBinding
import com.example.bookreview.model.Book
import com.example.bookreview.model.Review

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private lateinit var db : AppDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = getAppDataBase(this)

        val model = intent.getParcelableExtra<Book>("bookModel")
        Log.e("dfds", model.toString())
        binding.titleTextView.text = model?.title.orEmpty()
        binding.descriptionTextView.text = model?.description.orEmpty()
        Glide.with(binding.coverImageView.context).load(model?.coverSmallUrl.orEmpty()).into(binding.coverImageView)

        Thread{
            val review = db.reviewDao().getOneReview(model?.id?.toInt() ?: 0)
            runOnUiThread{
                binding.reviewEditTextView.setText(review?.review.orEmpty())
            }
        }.start()

        binding.saveBtn.setOnClickListener {
            Thread{
                db.reviewDao().saveReview(Review(
                    model?.id?.toInt() ?: 0,
                    binding.reviewEditTextView.text.toString()
                ))
            }.start()
        }
    }
}