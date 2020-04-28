package com.alexaat.randomdice.fragments

import android.media.MediaPlayer
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.alexaat.randomdice.R
import com.alexaat.randomdice.adapters.DiceListAdapter
import com.alexaat.randomdice.adapters.OnItemClickListener
import com.alexaat.randomdice.database.Dice
import com.alexaat.randomdice.databinding.FragmentDiceListBinding
import com.alexaat.randomdice.viewmodels.DiceListFragmentViewModel
import kotlinx.coroutines.*

class DiceListFragment : Fragment() {

   lateinit var onShuffleClickListener: OnShuffleClickListener

    lateinit var player: MediaPlayer

    var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val binding:FragmentDiceListBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_dice_list, container, false)

        player = MediaPlayer.create(context,R.raw.dice_roll)





        //view model
        binding.lifecycleOwner = this
        val viewModel = ViewModelProvider(this).get(DiceListFragmentViewModel::class.java)
        //adapter
        val onItemClickListener = OnItemClickListener{
            viewModel.onItemClicked(it)
        }
        val adapter = DiceListAdapter(onItemClickListener, viewModel)
        val manager = GridLayoutManager(context,3)
        manager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when(position){
                    0 -> 3
                    adapter.itemCount.minus(1) -> 3
                    else -> 1
                }
            }
        }
        binding.diceListRecyclerView.layoutManager = manager
        binding.diceListRecyclerView.adapter = adapter

        viewModel.diceList.observe(viewLifecycleOwner, Observer {

            if(it==null || it.isEmpty()){
                viewModel.generateData(9)
                return@Observer
            }
              val list = ArrayList<Dice>(it)
              adapter.addHeaderAndFooterAndSubmitList(list)
        })

        viewModel.lives.observe(viewLifecycleOwner, Observer {
            adapter.notifyItemChanged(0)
        })

        viewModel.level.observe(viewLifecycleOwner, Observer {
            adapter.notifyItemChanged(0)
        })

        viewModel.description.observe(viewLifecycleOwner, Observer {
            adapter.notifyItemChanged(adapter.itemCount-1)
        })

        viewModel.navigateToGameOverFragment.observe(viewLifecycleOwner, Observer {
           if(it){
               val navController = findNavController()
               val action = DiceListFragmentDirections.actionDiceListFragmentToGameOverFragment()
               navController.navigate(action)
           }
        })

        viewModel.navigateToGameWonFragment.observe(viewLifecycleOwner, Observer {
            if(it){
                val navController = findNavController()
                val action = DiceListFragmentDirections.actionDiceListFragmentToGameWonFragment()
                navController.navigate(action)
            }
        })

        viewModel.showNewLevelWelcomeMessage.observe(viewLifecycleOwner, Observer {
           if(it>1) {
               Toast.makeText(context, resources.getString(R.string.welcome_message, it), Toast.LENGTH_SHORT).show()
           }
        })

        viewModel.playSingleDiceThrow.observe(viewLifecycleOwner, Observer {
            if(it){
              uiScope.launch {
                  playSingleDiceThrowSound()
              }
              viewModel.playSingleDiceThrowComplete()
            }
        })

        viewModel.playMultipleDiceThrow.observe(viewLifecycleOwner, Observer {
            if(it){
                uiScope.launch {
                    playMultipleDiceThrowSound()
                }
                viewModel.playMultipleDiceThrowComplete()
            }
        })


        //click listener
        onShuffleClickListener = OnShuffleClickListener{viewModel.shuffle()}

        setHasOptionsMenu(true)

        binding.lifecycleOwner = this

        return binding.root
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.shuffle_menu_item -> onShuffleClickListener.onClick()
            android.R.id.home -> {
                findNavController().navigateUp()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    class OnShuffleClickListener(val listener:()->Unit){
        fun onClick() = listener()
    }

    private suspend fun playSingleDiceThrowSound(){
        withContext(Dispatchers.Main){
            player = MediaPlayer.create(context,R.raw.dice_roll)
            player.start()
            player.setOnCompletionListener {
                it.release()
            }
        }
    }

    private suspend fun playMultipleDiceThrowSound(){
        withContext(Dispatchers.Main){
            playSingleDiceThrowSound()
            Thread.sleep(25)
            playSingleDiceThrowSound()

        }
    }

}
