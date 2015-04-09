package com.victor.ringbutton;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

/**
 * RingButton
 * Created by Victor on 2015/4/9.
 */
public class RingButton extends View {
    private static final String BASE_COLOR = "#54D2E4";
    private static final int PRESS_RADIUS = 20;
    private static final int TEXT_SIZE = 22;
    private static final int BORDER_WIDTH = 2;
    private static final int TEXT_DIVIDER = 10;
    private static final int DIVIDE_SIZE = 2;

    private int centerY;
    private int centerX;
    private int outerRadius;

    private Paint circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint pressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint dividerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private boolean upPressed = false;
    private boolean downPressed = false;

    private String upText = "up";
    private String downText = "down";
    private int dividerColor = 0;
    private int dividerUpColor = 0;
    private int dividerShadowColor = 0;
    private int dividerSize = DIVIDE_SIZE;
    private Bitmap upBitmap;
    private Bitmap downBitmap;

    private Rect upRect = new Rect();
    private Rect downRect = new Rect();
    private RectF pressedRect;

    private int textDivider = 0;

    private OnClickListener onClickListener;

    public interface OnClickListener {
        public void clickUp();

        public void clickDown();
    }

    public RingButton(Context context) {
        super(context);
        init(context, null);
    }

    public RingButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public RingButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawCircle(centerX, centerY, outerRadius, circlePaint);

        if (upPressed) {
            canvas.drawArc(pressedRect, 180, 180, true, pressPaint);
        }
        if (downPressed) {
            canvas.drawArc(pressedRect, 0, 180, true, pressPaint);
        }

        // 绘制上方按钮文字
        if (!TextUtils.isEmpty(upText)) {
            canvas.drawText(upText, centerX - upRect.width() / 2, centerY - textDivider, textPaint);
        }
        // 绘制上方按钮图片
        if (null != upBitmap) {
            canvas.drawBitmap(upBitmap, centerX - upBitmap.getWidth() / 2, centerY - upRect.height() - upBitmap.getHeight() - (int) (textDivider * 1.5), textPaint);
        }
        // 绘制下方按钮图片
        int downBitmapHeight = 0;
        if (null != downBitmap) {
            canvas.drawBitmap(downBitmap, centerX - downBitmap.getWidth() / 2, centerY + textDivider / 2, textPaint);
            downBitmapHeight = downBitmap.getHeight();
        }
        // 绘制下方按钮文字
        if (!TextUtils.isEmpty(downText)) {
            canvas.drawText(downText, centerX - downRect.width() / 2, centerY + downBitmapHeight + upRect.height() + textDivider, textPaint);
        }

        // 绘制上分割线
        dividerPaint.setColor(dividerUpColor);
        canvas.drawLine(20, centerY - dividerSize, centerX * 2 - 20, centerY - dividerSize, dividerPaint);
        // 绘制分割线
        dividerPaint.setColor(dividerColor);
        canvas.drawLine(20, centerY, centerX * 2 - 20, centerY, dividerPaint);
        // 绘制下分割线
        dividerPaint.setColor(dividerShadowColor);
        canvas.drawLine(20, centerY + dividerSize, centerX * 2 - 20, centerY + dividerSize, dividerPaint);
        // 绘制外围空心圆
        canvas.drawCircle(centerX, centerY, outerRadius - 20, borderPaint);
        super.onDraw(canvas);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        centerX = w / 2;
        centerY = h / 2;
        outerRadius = Math.min(w, h) / 2;
        pressedRect = new RectF(PRESS_RADIUS, PRESS_RADIUS, (centerY - PRESS_RADIUS / 2) * 2, (centerX - PRESS_RADIUS / 2) * 2);
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (x > PRESS_RADIUS && x < centerX * 2 - PRESS_RADIUS) {
                float upY = (float) Math.sqrt(Math.pow((outerRadius - PRESS_RADIUS / 2), 2) - Math.pow((outerRadius - x), 2));
                if (y > centerY - upY && y < centerY) {
                    upPressed = true;
                    this.invalidate();
                } else if (y > centerY && y < centerY + upY) {
                    downPressed = true;
                    this.invalidate();
                }
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            if (upPressed) {
                if (null != onClickListener) {
                    onClickListener.clickUp();
                }
                upPressed = false;
                this.invalidate();
            }
            if (downPressed) {
                if (null != onClickListener) {
                    onClickListener.clickDown();
                }
                downPressed = false;
                this.invalidate();
            }
        }
        return super.onTouchEvent(event);
    }

    private void init(Context context, AttributeSet attrs) {
        this.setFocusable(true);
        setClickable(true);

        textDivider = dpToPx(context, TEXT_DIVIDER);

        int circleColor = Color.WHITE;
        int borderColor = Color.parseColor(BASE_COLOR);
        int borderWidth = dpToPx(context, BORDER_WIDTH);
        int textSize = dpToPx(context, TEXT_SIZE);
        int textColor = Color.parseColor(BASE_COLOR);
        int pressedColor = Color.LTGRAY;
        if (null != attrs) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ringButton);
            int baseColor = typedArray.getColor(R.styleable.ringButton_baseColor, Color.parseColor(BASE_COLOR));
            circleColor = typedArray.getColor(R.styleable.ringButton_circleColor, Color.parseColor("#f2f2f2"));
            borderColor = typedArray.getColor(R.styleable.ringButton_borderColor, baseColor);
            borderWidth = typedArray.getDimensionPixelSize(R.styleable.ringButton_borderWidth, dpToPx(context, dpToPx(context, BORDER_WIDTH)));
            textSize = typedArray.getDimensionPixelSize(R.styleable.ringButton_textSize, dpToPx(context, TEXT_SIZE));
            textColor = typedArray.getColor(R.styleable.ringButton_textColor, baseColor);
            dividerColor = typedArray.getColor(R.styleable.ringButton_dividerColor, baseColor);
            dividerUpColor = typedArray.getColor(R.styleable.ringButton_dividerUpColor, Color.parseColor("#dddddd"));
            dividerShadowColor = typedArray.getColor(R.styleable.ringButton_dividerShadowColor, Color.WHITE);
            pressedColor = typedArray.getColor(R.styleable.ringButton_pressedColor, Color.parseColor("#dddddd"));
            int upDrawable = typedArray.getResourceId(R.styleable.ringButton_upDrawable, -1);
            int downDrawable = typedArray.getResourceId(R.styleable.ringButton_downDrawable, -1);
            upText = typedArray.getString(R.styleable.ringButton_upText);
            downText = typedArray.getString(R.styleable.ringButton_downText);
            dividerSize = typedArray.getDimensionPixelSize(R.styleable.ringButton_dividerSize, dpToPx(context, DIVIDE_SIZE));
            typedArray.recycle();

            if (upDrawable > 0) {
                Drawable camera = context.getResources().getDrawable(upDrawable);
                BitmapDrawable bd = (BitmapDrawable) camera;
                upBitmap = bd.getBitmap();
            }

            if (downDrawable > 0) {
                Drawable handWrite = context.getResources().getDrawable(downDrawable);
                BitmapDrawable bd2 = (BitmapDrawable) handWrite;
                downBitmap = bd2.getBitmap();
            }
        }

        circlePaint.setStyle(Paint.Style.FILL);
        circlePaint.setColor(circleColor);

        borderPaint.setColor(borderColor);
        borderPaint.setStrokeWidth(borderWidth);
        borderPaint.setStyle(Paint.Style.STROKE);

        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextSize(textSize);
        textPaint.setColor(textColor);
        if (!TextUtils.isEmpty(upText)) {
            textPaint.getTextBounds(upText, 0, upText.length(), upRect);
        }
        if (!TextUtils.isEmpty(downText)) {
            textPaint.getTextBounds(downText, 0, downText.length(), downRect);
        }

        pressPaint.setColor(pressedColor);
        pressPaint.setStyle(Paint.Style.FILL);

        dividerPaint.setStrokeWidth(dividerSize);
    }

    public int dpToPx(Context context, float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal, context.getResources().getDisplayMetrics());
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

}
