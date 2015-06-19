package com.hellobird.circleseekbar;

import java.text.DecimalFormat;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import com.hellobird.circleseekbar.util.DimenUtils;

public class CircleSeekBar extends View {

	/* 最小宽度，单位为dp */
	private static int MIN_WIDTH = 50;

	/* 最小高度，单位为dp */
	private static int MIN_HEIGHT = 50;

	/* 默认模式 */
	public static int MODE_DEFAULT = 0;
	/* 笔画模式 */
	public static int MODE_STROKE = 0;
	/* 填充模式 */
	public static int MODE_FILL = 1;
	/* 笔画&填充模式 */
	public static int MODE_FILL_AND_STROKE = 2;

	/* 进度格式化默认值 */
	private static String PROGRESS_FORMAT_DEFAULT = "##0.0";

	/* 进度默认最大值 */
	private static float MAX_PROGRESS_DEFAULT = 100f;

	/* 开始位置角度默认值 */
	private static final float START_ANGLE_DEFAULT = 0f;

	/* 刷新滑动速度默认值 */
	private static final float VELOCITY_DEFAULT = 3.0f;

	/* 文字大小默认值,单位为sp */
	private static final float TEXT_SIZE_DEFAULT = 10.0f;

	/* 默认文字颜色 */
	private static final int TEXT_COLOR_DEFAULT = 0xffbf5252;

	/* 进度条边框宽度默认值,单位为dp */
	private static final float PROGRESS_WIDTH_DEFAULT = 5.0f;

	/* 默认进度颜色 */
	private static final int PROGRESS_COLOR_DEFAULT = 0xff3d85c6;

	/* 进度条底色默认值，单位为dp */
	private static final float S_PROGRESS_WIDTH_DEFAULT = 2.0f;

	/* 默认进度颜色 */
	private static final int S_PROGRESS_COLOR_DEFAULT = 0xffdddddd;

	private Context mContext;
	private Paint mPaint;
	private Paint mTextPaint;
	private Paint mProgressPaint;
	private Paint mSProgressPaint;

	private int mMode; // 进度模式
	private float mMaxProgress; // 最大进度
	private boolean mShowText; // 是否显示文字
	private float mStartAngle; // 起始角度
	private float mVelocity; // 速度
	private float mTextSize; // 字体大小
	private int mTextColor; // 字体颜色
	private float mProgressStrokeWidth; // 进度条宽度
	private int mProgressColor; // 进度颜色
	private float mSProgressStrokeWidth; // 二级进度宽度
	private int mSProgressColor; // 二级进度颜色
	private boolean mFadeEnable; // 是否开启淡入淡出效果
	private int mStartAlpha; // 开始透明度,0~255
	private int mEndAlpha; // 结束透明度,0~255
	private boolean mZoomEnable; // 二级进度缩放
	private boolean mCapRound; // 进度条首尾是否圆角

	private RectF mProgressRect;
	private RectF mSProgressRect;
	private Rect mTextBounds;

	private float mCurrentAngle; // 当前角度
	private float mTargetAngle; // 目标角度
	private boolean mUseCenter; // 是否从中心绘制
	private DecimalFormat mFormat; // 格式化数值

	public CircleSeekBar(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CircleSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		mContext = context;
		init(attrs);
	}

	private void init(AttributeSet attrs) {
		if (attrs != null) {
			TypedArray type = mContext.obtainStyledAttributes(attrs,
					R.styleable.CircleSeekBar);
			mMode = type.getInt(R.styleable.CircleSeekBar_mode, MODE_DEFAULT);
			mMaxProgress = type.getFloat(R.styleable.CircleSeekBar_maxProgress,
					MAX_PROGRESS_DEFAULT);
			mShowText = type.getBoolean(R.styleable.CircleSeekBar_showText,
					true);
			mStartAngle = type.getFloat(R.styleable.CircleSeekBar_startAngle,
					START_ANGLE_DEFAULT);
			mVelocity = type.getFloat(R.styleable.CircleSeekBar_velocity,
					VELOCITY_DEFAULT);
			mTextSize = type.getDimension(R.styleable.CircleSeekBar_textSize,
					DimenUtils.dip2px(mContext, TEXT_SIZE_DEFAULT));
			mTextColor = type.getColor(R.styleable.CircleSeekBar_textColor,
					TEXT_COLOR_DEFAULT);
			mProgressStrokeWidth = type.getDimension(
					R.styleable.CircleSeekBar_progressWidth,
					DimenUtils.dip2px(mContext, PROGRESS_WIDTH_DEFAULT));
			mProgressColor = type.getColor(
					R.styleable.CircleSeekBar_progressColor,
					PROGRESS_COLOR_DEFAULT);
			mSProgressStrokeWidth = type.getDimension(
					R.styleable.CircleSeekBar_sProgressWidth,
					DimenUtils.dip2px(mContext, S_PROGRESS_WIDTH_DEFAULT));
			mSProgressColor = type.getColor(
					R.styleable.CircleSeekBar_sProgressColor,
					S_PROGRESS_COLOR_DEFAULT);
			mFadeEnable = type.getBoolean(R.styleable.CircleSeekBar_fadeEnable,
					false);
			mStartAlpha = type
					.getInt(R.styleable.CircleSeekBar_startAlpha, 255);
			mEndAlpha = type.getInt(R.styleable.CircleSeekBar_endAlpha, 255);
			mZoomEnable = type.getBoolean(R.styleable.CircleSeekBar_zoomEnable,
					false);
			mCapRound = type.getBoolean(R.styleable.CircleSeekBar_capRound,
					true);
			float progress = type.getFloat(R.styleable.CircleSeekBar_progress,
					0);
			progress = progress > mMaxProgress || progress < 0f ? 0f : progress;
			mTargetAngle = progress / mMaxProgress * 360f;
			mCurrentAngle = mTargetAngle;
			type.recycle();
		} else {
			mMode = MODE_DEFAULT;
			mMaxProgress = MAX_PROGRESS_DEFAULT;
			mStartAngle = START_ANGLE_DEFAULT;
			mVelocity = VELOCITY_DEFAULT;
			mTextSize = TEXT_SIZE_DEFAULT;
			mTextColor = TEXT_COLOR_DEFAULT;
			mProgressStrokeWidth = PROGRESS_WIDTH_DEFAULT;
			mProgressColor = PROGRESS_COLOR_DEFAULT;
			mSProgressStrokeWidth = S_PROGRESS_WIDTH_DEFAULT;
			mSProgressColor = S_PROGRESS_COLOR_DEFAULT;
			mTargetAngle = 0f;
			mCurrentAngle = 0f;
			mStartAlpha = 255;
			mEndAlpha = 255;
			mZoomEnable = false;
			mCapRound = true;
		}
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mTextPaint = new Paint(mPaint);
		mTextPaint.setColor(mTextColor);
		mTextPaint.setTextSize(mTextSize);
		mProgressPaint = new Paint(mPaint);
		mProgressPaint.setColor(mProgressColor);
		mProgressPaint.setStrokeWidth(mProgressStrokeWidth);
		mSProgressPaint = new Paint(mProgressPaint);
		mSProgressPaint.setColor(mSProgressColor);
		mSProgressPaint.setStrokeWidth(mSProgressStrokeWidth);
		if (mCapRound) {
			mProgressPaint.setStrokeCap(Cap.ROUND);
		}
		if (mMode == MODE_FILL_AND_STROKE) {
			mProgressPaint.setStyle(Style.FILL);
			mSProgressPaint.setStyle(Style.FILL_AND_STROKE);
			mUseCenter = true;
		} else if (mMode == MODE_FILL) {
			mProgressPaint.setStyle(Style.FILL);
			mSProgressPaint.setStyle(Style.FILL);
			mUseCenter = true;
		} else {
			mProgressPaint.setStyle(Style.STROKE);
			mSProgressPaint.setStyle(Style.STROKE);
			mUseCenter = false;
		}
		mProgressRect = new RectF();
		mTextBounds = new Rect();
		mFormat = new DecimalFormat(PROGRESS_FORMAT_DEFAULT);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		/* 计算控件宽度与高度 */
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		int width;
		int height;
		if (widthMode == MeasureSpec.EXACTLY) {
			width = widthSize;
		} else {
			int desired = (int) (getPaddingLeft()
					+ DimenUtils.dip2px(mContext, MIN_WIDTH) + getPaddingRight());
			width = desired;
		}
		if (heightMode == MeasureSpec.EXACTLY) {
			height = heightSize;
		} else {
			int desired = (int) (getPaddingTop()
					+ DimenUtils.dip2px(mContext, MIN_HEIGHT) + getPaddingBottom());
			height = desired;
		}
		setMeasuredDimension(width, height);
		/* 计算进度显示的矩形框 */
		float radius = width > height ? height >> 1 : width >> 1;
		float maxStrokeWidth = mProgressStrokeWidth > mSProgressStrokeWidth ? mProgressStrokeWidth
				: mSProgressStrokeWidth;
		radius = radius - getMaxPadding() - maxStrokeWidth;
		int centerX = width >> 1;
		int centerY = height >> 1;
		mProgressRect.set(centerX - radius, centerY - radius, centerX + radius,
				centerY + radius);
		mSProgressRect = new RectF(mProgressRect);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// 判断当前角度偏移方向
		if (mCurrentAngle > mTargetAngle) {
			mCurrentAngle = mCurrentAngle - mVelocity;
			if (mCurrentAngle < mTargetAngle) {
				mCurrentAngle = mTargetAngle;
			}
		} else if (mCurrentAngle < mTargetAngle) {
			mCurrentAngle = mCurrentAngle + mVelocity;
			if (mCurrentAngle > mTargetAngle) {
				mCurrentAngle = mTargetAngle;
			}
		}
		float ratio = mCurrentAngle / 360f;
		// 设置透明度
		if (mFadeEnable) {
			int alpha = (int) ((mEndAlpha - mStartAlpha) * ratio);
			mProgressPaint.setAlpha(alpha);
		}
		// 设置二级进度缩放效果
		if (mZoomEnable) {
			zoomSProgressRect(ratio);
		}
		// 绘制二级进度条
		canvas.drawArc(mSProgressRect, 0, 360f, false, mSProgressPaint);
		// 绘制进度条
		canvas.drawArc(mProgressRect, mStartAngle, mCurrentAngle, mUseCenter,
				mProgressPaint);
		// 绘制字体
		if (mShowText) {
			String text = formatProgress(mCurrentAngle / 360f * mMaxProgress);
			mTextPaint.getTextBounds(text, 0, text.length(), mTextBounds);
			canvas.drawText(text, (getWidth() - mTextBounds.width()) >> 1,
					(getHeight() >> 1) + (mTextBounds.height() >> 1),
					mTextPaint);
		}
		// 如果当前进度不等于目标进度，继续绘制
		if (mCurrentAngle != mTargetAngle) {
			invalidate();
		}
	}

	/**
	 * 格式化进度
	 * 
	 * @param progress
	 * @return
	 */
	private String formatProgress(float progress) {
		return mFormat.format(progress);
	}

	/**
	 * 获取内边距最大值
	 * 
	 * @return
	 */
	private int getMaxPadding() {
		int maxPadding = getPaddingLeft();
		int paddingRight = getPaddingRight();
		int paddingTop = getPaddingTop();
		int paddingBottom = getPaddingBottom();
		if (maxPadding < paddingRight) {
			maxPadding = paddingRight;
		}
		if (maxPadding < paddingTop) {
			maxPadding = paddingTop;
		}
		if (maxPadding < paddingBottom) {
			maxPadding = paddingBottom;
		}
		return maxPadding;
	}

	/**
	 * 缩放二级进度条
	 * 
	 * @param ratio
	 */
	private void zoomSProgressRect(float ratio) {
		float width = mProgressRect.width();
		float height = mProgressRect.height();
		float centerX = mProgressRect.centerX();
		float centerY = mProgressRect.centerY();
		float offsetX = width * 0.5f * ratio;
		float offsetY = height * 0.5f * ratio;
		float left = centerX - offsetX;
		float right = centerX + offsetX;
		float top = centerY - offsetY;
		float bottom = centerY + offsetY;
		mSProgressRect.set(left, top, right, bottom);
	}

	@Override
	protected void onDisplayHint(int hint) {
		if (hint == View.VISIBLE) {
			mCurrentAngle = 0;
			invalidate();
		}
		super.onDisplayHint(hint);
	}

	/**
	 * 设置目标进度
	 * 
	 * @param progress
	 */
	public void setProgress(float progress) {
		progress = progress > mMaxProgress || progress < 0f ? 0f : progress;
		mTargetAngle = progress / mMaxProgress * 360f;
		postInvalidate();
	}

	/**
	 * 设置目标进度
	 * 
	 * @param progress
	 *            进度值
	 * @param isAnim
	 *            是否有动画
	 */
	public void setProgressWithAnim(float progress, boolean isAnim) {
		if (isAnim) {
			setProgress(progress);
		} else {
			progress = progress > mMaxProgress || progress < 0f ? 0f : progress;
			mCurrentAngle = progress / mMaxProgress * 360f;
			mTargetAngle = mCurrentAngle;
			postInvalidate();
		}
	}

	/**
	 * 设置进度画笔着色方式
	 * 
	 * @param shader
	 */
	public void setProgressShader(Shader shader) {
		this.mProgressPaint.setShader(shader);
		invalidate();
	}

	/**
	 * 设置二级进度画笔着色方式
	 * 
	 * @param shader
	 */
	public void setSProgressShader(Shader shader) {
		this.mSProgressPaint.setShader(shader);
		invalidate();
	}

	public void setMaxProgress(float max) {
		this.mMaxProgress = max;
	}

	public float getMaxProgress() {
		return mMaxProgress;
	}

	public int getMode() {
		return mMode;
	}

	public void setMode(int mMode) {
		this.mMode = mMode;
	}

	public float getStartAngle() {
		return mStartAngle;
	}

	public void setStartAngle(float mStartAngle) {
		this.mStartAngle = mStartAngle;
	}

	public float getVelocity() {
		return mVelocity;
	}

	public void setVelocity(float mVelocity) {
		this.mVelocity = mVelocity;
	}

	public float getTextSize() {
		return mTextSize;
	}

	public void setTextSize(float mTextSize) {
		this.mTextSize = mTextSize;
	}

	public int getTextColor() {
		return mTextColor;
	}

	public void setTextColor(int mTextColor) {
		this.mTextColor = mTextColor;
	}

	public float getProgressStrokeWidth() {
		return mProgressStrokeWidth;
	}

	public void setProgressStrokeWidth(float mProgressStrokeWidth) {
		this.mProgressStrokeWidth = mProgressStrokeWidth;
	}

	public int getProgressColor() {
		return mProgressColor;
	}

	public void setProgressColor(int mProgressColor) {
		this.mProgressColor = mProgressColor;
	}

	public float getSProgressStrokeWidth() {
		return mSProgressStrokeWidth;
	}

	public void setSProgressStrokeWidth(float mSProgressStrokeWidth) {
		this.mSProgressStrokeWidth = mSProgressStrokeWidth;
	}

	public int getSProgressColor() {
		return mSProgressColor;
	}

	public void setSProgressColor(int mSProgressColor) {
		this.mSProgressColor = mSProgressColor;
	}

	public boolean isFadeEnable() {
		return mFadeEnable;
	}

	public void setFadeEnable(boolean mFadeEnable) {
		this.mFadeEnable = mFadeEnable;
	}

	public int getStartAlpha() {
		return mStartAlpha;
	}

	public void setStartAlpha(int mStartAlpha) {
		this.mStartAlpha = mStartAlpha;
	}

	public int getEndAlpha() {
		return mEndAlpha;
	}

	public void setEndAlpha(int mEndAlpha) {
		this.mEndAlpha = mEndAlpha;
	}

	public boolean isZoomEnable() {
		return mZoomEnable;
	}

	public void setZoomEnable(boolean mZoomEnable) {
		this.mZoomEnable = mZoomEnable;
	}
}
