package io.github.jdiemke.triangulation;

import java.util.Arrays;

/**
 * 2D triangle class implementation.
 * 
 * @author Johannes Diemke
 */
public class Triangle2D {

    public Vector2D a;
    public Vector2D b;
    public Vector2D c;

    //we'll know which triangle has been morphed because q values will be different than normal values.
    //this allows us to always return q values for extended functions
    private Vector2D qA;
    private Vector2D qB;
    private Vector2D qC;

    /**
     * Constructor of the 2D triangle class used to create a new triangle
     * instance from three 2D vectors describing the triangle's vertices.
     * 
     * @param a
     *            The first vertex of the triangle
     * @param b
     *            The second vertex of the triangle
     * @param c
     *            The third vertex of the triangle
     */
    public Triangle2D(Vector2D a, Vector2D b, Vector2D c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    /**
     * Tests if a 2D point lies inside this 2D triangle. See Real-Time Collision
     * Detection, chap. 5, p. 206.
     * 
     * @param point
     *            The point to be tested
     * @return Returns true iff the point lies inside this 2D triangle
     */
    public boolean contains(Vector2D point) {
        double pab = point.sub(a).cross(b.sub(a));
        double pbc = point.sub(b).cross(c.sub(b));

        if (!hasSameSign(pab, pbc)) {
            return false;
        }

        double pca = point.sub(c).cross(a.sub(c));

        if (!hasSameSign(pab, pca)) {
            return false;
        }

        return true;
    }

    /**
     * Tests if a given point lies in the circumcircle of this triangle. Let the
     * triangle ABC appear in counterclockwise (CCW) order. Then when det &gt;
     * 0, the point lies inside the circumcircle through the three points a, b
     * and c. If instead det &lt; 0, the point lies outside the circumcircle.
     * When det = 0, the four points are cocircular. If the triangle is oriented
     * clockwise (CW) the result is reversed. See Real-Time Collision Detection,
     * chap. 3, p. 34.
     * 
     * @param point
     *            The point to be tested
     * @return Returns true iff the point lies inside the circumcircle through
     *         the three points a, b, and c of the triangle
     */
    public boolean isPointInCircumcircle(Vector2D point) {
        double a11 = a.x - point.x;
        double a21 = b.x - point.x;
        double a31 = c.x - point.x;

        double a12 = a.y - point.y;
        double a22 = b.y - point.y;
        double a32 = c.y - point.y;

        double a13 = (a.x - point.x) * (a.x - point.x) + (a.y - point.y) * (a.y - point.y);
        double a23 = (b.x - point.x) * (b.x - point.x) + (b.y - point.y) * (b.y - point.y);
        double a33 = (c.x - point.x) * (c.x - point.x) + (c.y - point.y) * (c.y - point.y);

        double det = a11 * a22 * a33 + a12 * a23 * a31 + a13 * a21 * a32 - a13 * a22 * a31 - a12 * a21 * a33
                - a11 * a23 * a32;

        if (isOrientedCCW()) {
            return det > 0.0d;
        }

        return det < 0.0d;
    }

    /**
     * Test if this triangle is oriented counterclockwise (CCW). Let A, B and C
     * be three 2D points. If det &gt; 0, C lies to the left of the directed
     * line AB. Equivalently the triangle ABC is oriented counterclockwise. When
     * det &lt; 0, C lies to the right of the directed line AB, and the triangle
     * ABC is oriented clockwise. When det = 0, the three points are colinear.
     * See Real-Time Collision Detection, chap. 3, p. 32
     * 
     * @return Returns true iff the triangle ABC is oriented counterclockwise
     *         (CCW)
     */
    public boolean isOrientedCCW() {
        double a11 = a.x - c.x;
        double a21 = b.x - c.x;

        double a12 = a.y - c.y;
        double a22 = b.y - c.y;

        double det = a11 * a22 - a12 * a21;

        return det > 0.0d;
    }

    /**
     * Returns true if this triangle contains the given edge.
     * 
     * @param edge
     *            The edge to be tested
     * @return Returns true if this triangle contains the edge
     */
    public boolean isNeighbour(Edge2D edge) {
        return (a == edge.a || b == edge.a || c == edge.a) && (a == edge.b || b == edge.b || c == edge.b);
    }

    /**
     * Returns the vertex of this triangle that is not part of the given edge.
     * 
     * @param edge
     *            The edge
     * @return The vertex of this triangle that is not part of the edge
     */
    public Vector2D getNoneEdgeVertex(Edge2D edge) {
        if (a != edge.a && a != edge.b) {
            return a;
        } else if (b != edge.a && b != edge.b) {
            return b;
        } else if (c != edge.a && c != edge.b) {
            return c;
        }

        return null;
    }

    /**
     * Returns true if the given vertex is one of the vertices describing this
     * triangle.
     * 
     * @param vertex
     *            The vertex to be tested
     * @return Returns true if the Vertex is one of the vertices describing this
     *         triangle
     */
    public boolean hasVertex(Vector2D vertex) {
        if (a == vertex || b == vertex || c == vertex) {
            return true;
        }

        return false;
    }

    /**
     * Returns an EdgeDistancePack containing the edge and its distance nearest
     * to the specified point.
     * 
     * @param point
     *            The point the nearest edge is queried for
     * @return The edge of this triangle that is nearest to the specified point
     */
    public EdgeDistancePack findNearestEdge(Vector2D point) {
        EdgeDistancePack[] edges = new EdgeDistancePack[3];

        edges[0] = new EdgeDistancePack(new Edge2D(a, b),
                computeClosestPoint(new Edge2D(a, b), point).sub(point).mag());
        edges[1] = new EdgeDistancePack(new Edge2D(b, c),
                computeClosestPoint(new Edge2D(b, c), point).sub(point).mag());
        edges[2] = new EdgeDistancePack(new Edge2D(c, a),
                computeClosestPoint(new Edge2D(c, a), point).sub(point).mag());

        Arrays.sort(edges);
        return edges[0];
    }

    public void computeClosestPointsToAEdge(Edge2D edge) {
        Vector2D tempqA = computeClosestPoint(edge,a);
        Vector2D tempqB = computeClosestPoint(edge,b);
        Vector2D tempqC = computeClosestPoint(edge,c);

        //TODO perform the moving operation while still retaining original values of a,b,c
        double potentialDistance = 10000.0;
        double magnitue;

        Vector2D potentialPoint = null;

        //Has A been used?
        if (qA == null || qA == a) {
            //not used yet, so find its mag and see if it will work
            magnitue = tempqA.sub(a).mag();
            if(magnitue < potentialDistance){
                potentialDistance = magnitue;
                potentialPoint = a;
            }
        }

        if (qB == null || qB == b) {
            magnitue = tempqB.sub(b).mag();
            if (magnitue < potentialDistance) {
                potentialDistance = magnitue;
                potentialPoint = b;

            }
        }

        if (qC == null || qB == c) {
            magnitue = tempqC.sub(c).mag();
            if (magnitue < potentialDistance) {
                potentialPoint = c;
            }
        }

        //By now we should know which point to morph
        if(a == potentialPoint) {qA = tempqA;}
        else if(b == potentialPoint) {qB = tempqB;}
        else if(c == potentialPoint) {qC = tempqC;}

    }

    public boolean hasMorphed(){
        return (qA != null && qA != a) || (qB != null && qB != b ) || (qC != null && qC != c);
    }

    public Vector2D qA() {
        if (qA != null ) {
            return qA;
        }else {
            return a;
        }
    }

    public Vector2D qB() {
        if (qB != null) {
            return qB;
        }else {
            return b;
        }
    }

    public Vector2D qC() {
        if (qC != null) {
            return qC;
        } else {
            return c;
        }
    }


    /**
     * Computes the closest point on the given edge to the specified point.
     * 
     * @param edge
     *            The edge on which we search the closest point to the specified
     *            point
     * @param point
     *            The point to which we search the closest point on the edge
     * @return The closest point on the given edge to the specified point
     */
    public Vector2D computeClosestPoint(Edge2D edge, Vector2D point) {
        Vector2D ab = edge.b.sub(edge.a);
        double t = point.sub(edge.a).dot(ab) / ab.dot(ab);

        if (t < 0.0d) {
            t = 0.0d;
        } else if (t > 1.0d) {
            t = 1.0d;
        }

        return edge.a.add(ab.mult(t));
    }
    /** Check whether P and Q lie on the same side of line AB */
    /*private fun Side(p: Vector2D, q: Vector2D, a: Vector2D, b: Vector2D): Float {
        val z1 = (b.x - a.x) * (p.y - a.y) - (p.x - a.x) * (b.y - a.y)
        val z2 = (b.x - a.x) * (q.y - a.y) - (q.x - a.x) * (b.y - a.y)
        return (z1 * z2).toFloat()
    }*/


/*
    public int isIntersecting(p0: Vector2D, p1: Vector2D, t0: Vector2D, t1: Vector2D,  Vector2D) {
        */
/* Check whether segment is outside one of the three half-planes
         * delimited by the triangle. *//*

        val f1 = Side(p0, t2, t0, t1)
        val f2 = Side(p1, t2, t0, t1)
        val f3 = Side(p0, t0, t1, t2)
        val f4 = Side(p1, t0, t1, t2)
        val f5 = Side(p0, t1, t2, t0)
        val f6 = Side(p1, t1, t2, t0)
        */
/* Check whether triangle is totally inside one of the two half-planes
         * delimited by the segment. *//*

        val f7 = Side(t0, t1, p0, p1)
        val f8 = Side(t1, t2, p0, p1)

        */
/* If segment is strictly outside triangle, or triangle is strictly
         * apart from the line, we're not intersecting *//*

        if (f1 < 0 && f2 < 0 || f3 < 0 && f4 < 0 || f5 < 0 && f6 < 0
                || f7 > 0 && f8 > 0)
            return NOT_INTERSECTING

        */
/* If segment is aligned with one of the edges, we're overlapping *//*

        if (f1 == 0f && f2 == 0f || f3 == 0f && f4 == 0f || f5 == 0f && f6 == 0f)
            return OVERLAPPING

        */
/* If segment is outside but not strictly, or triangle is apart but
         * not strictly, we're touching *//*

        if (f1 <= 0 && f2 <= 0 || f3 <= 0 && f4 <= 0 || f5 <= 0 && f6 <= 0
                || f7 >= 0 && f8 >= 0)
            return TOUCHING

        */
/* If both segment points are strictly inside the triangle, we
         * are not intersecting either *//*

        return if (f1 > 0 && f2 > 0 && f3 > 0 && f4 > 0 && f5 > 0 && f6 > 0) NOT_INTERSECTING else INTERSECTING

        */
/* Otherwise we're intersecting with at least one edge *//*

    }
*/




    /**
     * Tests if the two arguments have the same sign.
     * 
     * @param a
     *            The first floating point argument
     * @param b
     *            The second floating point argument
     * @return Returns true iff both arguments have the same sign
     */
    private boolean hasSameSign(double a, double b) {
        return Math.signum(a) == Math.signum(b);
    }

    @Override
    public String toString() {
        return "Triangle2D[" + a + ", " + b + ", " + c + "]";
    }

}