package com.vuforia.captureapp.model;

import java.util.*;
import android.app.*;
import android.content.*;
import android.widget.*;
import android.util.*;
import android.view.animation.*;
import android.view.*;
import android.view.inputmethod.*;
import android.os.*;

import com.vuforia.captureapp.R;

public class EditNameView extends EditText
{
    private int MAX_NAME_LENGTH;
    List<String> mCaptureNameList;
    private Dialog mContainingDialog;
    private Context mContext;
    private EditNameView mEditNameView;
    private ImageView mEditTextImage;
    private TextView mErrorTextView;
    private Animation mFadeOutAnim;
    private boolean mShiftPressed;

    public EditNameView(final Context mContext) {
        super(mContext);
        this.mShiftPressed = false;
        this.MAX_NAME_LENGTH = 64;
        this.mContext = mContext;
        this.mEditNameView = this;
    }

    public EditNameView(final Context mContext, final AttributeSet set) {
        super(mContext, set);
        this.mShiftPressed = false;
        this.MAX_NAME_LENGTH = 64;
        this.mContext = mContext;
        this.mEditNameView = this;
    }

    public void init(final Dialog mContainingDialog, final TextView mErrorTextView, final ImageView mEditTextImage, final List<String> mCaptureNameList) {
        this.mContainingDialog = mContainingDialog;
        this.mErrorTextView = mErrorTextView;
        this.mEditTextImage = mEditTextImage;
        this.mCaptureNameList = mCaptureNameList;
        this.mFadeOutAnim = AnimationUtils.loadAnimation(this.mContext, R.anim.error_text_animation);
        this.mEditTextImage.setImageResource(R.drawable.edit_text_image);
        this.mContainingDialog.setCanceledOnTouchOutside(false);
        this.setOnKeyListener(new OnKeyListener() {
            public boolean onKey(final View view, int i, final KeyEvent keyEvent) {
                boolean b = true;
                if ((i >= 7 && i <= 16 && !EditNameView.this.mShiftPressed) || (i >= 29 && i <= 54) || (i == 69 && EditNameView.this.mShiftPressed)) {
                    if (EditNameView.this.mEditNameView.getText().toString().length() >= EditNameView.this.MAX_NAME_LENGTH) {
                        EditNameView.this.mErrorTextView.setText(EditNameView.this.getResources().getString(R.string.edit_text_error_max_length));
                        EditNameView.this.mEditTextImage.setImageResource(R.drawable.edit_text_error_image);
                    }
                    else {
                        EditNameView.this.mEditTextImage.setImageResource(R.drawable.edit_text_image);
                        EditNameView.this.mErrorTextView.setText("");
                        b = false;
                    }
                }
                else if (i == 66 || i == 4 || i == 0) {
                    if (i == 4 && keyEvent.getAction() == 1) {
                        EditNameView.this.mContainingDialog.dismiss();
                    }
                    if (i == 66) {
                        String string;
                        char char1;
                        for (string = EditNameView.this.mEditNameView.getText().toString(), i = 0; i < string.length(); ++i) {
                            char1 = string.charAt(i);
                            if ((char1 < 'a' || char1 > 'z') && (char1 < 'A' || char1 > 'Z') && (char1 < '0' || char1 > '9') && char1 != '_') {
                                EditNameView.this.mEditTextImage.setImageResource(R.drawable.edit_text_error_image);
                                EditNameView.this.mErrorTextView.setText(EditNameView.this.getResources().getString(R.string.edit_text_error_character));
                                return b;
                            }
                        }
                        if (string == "" || EditNameView.this.mCaptureNameList.contains(string)) {
                            EditNameView.this.mEditTextImage.setImageResource(R.drawable.edit_text_error_image);
                            EditNameView.this.mErrorTextView.setText(EditNameView.this.getResources().getString(R.string.edit_text_error_duplicate_name));
                            return b;
                        }
                    }
                    b = false;
                }
                else if (i == 67) {
                    if (keyEvent.getAction() == 1) {
                        i = EditNameView.this.mEditNameView.getSelectionStart();
                        if (i > 0) {
                            final String string2 = EditNameView.this.mEditNameView.getText().toString();
                            String substring = "";
                            if (string2.length() > i) {
                                substring = string2.substring(i);
                            }
                            EditNameView.this.mEditNameView.setText(string2.substring(0, i - 1) + substring);
                            EditNameView.this.mEditNameView.setSelection(i - 1);
                        }
                    }
                    EditNameView.this.mEditTextImage.setImageResource(R.drawable.edit_text_image);
                    EditNameView.this.mErrorTextView.setText("");
                }
                else if (i == 59) {
                    if (keyEvent.getAction() == 0) {
                        EditNameView.this.mShiftPressed = true;
                    }
                    else if (keyEvent.getAction() == 1) {
                        EditNameView.this.mShiftPressed = false;
                    }
                    b = false;
                }
                else {
                    EditNameView.this.mErrorTextView.setText(EditNameView.this.getResources().getString(R.string.edit_text_error_character));
                    EditNameView.this.mEditTextImage.setImageResource(R.drawable.edit_text_error_image);
                }
                return b;
            }
        });
    }

    public InputConnection onCreateInputConnection(final EditorInfo editorInfo) {
        editorInfo.actionLabel = null;
        editorInfo.inputType = 1;
        editorInfo.imeOptions = 268435462;
        editorInfo.actionLabel = "OK";
        final BaseInputConnection baseInputConnection = new BaseInputConnection(this, false) {
            public boolean commitText(final CharSequence charSequence, final int n) {
                return super.commitText(charSequence, n);
            }

            public boolean deleteSurroundingText(final int n, final int n2) {
                final int selectionStart = EditNameView.this.mEditNameView.getSelectionStart();
                if (selectionStart > 0) {
                    final String string = EditNameView.this.mEditNameView.getText().toString();
                    String substring = "";
                    if (string.length() > selectionStart) {
                        substring = string.substring(selectionStart);
                    }
                    EditNameView.this.mEditNameView.setText(string.substring(0, selectionStart - 1) + substring);
                    EditNameView.this.mEditNameView.setSelection(selectionStart - 1);
                }
                EditNameView.this.mEditTextImage.setImageResource(R.drawable.edit_text_image);
                EditNameView.this.mErrorTextView.setText("");
                return super.deleteSurroundingText(n, n2);
            }

            public boolean finishComposingText() {
                return super.finishComposingText();
            }

            public boolean performPrivateCommand(final String s, final Bundle bundle) {
                return super.performPrivateCommand(s, bundle);
            }

            public boolean setComposingText(final CharSequence charSequence, int selectionStart) {
                CharSequence subSequence = charSequence;
                if (charSequence.length() + EditNameView.this.mEditNameView.getText().length() > EditNameView.this.MAX_NAME_LENGTH) {
                    subSequence = charSequence.subSequence(0, EditNameView.this.MAX_NAME_LENGTH - EditNameView.this.mEditNameView.getText().length());
                    EditNameView.this.mErrorTextView.setText(EditNameView.this.getResources().getString(R.string.edit_text_error_max_length));
                    EditNameView.this.mEditTextImage.setImageResource(R.drawable.edit_text_error_image);
                }
                if (EditNameView.this.mEditNameView != null) {
                    selectionStart = EditNameView.this.mEditNameView.getSelectionStart();
                    final String string = EditNameView.this.mEditNameView.getText().toString();
                    EditNameView.this.mEditNameView.setText(string.substring(0, selectionStart) + subSequence.toString() + string.substring(selectionStart));
                    EditNameView.this.mEditNameView.setSelection(subSequence.toString().length() + selectionStart);
                    EditNameView.this.mEditTextImage.setImageResource(R.drawable.edit_text_image);
                    EditNameView.this.mErrorTextView.setText("");
                }
                return true;
            }
        };
        this.mEditNameView.setSelection(this.mEditNameView.length());
        return baseInputConnection;
    }

    public boolean onKeyUp(final int n, final KeyEvent keyEvent) {
        return super.onKeyUp(n, keyEvent);
    }

    public void onSelectionChanged(final int n, final int n2) {
        super.onSelectionChanged(n, n2);
    }
}
