# ErrorLayout

[apk](https://github.com/daliammao/ErrorLayout/raw/master/app/apk/app-debug.apk)

每次 没有数据、网络出错、未登录，网络加载中我们总会想显示一个错误提示。它们的样式总是类似，每次都去绘制这些雷同的页面实在太令人头疼了。所以我把这种页面抽成了一个控件，并用status去切换它们。

status分为

```java
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
```

五种状态，可以在xml配置显示的文案和图片。

##### xml中配置示例

```xml
    <com.daliammao.widget.errorlayout.ErrorLayout xmlns:error="http://schemas.android.com/apk/res-auto"
        android:id="@+id/el_main"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        error:error_loading_note_text="@string/error_network_loading_try"
        error:error_network_button_text="@string/error_refresh_try"

        error:error_network_note_text="@string/error_network_error_try"
        error:error_network_src="@mipmap/ic_nodata_or_nologin"
        error:error_nodata_button_text="@string/error_refresh_try"
        error:error_nodata_note_text="@string/error_nodata_try"

        error:error_background="#eeeeee"

        error:error_nodata_src="@mipmap/ic_network_error"
        error:error_nologin_button_text="@string/error_nologin_try"
        error:error_nologin_note_text="@string/error_nologin_try"
        error:error_nologin_src="@mipmap/ic_launcher" />
```

也可以通过

```java
mErrorLayout.setResources(ErrorLayout.STATE_NETWORK_ERROR, ErrorLayout.RESOURCES_BUTTON, R.string.try_set_resources);

```
指定某个状态配置某个资源。

最后通过

```java
mErrorLayout.setErrorType(ErrorLayout.STATE_HIDE);
```
切换status显示指定状态的UI