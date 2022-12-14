package com.github.gamepiaynmo.custommodel.util;

import net.minecraft.util.math.Vec3d;

public class Vector3 {
    /** the x-component of this vector **/
    public double x;
    /** the y-component of this vector **/
    public double y;
    /** the z-component of this vector **/
    public double z;

    public final static Vector3 X = new Vector3(1, 0, 0);
    public final static Vector3 Y = new Vector3(0, 1, 0);
    public final static Vector3 Z = new Vector3(0, 0, 1);
    public final static Vector3 Zero = new Vector3(0, 0, 0);

    private final static Matrix4 tmpMat = new Matrix4();

    /** Constructs a vector at (0,0,0) */
    public Vector3 () {
    }

    /** Creates a vector with the given components
     * @param x The x-component
     * @param y The y-component
     * @param z The z-component */
    public Vector3 (double x, double y, double z) {
        this.set(x, y, z);
    }

    /** Creates a vector from the given vector
     * @param vector The vector */
    public Vector3 (final Vector3 vector) {
        this.set(vector);
    }

    /** Creates a vector from the given vector
     * @param vector The vector */
    public Vector3 (final Vec3d vector) {
        this.set(vector.x, vector.y, vector.z);
    }

    /** Creates a vector from the given array. The array must have at least 3 elements.
     *
     * @param values The array */
    public Vector3 (final double[] values) {
        this.set(values[0], values[1], values[2]);
    }

    /** Sets the vector to the given components
     *
     * @param x The x-component
     * @param y The y-component
     * @param z The z-component
     * @return this vector for chaining */
    public Vector3 set (double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    public Vector3 set (final Vector3 vector) {
        return this.set(vector.x, vector.y, vector.z);
    }

    /** Sets the components from the array. The array must have at least 3 elements
     *
     * @param values The array
     * @return this vector for chaining */
    public Vector3 set (final double[] values) {
        return this.set(values[0], values[1], values[2]);
    }

    public Vector3 cpy () {
        return new Vector3(this);
    }

    public Vector3 add (final Vector3 vector) {
        return this.add(vector.x, vector.y, vector.z);
    }

    /** Adds the given vector to this component
     * @param x The x-component of the other vector
     * @param y The y-component of the other vector
     * @param z The z-component of the other vector
     * @return This vector for chaining. */
    public Vector3 add (double x, double y, double z) {
        return this.set(this.x + x, this.y + y, this.z + z);
    }

    /** Adds the given value to all three components of the vector.
     *
     * @param values The value
     * @return This vector for chaining */
    public Vector3 add (double values) {
        return this.set(this.x + values, this.y + values, this.z + values);
    }

    public Vector3 sub (final Vector3 a_vec) {
        return this.sub(a_vec.x, a_vec.y, a_vec.z);
    }

    /** Subtracts the other vector from this vector.
     *
     * @param x The x-component of the other vector
     * @param y The y-component of the other vector
     * @param z The z-component of the other vector
     * @return This vector for chaining */
    public Vector3 sub (double x, double y, double z) {
        return this.set(this.x - x, this.y - y, this.z - z);
    }

    /** Subtracts the given value from all components of this vector
     *
     * @param value The value
     * @return This vector for chaining */
    public Vector3 sub (double value) {
        return this.set(this.x - value, this.y - value, this.z - value);
    }

    public Vector3 scl (double scalar) {
        return this.set(this.x * scalar, this.y * scalar, this.z * scalar);
    }

    public Vector3 scl (final Vector3 other) {
        return this.set(x * other.x, y * other.y, z * other.z);
    }

    /** Scales this vector by the given values
     * @param vx X value
     * @param vy Y value
     * @param vz Z value
     * @return This vector for chaining */
    public Vector3 scl (double vx, double vy, double vz) {
        return this.set(this.x * vx, this.y * vy, this.z * vz);
    }

    public Vector3 mulAdd (Vector3 vec, double scalar) {
        this.x += vec.x * scalar;
        this.y += vec.y * scalar;
        this.z += vec.z * scalar;
        return this;
    }

    public Vector3 mulAdd (Vector3 vec, Vector3 mulVec) {
        this.x += vec.x * mulVec.x;
        this.y += vec.y * mulVec.y;
        this.z += vec.z * mulVec.z;
        return this;
    }

    /** @return The euclidean length */
    public static double len (final double x, final double y, final double z) {
        return Math.sqrt(x * x + y * y + z * z);
    }

    public double len () {
        return Math.sqrt(x * x + y * y + z * z);
    }

    /** @return The squared euclidean length */
    public static double len2 (final double x, final double y, final double z) {
        return x * x + y * y + z * z;
    }

    public double len2 () {
        return x * x + y * y + z * z;
    }

    /** @param vector The other vector
     * @return Whether this and the other vector are equal */
    public boolean idt (final Vector3 vector) {
        return x == vector.x && y == vector.y && z == vector.z;
    }

    /** @return The euclidean distance between the two specified vectors */
    public static double dst (final double x1, final double y1, final double z1, final double x2, final double y2, final double z2) {
        final double a = x2 - x1;
        final double b = y2 - y1;
        final double c = z2 - z1;
        return Math.sqrt(a * a + b * b + c * c);
    }

    public double dst (final Vector3 vector) {
        final double a = vector.x - x;
        final double b = vector.y - y;
        final double c = vector.z - z;
        return Math.sqrt(a * a + b * b + c * c);
    }

    /** @return the distance between this point and the given point */
    public double dst (double x, double y, double z) {
        final double a = x - this.x;
        final double b = y - this.y;
        final double c = z - this.z;
        return Math.sqrt(a * a + b * b + c * c);
    }

    /** @return the squared distance between the given points */
    public static double dst2 (final double x1, final double y1, final double z1, final double x2, final double y2, final double z2) {
        final double a = x2 - x1;
        final double b = y2 - y1;
        final double c = z2 - z1;
        return a * a + b * b + c * c;
    }

    public double dst2 (Vector3 point) {
        final double a = point.x - x;
        final double b = point.y - y;
        final double c = point.z - z;
        return a * a + b * b + c * c;
    }

    /** Returns the squared distance between this point and the given point
     * @param x The x-component of the other point
     * @param y The y-component of the other point
     * @param z The z-component of the other point
     * @return The squared distance */
    public double dst2 (double x, double y, double z) {
        final double a = x - this.x;
        final double b = y - this.y;
        final double c = z - this.z;
        return a * a + b * b + c * c;
    }

    public Vector3 nor () {
        final double len2 = this.len2();
        if (len2 == 0f || len2 == 1f) return this;
        return this.scl(1f / Math.sqrt(len2));
    }

    /** @return The dot product between the two vectors */
    public static double dot (double x1, double y1, double z1, double x2, double y2, double z2) {
        return x1 * x2 + y1 * y2 + z1 * z2;
    }

    public double dot (final Vector3 vector) {
        return x * vector.x + y * vector.y + z * vector.z;
    }

    /** Returns the dot product between this and the given vector.
     * @param x The x-component of the other vector
     * @param y The y-component of the other vector
     * @param z The z-component of the other vector
     * @return The dot product */
    public double dot (double x, double y, double z) {
        return this.x * x + this.y * y + this.z * z;
    }

    /** Sets this vector to the cross product between it and the other vector.
     * @param vector The other vector
     * @return This vector for chaining */
    public Vector3 crs (final Vector3 vector) {
        return this.set(y * vector.z - z * vector.y, z * vector.x - x * vector.z, x * vector.y - y * vector.x);
    }

    /** Sets this vector to the cross product between it and the other vector.
     * @param x The x-component of the other vector
     * @param y The y-component of the other vector
     * @param z The z-component of the other vector
     * @return This vector for chaining */
    public Vector3 crs (double x, double y, double z) {
        return this.set(this.y * z - this.z * y, this.z * x - this.x * z, this.x * y - this.y * x);
    }

    /** Left-multiplies the vector by the given 4x3 column major matrix. The matrix should be composed by a 3x3 matrix representing
     * rotation and scale plus a 1x3 matrix representing the translation.
     * @param matrix The matrix
     * @return This vector for chaining */
    public Vector3 mul4x3 (double[] matrix) {
        return set(x * matrix[0] + y * matrix[3] + z * matrix[6] + matrix[9], x * matrix[1] + y * matrix[4] + z * matrix[7]
                + matrix[10], x * matrix[2] + y * matrix[5] + z * matrix[8] + matrix[11]);
    }

    /** Left-multiplies the vector by the given matrix, assuming the fourth (w) component of the vector is 1.
     * @param matrix The matrix
     * @return This vector for chaining */
    public Vector3 mul (final Matrix4 matrix) {
        final double[] l_mat = matrix.val;
        return this.set(x * l_mat[Matrix4.M00] + y * l_mat[Matrix4.M01] + z * l_mat[Matrix4.M02] + l_mat[Matrix4.M03], x
                * l_mat[Matrix4.M10] + y * l_mat[Matrix4.M11] + z * l_mat[Matrix4.M12] + l_mat[Matrix4.M13], x * l_mat[Matrix4.M20] + y
                * l_mat[Matrix4.M21] + z * l_mat[Matrix4.M22] + l_mat[Matrix4.M23]);
    }

    /** Multiplies the vector by the transpose of the given matrix, assuming the fourth (w) component of the vector is 1.
     * @param matrix The matrix
     * @return This vector for chaining */
    public Vector3 traMul (final Matrix4 matrix) {
        final double[] l_mat = matrix.val;
        return this.set(x * l_mat[Matrix4.M00] + y * l_mat[Matrix4.M10] + z * l_mat[Matrix4.M20] + l_mat[Matrix4.M30], x
                * l_mat[Matrix4.M01] + y * l_mat[Matrix4.M11] + z * l_mat[Matrix4.M21] + l_mat[Matrix4.M31], x * l_mat[Matrix4.M02] + y
                * l_mat[Matrix4.M12] + z * l_mat[Matrix4.M22] + l_mat[Matrix4.M32]);
    }

    /** Multiplies the vector by the given {@link Quaternion}.
     * @return This vector for chaining */
    public Vector3 mul (final Quaternion quat) {
        return quat.transform(this);
    }

    /** Multiplies this vector by the given matrix dividing by w, assuming the fourth (w) component of the vector is 1. This is
     * mostly used to project/unproject vectors via a perspective projection matrix.
     *
     * @param matrix The matrix.
     * @return This vector for chaining */
    public Vector3 prj (final Matrix4 matrix) {
        final double[] l_mat = matrix.val;
        final double l_w = 1f / (x * l_mat[Matrix4.M30] + y * l_mat[Matrix4.M31] + z * l_mat[Matrix4.M32] + l_mat[Matrix4.M33]);
        return this.set((x * l_mat[Matrix4.M00] + y * l_mat[Matrix4.M01] + z * l_mat[Matrix4.M02] + l_mat[Matrix4.M03]) * l_w, (x
                * l_mat[Matrix4.M10] + y * l_mat[Matrix4.M11] + z * l_mat[Matrix4.M12] + l_mat[Matrix4.M13])
                * l_w, (x * l_mat[Matrix4.M20] + y * l_mat[Matrix4.M21] + z * l_mat[Matrix4.M22] + l_mat[Matrix4.M23]) * l_w);
    }

    /** Multiplies this vector by the first three columns of the matrix, essentially only applying rotation and scaling.
     *
     * @param matrix The matrix
     * @return This vector for chaining */
    public Vector3 rot (final Matrix4 matrix) {
        final double[] l_mat = matrix.val;
        return this.set(x * l_mat[Matrix4.M00] + y * l_mat[Matrix4.M01] + z * l_mat[Matrix4.M02], x * l_mat[Matrix4.M10] + y
                * l_mat[Matrix4.M11] + z * l_mat[Matrix4.M12], x * l_mat[Matrix4.M20] + y * l_mat[Matrix4.M21] + z * l_mat[Matrix4.M22]);
    }

    /** Multiplies this vector by the transpose of the first three columns of the matrix. Note: only works for translation and
     * rotation, does not work for scaling. For those, use {@link #rot(Matrix4)} with {@link Matrix4#inv()}.
     * @param matrix The transformation matrix
     * @return The vector for chaining */
    public Vector3 unrotate (final Matrix4 matrix) {
        final double[] l_mat = matrix.val;
        return this.set(x * l_mat[Matrix4.M00] + y * l_mat[Matrix4.M10] + z * l_mat[Matrix4.M20], x * l_mat[Matrix4.M01] + y
                * l_mat[Matrix4.M11] + z * l_mat[Matrix4.M21], x * l_mat[Matrix4.M02] + y * l_mat[Matrix4.M12] + z * l_mat[Matrix4.M22]);
    }

    /** Translates this vector in the direction opposite to the translation of the matrix and the multiplies this vector by the
     * transpose of the first three columns of the matrix. Note: only works for translation and rotation, does not work for
     * scaling. For those, use {@link #mul(Matrix4)} with {@link Matrix4#inv()}.
     * @param matrix The transformation matrix
     * @return The vector for chaining */
    public Vector3 untransform (final Matrix4 matrix) {
        final double[] l_mat = matrix.val;
        x -= l_mat[Matrix4.M03];
        y -= l_mat[Matrix4.M03];
        z -= l_mat[Matrix4.M03];
        return this.set(x * l_mat[Matrix4.M00] + y * l_mat[Matrix4.M10] + z * l_mat[Matrix4.M20], x * l_mat[Matrix4.M01] + y
                * l_mat[Matrix4.M11] + z * l_mat[Matrix4.M21], x * l_mat[Matrix4.M02] + y * l_mat[Matrix4.M12] + z * l_mat[Matrix4.M22]);
    }

    /** Rotates this vector by the given angle in degrees around the given axis.
     *
     * @param degrees the angle in degrees
     * @param axisX the x-component of the axis
     * @param axisY the y-component of the axis
     * @param axisZ the z-component of the axis
     * @return This vector for chaining */
    public Vector3 rotate (double degrees, double axisX, double axisY, double axisZ) {
        return this.mul(tmpMat.setToRotation(axisX, axisY, axisZ, degrees));
    }

    /** Rotates this vector by the given angle in radians around the given axis.
     *
     * @param radians the angle in radians
     * @param axisX the x-component of the axis
     * @param axisY the y-component of the axis
     * @param axisZ the z-component of the axis
     * @return This vector for chaining */
    public Vector3 rotateRad (double radians, double axisX, double axisY, double axisZ) {
        return this.mul(tmpMat.setToRotationRad(axisX, axisY, axisZ, radians));
    }

    /** Rotates this vector by the given angle in degrees around the given axis.
     *
     * @param axis the axis
     * @param degrees the angle in degrees
     * @return This vector for chaining */
    public Vector3 rotate (final Vector3 axis, double degrees) {
        tmpMat.setToRotation(axis, degrees);
        return this.mul(tmpMat);
    }

    /** Rotates this vector by the given angle in radians around the given axis.
     *
     * @param axis the axis
     * @param radians the angle in radians
     * @return This vector for chaining */
    public Vector3 rotateRad (final Vector3 axis, double radians) {
        tmpMat.setToRotationRad(axis, radians);
        return this.mul(tmpMat);
    }

    public boolean isUnit () {
        return isUnit(0.000000001f);
    }

    public boolean isUnit (final double margin) {
        return Math.abs(len2() - 1f) < margin;
    }

    public boolean isZero () {
        return x == 0 && y == 0 && z == 0;
    }

    public boolean isZero (final double margin) {
        return len2() < margin;
    }

    public boolean isOnLine (Vector3 other, double epsilon) {
        return len2(y * other.z - z * other.y, z * other.x - x * other.z, x * other.y - y * other.x) <= epsilon;
    }

    public boolean isCollinear (Vector3 other, double epsilon) {
        return isOnLine(other, epsilon) && hasSameDirection(other);
    }

    public boolean isCollinearOpposite (Vector3 other, double epsilon) {
        return isOnLine(other, epsilon) && hasOppositeDirection(other);
    }

    public boolean hasSameDirection (Vector3 vector) {
        return dot(vector) > 0;
    }

    public boolean hasOppositeDirection (Vector3 vector) {
        return dot(vector) < 0;
    }

    public Vector3 lerp (final Vector3 target, double alpha) {
        x += alpha * (target.x - x);
        y += alpha * (target.y - y);
        z += alpha * (target.z - z);
        return this;
    }

    /** Spherically interpolates between this vector and the target vector by alpha which is in the range [0,1]. The result is
     * stored in this vector.
     *
     * @param target The target vector
     * @param alpha The interpolation coefficient
     * @return This vector for chaining. */
    public Vector3 slerp (final Vector3 target, double alpha) {
        final double dot = dot(target);
        // If the inputs are too close for comfort, simply linearly interpolate.
        if (dot > 0.9995 || dot < -0.9995) return lerp(target, alpha);

        // theta0 = angle between input vectors
        final double theta0 = Math.acos(dot);
        // theta = angle between this vector and result
        final double theta = theta0 * alpha;

        final double st = Math.sin(theta);
        final double tx = target.x - x * dot;
        final double ty = target.y - y * dot;
        final double tz = target.z - z * dot;
        final double l2 = tx * tx + ty * ty + tz * tz;
        final double dl = st * ((l2 < 0.0001f) ? 1f : 1f / Math.sqrt(l2));

        return scl(Math.cos(theta)).add(tx * dl, ty * dl, tz * dl).nor();
    }

    /** Converts this {@code Vector3} to a string in the format {@code (x,y,z)}.
     * @return a string representation of this object. */
    public String toString () {
        return "(" + x + "," + y + "," + z + ")";
    }

    /** Sets this {@code Vector3} to the value represented by the specified string according to the format of {@link #toString()}.
     * @param v the string.
     * @return this vector for chaining */
    public Vector3 fromString (String v) {
        int s0 = v.indexOf(',', 1);
        int s1 = v.indexOf(',', s0 + 1);
        if (s0 != -1 && s1 != -1 && v.charAt(0) == '(' && v.charAt(v.length() - 1) == ')') {
            try {
                double x = Double.parseDouble(v.substring(1, s0));
                double y = Double.parseDouble(v.substring(s0 + 1, s1));
                double z = Double.parseDouble(v.substring(s1 + 1, v.length() - 1));
                return this.set(x, y, z);
            } catch (NumberFormatException ex) {
                // Throw a GdxRuntimeException
            }
        }
        return Zero;
    }

    public Vector3 limit (double limit) {
        return limit2(limit * limit);
    }

    public Vector3 limit2 (double limit2) {
        double len2 = len2();
        if (len2 > limit2) {
            scl(Math.sqrt(limit2 / len2));
        }
        return this;
    }

    public Vector3 setLength (double len) {
        return setLength2(len * len);
    }

    public Vector3 setLength2 (double len2) {
        double oldLen2 = len2();
        return (oldLen2 == 0 || oldLen2 == len2) ? this : scl(Math.sqrt(len2 / oldLen2));
    }

    public Vector3 clamp (double min, double max) {
        final double len2 = len2();
        if (len2 == 0f) return this;
        double max2 = max * max;
        if (len2 > max2) return scl(Math.sqrt(max2 / len2));
        double min2 = min * min;
        if (len2 < min2) return scl(Math.sqrt(min2 / len2));
        return this;
    }

    public boolean epsilonEquals (final Vector3 other, double epsilon) {
        if (other == null) return false;
        if (Math.abs(other.x - x) > epsilon) return false;
        if (Math.abs(other.y - y) > epsilon) return false;
        return !(Math.abs(other.z - z) > epsilon);
    }

    /** Compares this vector with the other vector, using the supplied epsilon for fuzzy equality testing.
     * @return whether the vectors are the same. */
    public boolean epsilonEquals (double x, double y, double z, double epsilon) {
        if (Math.abs(x - this.x) > epsilon) return false;
        if (Math.abs(y - this.y) > epsilon) return false;
        return !(Math.abs(z - this.z) > epsilon);
    }

    public Vector3 setZero () {
        this.x = 0;
        this.y = 0;
        this.z = 0;
        return this;
    }
}