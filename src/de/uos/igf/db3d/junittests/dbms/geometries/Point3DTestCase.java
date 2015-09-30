/*
 * Copyright (C) Prof. Martin Breunig
 */

package de.uos.igf.db3d.junittests.dbms.geometries;

import junit.framework.TestCase;
import de.uos.igf.db3d.dbms.spatials.geometries3d.Line3D;
import de.uos.igf.db3d.dbms.spatials.geometries3d.Plane3D;
import de.uos.igf.db3d.dbms.spatials.geometries3d.Point3D;
import de.uos.igf.db3d.dbms.spatials.geometries3d.Segment3D;
import de.uos.igf.db3d.dbms.spatials.geometries3d.Vector3D;
import de.uos.igf.db3d.dbms.spatials.standard.GeoEpsilon;
import de.uos.igf.db3d.junittests.dbms.TestConstants;
import de.uos.igf.db3d.junittests.dbms.Utils;

/**
 * This testcase tests the (geometry) methods of the <code>Point3D</code> class.
 * 
 * Revision:<br>
 * 09/08 ueberarbeitet und geprueft - Edgar Butwilowski<br>
 * 29/01/04 ueberarbeitet und geprueft - wbaer
 * 
 * @author Sascha Klonus and Jens Pollehn
 */
public class Point3DTestCase extends TestCase {

	public final static Point3D P = new Point3D(-1, -1, -1);
	public final static Point3D P1 = new Point3D(2, 2, 2);
	public final static Point3D P2 = new Point3D(2, -1, 2);
	public final static Point3D P3 = new Point3D(1, -2, 1);
	public final static Point3D P4 = new Point3D(3, 0, 3); // on PLANE1
	public final static Point3D P5 = new Point3D(0, 0.6, -1.9);
	public final static Point3D P6 = new Point3D(-14.3, 5, 1);
	public final static Point3D P7 = new Point3D(0.345, 0.345, 0.345);
	public final static Point3D P8 = new Point3D(-0.5, -1, -1.6);

	GeoEpsilon scalarOp = new GeoEpsilon(TestConstants.EPSILON);
	Line3D LINE1;
	Plane3D PLANE1;
	Plane3D PLANE2;
	Segment3D SEG1;

	public void setUp() throws Exception {
		scalarOp = new GeoEpsilon(TestConstants.EPSILON);
		LINE1 = new Line3D(P, P1, scalarOp);
		PLANE1 = new Plane3D(P2, P3, new Point3D(0, -1, 5), scalarOp);
		PLANE2 = new Plane3D(new Point3D(1, 1, 1), new Point3D(-2.24, -3, 1),
				new Point3D(5.1, 2.78, 1), scalarOp);
		SEG1 = new Segment3D(P, P1, scalarOp);
	}

	public void testIntersectsPlane3D() {
		assertTrue(P3.intersects(PLANE1, scalarOp));
		assertTrue(P4.intersects(PLANE1, scalarOp));
		assertFalse(P5.intersects(PLANE1, scalarOp));
		assertFalse(P6.intersects(PLANE1, scalarOp));
	}

	public void testIntersectsLine3D() {
		assertTrue(P1.intersects(LINE1, scalarOp));
		assertTrue(P7.intersects(LINE1, scalarOp));
		assertFalse(P5.intersects(LINE1, scalarOp));
		assertFalse(P6.intersects(LINE1, scalarOp));
	}

	public void testIntersectionPlane3D() {
		assertEquals(Utils.getText(P3),
				Utils.getText(P3.intersection(PLANE1, scalarOp)));
		assertEquals(Utils.getText(P4),
				Utils.getText(P4.intersection(PLANE1, scalarOp)));
		assertNull(P5.intersection(PLANE1, scalarOp));
		assertNull(P6.intersection(PLANE1, scalarOp));
	}

	public void testIntersectionLine3D() {
		assertEquals(Utils.getText(P1),
				Utils.getText(P1.intersection(LINE1, scalarOp)));
		assertEquals(Utils.getText(P7),
				Utils.getText(P7.intersection(LINE1, scalarOp)));
		assertNull(P5.intersection(LINE1, scalarOp));
		assertNull(P6.intersection(LINE1, scalarOp));
	}

	public void testProjectionPlane3D() {
		assertEquals("P:(1.0,-2.0,1.0) ", Utils.getText(P3.projection(PLANE2)));
		assertEquals("P:(3.0,0.0,1.0) ", Utils.getText(P4.projection(PLANE2)));
		assertEquals("P:(0.0,0.6,1.0) ", Utils.getText(P5.projection(PLANE2)));
		assertEquals("P:(2.0,2.0,1.0) ", Utils.getText(P1.projection(PLANE2)));
	}

	public void testProjectionLine3D() {
		assertEquals("P:(-1.0,-1.0,-1.0) ", Utils.getText(P.projection(LINE1)));
		assertEquals(
				"P:(-0.43333333333333346,-0.43333333333333346,-0.43333333333333346) ",
				Utils.getText(P5.projection(LINE1)));
		assertEquals(
				"P:(-2.7666666666666666,-2.7666666666666666,-2.7666666666666666) ",
				Utils.getText(P6.projection(LINE1)));
		assertEquals(
				"P:(0.34499999999999975,0.34499999999999975,0.34499999999999975) ",
				Utils.getText(P7.projection(LINE1)));

		Line3D l = new Line3D(new Point3D(2, 1, 3), new Vector3D(1, 1, 1),
				scalarOp);
		Point3D p = new Point3D(3, 3, 3);

		assertEquals("P:(3.0,2.0,4.0) ", Utils.getText(p.projection(l)));
	}

	public void testProjectionSegment3D() {

		assertTrue(((Point3D) P.projection(SEG1, scalarOp))
				.isGeometryEquivalent(new Point3D(-1.0, -1.0, -1.0), scalarOp));
		assertEquals(
				"P:(-0.43333333333333346,-0.43333333333333346,-0.43333333333333346) ",
				Utils.getText(P5.projection(SEG1, scalarOp)));
		assertNull(P6.projection(SEG1, scalarOp));
		assertEquals(
				"P:(0.34499999999999975,0.34499999999999975,0.34499999999999975) ",
				Utils.getText(P7.projection(SEG1, scalarOp)));
		assertNull(P8.projection(SEG1, scalarOp));
	}

	public void testEuclideanDistanceSqrPoint3D() {
		Point3D point1 = new Point3D(0, 0, 0);
		Point3D point2 = new Point3D(2, 2, 2);

		assertEquals(12.0, point1.euclideanDistanceSQR(point2));
		assertEquals(0.0, point1.euclideanDistanceSQR(point1));
		assertEquals(3.9699999999999998, point1.euclideanDistanceSQR(P5));
		assertEquals(230.49, point1.euclideanDistanceSQR(P6));
	}

	public void testEuclideanDistancePoint3D() {
		Point3D point1 = new Point3D(0, 0, 0);
		Point3D point2 = new Point3D(2, 2, 2);

		assertEquals(3.4641016151377544, point1.euclideanDistance(point2));
		assertEquals(0.0, point1.euclideanDistance(point1));
		assertEquals(1.9924858845171274, point1.euclideanDistance(P5));
		assertEquals(15.181897114655994, point1.euclideanDistance(P6));
	}

	public void testIsEqualEquivalentable() {
		Point3D point1 = new Point3D(1, 1, 1);
		Point3D point2 = new Point3D(1, 1, 1);
		Point3D point3 = new Point3D(1, 1, -1);
		Point3D point4 = new Point3D(1.00001, 1, 1.000003);
		Point3D point5 = new Point3D(1.00001231, 1, 1.0000023);

		assertTrue(point1.isEqual(point2, scalarOp));
		assertEquals(point1.isEqualHC(10000), point2.isEqualHC(10000));
		assertFalse(point1.isEqual(point3, scalarOp));
		assertNotSame(point1.isEqualHC(10000), point3.isEqualHC(10000));
		assertTrue(point4.isEqual(point5, scalarOp));
		assertEquals(point4.isEqualHC(10000), point5.isEqualHC(10000));
	}

	public void tearDown() throws Exception {
	}

}
