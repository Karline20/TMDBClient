package coding.legaspi.tmdbclient.data.model.addevents


import com.google.gson.annotations.SerializedName

data class AddEvent(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String
)