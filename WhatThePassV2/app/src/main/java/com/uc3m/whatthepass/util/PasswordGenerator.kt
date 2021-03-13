package com.uc3m.whatthepass.util
import kotlin.random.Random


object PasswordGenerator {
    private val capitalLetter ="ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    private val lowerCaseLetters = "abcdefghijklmnopqrstuvwxyz"
    private val numeric = "0123456789"
    private val special = "!@#$%^*\\"

    fun generatePassword (isCapital:Boolean,isLower:Boolean,isNumeric:Boolean,
                          isSpecial:Boolean,length:Int, minNumNumeric:Int,minNumSpecial:Int):String{
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

        for (i in 0 until length) {
            val rIndex = random.nextInt(dictionary.length)
            password.append(dictionary[rIndex])
        }
        return verify(minNumNumeric,minNumSpecial,password.toString())

    }

    private fun verify (minNumNumeric:Int, minNumSpecial:Int, generatedPassword:String):String{
        val password = StringBuilder()
        for (i in 0 until generatedPassword.length){
            password.append(generatedPassword[i])
        }
        val random = Random(System.nanoTime())
        val dictionary=this.capitalLetter+this.lowerCaseLetters
        var numNumCheck = false
        var numSpeCheck = false
        val countNum = this.numeric.count{ generatedPassword.contains(it) }
        val countSpecial = this.numeric.count{ generatedPassword.contains(it) }
        var i =0
        var charList : MutableList<Int> = mutableListOf()
        for(ch:Char in generatedPassword){
            if (dictionary.contains(ch)){
                charList.add(i)
            }
            i++
        }
        if(countNum>=minNumNumeric){
            numNumCheck = true
        }
        if(countSpecial>=minNumSpecial){
            numSpeCheck = true
        }
        if (numNumCheck && numSpeCheck){
            return generatedPassword
        }
        if (!numNumCheck){
            val lessNum = minNumNumeric-countNum
            for (i in 1..lessNum){
                val rIndex = random.nextInt(charList.size)
                val nIndex = random.nextInt(this.numeric.length)
                password[rIndex]=this.numeric[nIndex]
                charList.removeAt(rIndex)
            }

        }
        if (!numSpeCheck){
            val lessNum = minNumSpecial-countSpecial
            for (i in 1..lessNum){
                val rIndex = random.nextInt(charList.size)
                val nIndex = random.nextInt(this.special.length)
                password[rIndex]=this.special[nIndex]
                charList.removeAt(rIndex)
            }
        }
        return password.toString()

    }
}