package nz.govt.msd.utils;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class TimeUtils {

	/**
	 * Prints out HH:MM.
	 * 
	 * @param hoursWorked
	 * @return
	 */
	public static String convertTimeInDecimalToHoursMins(Double hoursWorked) {

		int hours = (int) hoursWorked.doubleValue();
		long minutes = Math.round((hoursWorked - hours) * 60);

		return String.format("%s:%02d", hours, minutes);
	}

	@Test
	public void testZeroHoursZeroMins() {
		Double dt = 0.0;

		assertThat(TimeUtils.convertTimeInDecimalToHoursMins(dt), is(equalTo("0:00")));
	}

	@Test
	public void testHoursZeroMins() {

		assertThat(TimeUtils.convertTimeInDecimalToHoursMins(Double.valueOf("22")), is(equalTo("22:00")));
	}

	@Test
	public void testSingleDigitHour() {
		Double dt = 2.02;

		assertThat(TimeUtils.convertTimeInDecimalToHoursMins(dt), is(equalTo("2:01")));
	}

	@Test
	public void testOneMinute() {
		Double dt = 22.02;

		assertThat(TimeUtils.convertTimeInDecimalToHoursMins(dt), is(equalTo("22:01")));
	}
	

	@Test
	public void testThirtyOneMinutes() {
		Double dt = 22.52;

		assertThat(TimeUtils.convertTimeInDecimalToHoursMins(dt), is(equalTo("22:31")));
	}

	@Test
	public void testThirtyThreeMinutes() {
		Double dt = 22.55;

		assertThat(TimeUtils.convertTimeInDecimalToHoursMins(dt), is(equalTo("22:33")));
	}


	@Test
	public void test59Minutes() {
		Double dt = 22.98;

		assertThat(TimeUtils.convertTimeInDecimalToHoursMins(dt), is(equalTo("22:59")));
	}


	@Test
	public void testQuarterHour() {
		Double dt = 22.25;

		assertThat(TimeUtils.convertTimeInDecimalToHoursMins(dt), is(equalTo("22:15")));
	}

	@Test
	public void testHalfHour() {
		Double dt = 22.50;

		assertThat(TimeUtils.convertTimeInDecimalToHoursMins(dt), is(equalTo("22:30")));
	}

	@Test
	public void testThreeQuarterHour() {
		Double dt = 22.75;

		assertThat(TimeUtils.convertTimeInDecimalToHoursMins(dt), is(equalTo("22:45")));
	}

}
