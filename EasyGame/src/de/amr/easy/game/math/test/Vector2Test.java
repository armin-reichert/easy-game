package de.amr.easy.game.math.test;

import static de.amr.easy.game.math.Vector2.angle;
import static de.amr.easy.game.math.Vector2.diff;
import static de.amr.easy.game.math.Vector2.dist;
import static de.amr.easy.game.math.Vector2.dot;
import static de.amr.easy.game.math.Vector2.inverse;
import static de.amr.easy.game.math.Vector2.nullVector;
import static de.amr.easy.game.math.Vector2.sum;
import static de.amr.easy.game.math.Vector2.times;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.amr.easy.game.math.Vector2;

public class Vector2Test {

	private Vector2 v, w, x;

	@Before
	public void setUp() {
		v = new Vector2(5, -10);
		w = new Vector2(-3, 4);
		x = new Vector2(1, 1);
	}

	@Test
	public void testEquals() {
		assertTrue(v.equals(new Vector2(v.x, v.y)));
	}

	@Test
	public void testSum() {
		Vector2 sum = sum(v, w);
		assertTrue(sum.x == 2);
		assertTrue(sum.y == -6);
	}

	@Test
	public void testDiff() {
		Vector2 diff = diff(v, w);
		assertTrue(diff.x == 8);
		assertTrue(diff.y == -14);
		assertTrue(diff.equals(sum(v, inverse(w))));
	}

	@Test
	public void testNullVector() {
		Vector2 nullVector = nullVector();
		assertTrue(nullVector.x == 0);
		assertTrue(nullVector.y == 0);
		assertTrue(sum(nullVector, v).equals(v));
		assertTrue(sum(v, nullVector).equals(v));
	}

	@Test
	public void testInverse() {
		Vector2 inverse = inverse(v);
		assertTrue(inverse.x == -5);
		assertTrue(inverse.y == 10);
		assertTrue(Vector2.nullVector().equals(sum(v, inverse)));
		assertTrue(Vector2.nullVector().equals(sum(inverse, v)));
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
		Vector2 product = times(1.5f, v);
		assertTrue(product.x == 7.5f);
		assertTrue(product.y == -15);

		// 3 * (2 * v) = (3 * 2) * v
		assertTrue(times(3, times(2, v)).equals(times(3 * 2, v)));
		// (3 + 2) * v = 3 * v + 2 * v
		assertTrue(times(3 + 2, v).equals(sum(times(3, v), times(2, v))));
		// 3 * (v + w) = 3 * v + 3 * w
		assertTrue(times(3, sum(v, w)).equals(sum(times(3, v), times(3, w))));
		// 1 * v = v
		assertTrue(times(1f, v).equals(v));
	}

	@Test
	public void testScalarProduct() {
		float product = dot(v, w);
		assertTrue(product == -55);

		Vector2 e1 = new Vector2(1, 0);
		Vector2 e2 = new Vector2(0, 1);
		assertTrue(dot(e1, e2) == 0);
	}

	@Test
	public void testDistance() {
		assertTrue(dist(v, w) * dist(v, w) == dot(diff(v, w), diff(v, w)));
		assertTrue(dist(v, v) == 0);
		assertTrue(dist(v, w) > 0);
		assertTrue(dist(v, w) + dist(w, x) >= dist(v, x));
		assertTrue(dist(v, w) == dist(w, v));
	}

	@Test
	public void testLength() {
		assertTrue(nullVector().length() == 0);
		assertTrue(v.length() == inverse(v).length());
		assertTrue(v.length() + w.length() >= sum(v, w).length());
		assertTrue(v.length() >= 0);
		assertTrue(times(2, v).length() == 2 * v.length());
		assertTrue(diff(v, v).length() == 0);
	}
	
	@Test
	public void testAngle() {
		Vector2 v = new Vector2(1, 0), w;
		
		w = new Vector2(1, 1);
		assertEquals(45, angle(v, x), 0.001);
		
		w.assign(0, 1);
		assertEquals(90, angle(v, w), 0.001);
		
		w.assign(-1, 1);
		assertEquals(135, angle(v, w), 0.001);
		
		w.assign(-1, 0);
		assertEquals(180, angle(v, w), 0.001);
		
		w.assign(-1, -1);
		assertEquals(135, angle(v, w), 0.001);
		
		w.assign(0, -1);
		assertEquals(90, angle(v, w), 0.001);
		
		w.assign(1, -1);
		assertEquals(45, angle(v, w), 0.001);
	}

}
