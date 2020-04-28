package com.alexaat.randomdice.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.alexaat.randomdice.R
import com.alexaat.randomdice.databinding.FragmentTitleBinding


class TitleFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentTitleBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_title, container, false)

        binding.fragmentTitleStartButton.setOnClickListener{

            val action = TitleFragmentDirections.actionTitleFragmentToDiceListFragment()
            val navController = findNavController()
            navController.navigate(action)

        }


        return binding.root
    }

}
