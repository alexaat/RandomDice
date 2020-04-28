package com.alexaat.randomdice.adapters



import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.alexaat.randomdice.R
import com.alexaat.randomdice.database.Dice
import com.alexaat.randomdice.databinding.DiceListItemBinding
import com.alexaat.randomdice.databinding.DiceListItemFooterBinding
import com.alexaat.randomdice.databinding.DiceListItemHeaderBinding
import com.alexaat.randomdice.viewmodels.DiceListFragmentViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

val ITEM_TYPE_HEADER = 1
val ITEM_TYPE_LIST = 0
val ITEM_TYPE_FOOTER = -1

class DiceListAdapter(private val onItemClickListener: OnItemClickListener, private var viewModel: DiceListFragmentViewModel ): ListAdapter<DataItem, RecyclerView.ViewHolder>(DiceListDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_TYPE_HEADER -> TextViewHolderHeader.inflateItemFrom(parent)
            ITEM_TYPE_LIST -> DiceListViewHolder.inflateItemFrom(parent)
            ITEM_TYPE_FOOTER -> TextViewHolderFooter.inflateItemFrom(parent)
            else -> throw ClassCastException("Unknown viewType ${viewType}")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is DiceListViewHolder -> {
                val diceItem = getItem(position) as DataItem.DiceItem
                holder.bind(diceItem.dice, onItemClickListener)
            }
            is TextViewHolderHeader -> {
               holder.bind(viewModel)
            }
            is TextViewHolderFooter ->{
               holder.bind(viewModel)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is DataItem.Header -> ITEM_TYPE_HEADER
            is DataItem.DiceItem -> ITEM_TYPE_LIST
            is DataItem.Footer -> ITEM_TYPE_FOOTER
        }
    }

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    fun addHeaderAndFooterAndSubmitList(list: ArrayList<Dice>?) {
        adapterScope.launch {
            val items = when (list) {
                null -> listOf(DataItem.Header) + listOf(DataItem.Footer)
                else -> listOf(DataItem.Header) + list.map{DataItem.DiceItem(it)} + listOf(DataItem.Footer)
            }
            withContext(Dispatchers.Main) {
                submitList(items)
            }
        }
    }


    class DiceListViewHolder(private val binding:DiceListItemBinding) :RecyclerView.ViewHolder(binding.root){
        companion object{
           fun inflateItemFrom(parent: ViewGroup):DiceListViewHolder{
               val res = R.layout.dice_list_item
               val binding:DiceListItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), res, parent, false)
               return DiceListViewHolder(binding)
            }
        }

        fun bind(dice:Dice, onItemClickListener: OnItemClickListener){
            binding.dice = dice
            binding.onItemClickListener = onItemClickListener
            binding.executePendingBindings()
        }
    }

    class TextViewHolderHeader(private val binding:DiceListItemHeaderBinding): RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun inflateItemFrom(parent: ViewGroup): TextViewHolderHeader {
                val res = R.layout.dice_list_item_header
                val binding: DiceListItemHeaderBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context),res,parent,false)
                return TextViewHolderHeader(binding)
            }
        }

        fun bind(viewModel: DiceListFragmentViewModel){
            binding.viewModel = viewModel
        }
    }

    class TextViewHolderFooter(private val binding: DiceListItemFooterBinding): RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun inflateItemFrom(parent: ViewGroup): TextViewHolderFooter {
                val res = R.layout.dice_list_item_footer
                val inflater =  LayoutInflater.from(parent.context)
                val binding: DiceListItemFooterBinding = DataBindingUtil.inflate(inflater,res,parent,false)
                return TextViewHolderFooter(binding)
            }
        }

        fun bind(viewModel: DiceListFragmentViewModel){
            binding.viewModel = viewModel
        }
    }

}

class OnItemClickListener(val listener: (Long) -> Unit){
    fun onClick(id:Long){
        listener(id)
    }
}

class DiceListDiffCallback:DiffUtil.ItemCallback<DataItem>() {
    override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem == newItem
    }
}

sealed class DataItem {

    data class DiceItem(val dice: Dice) : DataItem() {
        override val id = dice.id
    }

    object Header : DataItem() {
        override val id = Long.MIN_VALUE
    }

    object Footer : DataItem() {
        override val id = Long.MAX_VALUE
    }

    abstract val id: Long
}

