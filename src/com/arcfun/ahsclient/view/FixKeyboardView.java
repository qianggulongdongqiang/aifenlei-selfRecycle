package com.arcfun.ahsclient.view;

import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
//import android.graphics.Paint;
//import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.Keyboard.Key;
import android.inputmethodservice.KeyboardView;
import android.util.AttributeSet;

import com.arcfun.ahsclient.R;

public class FixKeyboardView extends KeyboardView {
    private Context mContext;
    //private Paint mPaint;

    public FixKeyboardView(Context context) {
        super(context, null);
    }

    public FixKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context.getApplicationContext();
        /*mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
        mPaint.setTextSize(context.getResources().getDimension(
                R.dimen.actionbar_count_down_size));*/
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        List<Key> keys = getKeyboard().getKeys();
        for (Key key : keys) {
            if (key.codes[0] == Keyboard.KEYCODE_DONE) {
                Drawable dr = mContext.getResources().getDrawable(
                        key.pressed ? R.drawable.button_enter0 : R.drawable.button_enter);
                dr.setBounds(key.x, key.y, key.x + key.width, key.y
                        + key.height);
                dr.draw(canvas);
                /*canvas.drawText(key.label.toString(), key.x + key.width / 3,
                        key.y + key.height / 2, mPaint);*/
            } else if (key.codes[0] == Keyboard.KEYCODE_DELETE) {
                Drawable dr = mContext.getResources().getDrawable(
                        key.pressed ? R.drawable.button_delet0 : R.drawable.button_delet);
                dr.setBounds(key.x, key.y, key.x + key.width, key.y
                        + key.height);
                dr.draw(canvas);
            }
        }
    }
}
