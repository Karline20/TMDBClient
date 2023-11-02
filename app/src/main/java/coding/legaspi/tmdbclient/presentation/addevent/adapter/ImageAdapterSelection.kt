package coding.legaspi.tmdbclient.presentation.addevent.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import coding.legaspi.tmdbclient.R
import coding.legaspi.tmdbclient.data.model.adaptermodel.ImageItem


class ImageAdapterSelection(
    private val context: Context,
    private val imageList: ArrayList<ImageItem>
) : RecyclerView.Adapter<ImageAdapterSelection.ImageViewHolder>() {

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val delete: ImageView = itemView.findViewById(R.id.delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageItem = imageList[position]
        try {
            holder.imageView.setImageURI(imageItem.uri)
        }catch (e: Exception){
            holder.imageView.setImageResource(R.drawable.baseline_broken_image_24)
        }

        holder.delete.setOnClickListener {
            if (position >= 0 && position < imageList.size) {
                imageList.removeAt(position)
                notifyItemRemoved(position)
            } else {
               Toast.makeText(context, "Please select an image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount(): Int {
        return imageList.size
    }
}
