import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.map.Point;
import com.pokegoapi.util.MapUtil;

public class Path2 {
	private Point source;
	private Point destination;
	private Point intermediate;
	private double speed;
	private long startTime;
	private long endTime;
	private long totalTime;
	private boolean complete;

	public Path2(Point source, Point destination, double speed) {
		this.source = source;
		this.destination = destination;
		double metersPerHour = speed * 1000;
		this.speed = metersPerHour / 60 / 60 / 1000;
		this.intermediate = new Point(source.getLatitude(), source.getLongitude());
		this.totalTime = (long) (MapUtil.distFrom(source, destination) / this.speed);
	}

	/**
	 * Sets the start and end time for this Path, ready to begin moving
	 *
	 * @param api the current API
	 * @return the total time it will take for this path to complete
	 */
	public long start(PokemonGo api) {
		startTime = api.currentTimeMillis();
		endTime = startTime + totalTime;
		complete = false;
		return totalTime;
	}

	/**
	 * Calculates the desired intermediate point for this path, based on the current time
	 *
	 * @param api the current API
	 * @return the intermediate point for the given time
	 */
	public Point calculateIntermediate(PokemonGo api) {
		if (totalTime <= 0) {
			this.complete = true;
			return this.destination;
		}
		long time = Math.min(api.currentTimeMillis(), endTime) - startTime;
		if (time >= totalTime) {
			this.complete = true;
		}
		double intermediate = (double) time / totalTime;
		double latitude = source.getLatitude() + (destination.getLatitude() - source.getLatitude()) * intermediate;
		double longitude = source.getLongitude() + (destination.getLongitude() - source.getLongitude()) * intermediate;
		this.intermediate.setLatitude(latitude);
		this.intermediate.setLongitude(longitude);
		return this.intermediate;
	}

	/**
	 * Gets the amount of millis left before this path is complete
	 *
	 * @param api the current API
	 * @return the amount of millis left before this path completes
	 */
	public long getTimeLeft(PokemonGo api) {
		return Math.max(0, endTime - api.currentTimeMillis());
	}

	/**
	 * Changes the speed of this path
	 *
	 * @param api the current API
	 * @param speed the new speed to travel at
	 */
	public void setSpeed(PokemonGo api, double speed) {
		double metersPerHour = speed * 1000;
		this.speed = metersPerHour / 60 / 60 / 1000;
		this.source = calculateIntermediate(api);
		this.totalTime = (long) (MapUtil.distFrom(source, destination) / this.speed);
		start(api);
	}

	public Point getSource() {
		return source;
	}

	public void setSource(Point source) {
		this.source = source;
	}

	public Point getDestination() {
		return destination;
	}

	public void setDestination(Point destination) {
		this.destination = destination;
	}

	public Point getIntermediate() {
		return intermediate;
	}

	public void setIntermediate(Point intermediate) {
		this.intermediate = intermediate;
	}

	public double getSpeed() {
		return speed;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public long getTotalTime() {
		return totalTime;
	}

	public void setTotalTime(long totalTime) {
		this.totalTime = totalTime;
	}

	public boolean isComplete() {
		return complete;
	}

	public void setComplete(boolean complete) {
		this.complete = complete;
	}


}