package com.cw.financials.data.pricedata;

import java.util.Date;
import java.util.List;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;

public class PriceHistory {
	String symbol;
	Date beginningDate;
	Date endDate;
	int intervalDays;
	boolean completed;
	PriceDataInterval[] intervals;
	
	// format for displaying beginning and end dates
    static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

	public PriceHistory(String symbol, Date beginningDate, Date endDate, int intervalDays, boolean completed,
			List<PriceData> pdList) {
		this.symbol = symbol;
		this.beginningDate = beginningDate;
		this.endDate = endDate;
		this.intervalDays = intervalDays;
		this.completed = completed;
		if (pdList != null && !pdList.isEmpty()) {
			initialize(pdList);
		}
	}

	private void initialize(List<PriceData> pdList) {
		long totalDuration = endDate.getTime() - beginningDate.getTime();
		Duration intervalDuration = Duration.ofDays(intervalDays);
		int intervalCount = (int) Math.ceil(totalDuration/intervalDuration.toMillis());
		intervals = new PriceDataInterval[intervalCount];
		add(pdList);
	}

	private int getIntervalIndex(Date date) {
		Duration intervalDuration = Duration.ofDays(intervalDays);
		int index = (int)Math.floor((date.getTime() - beginningDate.getTime()) / 
				intervalDuration.toMillis());
		// clip boundaries of index to 0 and (n-1)
		if (index < 0) {
			index = 0;
		} else if (index > intervals.length - 1) {
			index = intervals.length - 1;
		}
		return index;
	}

	private void add(List<PriceData> pdList) {
		for (PriceData pd : pdList) {
			if (pd.timestamp != null) {
				int intervalIndex = getIntervalIndex(pd.timestamp);
				if (intervals[intervalIndex] == null) {
					intervals[intervalIndex] = new PriceDataInterval();
				}
				intervals[intervalIndex].accumulate(pd);
			}
		}
		// try to set percent change for all intervals except earliest interval
		for (int i = 0; i < intervals.length - 1; i++) {
			PriceDataInterval curr = intervals[i];
			if (curr != null && curr.getMean() != 0 
					&& intervals[i+1] != null 
					&& intervals[i+1].getMean() != 0) {
				PriceDataInterval prev = intervals[i+1];
				float increase = curr.getMean() - prev.getMean();
				float midpoint = 0.5f * (curr.getMean() + prev.getMean());
				float percentChange = (increase / midpoint) * 100;
				curr.setPercentChange(percentChange);
			}
		}
	}

	public String getBeginningDate() {
		return df.format(beginningDate);
	}

	public void setBeginningDate(Date beginningDate) {
		this.beginningDate = beginningDate;
	}

	public String getEndDate() {
		return df.format(endDate);
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

	public PriceDataInterval[] getIntervals() {
		return intervals;
	}

	public void setIntervals(PriceDataInterval[] intervals) {
		this.intervals = intervals;
	}
}
