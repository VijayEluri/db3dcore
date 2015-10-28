package de.uos.igf.db3d.dbms.newModel4d;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import de.uos.igf.db3d.dbms.geom.Point3D;
import de.uos.igf.db3d.dbms.model3d.ComplexGeoObj;

/**
 * This class represents a 4D TriangleNet. A TriangleNet objects consists of one
 * to many TriangleComponent objects.
 * 
 * Jedes TriangleComponent Objekt hat ein Zeitinterval, was dem des TriangleNet
 * Objektes entspricht. Allerdings kann jedes TriangleComponent objekt eine
 * andere zeitliche Diskretisierung besitzen (s. Dissertation).
 * 
 * @author Paul Vincent Kuper (kuper@kit.edu)
 */
public class TriangleNet4D implements Net4D {

	// The TriangleComponents of this TriangleNet
	// Due to the TriangleComponents we can handle different parts of the net
	// with a different temporal discretisation.
	Map<Integer, Component4D> components;

	// Connects TimeIntervals to TriangleComponent4D objects
	Map<TimeInterval, List<Integer>> timeIntervals;

	// Dates with a change of net topology
	LinkedList<Date> changeDates;

	// the start date of the existence interval for this TriangleNet4D object
	Date start;

	// the end date of the existence interval for this TriangleNet4D object
	Date end;

	// TimeInterval to Component Mapper
	TimeInterval currentInterval;

	// Die Elemente dieses Netzes mit ID. Fuer jeden Topologiewechsel eine Map:
	List<Map<Integer, Element4D>> elements;

	// indicates if Boundary elements should be handled explicitly
	boolean boundaryElements;

	// Die Boundary-Elemente dieses Netzes mit ID. Fuer jeden Topologiewechsel
	// eine Map:
	List<Map<Integer, Element4D>> boundaryElements1D;

	/**
	 * Constructor for a TriangleNet4D. The initial start date is set. Call
	 * createEndOfExistenceInterval() function to set an end date.
	 * 
	 * @param start
	 *            - Start Date
	 */
	public TriangleNet4D(Date start) {
		super();
		components = new HashMap<Integer, Component4D>();
		timeIntervals = new HashMap<TimeInterval, List<Integer>>();
		changeDates = new LinkedList<Date>();
		elements = new LinkedList<Map<Integer, Element4D>>();
		boundaryElements1D = new LinkedList<Map<Integer, Element4D>>();

		this.start = start;
		this.end = new Date(Long.MAX_VALUE);

		currentInterval = new TimeInterval(start, null);

		timeIntervals.put(currentInterval, new LinkedList<Integer>());
		boundaryElements = false;
	}

	/**
	 * Add a single triangle to the net.
	 * 
	 * @param triangle
	 */
	public void addTriangleComponent(TriangleComponent4D component) {

		if (components.containsKey(component.getID())) {
			throw new IllegalArgumentException(
					"You tried to add a triangleComponent that already exists to the TriangleNet.");
		}
		components.put(component.getID(), component);

		// update TimeInterval to Component Mapper
		timeIntervals.get(currentInterval).add(component.getID());
	}

	/**
	 * Creates the end of the time interval.
	 * 
	 * @param end
	 */
	public void createEndOfExistenceInterval(Date end) {
		this.end = end;
	}

	/**
	 * Returns TriangleComponents of this TriangleNet.
	 * 
	 * @return Map<Integer, TriangleComponent4D> - All TriangleComponents of
	 *         this net.
	 */
	public Map<Integer, Component4D> getComponents() {
		return components;
	}

	/**
	 * Returns a specific TriangleComponents of this TriangleNet.
	 * 
	 * @return TriangleComponent4D
	 */
	public Component4D getComponent(int ID) {
		return components.get(ID);
	}

	/**
	 * Add a single triangle to the net.
	 * 
	 * @param triangle
	 */
	public void addTriangle(Triangle4D triangle) {

		// Immer an der aktuellen Stelle einfuegen:
		if (elements.get(elements.size() - 1).containsKey(triangle.getID())) {
			throw new IllegalArgumentException(
					"You tried to add a triangle that already exists to the TriangleNet.");
		}
		elements.get(elements.size() - 1).put(triangle.getID(), triangle);
	}

	/**
	 * Returns elements of this TriangleNet.
	 * 
	 * @return Map<Integer, Element4D> - All elements of this net.
	 */
	public List<Map<Integer, Element4D>> getElements() {
		return elements;
	}

	public void addTimestep(Component4D component,
			HashMap<Integer, Point3D> newPoints, Date date) {

	}

	@Override
	public void addChangeTimestep(Date date) {
		changeDates.add(date);
		// Add new Post object:
		elements.add(new TreeMap<Integer, Element4D>());
		boundaryElements1D.add(new TreeMap<Integer, Element4D>());
	}

	public LinkedList<Date> getChangeDates() {
		return changeDates;
	}

	public Date getLastChangeDate() {
		return changeDates.getLast();
	}

	public void preparePostObject(Date date) {
		closeAllComponents(date);
		closeTimeInterval(date);
		addChangeTimestep(date);
	}

	/**
	 * We need to close the TimeIntervals of this net
	 * 
	 * @param date
	 */
	private void closeTimeInterval(Date date) {

		// close old TimeInterval
		currentInterval.setEnd(date);

		// start new TimeInterval
		currentInterval = new TimeInterval(date, null);

		// generate new List of Components for the new TimeInterval
		timeIntervals.put(currentInterval, new LinkedList<Integer>());
	}

	/**
	 * We need to close all TimeIntervals of all current components of this net
	 * 
	 * @param date
	 */
	private void closeAllComponents(Date date) {

		for (Integer ID : timeIntervals.get(currentInterval)) {
			Component4D comp = components.get(ID);
			comp.getTimeInterval().setEnd(date);
		}
	}

	public Date getStart() {
		return start;
	}

	public Date getEnd() {
		return end;
	}

	@Override
	public List<Component4D> getValidComponents(Date date) {

		for (TimeInterval interval : timeIntervals.keySet()) {
			if ((interval.getStart().before(date) || interval.getStart()
					.equals(date))
					&& (interval.getEnd().after(date) || interval.getEnd()
							.equals(date))) {

				List<Component4D> tmp = new LinkedList<Component4D>();

				for (Integer ID : timeIntervals.get(interval))
					tmp.add(components.get(ID));

				return tmp;
			}
		}
		return null;
	}

	@Override
	public Map<Integer, Element4D> getNetElements(Date date) {

		int index = 0;

		// is it invalid?
		if (date.before(changeDates.get(0)))
			return null;

		for (Date check : changeDates) {
			if (check.before(date) || check.equals(date))
				index++;
		}

		return elements.get(index - 1);
	}

	public boolean isBoundaryElements() {
		return boundaryElements;
	}

	public void setBoundaryElements(boolean boundaryElements) {
		this.boundaryElements = boundaryElements;
	}

	public Map<Integer, Element4D> getBoundaryElements1D(Date date) {
		int index = 0;

		// is it invalid?
		if (date.before(changeDates.get(0)))
			return null;

		for (Date check : changeDates) {
			if (check.before(date) || check.equals(date))
				index++;
		}

		return boundaryElements1D.get(index - 1);
	}

	public void addBoundaryElement(Segment4D seg) {

		boundaryElements1D.get(boundaryElements1D.size() - 1).put(seg.getID(),
				seg);
	}

	@Override
	public byte getType() {
		return ComplexGeoObj.TRIANGLE_NET_4D;
	}
}
