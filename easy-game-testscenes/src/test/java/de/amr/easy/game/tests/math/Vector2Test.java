package de.amr.easy.game.tests.math;

import static de.amr.easy.game.math.V2f.angle;
import static de.amr.easy.game.math.V2f.diff;
import static de.amr.easy.game.math.V2f.dot;
import static de.amr.easy.game.math.V2f.euclideanDist;
import static de.amr.easy.game.math.V2f.inverse;
import static de.amr.easy.game.math.V2f.smul;
import static de.amr.easy.game.math.V2f.sum;
import static de.amr.easy.game.math.V2f.v;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.amr.easy.game.math.V2f;

public class Vector2Test {

	private V2f v, w, x;

	@Before
	public void setUp() {
		v = v(5, -10);
		w = v(-3, 4);
		x = v(1, 1);
	}

	@Test
	public void testEquals() {
		assertEquals(v, v(v.x(), v.y()));
	}

	@Test
	public void testSum() {
		V2f sum = sum(v, w);
		assertEquals(2, sum.x(), V2f.EPSILON);
		assertEquals(-6, sum.y(), V2f.EPSILON);
	}

	@Test
	public void testDiff() {
		V2f diff = diff(v, w);
		assertEquals(8f, diff.x(), V2f.EPSILON);
		assertEquals(-14, diff.y(), V2f.EPSILON);
		assertEquals(sum(v, inverse(w)), diff);
	}

	@Test
	public void testNullVector() {
		assertEquals(0, V2f.NULL.x(), 0);
		assertEquals(0, V2f.NULL.y(), 0);
		assertEquals(sum(V2f.NULL, v), v);
		assertEquals(sum(v, V2f.NULL), v);
	}

	@Test
	public void testInverse() {
		V2f inverse = inverse(v);
		assertEquals(-5, inverse.x(), V2f.EPSILON);
		assertEquals(10, inverse.y(), V2f.EPSILON);
		assertEquals(V2f.NULL, sum(v, inverse));
		assertEquals(V2f.NULL, sum(inverse, v));
	}

	@Test
	public void testAssociativity() {
		assertTrue(sum(v, sum(w, x)).equals(sum(sum(v, w), x)));
	}

	@Test
	public void testCommutativity() {
		assertTrue(sum(v, w).equals(sum(w, v)));
	}

	@Test
	public void testMultiplicationWithScalar() {
		V2f product = smul(1.5f, v);
		assertEquals(7.5f, product.x(), V2f.EPSILON);
		assertEquals(-15.0f, product.y(), V2f.EPSILON);

		// 3 * (2 * v) = (3 * 2) * v
		assertTrue(smul(3, smul(2, v)).equals(smul(3 * 2, v)));
		// (3 + 2) * v = 3 * v + 2 * v
		assertTrue(smul(3 + 2, v).equals(sum(smul(3, v), smul(2, v))));
		// 3 * (v + w) = 3 * v + 3 * w
		assertTrue(smul(3, sum(v, w)).equals(sum(smul(3, v), smul(3, w))));
		// 1 * v = v
		assertTrue(smul(1f, v).equals(v));
		// 0 * v = 0
		assertTrue(smul(0, v) == V2f.NULL);
	}

	@Test
	public void testScalarProduct() {
		float product = dot(v, w);
		assertTrue(product == -55);

		V2f e1 = v(1, 0);
		V2f e2 = v(0, 1);
		assertTrue(dot(e1, e2) == 0);
	}

	@Test
	public void testDistance() {
		assertTrue(euclideanDist(v, w) * euclideanDist(v, w) == dot(diff(v, w), diff(v, w)));
		assertTrue(euclideanDist(v, v) == 0);
		assertTrue(euclideanDist(v, w) > 0);
		assertTrue(euclideanDist(v, w) + euclideanDist(w, x) >= euclideanDist(v, x));
		assertTrue(euclideanDist(v, w) == euclideanDist(w, v));
	}

	@Test
	public void testLength() {
		assertTrue(V2f.NULL.length() == 0);
		assertTrue(v.length() == inverse(v).length());
		assertTrue(v.length() + w.length() >= sum(v, w).length());
		assertTrue(v.length() >= 0);
		assertTrue(smul(2, v).length() == 2 * v.length());
		assertTrue(diff(v, v).length() == 0);
	}

	@Test
	public void testAngle() {
		V2f v = v(1, 0), w;

		w = v(1, 1);
		assertEquals(45, angle(v, x), 0.001);

		w = v(0, 1);
		assertEquals(90, angle(v, w), 0.001);

		w = v(-1, 1);
		assertEquals(135, angle(v, w), 0.001);

		w = v(-1, 0);
		assertEquals(180, angle(v, w), 0.001);

		w = v(-1, -1);
		assertEquals(135, angle(v, w), 0.001);

		w = v(0, -1);
		assertEquals(90, angle(v, w), 0.001);

		w = v(1, -1);
		assertEquals(45, angle(v, w), 0.001);
	}
}