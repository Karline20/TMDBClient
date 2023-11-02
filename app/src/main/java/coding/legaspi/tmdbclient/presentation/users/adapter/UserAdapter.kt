package coding.legaspi.tmdbclient.presentation.users.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coding.legaspi.tmdbclient.R
import coding.legaspi.tmdbclient.data.model.users.Users
import coding.legaspi.tmdbclient.presentation.home.adapter.HomeAdapter
import coding.legaspi.tmdbclient.utils.FirebaseManager
import com.bumptech.glide.Glide

class UserAdapter(
    private val context: Context,
    private val usersList: ArrayList<Users>
    ): RecyclerView.Adapter<UserAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.rv_users, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val id = usersList[position].id
        val userid = usersList[position].userid
        val firstname = usersList[position].firstname
        val lastname = usersList[position].lastname
        val address = usersList[position].address
        val age = usersList[position].age
        val gender = usersList[position].gender
        val date = usersList[position].date

        holder.id.text = id
        holder.userid.text = userid
        holder.name.text = "$firstname $lastname"
        holder.age.text = age
        holder.address.text = address
        holder.gender.text = gender
        holder.date.text = date

        FirebaseManager().fetchProfileFromFirebase(userid){
            if (it!=null){
                Glide.with(context)
                    .load(it.imageUri)
                    .placeholder(R.drawable.baseline_broken_image_24)
                    .error(R.drawable.baseline_broken_image_24)
                    .into(holder.profile)
            }
        }
    }

    override fun getItemCount(): Int {
        return usersList.size
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val id: TextView = itemView.findViewById(R.id.id)
        val userid: TextView = itemView.findViewById(R.id.userid)
        val name: TextView = itemView.findViewById(R.id.name)
        val address: TextView = itemView.findViewById(R.id.address)
        val age: TextView = itemView.findViewById(R.id.age)
        val gender: TextView = itemView.findViewById(R.id.gender)
        val date: TextView = itemView.findViewById(R.id.date)
        val profile: ImageView = itemView.findViewById(R.id.profile)

    }
}