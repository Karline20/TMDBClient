package coding.legaspi.tmdbclient.presentation.about.researcher.adapter

import android.content.Context
import android.content.Intent
import android.media.Image
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import coding.legaspi.tmdbclient.R
import coding.legaspi.tmdbclient.data.model.researchers.ResearchersOutput
import coding.legaspi.tmdbclient.presentation.about.researcher.AddResearcherActivity
import coding.legaspi.tmdbclient.presentation.viewmodel.EventViewModel
import coding.legaspi.tmdbclient.utils.DialogHelper
import coding.legaspi.tmdbclient.utils.FirebaseManager
import com.bumptech.glide.Glide

class ResearcherAdapter(
    private val researchersList: ArrayList<ResearchersOutput>,
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner,
    private val eventViewModel: EventViewModel,
    private val dialogHelper: DialogHelper
): RecyclerView.Adapter<ResearcherAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.epoxy_researcher, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val id = researchersList[position].id
        val name = researchersList[position].name
        val rposition = researchersList[position].position
        val address = researchersList[position].address
        val contact = researchersList[position].contact

        holder.txt_name.text = name
        holder.txt_position.text = rposition
        holder.txt_address.text = address
        holder.txt_contact.text = contact
        FirebaseManager().fetchResearchFromFirebase(id){
            val image = it.imageUri.toString()
            Glide.with(context)
                .load(image)
                .placeholder(R.drawable.person_red)
                .error(R.drawable.person_red)
                .into(holder.img)
        }

        holder.delete.setOnClickListener {
            dialogHelper.delete("Delete", "Are you sure you want to delete?", "Yes", "No") {
                if (it) {
                    delete(id)
                }
            }
        }

        holder.edit.setOnClickListener {
            dialogHelper.tutorial("Edit", "Do you want to edit this researcher?", "Yes", "No" ){
                if (it){
                    Log.d("Researcher", id)
                    val intent = Intent(context, AddResearcherActivity::class.java)
                    intent.putExtra("id", id)
                    intent.putExtra("name", name)
                    intent.putExtra("rposition", rposition)
                    intent.putExtra("address", address)
                    intent.putExtra("contact", contact)
                    context.startActivity(intent)
                }
            }
        }
    }

    private fun delete(id: String) {
        val deleteData = eventViewModel.deleteResearchers(id)
        deleteData.observe(lifecycleOwner, Observer { it ->
            if (it.isSuccessful){
                dialogHelper.connection("Deleted", "You have successfully deleted", "Okay"){
                    if (it){
                        notifyDataSetChanged()
                    }
                }
            }else{
                dialogHelper.connection("Delete", "Can't delete", "Okay"){
                    if (it){
                        notifyDataSetChanged()
                    }
                }
            }
        })
    }

    override fun getItemCount(): Int {
        return researchersList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txt_name: TextView = itemView.findViewById(R.id.txt_name)
        val txt_position: TextView = itemView.findViewById(R.id.txt_position)
        val txt_address: TextView = itemView.findViewById(R.id.txt_address)
        val txt_contact: TextView = itemView.findViewById(R.id.txt_contact)
        val img: ImageView = itemView.findViewById(R.id.img)
        val delete: ImageView = itemView.findViewById(R.id.delete)
        val edit: ImageView = itemView.findViewById(R.id.edit)
    }
}