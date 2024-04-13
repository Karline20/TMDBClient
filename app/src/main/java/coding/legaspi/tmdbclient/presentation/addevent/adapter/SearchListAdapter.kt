package coding.legaspi.tmdbclient.presentation.addevent.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coding.legaspi.tmdbclient.databinding.LocationSuggestBinding

class SearchListAdapter(private val onItemClick: (String) -> Unit) : RecyclerView.Adapter<SearchListAdapter.SearchViewHolder>() {

    private val suggestions: MutableList<String> = mutableListOf()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SearchListAdapter.SearchViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = LocationSuggestBinding.inflate(inflater, parent, false)
        return SearchViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: SearchListAdapter.SearchViewHolder, position: Int) {
        val suggestion = suggestions[position]
        holder.bind(suggestion)
    }

    override fun getItemCount(): Int {
       return suggestions.size
    }

    fun setSuggestions(newSuggestions: List<String>) {
        suggestions.clear()
        suggestions.addAll(newSuggestions)
        notifyDataSetChanged()
    }

    inner class SearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = LocationSuggestBinding.bind(itemView)

        fun bind(suggestion: String) {
            binding.txtLocationSuggest.text = suggestion

            itemView.setOnClickListener {
                onItemClick(suggestion)
            }
        }
    }
}

