package com.uc3m.whatthepass.util
import kotlin.random.Random


object PasswordGenerator {
    private val capitalLetter ="ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    private val lowerCaseLetters = "abcdefghijklmnopqrstuvwxyz"
    private val numeric = "0123456789"
    private val special = "!@#$%^*\\"

   suspend fun generatePassword(isCapital: Boolean, isLower: Boolean, isNumeric: Boolean,
                                isSpecial: Boolean, length: Int, minNumNumeric: Int, minNumSpecial: Int):String{
        if(!isCapital && !isLower && !isNumeric&&
        !isSpecial){
            return ""
        }
        var dictionary= "";
        if(isCapital){
            dictionary+=this.capitalLetter
        }
        if(isLower){
            dictionary+=this.lowerCaseLetters
        }
        if(isNumeric){
            dictionary+=this.numeric
        }
        if(isSpecial){
            dictionary+=this.special
        }
        val random = Random(System.nanoTime())
        val password = StringBuilder()

       for(i in 0 until minNumNumeric){
           val rIndex = random.nextInt(this.numeric.length)
           password.append(this.numeric[rIndex])
       }
       for(i in 0 until minNumSpecial){
           val rIndex = random.nextInt(this.special.length)
           password.append(this.special[rIndex])
       }
        for (i in 0 until (length-minNumNumeric-minNumSpecial)) {
            val rIndex = random.nextInt(dictionary.length)
            password.append(dictionary[rIndex])
        }

        return verify(password.toString())

   }

    private fun verify(generatedPassword: String):String{

       val list = generatedPassword.toMutableList()
        val random = Random(System.nanoTime())
        val password = StringBuilder()

        for (i in 0 until list.size) {
            val rIndex = random.nextInt(list.size)
            password.append(list[rIndex])
            list.removeAt(rIndex);
        }
       return password.toString()
    }
}