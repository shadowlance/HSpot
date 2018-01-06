package com.zhupiter.hspot.ui.widget;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.zhupiter.hspot.R;

/**
 * Created by zhupiter on 17-2-25.
 */

// TODO: 17-3-17 屏幕旋转时将view的长度修复
public class SearchBar extends FrameLayout implements android.support.v7.view.CollapsibleActionView,View.OnClickListener,TextView.OnEditorActionListener {

    private final static boolean STATE_SEARCH = true;
    private final static boolean STATE_ADVANCED = false;

    private static final String TAG = "SEARCHBAR";

    // TODO: 17-3-3 Edittext 能否变为AutoCompleteTextView
    private AppCompatEditText mEditText;
    private ImageView mDeleteButton;
    private ImageView mActionButton;

    private View mSearchFrame;
    private View mSearchLayout;

    private UpdatableTouchDelegate mTouchDelegate;
    private Rect mEditTextBound = new Rect();
    private Rect mEditTextBoundExpanded = new Rect();

    private ListView mSearchTips;

    private searchHelper mHelper;

    private boolean mSearchState;
    private boolean expandedInActionView;
    private int mCollapsedImeOptions;
    private boolean isCleaningFocus;
    private boolean mInAnimation;

    private Toolbar mToolBar;

    private int[] mTemp = new int[2];
    private int[] mTemp2 = new int[2];

    /*
    show IME
     */
    private Runnable mShowImeRunnable = new Runnable() {
        @Override
        public void run() {
            InputMethodManager imm = getContext().getSystemService(InputMethodManager.class);

            if (imm != null) {
                imm.showSoftInput(mEditText,0);
            }
        }
    };

    public SearchBar(Context context) {
        super(context);
        init(context);
    }

    public SearchBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SearchBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.search_bar, this);

        mSearchFrame = findViewById(R.id.search_frame);
        mSearchLayout = findViewById(R.id.search_edit_layout);
        mEditText = (AppCompatEditText) findViewById(R.id.text_search);
        mActionButton = (ImageView) findViewById(R.id.action_search);
        mDeleteButton = (ImageView) findViewById(R.id.bt_search_delete);
        mSearchTips = (ListView) findViewById(R.id.tips_search);

        mDeleteButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_clear_white, null));
        mActionButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_expand_more_white, null));

        mDeleteButton.setOnClickListener(this);
        mActionButton.setOnClickListener(this);

        mEditText.setOnClickListener(this);
        mEditText.addTextChangedListener(mTextWatcher);
        mEditText.setOnEditorActionListener(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        switch (widthMode) {
            case MeasureSpec.AT_MOST:
                width = Math.min(getPreferredWidth(), width);
                break;
            case MeasureSpec.EXACTLY:
                break;
            case MeasureSpec.UNSPECIFIED:
                width = getPreferredWidth();
                break;
        }
        widthMode = MeasureSpec.EXACTLY;
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        switch (heightMode) {
            case MeasureSpec.AT_MOST:
                height = Math.min(getPreferredHeight(), height);
                break;
            case MeasureSpec.EXACTLY:
                break;
            case MeasureSpec.UNSPECIFIED:
                height = getPreferredHeight();
                break;
        }
        heightMode = MeasureSpec.EXACTLY;
        super.onMeasure(MeasureSpec.makeMeasureSpec(width, widthMode),
                MeasureSpec.makeMeasureSpec(height, heightMode));

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            // Expand TextView touch target to be the height of the parent in order to
            // allow it to be up to 48dp.
            getChildBounds(mEditText, mEditTextBound);
            mEditTextBoundExpanded.set(
                    mEditTextBound.left, 0, mEditTextBound.right, bottom - top);
            if (mTouchDelegate == null) {
                mTouchDelegate = new UpdatableTouchDelegate(mEditTextBoundExpanded,
                        mEditTextBound, mEditText);
                setTouchDelegate(mTouchDelegate);
            } else {
                mTouchDelegate.setBounds(mEditTextBoundExpanded, mEditTextBound);
            }
        }
    }

    private void getChildBounds(View view, Rect rect) {
        view.getLocationInWindow(mTemp);
        getLocationInWindow(mTemp2);
        final int top = mTemp[1] - mTemp2[1];
        final int left = mTemp[0] - mTemp2[0];
        rect.set(left, top, left + view.getWidth(), top + view.getHeight());
    }

    private int getPreferredWidth() {
        return getContext().getResources().getDimensionPixelSize(R.dimen.search_bar_width);
    }

    private int getPreferredHeight() {
        return getContext().getResources().getDimensionPixelSize(R.dimen.search_bar_height);
    }

    @Override
    public void onClick(View v) {
        if (v == mEditText) {
            onTextClicked();
        } else if (v == mDeleteButton) {
            onDeleteClicked();
        } else if (v == mActionButton) {
            onActionButtonClicked(mSearchState);
        }
    }

    private void onDeleteClicked() {
        mEditText.setText("");
        mEditText.requestFocus();
        showImeSuggestionList();
    }

    private void onTextClicked() {
        // TODO: 17-3-9
        //showImeAndSuggestions();
    }

    private void onActionButtonClicked(boolean searchState) {
        if (null == mSearchFrame) {
            return;
        }
        if (searchState) {
            onSubmitQuery();
        } else {
            mHelper.showAdvancedLayout();
            setSearchState(STATE_SEARCH);
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int eventId, KeyEvent event) {
        if (v == mEditText) {
            if (eventId == EditorInfo.IME_ACTION_SEARCH || eventId == EditorInfo.IME_NULL) {
                onSubmitQuery();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean requestFocus(int direction, Rect previouslyFocusedRect) {
        //如果在清除focus，或不能获得焦点就暂时无效
        if (isCleaningFocus || !isFocusable()) {
            return false;
        } else {
            return super.requestFocus(direction, previouslyFocusedRect);
        }
    }

    @Override
    public void clearFocus(){
        isCleaningFocus = true;
        hideImeSuggestionList();
        super.clearFocus();
        hideImeSuggestionList();
        isCleaningFocus = false;
    }


    private void setQuery(String query, boolean submit) {
        mEditText.setText(query);
        if (query != null) {
            // TODO: 17-3-7 这是干啥？
            mEditText.setSelection(mEditText.length());
        }
        if (submit && TextUtils.isEmpty(query)) {
            onSubmitQuery();
        }
    }

    @Override
    public void onActionViewCollapsed(){
        setQuery("", false);
        clearFocus();
        mEditText.setImeOptions(mCollapsedImeOptions);
        showAllToolbarItems();
        expandedInActionView = false;
    }

    @Override
    public void onActionViewExpanded(){
        if (expandedInActionView) return;
        expandedInActionView = true;
        hideAllToolBarItems();
        mCollapsedImeOptions = mEditText.getImeOptions();
        mEditText.setImeOptions(mCollapsedImeOptions | EditorInfo.IME_FLAG_NO_FULLSCREEN);
        mEditText.setText("");
    }

    private void showAllToolbarItems() {
        mToolBar = (Toolbar) this.getParent();
        Menu menu = mToolBar.getMenu();
        menu.setGroupVisible(0,true);
    }

    private void hideAllToolBarItems() {
        mToolBar = (Toolbar) this.getParent();
        Menu menu = mToolBar.getMenu();
        menu.setGroupVisible(0,false);
    }

    private void setImeVisibility(final boolean visible) {
        if (visible) {
            post(mShowImeRunnable);
        } else {
            removeCallbacks(mShowImeRunnable);
            InputMethodManager imm = getContext().getSystemService(InputMethodManager.class);

            if (imm != null) {
                imm.hideSoftInputFromWindow(getWindowToken(), 0);
            }
        }
    }

    private void updateActionAndClose() {
        final boolean hasText = !TextUtils.isEmpty(mEditText.getText());
        mDeleteButton.setVisibility(hasText ? VISIBLE : GONE);
        setSearchState(hasText);
    }

    private void setSearchState(boolean doSearch) {
        Drawable searchIcon = getResources().getDrawable(R.drawable.ic_search_white,null);
        Drawable advancedOptionIcon = getResources().getDrawable(R.drawable.ic_expand_more_white,null);
        if (doSearch) {
            mActionButton.setImageDrawable(searchIcon);
            mSearchState = STATE_SEARCH;
        } else {
            mActionButton.setImageDrawable(advancedOptionIcon);
            mSearchState = STATE_ADVANCED;
        }
    }

    private void showImeSuggestionList(){
        showImeSuggestionList(true);
    }

    private void showImeSuggestionList(boolean animation){
        setImeVisibility(true);

        // TODO: 17-3-3
        //updateSuggestions()
        if (animation) {
            //do something with animations
        } else {
            mSearchTips.setVisibility(View.VISIBLE);
        }
    }

    private void hideImeSuggestionList() {
        hideImeSuggestionsList(true);
    }

    private void hideImeSuggestionsList(boolean animation) {
        setImeVisibility(false);

        //// TODO: 17-3-3
        //updateSuggestions()
        if (animation) {
            //do something with animations
        } else {
            mSearchTips.setVisibility(View.GONE);
        }
    }

    /*
    提交search请求
     */
    private void onSubmitQuery() {
        String query = mEditText.getText().toString().trim();
        if (!TextUtils.isEmpty(query)) {
            mHelper.onApplySearch(query);
            // TODO: 17-3-3 Add SearchDataBase
            hideImeSuggestionList();
        }
    }

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            CharSequence text = mEditText.getText();
            updateActionAndClose();
            // TODO: 17-3-9 Add Database
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    // TODO: 17-3-12 以后看懂代码。。。
    private static class UpdatableTouchDelegate extends TouchDelegate {
        /**
         * View that should receive forwarded touch events
         */
        private final View mDelegateView;
        /**
         * Bounds in local coordinates of the containing view that should be mapped to the delegate
         * view. This rect is used for initial hit testing.
         */
        private final Rect mTargetBounds;
        /**
         * Bounds in local coordinates of the containing view that are actual bounds of the delegate
         * view. This rect is used for event coordinate mapping.
         */
        private final Rect mActualBounds;
        /**
         * mTargetBounds inflated to include some slop. This rect is to track whether the motion events
         * should be considered to be be within the delegate view.
         */
        private final Rect mSlopBounds;
        private final int mSlop;
        /**
         * True if the delegate had been targeted on a down event (intersected mTargetBounds).
         */
        private boolean mDelegateTargeted;

        public UpdatableTouchDelegate(Rect targetBounds, Rect actualBounds, View delegateView) {
            super(targetBounds, delegateView);
            mSlop = ViewConfiguration.get(delegateView.getContext()).getScaledTouchSlop();
            mTargetBounds = new Rect();
            mSlopBounds = new Rect();
            mActualBounds = new Rect();
            setBounds(targetBounds, actualBounds);
            mDelegateView = delegateView;
        }
        public void setBounds(Rect desiredBounds, Rect actualBounds) {
            mTargetBounds.set(desiredBounds);
            mSlopBounds.set(desiredBounds);
            mSlopBounds.inset(-mSlop, -mSlop);
            mActualBounds.set(actualBounds);
        }
        @Override
        public boolean onTouchEvent(MotionEvent event) {
            final int x = (int) event.getX();
            final int y = (int) event.getY();
            boolean sendToDelegate = false;
            boolean hit = true;
            boolean handled = false;
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (mTargetBounds.contains(x, y)) {
                        mDelegateTargeted = true;
                        sendToDelegate = true;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_MOVE:
                    sendToDelegate = mDelegateTargeted;
                    if (sendToDelegate) {
                        if (!mSlopBounds.contains(x, y)) {
                            hit = false;
                        }
                    }
                    break;
                case MotionEvent.ACTION_CANCEL:
                    sendToDelegate = mDelegateTargeted;
                    mDelegateTargeted = false;
                    break;
            }
            if (sendToDelegate) {
                if (hit && !mActualBounds.contains(x, y)) {
                    // Offset event coordinates to be in the center of the target view since we
                    // are within the targetBounds, but not inside the actual bounds of
                    // mDelegateView
                    event.setLocation(mDelegateView.getWidth() / 2,
                            mDelegateView.getHeight() / 2);
                } else {
                    // Offset event coordinates to the target view coordinates.
                    event.setLocation(x - mActualBounds.left, y - mActualBounds.top);
                }
                handled = mDelegateView.dispatchTouchEvent(event);
            }
            return handled;
        }
    }

    public interface searchHelper{
        void onApplySearch(String query);
        void showAdvancedLayout();
    }

}