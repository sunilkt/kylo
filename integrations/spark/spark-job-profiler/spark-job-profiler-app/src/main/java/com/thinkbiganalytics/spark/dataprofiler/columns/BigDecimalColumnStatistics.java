package com.thinkbiganalytics.spark.dataprofiler.columns;

import com.thinkbiganalytics.spark.dataprofiler.model.MetricType;
import com.thinkbiganalytics.spark.dataprofiler.output.OutputRow;
import org.apache.spark.sql.types.StructField;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * Class to hold profile statistics for columns of bigdecimal data type <br>
 * [Hive data type: DECIMAL]
 * @author jagrut sharma
 *
 */
@SuppressWarnings("serial")
public class BigDecimalColumnStatistics extends ColumnStatistics {

	/* BigDecimal specific metrics */
	private BigDecimal max;
	private BigDecimal min;
	private BigDecimal sum;
	
	/* Other variables */
	private BigDecimal columnBigDecimalValue;
	private BigDecimal columnBigDecimalCount;
	
	
	/**
	 * One-argument constructor
	 * @param columnField field schema
	 */
	public BigDecimalColumnStatistics(StructField columnField) {
		
		super(columnField);
		
		max = BigDecimal.valueOf(Long.MIN_VALUE);
		min = BigDecimal.valueOf(Long.MAX_VALUE);
		
		sum = BigDecimal.ZERO;
		
		columnBigDecimalValue = BigDecimal.ZERO;
		columnBigDecimalCount = BigDecimal.ZERO;
	}

	
	/**
	 * Calculate bigdecimal-specific statistics by accommodating the value and frequency/count
	 */
	@Override
	public void accomodate(Object columnValue, Long columnCount) {
		
		accomodateCommon(columnValue, columnCount);
		
		if (columnValue != null) {
			
			columnBigDecimalValue = new BigDecimal(String.valueOf(columnValue));
			columnBigDecimalCount = new BigDecimal(columnCount);
			
			if (max.compareTo(columnBigDecimalValue) < 0) {
				max = columnBigDecimalValue;
			}
			
			if (min.compareTo(columnBigDecimalValue) > 0) {
				min = columnBigDecimalValue;
			}
			
			sum = sum.add(columnBigDecimalValue.multiply(columnBigDecimalCount));
			
		}
		
	}

	
	/**
	 * Combine with another column statistics 
	 */
	@Override
	public void combine(ColumnStatistics v_columnStatistics) {
		
		combineCommon(v_columnStatistics);
		
		BigDecimalColumnStatistics vBigDecimal_columnStatistics = (BigDecimalColumnStatistics) v_columnStatistics;
		
		if (max.compareTo(vBigDecimal_columnStatistics.max) < 0) {
			max = vBigDecimal_columnStatistics.max;
		}
		
		if (min.compareTo(vBigDecimal_columnStatistics.min) > 0) {
			min = vBigDecimal_columnStatistics.min;
		}
		
		sum = sum.add(vBigDecimal_columnStatistics.sum);
	}

	
	/**
	 * Print statistics to console
	 */
	@Override
	public String getVerboseStatistics() {

		return "{\n" + getVerboseStatisticsCommon()
		+ "\n"
		+ "BigDecimalColumnStatistics ["
		+ "max=" + max
		+ ", min=" + min
		+ ", sum=" + sum
		+ "]\n}";
	}

	
	/**
	 * Write statistics for output result table
	 */
	@Override
	public void writeStatistics() {
		writeStatisticsCommon();
		
		rows = new ArrayList<>();
		rows.add(new OutputRow(columnField.name(), String.valueOf(MetricType.MAX), String.valueOf(max)));
		rows.add(new OutputRow(columnField.name(), String.valueOf(MetricType.MIN), String.valueOf(min)));
		rows.add(new OutputRow(columnField.name(), String.valueOf(MetricType.SUM), String.valueOf(sum)));
		outputWriter.addRows(rows);
	}

	
	/**
	 * Get maximum value
	 * @return max value
	 */
	public BigDecimal getMax() {
		return max;
	}

	
	/**
	 * Get minimum value
	 * @return min value
	 */
	public BigDecimal getMin() {
		return min;
	}

	
	/**
	 * Get sum
	 * @return sum
	 */
	public BigDecimal getSum() {
		return sum;
	}	
	
}