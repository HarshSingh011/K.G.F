package com.weblite.kgf.domain.model

data class VipInfo(
    val daysLeft: Int,
    val userId: String,
    val totalExp: Long,
    val levelUser: Int
)
