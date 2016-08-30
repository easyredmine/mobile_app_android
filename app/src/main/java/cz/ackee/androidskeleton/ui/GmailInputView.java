package cz.ackee.androidskeleton.ui;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cz.ackee.androidskeleton.R;
import cz.ackee.androidskeleton.activity.base.BaseFragmentActivity;
import cz.ackee.androidskeleton.adapter.HRArrayAdapter;
import cz.ackee.androidskeleton.adapter.Searchable;
import cz.ackee.androidskeleton.fragment.dialog.MultipleItemsDialogFragment;
import cz.ackee.androidskeleton.iface.OnDateSelectedListener;
import cz.ackee.androidskeleton.model.CustomFieldValues;
import cz.ackee.androidskeleton.model.NameValueEntity;
import cz.ackee.androidskeleton.model.TimeEntryActivity;
import cz.ackee.androidskeleton.model.response.CustomField;
import cz.ackee.androidskeleton.utils.TimeUtils;
import cz.ackee.androidskeleton.utils.Utils;

/**
 * TODO add class description
 * Created by Petr Schneider[petr.schneider@ackee.cz] on {16.3. 2015}
 */
public class GmailInputView extends RelativeLayout implements OnDateSelectedListener, DialogInterface.OnMultiChoiceClickListener, DialogInterface.OnClickListener {
    public static final String TAG = GmailInputView.class.getName();
    public static final int INPUT_TYPE_DECIMAL = 1;
    public static final int INPUT_TYPE_TEXT = 2;
    public static final int COMPONENT_TEXT = 0;
    public static final int COMPONENT_SPINNER = 1;
    public static final int COMPONENT_DATE = 2;
    public static final int COMPONENT_SWITCH = 3;
    public static final int INPUT_TYPE_NUMBER = 3;
    public static final int COMPONENT_MULTIPLE_ITEMS = 4;
    public static final int COMPONENT_NOT_EDITABLE = 5;
    public static final int COMPONENT_AUTOCOMPLETE = 155;
    private static final String SUPER_STATE_KEY = "superState";
    private static final String VALUE_KEY = "value";
    private static final String SELECTION_KEY = "selection";

    @InjectView(R.id.title)
    TextView mTitle;
    @InjectView(R.id.edit)
    EditText mEditText;
    @InjectView(R.id.spinner)
    Spinner mSpinner;
    @InjectView(R.id.wrapper)
    LinearLayout wrapper;
    @InjectView(R.id.vSwitch)
    SwitchCompat mSwitch;
    @InjectView(R.id.autocomplete)
    AutoCompleteTextView mAutoComplete;
    @InjectView(R.id.divider)
    View mDivider;

    int mQueryId;
    String mQueryString;
    CustomFieldValues mCustomFieldValues;
    CustomField mCustomField;
    OnCustomValueChangeListener mValueChangeListener;
    private ArrayList<NameValueEntity> mItems;
    private boolean[] mCheckedItems;
    private boolean[] mTmpCheckedValues;
    private int mComponent;

    public GmailInputView(Context context) {
        super(context);
        init(null);
    }

    public GmailInputView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public GmailInputView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);

    }

    @SuppressWarnings("unused")
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public GmailInputView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        inflate(getContext(), R.layout.widget_input_gmail, this);

        ButterKnife.inject(this);

        TypedArray a = getContext().getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.GmaiInputItem,
                0, 0);

//        AutofitHelper.create(mTitle);

        try {
            int arg = a.getResourceId(R.styleable.GmaiInputItem_input_title, 0);
            if (arg != 0) {
                String title = getContext().getString(arg);
                mTitle.setText(title);
            }
            arg = a.getResourceId(R.styleable.GmaiInputItem_input_hint, 0);
            if (arg != 0) {
                String hint = getContext().getString(arg);
                mEditText.setHint(hint);
            }
            int inputType = a.getInt(R.styleable.GmaiInputItem_input_inputType, 0);
            initInputStyle(inputType);
            int inputComponent = a.getInt(R.styleable.GmaiInputItem_input_inputComponent, 0);
            int uniqueId = a.getInt(R.styleable.GmaiInputItem_input_unique_id, 1);

            Log.d("u", "Unique id " + uniqueId + " " + getId());

            setInputCompontent(inputComponent);


        } finally {
            a.recycle();
        }
    }

    public void setInputCompontent(int inputComponent) {
        mComponent = inputComponent;
        mEditText.setVisibility(View.GONE);
        switch (inputComponent) {
            case COMPONENT_TEXT:
                mEditText.setVisibility(View.VISIBLE);
                mEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (!hasFocus && mValueChangeListener != null) {
                            mValueChangeListener.onValueChanged();
                        }
                    }
                });
                setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mEditText.requestFocus();

                        mEditText.post(new Runnable() {
                            @Override
                            public void run() {
                                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.showSoftInput(mEditText, InputMethodManager.SHOW_IMPLICIT);

                            }
                        });

                    }
                });
                break;
            case COMPONENT_SPINNER:
                mSpinner.setVisibility(View.VISIBLE);
                if (mValueChangeListener != null) {
                    mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            mValueChangeListener.onValueChanged();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                }
                break;
            case COMPONENT_AUTOCOMPLETE:
                mAutoComplete.setVisibility(VISIBLE);
                mAutoComplete.setClickable(true);
                mAutoComplete.setThreshold(0);
                mAutoComplete.setSingleLine(true);
                mAutoComplete.addTextChangedListener(new TextWatcher() {
                    int previousLength = 0;

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (mAutoComplete.getAdapter() instanceof Searchable) {
                            Searchable adapter = (Searchable) mAutoComplete.getAdapter();
                            adapter.searchString(s);
                        }
                        if (TextUtils.isEmpty(s) && previousLength > 0) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mAutoComplete.showDropDown();
                                }
                            }, 150);
                        }
                        previousLength = s.length();
                    }
                });

                if (mValueChangeListener != null) {
                    mAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            mValueChangeListener.onValueChanged();
                        }
                    });
                }
                break;
            case COMPONENT_SWITCH:
                mSwitch.setVisibility(View.VISIBLE);
                mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (mValueChangeListener != null) {
                            mValueChangeListener.onValueChanged();
                        }
                    }
                });
                break;
            case COMPONENT_DATE:
                mEditText.setVisibility(View.VISIBLE);
                mEditText.setFocusable(false);
                mEditText.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DatePickerDialogFragment fragment = DatePickerDialogFragment.newInstance(mEditText.getText().toString());
                        fragment.mDateSelectedListener = GmailInputView.this;
                        fragment.show(((BaseFragmentActivity) getContext()).getSupportFragmentManager(), GmailInputView.TAG + getId());
                    }
                });
                break;

            case COMPONENT_MULTIPLE_ITEMS:
                mEditText.setVisibility(View.VISIBLE);
                mEditText.setFocusable(false);
                mEditText.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mTmpCheckedValues = new boolean[mCustomFieldValues.getValues().size()];
                        getCheckedItems();
                        System.arraycopy(mCheckedItems, 0, mTmpCheckedValues, 0, mCheckedItems.length);

                        MultipleItemsDialogFragment fragment = MultipleItemsDialogFragment.newInstance(mTitle.getText().toString(), Utils.getStringList(mItems), getCheckedItems());
                        fragment.mOnMultiChoiceClickListener = GmailInputView.this;
                        fragment.mOnDoneListener = GmailInputView.this;
                        fragment.show(((BaseFragmentActivity) getContext()).getSupportFragmentManager(), GmailInputView.TAG + getId());
                    }
                });
                break;
            case COMPONENT_NOT_EDITABLE:
                mEditText.setVisibility(View.VISIBLE);
                mEditText.setEnabled(false);
                break;
        }
    }

    private boolean[] getCheckedItems() {
        if (mCheckedItems != null) {
            return mCheckedItems;
        }
        mCheckedItems = new boolean[mCustomFieldValues.getValues().size()];
        int i = 0;
        for (NameValueEntity nve : mCustomFieldValues.getValues()) {
            boolean found = false;
            for (String val : mCustomField.getValues()) {
                if (nve.getValue().equals(val)) {
                    found = true;
                    break;
                }
            }
            mCheckedItems[i++] = found;
        }

        return mCheckedItems;
    }

    public void setItems(List<NameValueEntity> items) {
        mItems = (ArrayList<NameValueEntity>) items;
    }

    public void initInputStyle(int inputType) {
        switch (inputType) {
            case INPUT_TYPE_DECIMAL:
                mEditText.setRawInputType(InputType.TYPE_CLASS_NUMBER
                        | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                mEditText.setKeyListener(DigitsKeyListener.getInstance(false, true));
                mEditText.setSingleLine(true);
                break;
            case INPUT_TYPE_TEXT:
                mEditText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (s != null && !s.toString().isEmpty()) {
                            mTitle.setVisibility(View.GONE);
                            if (((LinearLayout) findViewById(R.id.wrapper)).getWeightSum() != 1) {
                                ((LinearLayout) findViewById(R.id.wrapper)).setWeightSum(1);
                            }
                        } else {
                            mTitle.setVisibility(View.VISIBLE);
                            if (((LinearLayout) findViewById(R.id.wrapper)).getWeightSum() != 2) {
                                ((LinearLayout) findViewById(R.id.wrapper)).setWeightSum(2);
                            }
                        }
                    }
                });
                break;
            case INPUT_TYPE_NUMBER:
                mEditText.setRawInputType(InputType.TYPE_CLASS_NUMBER);
                mEditText.setKeyListener(DigitsKeyListener.getInstance(false, false));
                mEditText.setSingleLine(true);
                break;
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle b = new Bundle();
        b.putParcelable(SUPER_STATE_KEY, super.onSaveInstanceState());
        b.putString(VALUE_KEY, mEditText.getText().toString());
        b.putInt(SELECTION_KEY, mSpinner.getSelectedItemPosition());
        return b;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        Bundle b = (Bundle) state;
        super.onRestoreInstanceState(b.getParcelable(SUPER_STATE_KEY));
        mEditText.setText(b.getString(VALUE_KEY));
        mSpinner.setSelection(b.getInt(SELECTION_KEY));
        DatePickerDialogFragment fragment = (DatePickerDialogFragment) ((BaseFragmentActivity) getContext()).getSupportFragmentManager().findFragmentByTag(GmailInputView.TAG + getId());
        if (fragment != null) {
            fragment.mDateSelectedListener = this;
        }
        MultipleItemsDialogFragment dialogFragment = (MultipleItemsDialogFragment) ((BaseFragmentActivity) getContext()).getSupportFragmentManager().findFragmentByTag(GmailInputView.TAG + getId());
        if (dialogFragment != null) {
            dialogFragment.mOnDoneListener = this;
            dialogFragment.mOnMultiChoiceClickListener = this;
        }

    }

    @Override
    public void onDateSelected(Calendar calendar) {
        mEditText.setText(TimeUtils.DUE_DATE.format(calendar.getTime()));
    }

    public void showDivider(boolean show) {
        if (show) {
            mDivider.setVisibility(VISIBLE);
        } else {
            mDivider.setVisibility(INVISIBLE);
        }
    }

    public void setTitle(String name) {
        mTitle.setText(name);
    }

    public void setQueryId(int id) {
        mQueryId = id;
    }

    public String getText() {
        return mEditText.getText().toString();
    }

    public void setText(String text) {
        mEditText.setText(text);
    }

    public void setAutoCompleteAdapter(HRArrayAdapter adapter) {
        mAutoComplete.setAdapter(adapter);
    }

    public void setAdapter(ArrayAdapter adapter) {
        mSpinner.setAdapter(adapter);
    }

    public void setSelection(int selection) {
        if (mComponent == COMPONENT_AUTOCOMPLETE) {
            if (selection < mAutoComplete.getAdapter().getCount()) {
                Object entity = mAutoComplete.getAdapter().getItem(selection);
                mAutoComplete.setText(entity.toString());
                mAutoComplete.setSelection(mAutoComplete.getText().length());
            }
        } else {
            mSpinner.setSelection(selection);
        }
    }

    public TimeEntryActivity getSelectedItem() {
        return (TimeEntryActivity) mSpinner.getSelectedItem();
    }

    public Spinner getSpinner() {
        return mSpinner;
    }

    public EditText editText() {
        return mEditText;
    }

    public AutoCompleteTextView getAutoComplete() {
        return mAutoComplete;
    }

    @Override
    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
        mTmpCheckedValues[which] = isChecked;
//        showCheckedValues();
    }

    public void showCheckedValues() {
        if (mCheckedItems == null) {
            getCheckedItems();
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mCheckedItems.length; i++) {
            if (mCheckedItems[i]) {
                if (sb.toString().length() > 0) {
                    sb.append(", ");
                }
                sb.append(mCustomFieldValues.getValues().get(i).getName());
            }
        }
        mEditText.setText(sb.toString());
    }

    public void setSwitchValue(boolean boolValue) {
        mSwitch.setChecked(boolValue);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        System.arraycopy(mTmpCheckedValues, 0, mCheckedItems, 0, mCheckedItems.length);
        if (mValueChangeListener != null) {
            mValueChangeListener.onValueChanged();
        }
        showCheckedValues();
    }

    public CustomField getCustomField() {
        CustomField cv = mCustomField;
        switch (mComponent) {
            case COMPONENT_MULTIPLE_ITEMS:
                ArrayList<String> values = new ArrayList<>();
                for (int i = 0; i < mCheckedItems.length; i++) {
                    if (mCheckedItems[i]) {
                        values.add(mCustomFieldValues.getValues().get(i).getValue());
                    }
                }
                cv.setValue(values);
                break;
            case COMPONENT_SWITCH:
                cv.setValue(mSwitch.isChecked() ? "1" : "0");
                break;
            case COMPONENT_DATE:
                cv.setValue(TimeUtils.getTimeFormatted(mEditText.getText().toString(), TimeUtils.DUE_DATE, TimeUtils.ATOM_FORMAT_DATE));
                break;
            case COMPONENT_SPINNER:
                if (mCustomField.getFieldFormat().equals("easy_percent")) {
                    cv.setValue(mSpinner.getSelectedItemPosition() * 10.0 + "");
                } else {
                    if (mSpinner.getSelectedItemPosition() != 0) {
                        cv.setValue(mCustomFieldValues.getValues().get(mSpinner.getSelectedItemPosition() - 1).getValue());
                    }
                }
                break;
            case COMPONENT_TEXT:
                cv.setValue(mEditText.getText().toString());
                break;
        }
        return cv;
    }

    public void setValueChangeListener(OnCustomValueChangeListener valueChangeListener) {
        mValueChangeListener = valueChangeListener;
    }

    public void setCustomField(CustomField cf, CustomFieldValues cfv) {
        mCustomField = cf;
        mCustomFieldValues = cfv;
        if (mCustomFieldValues != null && mCustomFieldValues.getValues() != null && mCustomField.getValues() != null) {
            getCheckedItems();
        }
    }

    public interface OnCustomValueChangeListener {
        void onValueChanged();
    }

    public static class DatePickerDialogFragment extends DialogFragment {
        private static final String DATE_KEY = "date";

        public OnDateSelectedListener mDateSelectedListener;

        public static DatePickerDialogFragment newInstance(String time) {
            Bundle args = new Bundle();
            args.putString(DATE_KEY, time);
            DatePickerDialogFragment tpdf = new DatePickerDialogFragment();
            tpdf.setArguments(args);
            return tpdf;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            try {
                String date = getArguments().getString(DATE_KEY);
                Date d;
                if (TextUtils.isEmpty(date)) {
                    d = Calendar.getInstance().getTime();
                } else {
                    d = TimeUtils.DUE_DATE.parse(getArguments().getString(DATE_KEY));
                }
                Calendar c = Calendar.getInstance();
                c.setTime(d);
                DatePickerDialog dpd = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        dismiss();
                        if (mDateSelectedListener != null) {
                            Calendar c = Calendar.getInstance();
                            c.set(Calendar.MONTH, monthOfYear);
                            c.set(Calendar.YEAR, year);
                            c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                            mDateSelectedListener.onDateSelected(c);
                        }
                    }
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
                return dpd;

            } catch (ParseException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
