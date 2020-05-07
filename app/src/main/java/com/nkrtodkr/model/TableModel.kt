package com.nkrtodkr.model

import android.util.Log
import android.widget.TableLayout
import com.nkrtodkr.utils.extensions.getRowChildAt
import com.nkrtodkr.utils.extensions.getTextChildAt
import java.io.Serializable

class TableModel(): Serializable {
    var rowCount: Int = 0
    var columnCount: Int = 0
    var characters: List<String> = listOf()
    var states: List<String> = listOf()
    var cells: List<List<String>> = listOf()
    var trOrFl: List<String> = listOf()

    constructor(table: TableLayout) : this() {
        columnCount = table.getRowChildAt(0).childCount - 2
        rowCount = table.childCount - 1
        var row = table.getRowChildAt(0)

        val charactersTmp: ArrayList<String> = ArrayList()
        val statesTmp: ArrayList<String> = ArrayList()
        val cellsTmp: ArrayList<ArrayList<String>> = ArrayList()
        val trOrFlTmp: ArrayList<String> = ArrayList()

        /*Get input characters*/
        for (i in 1 until row.childCount - 1) {
            charactersTmp.add(row.getTextChildAt(i))
        }

        /*Get input state, transitions and trOrFl*/
        for (i in 1..rowCount) {
            row = table.getRowChildAt(i)
            statesTmp.add(row.getTextChildAt(0))
            val list = ArrayList<String>()
            for (j in 1..columnCount) {
                list.add(row.getTextChildAt(j))
            }
            cellsTmp.add(list)
            trOrFlTmp.add(row.getTextChildAt(columnCount + 1))
        }

        characters = charactersTmp
        states = statesTmp
        cells = cellsTmp
        trOrFl = trOrFlTmp
    }
}



