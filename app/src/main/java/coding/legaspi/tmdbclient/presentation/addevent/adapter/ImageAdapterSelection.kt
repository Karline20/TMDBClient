package coding.legaspi.tmdbclient.presentation.addevent.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import coding.legaspi.tmdbclient.R
import coding.legaspi.tmdbclient.data.model.adaptermodel.ImageItem
import coding.legaspi.tmdbclient.utils.FirebaseManager
import com.bumptech.glide.Glide


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
        Log.d("Edit some", "imageItem ${imageItem.uri}")
        try {
            Glide.with(context)
                .load(imageItem.uri)
                .placeholder(R.drawable.baseline_broken_image_24)
                .error(R.drawable.baseline_broken_image_24)
                .override(250, 250)
                .into(holder.imageView)
            //holder.imageView.setImageURI(imageItem.uri)
        }catch (e: Exception){
            Log.e("Edit some", "Exception $e")
            holder.imageView.setImageResource(R.drawable.baseline_broken_image_24)
        }

        Log.d("Edit some", "$position ${imageItem.id} ${imageItem.uri}")
        holder.delete.setOnClickListener {
            FirebaseManager().deleteImageToFirebase(context, imageItem.id, imageItem.eventId){
                if (true){
                    if (position >= 0 && position < imageList.size) {
                        imageList.removeAt(position)
                        //notifyDataSetChanged()
                        notifyItemRemoved(position)
                    } else {
                        Toast.makeText(context, "Please select an image", Toast.LENGTH_SHORT).show()
                    }
                }else{
                    Toast.makeText(context, "Can't delete image!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return imageList.size
    }
}
