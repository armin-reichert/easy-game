package de.amr.easy.game.math.test;

import static de.amr.easy.game.math.Vector2f.angle;
import static de.amr.easy.game.math.Vector2f.diff;
import static de.amr.easy.game.math.Vector2f.dist;
import static de.amr.easy.game.math.Vector2f.dot;
import static de.amr.easy.game.math.Vector2f.inverse;
import static de.amr.easy.game.math.Vector2f.sum;
import static de.amr.easy.game.math.Vector2f.smul;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.amr.easy.game.math.Vector2f;

public class Vector2Test {

	private Vector2f v, w, x;

	@Before
	public void setUp() {
		v = Vector2f.of(5, -10);
		w = Vector2f.of(-3, 4);
		x = Vector2f.of(1, 1);
	}

	@Test
	public void testEquals() {
		assertTrue(v.equals(Vector2f.of(v.x, v.y)));
	}

	@Test
	public void testSum() {
		Vector2f sum = sum(v, w);
		assertTrue(sum.x == 2);
		assertTrue(sum.y == -6);
	}

	@Test
	public void testDiff() {
		Vector2f diff = diff(v, w);
		assertTrue(diff.x == 8);
		assertTrue(diff.y == -14);
		assertTrue(diff.equals(sum(v, inverse(w))));
	}

	@Test
	public void testNullVector() {
		assertTrue(Vector2f.NULL.x == 0);
		assertTrue(Vector2f.NULL.y == 0);
		assertTrue(sum(Vector2f.NULL, v).equals(v));
		assertTrue(sum(v, Vector2f.NULL).equals(v));
	}

	@Test
	public void testInverse() {
		Vector2f inverse = inverse(v);
		assertTrue(inverse.x == -5);
		assertTrue(inverse.y == 10);
		assertTrue(Vector2f.NULL.equals(sum(v, inverse)));
		assertTrue(Vector2f.NULL.equals(sum(inverse, v)));
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
		Vector2f product = smul(1.5f, v);
		assertTrue(product.x == 7.5f);
		assertTrue(product.y == -15);

		// 3 * (2 * v) = (3 * 2) * v
		assertTrue(smul(3, smul(2, v)).equals(smul(3 * 2, v)));
		// (3 + 2) * v = 3 * v + 2 * v
		assertTrue(smul(3 + 2, v).equals(sum(smul(3, v), smul(2, v))));
		// 3 * (v + w) = 3 * v + 3 * w
		assertTrue(smul(3, sum(v, w)).equals(sum(smul(3, v), smul(3, w))));
		// 1 * v = v
		assertTrue(smul(1f, v).equals(v));
	}

	@Test
	public void testScalarProduct() {
		float product = dot(v, w);
		assertTrue(product == -55);

		Vector2f e1 = Vector2f.of(1, 0);
		Vector2f e2 = Vector2f.of(0, 1);
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
		assertTrue(Vector2f.NULL.length() == 0);
		assertTrue(v.length() == inverse(v).length());
		assertTrue(v.length() + w.length() >= sum(v, w).length());
		assertTrue(v.length() >= 0);
		assertTrue(smul(2, v).length() == 2 * v.length());
		assertTrue(diff(v, v).length() == 0);
	}

	@Test
	public void testAngle() {
		Vector2f v = Vector2f.of(1, 0), w;

		w = Vector2f.of(1, 1);
		assertEquals(45, angle(v, x), 0.001);

		w = Vector2f.of(0, 1);
		assertEquals(90, angle(v, w), 0.001);

		w = Vector2f.of(-1, 1);
		assertEquals(135, angle(v, w), 0.001);

		w = Vector2f.of(-1, 0);
		assertEquals(180, angle(v, w), 0.001);

		w = Vector2f.of(-1, -1);
		assertEquals(135, angle(v, w), 0.001);

		w = Vector2f.of(0, -1);
		assertEquals(90, angle(v, w), 0.001);

		w = Vector2f.of(1, -1);
		assertEquals(45, angle(v, w), 0.001);
	}

}
