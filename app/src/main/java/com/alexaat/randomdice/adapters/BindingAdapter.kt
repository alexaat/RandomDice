package com.alexaat.randomdice.adapters


import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.alexaat.randomdice.R

@BindingAdapter("set_dice_face")
fun ImageView.setDiceFace(face:Int){
    when(face){
        1 -> setBackgroundResource(R.drawable.dice_1)
        2 -> setBackgroundResource(R.drawable.dice_2)
        3 -> setBackgroundResource(R.drawable.dice_3)
        4 -> setBackgroundResource(R.drawable.dice_4)
        5 -> setBackgroundResource(R.drawable.dice_5)
        6 -> setBackgroundResource(R.drawable.dice_6)
   }
}

@BindingAdapter("set_level")
fun TextView.setLevel(level:Int){
    text = resources.getString(R.string.level_text_format, level)
}

@BindingAdapter("set_lives")
fun TextView.setLives(lives:Int){
    text = resources.getString(R.string.lives_text_format, lives)
}

@BindingAdapter("set_level_description")
fun TextView.setLevelDescription(description:String){
    text = description
}