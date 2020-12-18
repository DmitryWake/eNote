package com.stgroup.enote.objects

import com.stgroup.enote.models.NoteModel
import java.util.*
import kotlin.math.min

class SearchEngine(private val dataList: List<NoteModel>) {
    // Function returns Levenshtein distance of two strings
    private fun levenshtein(first_string: String, second_string: String): Int{
        val n : Int = first_string.length
        val m : Int = second_string.length
        val dp: Array<Array<Int>> = Array(n + 1) { Array(m + 1) { 1000 } }
        for (i in 0 until n + 1) dp[i][0] = i
        for (j in 0 until m + 1) dp[0][j] = j

        for (i in 1 until n + 1){
            for (j in 1 until m + 1){
                dp[i][j] = dp[i - 1][j - 1]
                if (first_string[i - 1] != second_string[j - 1]) dp[i][j] += 1
                dp[i][j] = min(dp[i][j], dp[i - 1][j] + 1)
                dp[i][j] = min(dp[i][j], dp[i][j - 1] + 1)
            }
        }
        return dp[n][m]
    }

    private fun damerau_levenshtein(first_string: String, second_string: String): Int{
        val n : Int = first_string.length
        val m : Int = second_string.length
        val dp: Array<Array<Int>> = Array(n + 1) { Array(m + 1) { 1000 } }
        for (i in 0 until n + 1) dp[i][0] = i
        for (j in 0 until m + 1) dp[0][j] = j

        for (i in 1 until n + 1){
            for (j in 1 until m + 1){
                dp[i][j] = dp[i - 1][j - 1]
                if (first_string[i - 1] != second_string[j - 1]) dp[i][j] += 1
                dp[i][j] = min(dp[i][j], dp[i - 1][j] + 1)
                dp[i][j] = min(dp[i][j], dp[i][j - 1] + 1)
                if (i > 1 && j > 1 && first_string[i - 1] == second_string[j - 2] && first_string[i - 2] == second_string[j - 1])
                    dp[i][j] = min(dp[i][j], dp[i - 2][j - 2] + 1)
            }
        }
        return dp[n][m]
    }

    /*
    Возвращает не более трех заметок с минимальным расстоянием Левенштейна.
    Расстояние Левенштейна не должно превышать 2

    !! Протестировать если dataList.size == 0
    */
    fun search(name: String, id_category: String? = null): List<NoteModel>{
        val answer = if (id_category == null){
            dataList.filter {damerau_levenshtein(name.toLowerCase(Locale.ROOT), it.name.toLowerCase(Locale.ROOT)) < 3 }
        } else
            dataList.filter {damerau_levenshtein(name.toLowerCase(Locale.ROOT), it.name.toLowerCase(Locale.ROOT)) < 3 && it.categoryId == id_category }

        return answer.subList(0, min(answer.size, 3))
    }
}