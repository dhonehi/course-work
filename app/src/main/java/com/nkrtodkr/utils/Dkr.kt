package com.nkrtodkr.utils

import android.util.Log
import com.nkrtodkr.MainActivity
import com.nkrtodkr.R
import com.nkrtodkr.utils.extensions.containsState
import com.nkrtodkr.model.TableModel
import kotlin.IllegalStateException

class Dkr {
    private var tableTransitions: ArrayList<ArrayList<State>> = ArrayList()
    private var trueStates: ArrayList<String> = ArrayList()

    private lateinit var tableModel: TableModel

    private fun parse() {
        tableModel.cells.forEachIndexed { rowIndex, row ->
            val rowTransitions = ArrayList<State>()

            if (tableModel.trOrFl[rowIndex] == "+")
                trueStates.add(tableModel.states[rowIndex])

            row.forEach { cell ->
                rowTransitions.add(State(cell))
            }
            tableTransitions.add(rowTransitions)
        }
    }

    fun createDKR(tableModel: TableModel): TableModel {
        this.tableModel = tableModel
        tableTransitions.clear()
        trueStates.clear()
        parse()
        val states = ArrayList<State>()
        val transitions: ArrayList<ArrayList<State>> = ArrayList()
        val trOrFl = ArrayList<String>()

        states.add(State(tableModel.states[0]))
        if (states[0].containsTrueStateItem())
            trOrFl.add("+")
        else
            trOrFl.add("-")

        val list = ArrayList<State>()
        for (i in 0 until tableModel.columnCount) {
            val state = tableTransitions[0][i]
            list.add(state)
        }
        transitions.add(list)

        var row = 0
        var i = 0

        while (i <= row) {
            for (column in 0 until tableModel.columnCount) {
                val state = transitions[i][column]
                if (state.toString().isNotEmpty()) {
                    if (!states.containsState(state)) {
                        ++row
                        if (state.containsTrueStateItem())
                            trOrFl.add("+")
                        else
                            trOrFl.add("-")
                        states.add(state)
                        transitions.add(createRowTransitions(state))
                    }
                }
            }
            ++i
        }


        return TableModel().apply {
            this.rowCount = states.size
            this.columnCount = tableModel.columnCount
            this.characters = tableModel.characters
            this.trOrFl = trOrFl
            this.states = states.map { it.toString() }
            this.cells = transitions.map { it.map { it.toString() } }
        }
    }

    private fun createRowTransitions(state: State): ArrayList<State> {
        val list = ArrayList<State>()

        for (charIndex in tableModel.characters.indices) {
            val stateTmp = State()
            for (i in state.states) {
                val rowIndex = tableModel.states.indexOf(i)

                if (rowIndex == -1)
                    throw IllegalStateException()

                for (j in tableTransitions[rowIndex][charIndex].states) {
                    stateTmp.addIfNotContains(j)
                }
            }
            list.add(stateTmp)
        }

        return list
    }

    inner class State() {
        var states: MutableList<String> = mutableListOf()

        constructor(statesStr: String) : this() {
            states = statesStr.replace(" ", "").split(",").toMutableList()
        }

        fun containsTrueStateItem(): Boolean {
            states.forEach {
                if (trueStates.contains(it)) return true
            }
            return false
        }

        fun addIfNotContains(state: String) {
            if (!states.contains(state) && state.isNotEmpty())
                states.add(state)
        }

        fun contains(state: String): Boolean {
            return states.contains(state)
        }

        override fun equals(other: Any?): Boolean {
            if (other is State && states.size == other.states.size) {
                return states.containsAll(other.states) && other.states.containsAll(states)
            }
            return false
        }

        override fun hashCode(): Int {
            return states.hashCode()
        }

        override fun toString(): String {
            return states.joinToString()
        }
    }
}

/*
class DKR(
    table: TableLayout
) {
    private var rowCount = 0
    private var columnCount = 0

    private var inputCharacters: ArrayList<String> = ArrayList()
    private var inputState: ArrayList<String> = ArrayList()
    private var inputTransitions: ArrayList<ArrayList<State>> = ArrayList()
    private var inputTrOrFl: ArrayList<String> = ArrayList()
    private var trueStates: ArrayList<String> = ArrayList()

    init {
        parse(table)
    }

    private fun parse(table: TableLayout) {
        columnCount = (table[0] as TableRow).childCount
        rowCount = table.childCount
        var row = table.getChildAt(0) as TableRow

        */
/*Get input characters*//*

        for (i in 1 until row.childCount - 1) {
            inputCharacters.add((row.getChildAt(i) as EditText).text.toString())
        }

        */
/*Get input state, transitions and trOrFl*//*

        for (i in 1 until rowCount) {
            row = table.getChildAt(i) as TableRow
            inputState.add((row.getChildAt(0) as EditText).text.toString())
            val list = ArrayList<State>()
            for (j in 1 until columnCount) {
                if (j == columnCount - 1) {
                    if (row.getTextChildAt(j) == "+") trueStates.add(inputState[i - 1])
                } else {
                    list.add(State((row.getChildAt(j) as EditText).text.toString()))
                }
            }
            inputTransitions.add(list)
            inputTrOrFl.add((row.getChildAt(columnCount - 1) as EditText).text.toString())
        }

        columnCount -= 2
        rowCount -= 2
    }

    fun createDKR() {
        val states = ArrayList<State>()
        val transitions: ArrayList<ArrayList<State>> = ArrayList()
        val trOrFl = ArrayList<String>()

        states.add(State(inputState[0]))
        val list = ArrayList<State>()
        for (i in 0 until columnCount) list.add(inputTransitions[0][i])

        transitions.add(list)
        //var isNewState = true
        var row = 0
        var i = 0

       while (i <= row) {
            for (column in 0 until columnCount) {
                val state = transitions[i][column]
                if (state.toString().isNotEmpty()) {
                    if (!states.containsState(state)) {
                        ++row
                        if (state.containsTrueStateItem())
                            trOrFl.add("+")
                        else
                            trOrFl.add("-")
                        states.add(state)
                        transitions.add(createRowTransitions(state))
                    }
                }
            }
            ++i
        }

        */
/*while (isNewState) {
            isNewState = false
            for (i in 0 until row) {
                for (column in 0 until columnCount) {
                    val state = transitions[i][column]
                    if (state.toString().isNotEmpty()) {
                        if (!states.containsState(state)) {
                            ++row
                            if (state.containsTrueStateItem())
                                trOrFl.add("+")
                            else
                                trOrFl.add("-")
                            isNewState = true
                            states.add(state)
                            transitions.add(createRowTransitions(state))
                        }
                    }
                }
            }
        }*//*


        var tmp = ""
        Log.d("MSG", states.size.toString())
        states.forEachIndexed { index, state ->
            for (i in transitions[index]) {
                tmp += "$i   "
            }
            Log.d("MSG", "$state :  " + tmp)
            tmp = ""
        }
    }

    private fun createRowTransitions(state: State): ArrayList<State> {
        val list = ArrayList<State>()

        for (charIndex in inputCharacters.indices) {
            val stateTmp = State()
            for (i in state.states) {
                val rowIndex = inputState.indexOf(i)
                for (j in inputTransitions[rowIndex][charIndex].states) {
                    stateTmp.addIfNotContains(j)
                }
            }
            list.add(stateTmp)
        }

        return list
    }

    inner class State() {
        var states: MutableList<String> = mutableListOf()

        constructor(statesStr: String) : this() {
            states = statesStr.replace(" ", "").split(",").toMutableList()
        }

        fun containsTrueStateItem(): Boolean {
            states.forEach {
                if (trueStates.contains(it)) return true
            }
            return false
        }

        fun addIfNotContains(state: String) {
            if (!states.contains(state) && state.isNotEmpty())
                states.add(state)
        }

        fun contains(state: String): Boolean {
            return states.contains(state)
        }

        override fun equals(other: Any?): Boolean {
            if (other is State && states.size == other.states.size) {
                */
/*val comp = states.containsAll(other.states)
                val comp1 = other.states.containsAll(states)*//*

                return states.containsAll(other.states) && other.states.containsAll(states)
            }
            return false
        }

        override fun hashCode(): Int {
            return states.hashCode()
        }

        override fun toString(): String {
            return states.joinToString()
            */
/*var str = ""
            for (i in states.indices) {
                str += if (i == states.size - 1) {
                    states[i]
                } else {
                    "${states[i]},"
                }
            }
            return str*//*

        }
    }
}*/
