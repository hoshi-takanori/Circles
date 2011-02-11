package jp.que.hoshi.circles;

import android.graphics.Canvas;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

public class CirclesWallpaper extends WallpaperService {
	private final Handler mHandler = new Handler();

	public Engine onCreateEngine() {
		return new CirclesWallpaperEngine();
	}

	public class CirclesWallpaperEngine extends Engine {
		private final DottedCircles circles = new DottedCircles();

		private final Runnable mRunnable = new Runnable() {
			public void run() {
				circles.tick();
				animate();
			}
		};

		private boolean mVisible = false;

		private long tapTime;
		private float tapX;
		private float tapY;

		public void onCreate (SurfaceHolder holder) {
			super.onCreate(holder);
			setTouchEventsEnabled(true);
		}

		public void onDestroy() {
			super.onDestroy();
			mHandler.removeCallbacks(mRunnable);
		}

		public void onVisibilityChanged(boolean visible) {
			mVisible = visible;
			animate();
		}

		public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
			super.onSurfaceChanged(holder, format, width, height);
			circles.init(width, height);
			animate();
		}

		public void onSurfaceDestroyed(SurfaceHolder holder) {
			super.onSurfaceDestroyed(holder);
			mVisible = false;
			animate();
		}

		public void onOffsetsChanged(float xOffset, float yOffset, float xStep, float yStep, int xPixels, int yPixels) {
			if (xPixels != circles.getOffset()) {
				circles.setOffset(xPixels);
				if (mVisible) {
					drawFrame();
				}
			}
		}

		public void onTouchEvent(MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				tapTime = event.getEventTime();
				tapX = event.getX();
				tapY = event.getY();
			} else if (event.getAction() == MotionEvent.ACTION_UP &&
					event.getEventTime() - tapTime < 250 &&
					Math.abs(event.getX() - tapX) < 4 &&
					Math.abs(event.getY() - tapY) < 4) {
				circles.add(tapX, tapY);
			}
		}

		private void animate() {
			mHandler.removeCallbacks(mRunnable);
			if (mVisible) {
				drawFrame();
				mHandler.postDelayed(mRunnable, 1000 / 30);
			}
		}

		private void drawFrame() {
			SurfaceHolder holder = getSurfaceHolder();
			Canvas canvas = null;
			try {
				canvas = holder.lockCanvas();
				if (canvas != null) {
					circles.draw(canvas);
				}
			} finally {
				if (canvas != null) {
					holder.unlockCanvasAndPost(canvas);
				}
			}
		}
	}
}
