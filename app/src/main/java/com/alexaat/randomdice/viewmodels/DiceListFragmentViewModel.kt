package com.alexaat.randomdice.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alexaat.randomdice.database.Dice
import com.alexaat.randomdice.getRandomList


class DiceListFragmentViewModel : ViewModel() {

    private val livesArray = arrayOf(15,14,13)
    private val descriptionArray = arrayOf("Make a total number less than 15", "Make any row or column total between 8 and 10", "Make all dice in any row or column 6")

    private val _level = MutableLiveData(1)
    val level:LiveData<Int>
        get() = _level

    private val _lives = MutableLiveData(livesArray[level.value!!.minus(1)])
    val lives:LiveData<Int>
        get() = _lives

    private val _description = MutableLiveData(descriptionArray[level.value!!.minus(1)])
    val description:LiveData<String>
        get() = _description

    private val _navigateToGameOverFragment = MutableLiveData(false)
    val navigateToGameOverFragment:LiveData<Boolean>
        get() = _navigateToGameOverFragment


    private val _navigateToGameWonFragment = MutableLiveData(false)
    val navigateToGameWonFragment:LiveData<Boolean>
        get() = _navigateToGameWonFragment

    private val _showNewLevelWelcomeMessage = MutableLiveData(1)
    val showNewLevelWelcomeMessage:LiveData<Int>
        get() = _showNewLevelWelcomeMessage

    private val _diceList = MutableLiveData<List<Dice>>(null)
    val diceList:LiveData<List<Dice>>
        get() = _diceList

    private val _playSingleDiceThrow = MutableLiveData(false)
    val playSingleDiceThrow:LiveData<Boolean>
        get() = _playSingleDiceThrow

    private val _playMultipleDiceThrow = MutableLiveData(false)
    val playMultipleDiceThrow:LiveData<Boolean>
        get() = _playMultipleDiceThrow

    fun generateData(size:Int){
        var list:ArrayList<Dice>
        do {
            list = ArrayList()
            val randomList = getRandomList(size)
            for (i in 0 until size) {
                list.add(Dice(i.toLong().plus(1), randomList[i]))
            }
        }while (checkLevelIsComplete(list))
        _playMultipleDiceThrow.value = true
        _diceList.value = list
    }

    fun shuffle() {
         var size = 9
         diceList.value?.let{
             size = it.size
         }
        var list:ArrayList<Dice>
        do {
            list = ArrayList()
            val randomList = getRandomList(size)
            for (i in 0 until size) {
                list.add(Dice(i.toLong().plus(1), randomList[i]))
            }
        }while(checkLevelIsComplete(list))
        _diceList.value = list
        _playMultipleDiceThrow.value = true
        _lives.value =  _lives.value?.minus(1)

        diceList.value?.let{
            checkGameStatus(it)
        }


    }

    fun onItemClicked(id: Long) {
        _lives.value = _lives.value?.minus(1)
        val currentFace = diceList.value!![id.toInt()-1].face
        var dice:Dice
        do{
            dice = Dice(id, (1..6).random())
            val newFace = dice.face
        }while(newFace==currentFace)

        val newList = ArrayList<Dice>(diceList.value)
        newList.removeAt(id.toInt()-1)
        newList.add(id.toInt()-1, dice)
        checkGameStatus(newList)
        _diceList.value = newList
        _playSingleDiceThrow.value = true
    }

    private fun checkLevelIsComplete(newList:List<Dice>):Boolean{
        var sum = 0
        if(level.value == 1) {
            for (dice in newList) {
                sum += dice.face
            }
            if (sum < 15) {
                return true
            }
        }
        if(level.value==2){
            if((newList[0].face + newList[1].face+ newList[2].face) in 8..10) return true
            if((newList[3].face + newList[4].face+ newList[5].face) in 8..10) return true
            if((newList[6].face + newList[7].face+ newList[8].face) in 8..10) return true
            if((newList[0].face + newList[3].face+ newList[6].face) in 8..10) return true
            if((newList[1].face + newList[4].face+ newList[7].face) in 8..10) return true
            if((newList[2].face + newList[5].face+ newList[8].face) in 8..10) return true
        }
        if(level.value==3){
            if((newList[0].face + newList[1].face+ newList[2].face) == 18) return true
            if((newList[3].face + newList[4].face+ newList[5].face) == 18) return true
            if((newList[6].face + newList[7].face+ newList[8].face) == 18) return true
            if((newList[0].face + newList[3].face+ newList[6].face) == 18) return true
            if((newList[1].face + newList[4].face+ newList[7].face) == 18) return true
            if((newList[2].face + newList[5].face+ newList[8].face) == 18) return true

        }
        return false
    }

    private fun checkGameStatus(newList:List<Dice>){
        if(checkLevelIsComplete(newList)){
             shuffle()
             if(level.value==3){
                 _navigateToGameWonFragment.value = true
                 _navigateToGameWonFragment.value = false
                 return
             }
            _level.value = _level.value?.plus(1)
            _lives.value = livesArray[level.value!!.minus(1)]
            _description.value = descriptionArray[level.value!!.minus(1)]
            _showNewLevelWelcomeMessage.value = level.value
            _showNewLevelWelcomeMessage.value = 1
            return
        }
        if(lives.value==0){
            _navigateToGameOverFragment.value = true
            _navigateToGameOverFragment.value = false
        }
    }

    fun playSingleDiceThrowComplete(){
        _playSingleDiceThrow.value = false
    }

    fun playMultipleDiceThrowComplete(){
        _playMultipleDiceThrow.value = false
    }




}