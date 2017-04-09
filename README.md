# CommonViews
一些常用的自定义控件
## 使用
依赖aar->compile project(':commonviewslib')
## RoundCornerTextView
两头圆形的圆角矩形
```
<RelativeLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:cs="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    >
<com.cshawn.commonviewslib.roundcorner.RoundCornerTextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="RoundCornerTextView"
        android:padding="10dp"
        android:textSize="20sp"
        android:clickable="true"
        cs:solidColor="@color/solid_color_selector"   //填充色
        cs:strokeWidth="5dp"                          //描边宽度
        cs:strokeColor="@color/stroke_color_selector" //描边颜色
        cs:radius="30dp"                              //圆角半径
        cs:radius_right_top="100dp"                   //右上角圆角半径
        cs:radius_right_bottom="0dp"                  //右下角圆角半径
        cs:selfRoundCorner="false"                    //是否自定义圆角半径，true时上边三个值才会生效，false时显示为正常圆角矩形
        cs:shape="rectangle"                          //形状，rectangle为圆角矩形，oval为椭圆形
        cs:backgroundFitType="inside"                 //背景图展示方式，back为背景图，crop为贴边裁剪，inside为内部填充
        cs:scaleType="fitXY"                          //图片缩放方式，同ImageView
        />
</RelativeLayout>
```
