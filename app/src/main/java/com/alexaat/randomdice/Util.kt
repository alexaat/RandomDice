package com.alexaat.randomdice

import java.util.ArrayList

fun getRandomList(size:Int):ArrayList<Int>{
    val list = ArrayList<Int>()
    while(list.size<size){
    val r=   (1..6).random()
    list.add(r)
    }
    return list
}