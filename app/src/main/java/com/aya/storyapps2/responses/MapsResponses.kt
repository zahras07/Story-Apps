package com.aya.storyapps2.responses

import com.google.gson.annotations.SerializedName

data class MapsResponse(

	@field:SerializedName("listStory")
	val listStory: List<MapsList>,

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class MapsList(

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("lon")
	val lon: Double? = null,

	@field:SerializedName("lat")
	val lat: Double? = null
)

