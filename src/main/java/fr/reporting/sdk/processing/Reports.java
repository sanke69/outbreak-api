package fr.reporting.sdk.processing;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

import fr.reporting.api.Report;
import fr.reporting.api.Report.DailyPeriod;

public final class Reports {

	public static <DR extends Report.Daily>
	Collection<DR> 	aggregate(Collection<DR> _variations, Function<DR, ?> classifier, Report.Aggregator<DR> _aggregator) {
		List<DR> totalReports = new ArrayList<DR>();
	
		_variations.stream().collect(Collectors.groupingBy(
										r -> classifier.apply(r),
										Collectors.mapping(
											Function.identity(),
											Collectors.toCollection(() -> new TreeSet<DR>(Report.Daily.comparatorByDate())))))
							.forEach((country, crs) -> { _aggregator.reset(); crs.forEach(r -> totalReports.add( _aggregator.aggregate(r) )); });
		
		return totalReports;
	}

	public static <DR extends Report.Daily>
	DailyPeriod 	period(Collection<DR> _reports) {
		LocalDate firstDate = _reports.stream()
			      .min(Comparator.comparing( Report.Daily::getDate ))
			      .map( Report.Daily::getDate )
			      .orElseThrow(NoSuchElementException::new);
		LocalDate lastDate  = _reports.stream()
					      .max(Comparator.comparing( Report.Daily::getDate ))
					      .map( Report.Daily::getDate )
					      .orElseThrow(NoSuchElementException::new);
		
		return new DailyPeriod(firstDate, lastDate);
	}

}
