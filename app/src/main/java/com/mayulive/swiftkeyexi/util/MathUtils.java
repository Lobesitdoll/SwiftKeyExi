package com.mayulive.swiftkeyexi.util;

import android.graphics.Point;
import android.graphics.PointF;
import android.util.Log;

import java.util.Vector;

/**
 * Created by Roughy on 5/22/2017.
 */

public class MathUtils
{
	//Stolen from https://gist.github.com/aslakhellesoy/1134482#gistcomment-44491
	public static float roundToNearestMultiple(float number, float multiple)
	{
		return (number % multiple) > (multiple/2f) ? number + multiple - number % multiple : number - number % multiple;
	}

	/*
		Ratio is 0.5 for rounding up or down at halfways.
		0 will always round down, 1 will always round up
	 */
	public static float roundToNearestMultiple(float number, float multiple, float weight)
	{
		return (number % multiple) > (multiple * (1f-weight) ) ? number + multiple - number % multiple : number - number % multiple;
	}

	public static float roundUpToNearestMultiple(float number, float multiple)
	{
		return  number + multiple - number % multiple;
	}

	public static class Vector2D extends PointF
	{
		public Vector2D(float x, float y)
		{
			this.set(x,y);
		}

		public Vector2D(double x, double y)
		{
			this.set((float)x,(float)y);
		}

		public Vector2D(PointF other)
		{
			this.set(other.x, other.y);
		}

		public Vector2D addInPlace(PointF other)
		{
			this.x += other.x;
			this.y += other.y;
			return this;
		}

		public static Vector2D subtract(PointF amount, PointF from)
		{
			return new Vector2D(from).subtractInPlace(amount);
		}

		public static Vector2D add(PointF amount, PointF from)
		{
			return new Vector2D(from).addInPlace(amount);
		}

		public Vector2D subtractInPlace(PointF other)
		{
			this.x -= other.x;
			this.y -= other.y;
			return this;
		}

		public Vector2D multiplyInPlace(PointF other)
		{
			this.x *= other.x;
			this.y *= other.y;
			return this;
		}

		public Vector2D multiplyInPlace(float other)
		{
			this.x *= other;
			this.y *= other;
			return this;
		}

		public Vector2D divideInPlace(PointF other)
		{
			this.x /= other.x;
			this.y /= other.y;
			return this;
		}

		public Vector2D divideInPlace(float other)
		{
			this.x /= other;
			this.y /= other;
			return this;
		}

		public Vector2D add(PointF other)
		{
			return new Vector2D(other).addInPlace(this);
		}

		public Vector2D subtract(PointF other)
		{
			return new Vector2D(other).subtractInPlace(this);
		}


		public Vector2D multiply(PointF other)
		{
			return new Vector2D(other).multiplyInPlace(this);
		}

		public Vector2D multiply(float other)
		{
			return new Vector2D(this).multiplyInPlace(other);
		}

		public Vector2D divide(PointF other)
		{
			return new Vector2D(this).divideInPlace(other);
		}

		public Vector2D divide(float other)
		{
			return new Vector2D(this).divideInPlace(other);
		}

		@Override
		public String toString()
		{
			return "X: "+this.x+", Y: "+this.y;
		}
	}


	public static Vector2D AngleToVector(float angle)
	{
		return new Vector2D(Math.cos(angle), Math.sin(angle));
	}


	public static float getCircleCircumference(float radius)
	{
		return 2f * (float)Math.PI * radius;


	}



}