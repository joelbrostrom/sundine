package com.appshack.sundine.network.responsemodels

import com.fasterxml.jackson.annotation.JsonProperty

data class ApiResponseDataModel(

		@field:JsonProperty("results")
	val sunPathDataModel: SunPathDataModel? = null,

		@field:JsonProperty("status")
	val status: String? = null
)