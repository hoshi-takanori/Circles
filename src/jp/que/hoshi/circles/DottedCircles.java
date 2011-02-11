package jp.que.hoshi.circles;

import java.util.ArrayList;

import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

class Circle {
	int offset;
	float x;
	float y;
	float rad;
	float hue;
	int size;

	public Circle(int offset, float x, float y) {
		this.offset = offset;
		this.x = x;
		this.y = y;
		rad = (float) (Math.random() * Math.PI / 6);
		hue = (float) (Math.random() * 360);
		size = 0;
	}

	public static Circle create(int offset, int width, int height) {
		float x = (float) (Math.random() * width);
		float y = (float) (Math.random() * height);
		return new Circle(offset, x, y);
	}
}

public class DottedCircles {
	private final ArrayList<Circle> circles = new ArrayList<Circle>();

	private final Paint paint = new Paint();
	private final RectF rect = new RectF();
	private final float[] hsv = { 0, 1, 0 };

	private int offset;
	private int width;
	private int height;

	private int maxSize;
	private int partSize;

	public DottedCircles() {
		paint.setStyle(Paint.Style.FILL);
		paint.setMaskFilter(new BlurMaskFilter(2, BlurMaskFilter.Blur.NORMAL));
	}

	public void init(int width, int height) {
		this.width = width;
		this.height = height;

		if (width + height < 1000) {
			maxSize = 80;
			partSize = 2;
		} else {
			maxSize = 100;
			partSize = 3;
		}
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public void add(float x, float y) {
		circles.add(new Circle(offset, x, y));
	}

	public void tick() {
		for (int i = circles.size(); --i >= 0; ) {
			Circle circle = circles.get(i);
			circle.size++;
			if (circle.size >= maxSize) {
				circles.remove(i);
			}
		}

		if (Math.random() < 0.1) {
			circles.add(Circle.create(offset, width, height));
		}
	}

	public void draw(Canvas canvas) {
		canvas.drawColor(Color.BLACK);

		for (int i = 0; i < circles.size(); i++) {
			Circle circle = circles.get(i);

			hsv[0] = circle.hue;
			float s = (float) circle.size / maxSize;
			hsv[2] = 1 - s * s;
			paint.setColor(Color.HSVToColor(hsv));

			for (int j = 0; j < 12; j++) {
				double rad = Math.PI * j / 6 + circle.rad;
				float x = circle.x + (float) Math.cos(rad) * circle.size - circle.offset + offset;
				float y = circle.y + (float) Math.sin(rad) * circle.size;
				rect.set(x - partSize, y - partSize, x + partSize, y + partSize);
				canvas.drawOval(rect, paint);
				canvas.drawOval(rect, paint);
			}
		}
	}
}
