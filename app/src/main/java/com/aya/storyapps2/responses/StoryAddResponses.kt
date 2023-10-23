package com.aya.storyapps2.responses

import com.google.gson.annotations.SerializedName

data class StoryAddResponses(

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)
