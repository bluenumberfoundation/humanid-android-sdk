package com.humanid.humanidui.util.validation

import android.view.View

interface Rule {
    val errorMessage: String
    fun isRulePassed(view: View): Boolean
}