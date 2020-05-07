package com.nkrtodkr.utils.extensions

import android.graphics.Typeface
import android.widget.EditText
import android.widget.TableLayout
import android.widget.TableRow
import com.nkrtodkr.utils.Dkr

fun ArrayList<Dkr.State>.containsState(state: Dkr.State): Boolean {
    this.forEach {
        if (it == state) return true
    }
    return false
}

fun TableRow.getTextChildAt(index: Int) =
    (this.getChildAt(index) as EditText).text.toString()

fun TableRow.setTextForChildAt(index: Int, text: String) {
    (this.getChildAt(index) as EditText).setText(text)
}

fun TableLayout.getRowChildAt(index: Int) =
    (this.getChildAt(index) as TableRow)