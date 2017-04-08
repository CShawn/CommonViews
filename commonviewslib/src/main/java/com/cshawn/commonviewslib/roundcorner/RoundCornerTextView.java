package com.cshawn.commonviewslib.roundcorner;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.FloatRange;
import android.support.annotation.IntDef;
import android.support.annotation.IntRange;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.cshawn.commonviewslib.R;

/**
 * Created by C.Shawn on 2017/3/23 0023.
 *
 * @author C.Shawn
 */
public class RoundCornerTextView extends TextView {
    @ColorInt int stroke_color;
    ColorStateList stroke_colors;
    @ColorInt int solid_color;
    ColorStateList solid_colors;
    float stroke_width;
    float corner_radius;
    float radius_left_top;
    float radius_right_top;
    float radius_right_bottom;
    float radius_left_bottom;
    boolean self_round_corner;
    private Paint mPaint;
    private float[] radiusArray = new float[8];
    int shape;
    private Drawable mDrawable;

    private Matrix mMatrix;
    private int mScaleType;
    private int mBackgroundFitType;
    private Matrix mDrawMatrix = null;

    private boolean clearBackground;

    @IntDef({RECTANGLE, OVAL})
    public @interface RoundShape {}
    public static final int RECTANGLE = 0;
    public static final int OVAL = 1;

    @IntDef({BACK, CROP, INSIDE})
    public @interface BackgroundType {}
    public static final int BACK = 0;
    public static final int CROP = 1;
    public static final int INSIDE = 2;

    @IntDef({MATRIX, FIT_XY,FIT_START,FIT_CENTER,FIT_END,CENTER,CENTER_CROP,CENTER_INSIDE})
    public @interface ScaleType {}
    public static final int MATRIX = 0;
    public static final int FIT_XY = 1;
    public static final int FIT_START = 2;
    public static final int FIT_CENTER = 3;
    public static final int FIT_END = 4;
    public static final int CENTER = 5;
    public static final int CENTER_CROP = 6;
    public static final int CENTER_INSIDE = 7;

    public RoundCornerTextView(Context context) {
        super(context);
        mPaint=new Paint();
        mPaint.setAntiAlias(true);
        initView();
    }

    public RoundCornerTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mPaint=new Paint();
        mPaint.setAntiAlias(true);
        initView();
        obtainStyledAttributes(attrs);
    }

    public RoundCornerTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint=new Paint();
        mPaint.setAntiAlias(true);
        initView();
        obtainStyledAttributes(attrs);
    }

    private void initView() {
        mMatrix = new Matrix();
        mScaleType = FIT_CENTER;
    }

    private void obtainStyledAttributes(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.RoundCornerTextView);
        shape = a.getInt(R.styleable.RoundCornerTextView_shape,RECTANGLE);
        mScaleType = a.getInt(R.styleable.RoundCornerTextView_scaleType,FIT_CENTER);
        mBackgroundFitType = a.getInt(R.styleable.RoundCornerTextView_backgroundFitType,BACK);
        stroke_colors = a.getColorStateList(R.styleable.RoundCornerTextView_strokeColor);
        solid_colors = a.getColorStateList(R.styleable.RoundCornerTextView_solidColor);
        stroke_width = a.getDimensionPixelSize(R.styleable.RoundCornerTextView_strokeWidth,0);
        self_round_corner = a.getBoolean(R.styleable.RoundCornerTextView_selfRoundCorner,false);
        if (self_round_corner) {
            corner_radius = a.getDimensionPixelSize(R.styleable.RoundCornerTextView_radius,0);
            radius_left_top = a.getDimensionPixelSize(R.styleable.RoundCornerTextView_radius_left_top,-1);
            if (radius_left_top < 0) {
                radius_left_top = corner_radius;
            }
            radius_right_top = a.getDimensionPixelSize(R.styleable.RoundCornerTextView_radius_right_top,-1);
            if (radius_right_top < 0) {
                radius_right_top = corner_radius;
            }
            radius_right_bottom = a.getDimensionPixelSize(R.styleable.RoundCornerTextView_radius_right_bottom,-1);
            if (radius_right_bottom < 0) {
                radius_right_bottom = corner_radius;
            }
            radius_left_bottom = a.getDimensionPixelSize(R.styleable.RoundCornerTextView_radius_left_bottom,-1);
            if (radius_left_bottom < 0) {
                radius_left_bottom = corner_radius;
            }
        }
        a.recycle();
        setStrokeColor(stroke_colors != null ? stroke_colors : ColorStateList.valueOf(getCurrentTextColor()));
        setSolidColor(solid_colors != null ? solid_colors : ColorStateList.valueOf(Color.TRANSPARENT));
        mDrawable=getBackground();

        configureBounds();
    }

    public void setShape(@RoundShape int shape) {
        this.shape = shape;
        invalidate();
    }

    public void setRadius(@FloatRange(from = 0) float leftTop,@FloatRange(from = 0) float rightTop,@FloatRange(from = 0) float rightBottom,@FloatRange(from = 0) float leftBottom) {
        radius_left_top = leftTop;
        radius_left_bottom = leftBottom;
        radius_right_bottom = rightBottom;
        radius_right_top = rightTop;
        setRadius(leftTop,leftTop,rightTop,rightTop,rightBottom,rightBottom,leftBottom,leftBottom);
        invalidate();
    }

    public void setRadius(@FloatRange(from = 0) float radius) {
        corner_radius=radius;
        radius_left_top = radius;
        radius_left_bottom = radius;
        radius_right_bottom = radius;
        radius_right_top = radius;
        setRadius(radius,radius,radius,radius,radius,radius,radius,radius);
        invalidate();
    }

    public void setStrokeWidth(@IntRange(from = 0) int strokeWidthPixels) {
        strokeWidthPixels = strokeWidthPixels <0 ? 0 :strokeWidthPixels;
        if (stroke_width != strokeWidthPixels) {
            stroke_width = strokeWidthPixels;
            invalidate();
        }
    }

    public void setStrokeColor(@ColorInt int strokeColor) {
        stroke_colors = ColorStateList.valueOf(strokeColor);
        int color = stroke_colors.getColorForState(getDrawableState(), 0);
        if (color != stroke_color) {
            stroke_color = color;
            invalidate();
        }
    }

    public void setStrokeColor(ColorStateList strokeColors) {
        if (strokeColors == null) {
            throw new NullPointerException();
        }
        stroke_colors = strokeColors;
        int color = stroke_colors.getColorForState(getDrawableState(), 0);
        if (color != stroke_color) {
            stroke_color = color;
            invalidate();
        }
    }

    public int getStrokeColor() {
        return stroke_color;
    }

    public void setSolidColor(@ColorInt int solidColor) {
        solid_colors = ColorStateList.valueOf(solidColor);
        int color = solid_colors.getColorForState(getDrawableState(), 0);
        if (color != solid_color) {
            solid_color = color;
            invalidate();
        }
    }

    public void setSolidColor(ColorStateList solidColors) {
        if (solidColors == null) {
            throw new NullPointerException();
        }
        solid_colors = solidColors;
        int color = solid_colors.getColorForState(getDrawableState(), 0);
        if (color != solid_color) {
            solid_color = color;
            invalidate();
        }
    }

    public int getSolidcolor() {
        return solid_color;
    }

    public ColorStateList getSolidColors() {
        return solid_colors;
    }

    public ColorStateList getStrokecolors() {
        return stroke_colors;
    }

    public float getStrokeWidth() {
        return stroke_width;
    }

    public void setStrokeWidth(float strokeWidth) {
        this.stroke_width = strokeWidth;
    }

    public float getCornerRadius() {
        return corner_radius;
    }

    public float getRadius_left_top() {
        return radius_left_top;
    }

    public float getRadius_right_top() {
        return radius_right_top;
    }

    public float getRadius_right_bottom() {
        return radius_right_bottom;
    }

    public float getRadius_left_bottom() {
        return radius_left_bottom;
    }

    public void setCornerRadius(float cornerRadius) {
        if (this.corner_radius != cornerRadius) {
            this.corner_radius = cornerRadius;
            invalidate();
        }
    }
    public void setCornerRadius(float radius_left_top,float radius_right_top,float radius_right_bottom,float radius_left_bottom) {
        this.radius_left_top = radius_left_top;
        this.radius_right_top = radius_right_top;
        this.radius_right_bottom = radius_right_bottom;
        this.radius_left_bottom = radius_left_bottom;
        invalidate();
    }

    public boolean isSelfRoundCorner() {
        return self_round_corner;
    }

    /**
     * 是否自定义圆角半径
     * @param self
     */
    public void setSelfRoundCorner(boolean self) {
        this.self_round_corner = self;
    }

    public int getShape() {
        return shape;
    }

    public int getScaleType() {
        return mScaleType;
    }

    public void setScaleType(@ScaleType int mScaleType) {
        if (this.mScaleType != mScaleType) {
            this.mScaleType = mScaleType;
            invalidate();
        }
    }

    public int getBackgroundFitType() {
        return mBackgroundFitType;
    }

    public void setBackgroundFitType(@BackgroundType int backgroundType) {
        if (mBackgroundFitType != backgroundType) {
            mBackgroundFitType = backgroundType;
            invalidate();
        }
    }

    @Override
    public void setBackground(Drawable background) {
        super.setBackground(background);
        updateBackground();
    }

    @Override
    public void setBackgroundColor(@ColorInt int color) {
        super.setBackgroundColor(color);
        updateBackground();
    }

    @Override
    public void setBackgroundResource(@DrawableRes int resid) {
        super.setBackgroundResource(resid);
        updateBackground();
    }

    @Override
    public void setBackgroundDrawable(Drawable background) {
        super.setBackgroundDrawable(background);
        updateBackground();
    }

    @Override
    public void setBackgroundTintList(@Nullable ColorStateList tint) {
        super.setBackgroundTintList(tint);
        updateBackground();
    }

    @Override
    public void setBackgroundTintMode(@Nullable PorterDuff.Mode tintMode) {
        super.setBackgroundTintMode(tintMode);
        updateBackground();
    }

    private void updateBackground() {
        if (!clearBackground) {
            mDrawable = getBackground();
            if (getBackground() == null) {
                setBackgroundColor(Color.TRANSPARENT);
            }
        }
        invalidate();
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        if ((stroke_colors != null && stroke_colors.isStateful())
                || (solid_colors != null && solid_colors.isStateful())
                || (mDrawable != null && mDrawable.isStateful())) {
            boolean inval = false;
            if (stroke_colors != null) {
                int color = stroke_colors.getColorForState(getDrawableState(), 0);
                if (color != stroke_color) {
                    stroke_color = color;
                    inval = true;
                }
            }
            if (solid_colors != null) {
                int color = solid_colors.getColorForState(getDrawableState(), 0);
                if (color != solid_color) {
                    solid_color = color;
                    inval = true;
                }
            }
            Drawable drawable = mDrawable;
            if (drawable != null && drawable.isStateful()
                    && drawable.setState(getDrawableState())) {
                configureBounds();
                inval = true;
            }
            if (inval) {
                invalidate();
            }
        }
    }

    private void setRadius(float ltX, float ltY, float rtX, float rtY, float rbX, float rbY, float lbX, float lbY) {
        radiusArray[0] = ltX;
        radiusArray[1] = ltY;
        radiusArray[2] = rtX;
        radiusArray[3] = rtY;
        radiusArray[4] = rbX;
        radiusArray[5] = rbY;
        radiusArray[6] = lbX;
        radiusArray[7] = lbY;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        configureBounds();
        float yRadius = 0.5f * getHeight();
        float xRadius = 0.5f * Math.min(getWidth(),getHeight());
        stroke_width = Math.min(xRadius , stroke_width);
        stroke_width = stroke_width < 0 ? 0 : stroke_width;
        RectF strokeRect = new RectF(0.5f * stroke_width, 0.5f * stroke_width, getWidth() - 0.5f * stroke_width, getHeight() - 0.5f * stroke_width);

        if (mDrawable != null&&!(mBackgroundFitType == INSIDE && xRadius <= stroke_width)) {
            if (mDrawMatrix == null) {
                mDrawable.draw(canvas);
            } else {
                final int saveCount = canvas.getSaveCount();
                canvas.save();
                if (mDrawMatrix != null) {
                    canvas.concat(mDrawMatrix);
                }
                mDrawable.draw(canvas);
                canvas.restoreToCount(saveCount);
            }
        }

        if ((mBackgroundFitType == INSIDE && xRadius > stroke_width)|| mBackgroundFitType == CROP) {
            RectF dstRectF = new RectF(0, 0, getWidth(), getHeight());
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            Bitmap dstBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            Canvas c2 = new Canvas(dstBitmap);
            Paint p2 = new Paint(Paint.ANTI_ALIAS_FLAG);
            p2.setColor(Color.BLACK);
            if (shape == OVAL) {
                c2.drawOval(dstRectF, p2);
            } else {
                if (self_round_corner) {
                    setRadius(getInnerPicRadius(radius_left_top, xRadius), getInnerPicRadius(radius_left_top, xRadius),
                            getInnerPicRadius(radius_right_top, xRadius), getInnerPicRadius(radius_right_top, xRadius),
                            getInnerPicRadius(radius_right_bottom, xRadius), getInnerPicRadius(radius_right_bottom, xRadius),
                            getInnerPicRadius(radius_left_bottom, xRadius), getInnerPicRadius(radius_left_bottom, xRadius));
                    Path path = new Path();
                    path.addRoundRect(dstRectF, radiusArray, Path.Direction.CW);
                    c2.drawPath(path, p2);
                } else {
                    c2.drawRoundRect(dstRectF, xRadius, yRadius, p2);
                }
            }
            Paint paint = new Paint();
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
            canvas.drawBitmap(dstBitmap, 0, 0, paint);
        }
        mPaint.reset();

        if (stroke_width > 0) {
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(stroke_width);
            mPaint.setColor(stroke_color);
            if (shape == OVAL) {
                canvas.drawOval(strokeRect,mPaint);
            } else {
                if (self_round_corner) {
                    setRadius(getStrokeRadius(radius_left_top, xRadius), getStrokeRadius(radius_left_top, xRadius),
                            getStrokeRadius(radius_right_top, xRadius), getStrokeRadius(radius_right_top, xRadius),
                            getStrokeRadius(radius_right_bottom, xRadius), getStrokeRadius(radius_right_bottom, xRadius),
                            getStrokeRadius(radius_left_bottom, xRadius), getStrokeRadius(radius_left_bottom, xRadius));
                    Path path = new Path();
                    path.addRoundRect(strokeRect, radiusArray, Path.Direction.CW);
                    canvas.drawPath(path, mPaint);
                } else {
                    canvas.drawRoundRect(strokeRect, xRadius, yRadius, mPaint);
                }
            }
        }
        if (solid_color != Color.TRANSPARENT) {
            RectF innerRect=new RectF(stroke_width,stroke_width, getWidth() - stroke_width, getHeight() - stroke_width);
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(solid_color);
            if (shape == OVAL) {
                canvas.drawOval(innerRect,mPaint);
            } else {
                if (self_round_corner) {
                    setRadius(getInnerRadius(radius_left_top, xRadius), getInnerRadius(radius_left_top, xRadius),
                            getInnerRadius(radius_right_top, xRadius), getInnerRadius(radius_right_top, xRadius),
                            getInnerRadius(radius_right_bottom, xRadius), getInnerRadius(radius_right_bottom, xRadius),
                            getInnerRadius(radius_left_bottom, xRadius), getInnerRadius(radius_left_bottom, xRadius));
                    Path path = new Path();
                    path.addRoundRect(innerRect, radiusArray, Path.Direction.CW);
                    canvas.drawPath(path, mPaint);
                } else {
                    canvas.drawRoundRect(innerRect, xRadius, yRadius, mPaint);
                }
            }
        }
        super.onDraw(canvas);
        if (getBackground() != null) {
            clearBackground=true;
            setBackgroundDrawable(null);
            clearBackground=false;
        }
    }

    private float getInnerPicRadius(float radius,float xRadius){
        return Math.min(radius,xRadius)+0.5f*stroke_width<0 ? 0 : Math.min(radius,xRadius)+0.5f*stroke_width;
    }

    private float getInnerRadius(float radius,float xRadius){
        return Math.min(radius,xRadius)-0.5f*stroke_width<0 ? 0 : Math.min(radius,xRadius)-0.5f*stroke_width;
    }

    private float getStrokeRadius(float radius,float xRadius){
        return Math.min(radius,xRadius);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if(mDrawable != null && mDrawable.getIntrinsicWidth() > 0 && mDrawable.getIntrinsicHeight() > 0) {
            int widthMode = MeasureSpec.getMode(widthMeasureSpec);
            int heightMode = MeasureSpec.getMode(heightMeasureSpec);
            int widthSize = MeasureSpec.getSize(widthMeasureSpec);
            int heightSize = MeasureSpec.getSize(heightMeasureSpec);

            int width;
            int height;
            if (widthMode == MeasureSpec.AT_MOST) {
                width=Math.max(getWidth(),mDrawable.getIntrinsicWidth());
                if (mBackgroundFitType == INSIDE && stroke_width > 0) {
                    width += 2 * stroke_width;
                }
                width = Math.min(width, widthSize);
            }else {
                width = widthSize;
            }
            if (heightMode == MeasureSpec.AT_MOST) {
                height=Math.max(getHeight(),mDrawable.getIntrinsicHeight());
                if (mBackgroundFitType == INSIDE && stroke_width > 0) {
                    height += 2 * stroke_width;
                }
                height = Math.min(height, heightSize);
            }else {
                height = heightSize;
            }
            if (widthMode == MeasureSpec.AT_MOST||heightMode == MeasureSpec.AT_MOST) {
                setMeasuredDimension(width, height);
            }
        }
    }

    private void configureBounds() {
        if (mDrawable == null) {
            return;
        }
        final int dwidth = mDrawable.getIntrinsicWidth();
        final int dheight = mDrawable.getIntrinsicHeight();

        final int vwidth = getWidth();
        final int vheight = getHeight();

        final float iwidth = mBackgroundFitType != INSIDE ? (float) vwidth : vwidth - 2 * stroke_width;
        final float iheight = mBackgroundFitType != INSIDE ? (float) vheight : vheight - 2 * stroke_width;

        final boolean fits = (dwidth < 0 || vwidth == dwidth)
                && (dheight < 0 || vheight == dheight);

        if (dwidth <= 0 || dheight <= 0 || FIT_XY == mScaleType) {
            // If the drawable has no intrinsic size, or we're told to
            // scale to fit, then we just fill our entire view.
            if (mBackgroundFitType == INSIDE) {
                mDrawable.setBounds((int) stroke_width, (int) stroke_width, (int) (vwidth - stroke_width), (int) (vheight - stroke_width));
            } else {
                mDrawable.setBounds(0,0,vwidth,vheight);
            }
            mDrawMatrix = null;
        } else {
            // We need to do the scaling ourself, so have the drawable
            // use its native size.
            mDrawable.setBounds(0, 0, dwidth, dheight);

            if (MATRIX == mScaleType) {
                // Use the specified matrix as-is.
                if (mMatrix.isIdentity()) {
                    mDrawMatrix = null;
                } else {
                    mDrawMatrix = mMatrix;
                }
            } else if (fits) {
                // The bitmap fits exactly, no transform needed.
                mDrawMatrix = null;
            } else if (CENTER == mScaleType) {
                // Center bitmap in view, no scaling.
                mDrawMatrix = mMatrix;
                if (mBackgroundFitType == INSIDE && stroke_width > 0) {
                    float scaleX = iwidth / vwidth;
                    float scaleY = iheight / vheight;
                    mDrawMatrix.setScale(scaleX, scaleY);
                    mDrawMatrix.postTranslate(Math.round((vwidth - dwidth * scaleX) * 0.5f),
                            Math.round((vheight - dheight * scaleY) * 0.5f));
                } else {
                    mDrawMatrix.setTranslate(Math.round((vwidth - dwidth) * 0.5f),
                            Math.round((vheight - dheight) * 0.5f));
                }
            } else if (CENTER_CROP == mScaleType) {
                mDrawMatrix = mMatrix;

                float scale;
                float dx = 0, dy = 0;

                if (dwidth * vheight > vwidth * dheight) {
                    scale = iheight / (float) dheight;
                    dx = (vwidth - dwidth * scale) * 0.5f;
                } else {
                    scale = iwidth / (float) dwidth;
                    dy = (vheight - dheight * scale) * 0.5f;
                }
                mDrawMatrix.setScale(scale, scale);
                mDrawMatrix.postTranslate(Math.round(dx), Math.round(dy));
            } else if (CENTER_INSIDE == mScaleType) {
                mDrawMatrix = mMatrix;
                float scale;
                float dx;
                float dy;

                if (dwidth <= iwidth && dheight <= iheight) {
                    scale = 1.0f;
                } else {
                    scale = Math.min(iwidth / (float) dwidth,
                            iheight / (float) dheight);
                }

                dx = Math.round((vwidth - dwidth * scale) * 0.5f);
                dy = Math.round((vheight - dheight * scale) * 0.5f);
                mDrawMatrix.setScale(scale, scale);
                mDrawMatrix.postTranslate(dx, dy);
            } else {
                RectF mTempSrc = new RectF(0, 0, dwidth, dheight);
                if (iwidth > 0 && iheight > 0) {
                    RectF mTempDst = new RectF(0, 0, iwidth, iheight);
                    mDrawMatrix = mMatrix;
                    mDrawMatrix.setRectToRect(mTempSrc, mTempDst,sS2FArray[mScaleType-1]);
                    if (mBackgroundFitType == INSIDE && stroke_width > 0) {
                        mDrawMatrix.postTranslate(stroke_width, stroke_width);
                    }
                }
            }
        }
    }
    private static final Matrix.ScaleToFit[] sS2FArray = {
            Matrix.ScaleToFit.FILL,
            Matrix.ScaleToFit.START,
            Matrix.ScaleToFit.CENTER,
            Matrix.ScaleToFit.END
    };
}