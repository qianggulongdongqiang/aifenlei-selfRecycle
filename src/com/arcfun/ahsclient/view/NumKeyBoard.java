package com.arcfun.ahsclient.view;

import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.arcfun.ahsclient.R;

public class NumKeyBoard extends LinearLayout {
    public interface OnInputDoneListener {
        /** -1 error; 0 input; 1 finish */
        public void onInput(int type);
    }

    private EditText mEditText;
    private OnInputDoneListener mListener;
    private static final int PHONE_LENGTH = 15;

    public NumKeyBoard(Context context) {
        this(context, null);
    }

    public NumKeyBoard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        setOrientation(VERTICAL);
        LayoutInflater.from(context).inflate(R.layout.ahs_keyborad_layout,
                this, true);

        KeyboardView kv = (KeyboardView) findViewById(R.id.input_keybord);
        Keyboard kb = new Keyboard(context, R.layout.ahs_keyborad);

        kv.setKeyboard(kb);
        kv.setEnabled(true);
        kv.setOnKeyboardActionListener(mActionListener);
    }

    public void setEditText(EditText et, OnInputDoneListener listener) {
        this.mEditText = et;
        this.mListener = listener;
    }

    public EditText getEditText() {
        return mEditText;
    }

    private KeyboardView.OnKeyboardActionListener mActionListener = new KeyboardView.OnKeyboardActionListener() {
        @Override
        public void swipeUp() {
        }

        @Override
        public void swipeRight() {
        }

        @Override
        public void swipeLeft() {
        }

        @Override
        public void swipeDown() {
        }

        @Override
        public void onText(CharSequence text) {
        }

        @Override
        public void onRelease(int primaryCode) {
        }

        @Override
        public void onPress(int primaryCode) {
        }

        @Override
        public void onKey(int primaryCode, int[] keyCodes) {
            if (mEditText != null) {
                Editable editable = mEditText.getText();
                int selectPos = mEditText.getSelectionStart();

                if (primaryCode == Keyboard.KEYCODE_DONE) {
                    if (mListener != null) {
                        mListener.onInput(1);
                    }
                } else if (primaryCode == Keyboard.KEYCODE_DELETE) {
                    if (mListener != null) {
                        mListener.onInput(0);
                    }
                    if (editable != null && editable.length() > 0) {
                        if (selectPos > 0) {
                            editable.delete(selectPos - 1, selectPos);
                        }
                    }
                } else {
                    if (editable != null && editable.length() < PHONE_LENGTH) {
                        if (mListener != null) {
                            mListener.onInput(0);
                        }
                        editable.insert(selectPos,
                                Character.toString((char) primaryCode));
                    } else {
                        if (mListener != null) {
                            mListener.onInput(-1);
                        }
                    }
                }
            }
        }
    };
}