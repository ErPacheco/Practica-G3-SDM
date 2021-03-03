package com.uc3m.whatthepass.views.passAndFiles

import com.uc3m.whatthepass.models.Password

interface Comunicator {
    fun passDataCom(password_input: Password)
}