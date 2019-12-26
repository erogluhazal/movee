package com.example.imdb.model.cast

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class CrewModel(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("job") var job:String? = null,
    @SerializedName("birthday") var birthday: String? = null,
    @SerializedName("place_of_birth") var placeOfBirth: String? = null,
    @SerializedName("profile_path") var profilePath: String? = null,
    @SerializedName("biography") var biography: String? = null
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    fun updateDetails(crew: CrewModel) {
        crew.let {
            with(this) {
                id = crew.id
                name = crew.name
                birthday = crew.birthday
                placeOfBirth = crew.placeOfBirth
                profilePath = crew.profilePath
                biography = crew.biography
            }
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(id)
        parcel.writeString(name)
        parcel.writeString(job)
        parcel.writeString(birthday)
        parcel.writeString(placeOfBirth)
        parcel.writeString(profilePath)
        parcel.writeString(biography)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CrewModel> {
        override fun createFromParcel(parcel: Parcel): CrewModel {
            return CrewModel(parcel)
        }

        override fun newArray(size: Int): Array<CrewModel?> {
            return arrayOfNulls(size)
        }
    }

}