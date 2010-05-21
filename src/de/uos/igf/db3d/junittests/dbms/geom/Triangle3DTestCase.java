/*
 * Copyright (C) Prof. Martin Breunig
 */

package de.uos.igf.db3d.junittests.dbms.geom;

import junit.framework.TestCase;
import de.uos.igf.db3d.dbms.geom.Point3D;
import de.uos.igf.db3d.dbms.geom.ScalarOperator;
import de.uos.igf.db3d.dbms.geom.Segment3D;
import de.uos.igf.db3d.dbms.geom.SimpleGeoObj;
import de.uos.igf.db3d.dbms.geom.Triangle3D;
import de.uos.igf.db3d.dbms.geom.Wireframe3D;

/**
 * This testcase tests the (geometry) methods of the <code>Triangle3D</code>
 * class.
 * 
 * @author Edgar Butwilowski
 */
public class Triangle3DTestCase extends TestCase {

	public void setUp() throws Exception {

	}

	public void testIntersectionTriangleSegment() {

		/**
		 * <tt>
		 *  +-------+
		 *   \     /
		 * +--\---/--+
		 *     \ /
		 *      +
		 * </tt>
		 */
		ScalarOperator sop = new ScalarOperator();
		Triangle3D triangle = new Triangle3D(new Point3D(2.0, 3.0, 0.0),
				new Point3D(1.0, 4.0, 0.0), new Point3D(3.0, 4.0, 0.0), sop);
		Segment3D segment = new Segment3D(new Point3D(0.0, 3.5, 0.0),
				new Point3D(5.0, 3.5, 0.0), sop);

		SimpleGeoObj result = triangle.intersection(segment, sop);

		// return type in this case must be a segment:
		assertTrue(result instanceof Segment3D);

		if (result instanceof Segment3D) {
			Segment3D cutSegment = (Segment3D) result;
			// cutting points must be [2.5,3.5] and [1.5,3.5]:
			assertTrue(cutSegment.getPoint(0).isEqual(
					new Point3D(2.5, 3.5, 0.0), sop));
			assertTrue(cutSegment.getPoint(1).isEqual(
					new Point3D(1.5, 3.5, 0.0), sop));
		}

		/**
		 * <tt>
		 *  +-------+
		 *   \     /
		 *    \   /
		 *     \ /
		 * +----+----+
		 * </tt>
		 */
		triangle = new Triangle3D(new Point3D(2.0, 3.0, 0.0), new Point3D(1.0,
				4.0, 0.0), new Point3D(3.0, 4.0, 0.0), sop);
		segment = new Segment3D(new Point3D(1.0, 3.0, 0.0), new Point3D(3.0,
				3.0, 0.0), sop);

		result = triangle.intersection(segment, sop);

		// return type in this case must be a point:
		assertTrue(result instanceof Point3D);

		if (result instanceof Point3D) {
			Point3D cutPoint = (Point3D) result;
			// cutting points must be [1.5,3.5] and [2.5,3.5]:
			assertTrue(cutPoint.isEqual(new Point3D(2.0, 3.0, 0.0), sop));
		}

		/**
		 * <tt>
		 *             +
		 *            /
		 *           /
		 *  +-------+
		 *   \     /
		 *    \   /
		 *     \ /
		 *      +
		 *     /
		 *    /
		 *   +
		 * </tt>
		 */
		triangle = new Triangle3D(new Point3D(2.0, 2.0, 0.0), new Point3D(1.0,
				4.0, 0.0), new Point3D(4.0, 4.0, 0.0), sop);
		segment = new Segment3D(new Point3D(1.0, 1.0, 0.0), new Point3D(5.0,
				5.0, 0.0), sop);

		result = triangle.intersection(segment, sop);

		// return type in this case must be a segment:
		assertTrue(result instanceof Segment3D);

		// TODO test points...

	}

	public void testIntersectionTriangleTriangle() {

		/*
		 * In the following the intersection() method of the Triangle3D class,
		 * i.e. intersection properties of alterating geometry configurations
		 * are tested. The respective configuration of the triangle geometry is
		 * depicted in some sort of ASCII art:
		 */

		/**
		 * <tt>
		 * +-----+
		 *  \   /
		 *   \ /
		 *    +
		 *   / \
		 *  /   \
		 * +-----+
		 * </tt>
		 */
		ScalarOperator sop = new ScalarOperator();
		Triangle3D triangleA = new Triangle3D(new Point3D(1.0, 1.0, 0.0),
				new Point3D(3.0, 1.0, 0.0), new Point3D(2.0, 3.0, 0.0), sop);
		Triangle3D triangleB = new Triangle3D(new Point3D(2.0, 3.0, 0.0),
				new Point3D(1.0, 4.0, 0.0), new Point3D(3.0, 4.0, 0.0), sop);

		SimpleGeoObj result = triangleB.intersection(triangleA, sop);

		// return type in this case must be a point:
		assertTrue(result instanceof Point3D);
		if (result instanceof Point3D) {
			Point3D resultPoint = (Point3D) result;
			assertTrue(resultPoint.getX() == 2.0);
			assertTrue(resultPoint.getY() == 3.0);
			assertTrue(resultPoint.getZ() == 0.0);
		}

		/**
		 * <tt>
		 *    +-----+
		 *   / \   /
		 *  /   \ /
		 * +-----+
		 * </tt>
		 */
		triangleA = new Triangle3D(new Point3D(1.0, 1.0, 0.0), new Point3D(3.0,
				1.0, 0.0), new Point3D(2.0, 3.0, 0.0), sop);
		triangleB = new Triangle3D(new Point3D(2.0, 3.0, 0.0), new Point3D(3.0,
				1.0, 0.0), new Point3D(4.0, 2.0, 0.0), sop);

		result = triangleB.intersection(triangleA, sop);

		// return type in this case must be a segment:
		assertTrue(result instanceof Segment3D);

		/**
		 * <tt>
		 *    ++
		 *   // \\
		 *  //   \\
		 * ++-----++
		 * </tt>
		 * 
		 * the triangles are congruent
		 */
		triangleA = new Triangle3D(new Point3D(1.0, 1.0, 0.0), new Point3D(3.0,
				1.0, 0.0), new Point3D(2.0, 3.0, 0.0), sop);
		triangleB = new Triangle3D(new Point3D(1.0, 1.0, 0.0), new Point3D(3.0,
				1.0, 0.0), new Point3D(2.0, 3.0, 0.0), sop);

		result = triangleB.intersection(triangleA, sop);

		// return type in this case must be a triangle:
		assertTrue(result instanceof Triangle3D);

		/**
		 * <tt>
		 *        +
		 *       / \
		 *      /   \
		 *     /  +  \
		 *    /  / \  \
		 *   /  /   \  \
		 *  /  +-----+  \
		 * +-------------+
		 * </tt>
		 * 
		 * one triangle completely lies in the other
		 */
		triangleA = new Triangle3D(new Point3D(1.0, 1.0, 0.0), new Point3D(5.0,
				1.0, 0.0), new Point3D(3.0, 4.0, 0.0), sop);
		triangleB = new Triangle3D(new Point3D(2.0, 2.0, 0.0), new Point3D(4.0,
				2.0, 0.0), new Point3D(3.0, 3.0, 0.0), sop);

		result = triangleA.intersection(triangleB, sop);

		// return type in this case must be a triangle:
		assertTrue(result instanceof Triangle3D);

		/**
		 * <tt>
		 *        
		 *      +  
		 *     / \  
		 *    / + \ 
		 *   +-/-\-+  
		 *    /   \  
		 *   +-----+  
		 * 
		 * </tt>
		 */
		triangleA = new Triangle3D(new Point3D(1.0, 1.0, 0.0), new Point3D(3.0,
				1.0, 0.0), new Point3D(2.0, 3.0, 0.0), sop);
		triangleB = new Triangle3D(new Point3D(1.0, 2.0, 0.0), new Point3D(3.0,
				2.0, 0.0), new Point3D(2.0, 4.0, 0.0), sop);

		result = triangleB.intersection(triangleA, sop);

		// return type in this case must be a triangle:
		assertTrue(result instanceof Triangle3D);

		triangleA = new Triangle3D(new Point3D(1.0, 1.0, 0.0), new Point3D(3.0,
				1.0, 0.0), new Point3D(2.0, 3.0, 0.0), sop);
		triangleB = new Triangle3D(new Point3D(2.0, 2.0, 0.0), new Point3D(4.0,
				2.0, 0.0), new Point3D(3.0, 4.0, 0.0), sop);

		result = triangleA.intersection(triangleB, sop);

		// return type in this case must be a triangle:
		assertTrue(result instanceof Triangle3D);

		/**
		 * <tt>
		 *     +
		 *     /\
		 * +--/--\--+ 
		 *  \/    \/
		 *  /\    /\
		 * +--------+
		 *     \/
		 *     +
		 * </tt>
		 */
		triangleA = new Triangle3D(new Point3D(3.0, 3.0, 0.0), new Point3D(5.0,
				6.0, 0.0), new Point3D(7.0, 3.0, 0.0), sop);
		triangleB = new Triangle3D(new Point3D(5.0, 2.0, 0.0), new Point3D(7.0,
				5.0, 0.0), new Point3D(3.0, 5.0, 0.0), sop);

		result = triangleA.intersection(triangleB, sop);

		// return type in this case must be a wireframe:
		assertTrue(result instanceof Wireframe3D);
		
        /**
         * <tt>
         *+----+----+
         * \   /\  /
         *  \ /  \/
         *   /   /\
         *  / \ /  \
         * +---+----+
         *     
         * </tt>
         */
		triangleA = new Triangle3D(new Point3D(0.0, 0.0, 2.0), new Point3D(2.0,
				2.0, 2.0), new Point3D(4.0, 0.0, 2.0), sop);
		triangleB = new Triangle3D(new Point3D(0.0, 2.0, 2.0), new Point3D(4.0,
				2.0, 2.0), new Point3D(2.0, 0.0, 2.0), sop);

		result = triangleA.intersection(triangleB, sop);

		// return type in this case must be a wireframe:
		assertTrue(result instanceof Wireframe3D);

		/**
		 * <tt>
		 *             +
		 *            / \
		 *      +    /   \
		 *     / \  +-----+
		 *    /   \  
		 *   +-----+  
		 * </tt>
		 */
		triangleA = new Triangle3D(new Point3D(1.0, 1.0, 0.0), new Point3D(3.0,
				1.0, 0.0), new Point3D(2.0, 3.0, 0.0), sop);
		triangleB = new Triangle3D(new Point3D(3.0, 2.0, 0.0), new Point3D(5.0,
				2.0, 0.0), new Point3D(4.0, 4.0, 0.0), sop);

		result = triangleB.intersection(triangleA, sop);

		// return value in this case must be a null pointer:
		assertTrue(result == null);

	}

	public void tearDown() throws Exception {
	}


}
