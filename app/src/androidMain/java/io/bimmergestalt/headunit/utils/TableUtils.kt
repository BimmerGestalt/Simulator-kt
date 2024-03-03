package io.bimmergestalt.headunit.utils

import de.bmw.idrive.BMWRemoting.RHMIDataTable
import kotlin.math.max
import kotlin.math.min

val RHMIDataTable.lastRow: Int
	get() = fromRow + numRows - 1
fun RHMIDataTable.includes(row: Int): Boolean =
	row in fromRow..lastRow

/**
 * If this table and the other table don't have a gap between them
 * They may overlap any amount
 */
fun RHMIDataTable.abuts(table: RHMIDataTable): Boolean {
	return (table.fromRow <= this.fromRow - 1 && table.lastRow >= this.fromRow - 1) ||
			(table.fromRow >= this.fromRow - 1 && table.fromRow <= this.lastRow + 1)
}

/**
 * If the other table completely covers this table, and maybe more
 */
fun RHMIDataTable.isWithin(table: RHMIDataTable): Boolean {
	return (table.fromRow <= this.fromRow && table.lastRow >= this.lastRow)
}
fun RHMIDataTable.overlaps(table: RHMIDataTable): Boolean {
	return (this.fromRow <= table.fromRow && this.lastRow >= table.lastRow)
}
fun RHMIDataTable.isSameSize(table: RHMIDataTable): Boolean {
	return (this.totalRows == table.totalRows && this.totalColumns == table.totalColumns)
}

/**
 * Update this table to include the new table data
 * possibly adjusting the extents
 * Doesn't properly handle tables that don't have all the totalCols
 */
@Throws(IllegalArgumentException::class)
fun RHMIDataTable.merge(table: RHMIDataTable) {
	if (totalRows != table.totalRows || totalColumns != table.totalColumns) {
		throw IllegalArgumentException("Additional RHMIDataTable must be the same size")
	}
	val fullyWithin = fromRow <= table.fromRow && lastRow >= table.lastRow
	if (fullyWithin) {
		val destOffset = table.fromRow - fromRow
		table.data.forEachIndexed { index, row ->
			data[destOffset + index] = row
		}
	} else {
		val range = min(fromRow, table.fromRow)..max(lastRow, table.lastRow)
		val srcOffset = table.fromRow - range.first      // newData[srcOffset] == table.data[0]
		val destOffset = fromRow - range.first          // newData[destOffset] == this.data[0]
		val newData = Array<Array<Any>>(range.count()) {
			table.data.getOrNull(it - srcOffset) ?: data.getOrNull(it - destOffset) ?: emptyArray()
		}
		data = newData
		fromRow = range.first
		numRows = newData.size
	}
}

