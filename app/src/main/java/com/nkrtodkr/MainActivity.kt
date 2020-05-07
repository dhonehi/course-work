package com.nkrtodkr

import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.nkrtodkr.model.TableModel
import com.nkrtodkr.utils.Constants
import com.nkrtodkr.utils.Dkr
import com.nkrtodkr.utils.extensions.getRowChildAt
import com.nkrtodkr.utils.extensions.setTextForChildAt
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.result_table.*
import kotlinx.coroutines.*


class MainActivity : AppCompatActivity() {
    private lateinit var params: TableRow.LayoutParams
    private lateinit var dkr: Dkr

    private val scopeMain = CoroutineScope(Dispatchers.Main)
    private var tableModel: TableModel? = null
    private var tableModelResult: TableModel? = null
    private var firstEventConsumedRow = false
    private var firstEventConsumedColumn = false
    private var prevRowCount = 2
    private var prevColumnCount = 2
    private var padding = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        params = TableRow.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        padding = resources.getDimension(R.dimen.total_padding).toInt()

        dkr = Dkr()

        initClickListeners()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(Constants.ROW_COUNT, spRowCount.selectedItemPosition + 2)
        outState.putInt(Constants.COLUMN_COUNT, spColumnCount.selectedItemPosition + 2)
        outState.putSerializable(Constants.TABLE_MODEL, TableModel(tlTable))
        tableModelResult?.let {
            outState.putSerializable(Constants.RESULT_TABLE_MODEL, it)
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        if (savedInstanceState.containsKey(Constants.RESULT_TABLE_MODEL)) {
            val restoreResultTable =
                savedInstanceState.getSerializable(Constants.RESULT_TABLE_MODEL) as TableModel
            showResult(restoreResultTable)
        }
        val rowCount = savedInstanceState.getInt(Constants.ROW_COUNT)
        val columnCount = savedInstanceState.getInt(Constants.COLUMN_COUNT)
        spRowCount.setSelection(rowCount - 2)
        spColumnCount.setSelection(columnCount - 2)
        val restoredTable = savedInstanceState.getSerializable(Constants.TABLE_MODEL) as TableModel
        createTable(tlTable, restoredTable)
    }

    private fun createTable(table: TableLayout, tableModel: TableModel, cellFocusable: Boolean = true) {
        for (i in 3..tableModel.columnCount)
            createTableColumn(table, i, cellFocusable)

        for (i in table.childCount - 1 until tableModel.rowCount)
            table.addView(createTableRow(tableModel.columnCount + 2, cellFocusable))

        var rowTmp = table.getRowChildAt(0)
        tableModel.characters.forEachIndexed { index, char ->
            rowTmp.setTextForChildAt(index + 1, char)
        }

        tableModel.cells.forEachIndexed { rowIndex, row ->
            rowTmp = table.getRowChildAt(rowIndex + 1)
            rowTmp.setTextForChildAt(0, tableModel.states[rowIndex])
            rowTmp.setTextForChildAt(tableModel.columnCount + 1, tableModel.trOrFl[rowIndex])
            row.forEachIndexed { columnIndex, cell ->
                rowTmp.setTextForChildAt(columnIndex + 1, cell)
            }
        }
    }

    private fun createTableRow(cellCount: Int, cellFocusable: Boolean = true) =
        TableRow(this).apply {
            for (i in 0 until cellCount) {
                addView(createTableCell(cellFocusable), i)
            }
        }

    private fun createTableColumn(
        table: TableLayout,
        indexColumn: Int,
        cellFocusable: Boolean = true
    ) {
        var row = table.getRowChildAt(0)
        row.addView(createTableCell(cellFocusable), indexColumn)
        row.setTextForChildAt(indexColumn, ('a' + indexColumn - 1).toString())
        for (i in 1 until table.childCount) {
            row = table.getRowChildAt(i)
            row.addView(createTableCell(cellFocusable), indexColumn)
        }
    }

    private fun createTableCell(focusable: Boolean) =
        EditText(this).apply {
            setBackgroundResource(R.drawable.border)
            setTypeface(null, Typeface.BOLD)
            setPadding(padding, padding, padding, padding)
            setTextSize(
                TypedValue.COMPLEX_UNIT_PX,
                resources.getDimensionPixelSize(R.dimen.item_size).toFloat()
            )
            gravity = Gravity.CENTER
            layoutParams = params
            isFocusable = focusable
        }

    private fun transformRows(rowCount: Int, columnCount: Int) {
        if (rowCount > prevRowCount) {
            for (i in prevRowCount until rowCount) {
                val row = createTableRow(columnCount + 2)
                row.setTextForChildAt(0, "q$i")
                tlTable.addView(row, i + 1)
            }
        } else if (rowCount < prevRowCount) {
            for (i in prevRowCount downTo rowCount + 1)
                tlTable.removeViewAt(i)
        }
    }

    private fun transformColumns(rowCount: Int, columnCount: Int) {
        if (columnCount > prevColumnCount) {
            for (i in prevColumnCount + 1..columnCount) {
                createTableColumn(tlTable, i)
            }
        } else if (columnCount < prevColumnCount) {
            for (i in 0..rowCount) {
                val row = tlTable.getChildAt(i) as TableRow
                for (j in prevColumnCount downTo columnCount + 1) {
                    row.removeViewAt(j)
                }
            }
        }
    }

    private fun initClickListeners() {
        spRowCount.setOnTouchListener { _, _ ->
            firstEventConsumedRow = true
            prevRowCount = spRowCount.selectedItemPosition + 2
            false
        }
        spColumnCount.setOnTouchListener { _, _ ->
            firstEventConsumedColumn = true
            prevColumnCount = spColumnCount.selectedItemPosition + 2
            false
        }
        spRowCount.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (firstEventConsumedRow)
                    transformRows(position + 2, spColumnCount.selectedItemPosition + 2)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        spColumnCount.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (firstEventConsumedColumn)
                    transformColumns(spRowCount.selectedItemPosition + 2, position + 2)

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        btnTransform.setOnClickListener {
            scopeMain.launch {
                try {
                    val result =
                        withContext(this.coroutineContext) { dkr.createDKR(TableModel(tlTable)) }

                    showResult(result)
                } catch (e: Exception) {
                    Snackbar.make(
                        it,
                        getString(R.string.error_not_found_state),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun clearTable() {
        for (i in 1 until tlResultTable.childCount) {
            tlResultTable.removeViewAt(1)
        }
    }

    private fun showResult(tableModel: TableModel) {
        clearTable()
        tableModelResult = tableModel
        llResult.visibility = LinearLayout.VISIBLE
        createTable(tlResultTable, tableModel, false)
    }
}
