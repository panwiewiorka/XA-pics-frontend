package xapics.app.presentation.screens.picsList

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import xapics.app.Pic
import xapics.app.databinding.ItemPicslistBinding

class PicsListAdapter (
    var picsList: List<Pic>,
    val saveCaption: () -> Unit,
    val goToPicScreen: (picIndex: Int) -> Unit,
    ) : RecyclerView.Adapter<PicsListAdapter.PicsViewHolder>() {

    inner class PicsViewHolder(val binding: ItemPicslistBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PicsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemPicslistBinding.inflate(layoutInflater, parent, false)
        return PicsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PicsViewHolder, position: Int) {
        holder.binding.apply {
            tvPic.text = picsList[position].description
            tvPic.setOnClickListener {
                saveCaption()
                goToPicScreen(position)
            }
//            picView.setImageURI(picsList[position].imageUrl.toUri())
        }
    }

    override fun getItemCount(): Int {
        return picsList.size
    }
}