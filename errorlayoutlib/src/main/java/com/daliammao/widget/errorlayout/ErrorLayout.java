package com.daliammao.widget.errorlayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.daliammao.widget.errorlayout.utils.UiUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author: zhoupengwei
 * @time:2015/8/5-14:36
 * @Email: 496946423@qq.com
 * @desc: 自定义空视图
 */
public class ErrorLayout extends LinearLayout {

    //状态值
    //普通状态-隐藏控件
    public static final int STATE_HIDE = 1;
    //网络错误
    public static final int STATE_NETWORK_ERROR = 2;
    //网络加载
    public static final int STATE_NETWORK_LOADING = 3;
    //没有数据
    public static final int STATE_NODATA = 4;
    //没有登录
    public static final int STATE_NOLOGIN = 5;

    //资源标记
    //需要显示的图片
    public static final int RESOURCES_IMAGE = 6;
    //需要提示的话
    public static final int RESOURCES_NOTE = 7;
    //按钮显示的话
    public static final int RESOURCES_BUTTON = 8;

    private int mCurrentState = STATE_HIDE;

    //显示错误图片
    private ImageView mErrorImage;
    //加载中转轮
    private ProgressBar mErrorProgress;
    //错误提示
    private TextView mErrorNote;
    //按钮
    private TextView mErrorButton;

    /**
     * 第一个值 状态 第二个值 资源类型 第三个值 资源id
     */
    private Map<Integer, Map<Integer, Integer>> mResourcesTable = new HashMap<>();
    //加载中动画
    private int mIndeterminateDrawable = 0;
    private AnimationDrawable mProgressAnimation;

    private boolean isButtonVisible = false;
    private boolean isClickEnable = true;
    private OnErrorClickListener mErrorListener;

    public ErrorLayout(Context context) {
        this(context, null);
    }

    public ErrorLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ErrorLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setBackgroundColor(Color.WHITE);
        resourcesReset();
        View view = View.inflate(context, R.layout.view_error_layout, null);
        mErrorImage = (ImageView) view.findViewById(R.id.iv_error_image);
        mErrorProgress = (ProgressBar) view.findViewById(R.id.pb_error_progress);
        mErrorNote = (TextView) view.findViewById(R.id.tv_error_note);
        mErrorButton = (TextView) view.findViewById(R.id.tv_error_button);

        TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.ErrorLayout, 0, 0);
        if (arr != null) {
            setResources(STATE_NETWORK_ERROR, RESOURCES_IMAGE,
                    arr.getResourceId(R.styleable.ErrorLayout_error_network_src, getResources(STATE_NETWORK_ERROR, RESOURCES_IMAGE)));
            setResources(STATE_NODATA, RESOURCES_IMAGE,
                    arr.getResourceId(R.styleable.ErrorLayout_error_nodata_src, getResources(STATE_NODATA, RESOURCES_IMAGE)));
            setResources(STATE_NOLOGIN, RESOURCES_IMAGE,
                    arr.getResourceId(R.styleable.ErrorLayout_error_nologin_src, getResources(STATE_NOLOGIN, RESOURCES_IMAGE)));

            setResources(STATE_NETWORK_ERROR, RESOURCES_NOTE,
                    arr.getResourceId(R.styleable.ErrorLayout_error_network_note_text, getResources(STATE_NETWORK_ERROR, RESOURCES_NOTE)));
            setResources(STATE_NODATA, RESOURCES_NOTE,
                    arr.getResourceId(R.styleable.ErrorLayout_error_nodata_note_text, getResources(STATE_NODATA, RESOURCES_NOTE)));
            setResources(STATE_NOLOGIN, RESOURCES_NOTE,
                    arr.getResourceId(R.styleable.ErrorLayout_error_nologin_note_text, getResources(STATE_NOLOGIN, RESOURCES_NOTE)));
            setResources(STATE_NETWORK_LOADING, RESOURCES_NOTE,
                    arr.getResourceId(R.styleable.ErrorLayout_error_loading_note_text, getResources(STATE_NETWORK_LOADING, RESOURCES_NOTE)));

            setResources(STATE_NETWORK_ERROR, RESOURCES_BUTTON,
                    arr.getResourceId(R.styleable.ErrorLayout_error_network_button_text, getResources(STATE_NETWORK_ERROR, RESOURCES_BUTTON)));
            setResources(STATE_NODATA, RESOURCES_BUTTON,
                    arr.getResourceId(R.styleable.ErrorLayout_error_nodata_button_text, getResources(STATE_NODATA, RESOURCES_BUTTON)));
            setResources(STATE_NOLOGIN, RESOURCES_BUTTON,
                    arr.getResourceId(R.styleable.ErrorLayout_error_nologin_button_text, getResources(STATE_NOLOGIN, RESOURCES_BUTTON)));

            mIndeterminateDrawable = arr.getResourceId(R.styleable.ErrorLayout_error_loading_indeterminateDrawable, mIndeterminateDrawable);

            Drawable btnBackground = arr.getDrawable(R.styleable.ErrorLayout_error_button_background);
            if (btnBackground == null) {
                mErrorButton.setBackgroundResource(R.drawable.shape_transparent_3dp);
            } else {
                mErrorButton.setBackgroundDrawable(btnBackground);
            }

            mErrorNote.setTextColor(
                    arr.getColor(R.styleable.ErrorLayout_error_note_textcolor, 0XFF666666));
            mErrorNote.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    arr.getDimension(R.styleable.ErrorLayout_error_note_textsize, mErrorButton.getTextSize()));
            mErrorButton.setTextColor(
                    arr.getColor(R.styleable.ErrorLayout_error_button_textcolor, 0XFF666666));
            mErrorButton.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    arr.getDimension(R.styleable.ErrorLayout_error_button_textsize, mErrorButton.getTextSize()));

            int buttonPaddingLeft =
                    arr.getDimensionPixelOffset(R.styleable.ErrorLayout_error_button_paddingLeft, UiUtil.dip2px(getContext(), 10));
            int buttonPaddingTop =
                    arr.getDimensionPixelOffset(R.styleable.ErrorLayout_error_button_paddingTop, UiUtil.dip2px(getContext(), 5));
            int buttonPaddingRight =
                    arr.getDimensionPixelOffset(R.styleable.ErrorLayout_error_button_paddingRight, UiUtil.dip2px(getContext(), 10));
            int buttonPaddingBottom =
                    arr.getDimensionPixelOffset(R.styleable.ErrorLayout_error_button_paddingBottom, UiUtil.dip2px(getContext(), 5));
            int buttonPadding =
                    arr.getDimensionPixelOffset(R.styleable.ErrorLayout_error_button_padding, 0);
            if (buttonPadding == 0) {
                mErrorButton.setPadding(buttonPaddingLeft, buttonPaddingTop, buttonPaddingRight, buttonPaddingBottom);
            } else {
                mErrorButton.setPadding(buttonPadding, buttonPadding, buttonPadding, buttonPadding);
            }

            LayoutParams noteParams = (LayoutParams) mErrorNote.getLayoutParams();
            int notedistance = arr.getDimensionPixelOffset(R.styleable.ErrorLayout_error_image_note_distance, UiUtil.dip2px(getContext(), 34));
            noteParams.setMargins(0,
                    notedistance
                    , 0, 0);
            mErrorNote.setLayoutParams(noteParams);
            LayoutParams buttonParams = (LayoutParams) mErrorButton.getLayoutParams();
            int buttondistance = arr.getDimensionPixelOffset(R.styleable.ErrorLayout_error_note_button_distance, UiUtil.dip2px(getContext(), 30));
            buttonParams.setMargins(0,
                    buttondistance
                    , 0, 0);
            mErrorButton.setLayoutParams(buttonParams);


            Drawable background = arr.getDrawable(R.styleable.ErrorLayout_error_background);
            if (background != null) {
                setBackgroundDrawable(background);
            }

            arr.recycle();
        }

        mErrorImage.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isClickEnable && !isButtonVisible && mErrorListener != null) {
                    mErrorListener.onClick(ErrorLayout.this, mCurrentState);
                }
            }
        });
        mErrorButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isClickEnable && isButtonVisible && mErrorListener != null) {
                    mErrorListener.onClick(ErrorLayout.this, mCurrentState);
                }
            }
        });

        addView(view);
        setErrorType(mCurrentState);
    }

    @Override
    public void setVisibility(int visibility) {
        if (visibility == GONE)
            mCurrentState = STATE_HIDE;
        super.setVisibility(visibility);
    }

    public void dismiss() {
        setErrorType(STATE_HIDE);
    }

    public int getErrorState() {
        return mCurrentState;
    }

    public void setErrorType(int state) {
        switch (state) {
            case STATE_HIDE:
                setVisibility(View.GONE);
                //设置STATE_HIDE就不用进行下面的步骤了
                return;
            case STATE_NETWORK_ERROR:
                mErrorImage.setVisibility(View.VISIBLE);
                mErrorProgress.setVisibility(View.GONE);
                if (isButtonVisible) {
                    mErrorButton.setVisibility(isButtonVisible ? VISIBLE : GONE);
                }
                isClickEnable = true;
                break;
            case STATE_NETWORK_LOADING:
                if (mIndeterminateDrawable <= 0) {
                    mErrorImage.setVisibility(View.GONE);
                    mErrorProgress.setVisibility(View.VISIBLE);
                } else {
                    mErrorImage.setVisibility(View.VISIBLE);
                    mErrorProgress.setVisibility(View.GONE);
                }
                mErrorButton.setVisibility(View.GONE);
                isClickEnable = false;
                break;
            case STATE_NODATA:
                mErrorImage.setVisibility(View.VISIBLE);
                mErrorProgress.setVisibility(View.GONE);
                if (isButtonVisible) {
                    mErrorButton.setVisibility(isButtonVisible ? VISIBLE : GONE);
                }
                isClickEnable = true;
                break;
            case STATE_NOLOGIN:
                mErrorImage.setVisibility(View.VISIBLE);
                mErrorProgress.setVisibility(View.GONE);
                if (isButtonVisible) {
                    mErrorButton.setVisibility(isButtonVisible ? VISIBLE : GONE);
                }
                isClickEnable = true;
                break;
            default:
                //未知状态值,不必处理
                return;
        }
        mCurrentState = state;
        setVisibility(View.VISIBLE);
        if (getResources(state, RESOURCES_NOTE) != 0) {
            mErrorNote.setText(getResources(state, RESOURCES_NOTE));
        } else {
            mErrorNote.setText("");
        }
        if (getResources(state, RESOURCES_BUTTON) != 0) {
            mErrorButton.setText(getResources(state, RESOURCES_BUTTON));
        } else {
            mErrorButton.setText("");
        }

        if (state == STATE_NETWORK_LOADING && mIndeterminateDrawable > 0) {
            startLoadingAnimation();
        } else {
            stopLoadingAnimation();
            int img = getResources(state, RESOURCES_IMAGE);
            try {
                mErrorImage.setImageDrawable(getContext().getResources().getDrawable(img));
            } catch (Exception ignored) {
                //如过出错(可能找不到该id对应资源)，则清空。
                mErrorImage.setImageDrawable(null);
            }
        }
    }

    /**
     * 返回指定状态与资源类型对应的资源
     *
     * @param state
     * @param resourceType
     * @return
     */
    private int getResources(int state, int resourceType) {
        if (mResourcesTable.containsKey(state)) {
            Map<Integer, Integer> values = mResourcesTable.get(state);
            if (values.containsKey(resourceType)) {
                return values.get(resourceType);
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }

    /**
     * 根据指定的状态,指定的资源类型设置资源
     *
     * @param state        指定状态
     * @param resourceType 指定资源类型
     * @param resource     资源
     */
    public void setResources(int state, int resourceType, int resource) {
        Map<Integer, Integer> values;
        if (mResourcesTable.containsKey(state)) {
            values = mResourcesTable.get(state);
        } else {
            values = new HashMap<>();
        }
        values.put(resourceType, resource);
        mResourcesTable.put(state, values);
    }

    /**
     * 批量设置资源,即states集合里的所有状态所有resourceType类型的资源全部设置为resource值
     *
     * @param states       指定状态的集合
     * @param resourceType 指定资源的类型
     * @param resource     资源
     */
    public void batchSetResources(Set<Integer> states, int resourceType, int resource) {
        for (int state : states) {
            setResources(state, resourceType, resource);
        }
    }

    /**
     * 按钮是否显示
     *
     * @return
     */
    public boolean isButtonVisible() {
        return isButtonVisible;
    }

    /**
     * 设置按钮是否显示
     *
     * @param isButtonVisible
     */
    public void setIsButtonVisible(boolean isButtonVisible) {
        this.isButtonVisible = isButtonVisible;
    }

    public void setProgressIndeterminateDrawable(int animResources) {
        this.mIndeterminateDrawable = animResources;
    }

    private void startLoadingAnimation() {
        try {
            mErrorImage.setImageDrawable(getContext().getResources().getDrawable(mIndeterminateDrawable));
            mProgressAnimation = (AnimationDrawable) mErrorImage.getDrawable();
        } catch (Exception e) {
            mProgressAnimation = null;
        }
        if (mProgressAnimation == null) {
            return;
        }

        mProgressAnimation.start();
    }

    private void stopLoadingAnimation() {
        if (mProgressAnimation == null) {
            return;
        }
        mProgressAnimation.stop();
    }

    /**
     * 重置所有资源,恢复到默认状态
     */
    public void resourcesReset() {
        mResourcesTable.clear();

        setResources(STATE_NETWORK_ERROR, RESOURCES_IMAGE, R.mipmap.ic_network_error);
        setResources(STATE_NODATA, RESOURCES_IMAGE, R.mipmap.ic_nodata_or_nologin);
        setResources(STATE_NOLOGIN, RESOURCES_IMAGE, R.mipmap.ic_nodata_or_nologin);

        setResources(STATE_NETWORK_ERROR, RESOURCES_NOTE, R.string.error_network_error);
        setResources(STATE_NETWORK_LOADING, RESOURCES_NOTE, R.string.error_network_loading);
        setResources(STATE_NODATA, RESOURCES_NOTE, R.string.error_nodata);
        setResources(STATE_NOLOGIN, RESOURCES_NOTE, R.string.error_nologin);

        setResources(STATE_NETWORK_ERROR, RESOURCES_BUTTON, R.string.error_refresh);
        setResources(STATE_NODATA, RESOURCES_BUTTON, R.string.error_refresh);
        setResources(STATE_NOLOGIN, RESOURCES_BUTTON, R.string.error_relogin);
    }

    /**
     * 设置该组件专门的点击监听.设置完毕后,如果按钮不显示,则点击图片触发.如果按钮显示则点击按钮促发.
     *
     * @param listener
     */
    public void setonErrorClickListener(OnErrorClickListener listener) {
        this.mErrorListener = listener;
    }

    /**
     * 点击监听类
     */
    public interface OnErrorClickListener {
        void onClick(View v, int state);
    }
}
