## Android 动态换肤框架
Android 动态换肤框架，支持动态加载插件资源，无需重启Activity，拓展能力很强

<br>
<br>

## 演示
**Pixel 4 XL Android 13 实机测试**





https://user-images.githubusercontent.com/65901383/226703180-b9e8e8b9-b5ae-496d-a5a8-185d2a8203f1.mp4

<br>

[下载演示Demo](http://81.71.83.180/apk/SkinPlugin.apk)



<br>
<br>
<br>
<br>
<br>


## 使用

### 1、实现换肤能力
将需要换肤页面的Activity继承自`SkinBaseActivity`
```java
public class MainActivity extends SkinBaseActivity {
    //....
}
```

<br>
<br>
<br>
<br>

### 2、加载皮肤包
```java
  SkinManager.INSTANCE.loadSkinFile(Environment.getExternalStorageDirectory() + "/skin.apk");
```

<br>
<br>

对于Assets目录，使用
```java
SkinManager.INSTANCE.loadSkinAssets("skin.apk");
```
加载Assts文件，会copy一份到sdcard下，具体路径为：&nbsp;
`/sdcard/Android/data/${packageName}/cache/skinCache/**.apk`
<br>
更推荐使用`SkinManager.INSTANCE.loadSkinFile`


<br>
<br>


***使用`app:skinEnable="true or false"` 来决定View是否开启换肤。  
优先级大于`setSkinGlobalEnable`,默认全局开启***

<br>
<br>
<br>
<br>
<br>
<br>


## 换肤能力
框架默认支持`background`、`src`、`textColor`、`text`常用资源切换，若需要增加功能

<br>

使用`addSkinAttrHolder`方法添加需要的属性
```java
public <T> void addSkinAttrHolder(String attrName, ISkinMethodHolder<? extends View, ?> methodHolder)
```

<br>

<br>

**这个方法接收两个参数**
1. 第一个参数是属性名，如：
```xml
<EditText
  android:id="@+id/editText"
  android:layout_width="match_parent"
  android:layout_height="wrap_content"
  android:textColorHint="@color/color_blue_50" />
```
这里有一个`EditText`，我们要增加textColorHint的换肤能力，那么属性名就是`textColorHint`.

<br>

<br>

2. 第二个参数是属性`set`方法的`方法引用`，如 `EditText::setHintTextColor`
```java
示例
ISkinMethodHolder<EditText, Integer> setHintTextColor = EditText::setHintTextColor;
SkinManager.INSTANCE.addSkinAttrHolder("textColorHint", setHintTextColor);
```

到这里，我们增加一个换肤能力就实现了！！！

<br>
<br>
<br>

**请注意，`ISkinMethodHolder<EditText, Integer>`接口中的第二个类型表示的是`set`方法的参数类型**

_**必须是设置资源值的方法，不能使用设置资源Id的重载方法！！！**_

原因是我们即使我们传入资源包中的资源ID，`set`方法内部也是通过APP的Resource对象获取的`值`

<br>
<br>
<br>
<br>

**关于复杂参数的`set`方法处理**

比如`TextView`的`setTextSize()`方法，接受两个参数：
![image.png](https://cdn.nlark.com/yuque/0/2023/png/29246190/1679416256338-e755e6df-f224-45b9-bf16-a74ba70818fe.png#averageHue=%232b2b2b&clientId=ufa0a578a-b5ad-4&from=paste&height=280&id=u99af143c&name=image.png&originHeight=350&originWidth=902&originalType=binary&ratio=1.25&rotation=0&showTitle=false&size=54312&status=done&style=none&taskId=ue740713b-23ff-48a1-9867-80c7f355ca0&title=&width=721.6)

<br>

我们还是可以通过方法引用来实现，这里需要转化一下

**处理方式**：
```java

static void setTextSize(TextView view, float px) {
    view.setTextSize(TypedValue.COMPLEX_UNIT_PX, px);
}

public static final ISkinMethodHolder<TextView, Float> setTextSize = SkinMethod::setTextSize;


//	......

SkinManager.INSTANCE.addSkinAttrHolder("textSize", setTextSize);

```

<br>
<br>
<br>

_**关于自定义View，请提供属性的`set`方法即可**_


<br>
<br>
<br>
<br>
<br>
<br>
<br>



## 功能拓展

### MethodAcceptAndThen
使用`ISkinMethodHolder.andThen()`来转换成`MethodAcceptAndThen`对象
这是对`ISkinMethodHolder`的功能拓展，增加了一个`after`操作，用于这条属性在执行完换肤之后的操作.

<br>

**_注意！！ 只有设置了 `app:skinMethodTag=""`属性的`View` 才会执行此操作，未设置完全不会执行_**
```xml
<TextView
  android:layout_width="match_parent"
  android:layout_height="wrap_content"
  android:text="默认主题"
  app:skinMethodTag="title"
  android:textColor="@color/color_blue_50" />
```


```java
示例
//文字颜色
ISkinMethodHolder<TextView, Integer> setTextColor = TextView::setTextColor;
MethodAcceptAndThen<TextView, Integer> setTextColorAndThen = setTextColor.andThen((textView, integer) -> {
    if ("title".equals(textView.getTag(R.id.skinMethodTagID))) {
        textView.setText("春节主题皮肤");
    }
});
SkinManager.INSTANCE.addSkinAttrHolder("setTextColor", setTextColorAndThen);
```

<br>
<br>
<br>


### MethodAcceptProxy
使用`ISkinMethodHolder.proxy()`来转换成`MethodAcceptProxy`对象
这是对`ISkinMethodHolder`的功能拓展，代理当前`set`方法，实现自己逻辑.

<br>

```xml
示例
ISkinMethodHolder<View, Drawable> setBackground = View::setBackground;
MethodAcceptProxy<View, Drawable> setBackgroundProxy = setBackground.proxy((view, value, methodHolder) -> {
    if ("mProxyTag".equals(view.getTag(R.id.skinMethodTagID))) {
        //You can do something
    }
});
SkinManager.INSTANCE.addSkinAttrHolder("setBackground", setBackgroundProxy);
```

<br>

**_注意！！ 只有设置了`app:skinMethodTag=""`属性的`View`才会执行此操作，未设置完全不会执行_**


<br>
<br>
<br>

## 动态添加View换肤

对于动态添加到布局中的 `View`（例如，在 Java 或 Kotlin 代码中创建的 `View`），可以使用 `SkinBinder` 来为其绑定换肤能力。这可以确保即使是动态创建的视图也能够响应主题变化。

### 使用 SkinBinder

`SkinBinder` 提供了一个便捷的 `bind` 方法，允许您为动态创建的 `View` 注册一个或多个换肤属性。当皮肤切换时，绑定的属性会自动更新。

```java
// 方式1：绑定到 View 生命周期（推荐）
SkinBinder.bind(textView)
    .bindColor(R.color.text_color, TextView::setTextColor)
    .bindDrawable(R.drawable.bg, View::setBackground)
    .bind();
// 方式2：绑定到 LifecycleOwner（Activity/Fragment）
SkinBinder.bind(textView)
    .bindColor(R.color.text_color, TextView::setTextColor)
    .bindTo(lifecycleOwner);
```

当 `View` 不再需要时（例如，当包含它的 `Activity` 或 `Fragment` 被销毁时），`SkinBinder` 会自动处理解绑，无需手动操作，从而避免内存泄漏。

<br>
<br>
<br>

## 换肤监听

如果您需要在换肤发生时执行自定义逻辑（例如更新非 View 组件、刷新缓存等），可以注册换肤监听。

### 注册与注销监听

建议在Activity/Fragment中创建`OnSkinChangeListener`实例，并在生命周期方法中注册和注销，以避免内存泄漏。

```java
// 1. 创建监听器实例
private final OnSkinChangeListener mSkinChangeListener = new OnSkinChangeListener() {
    @Override
    public void onSkinChanged(boolean isDefault) {
        // ...
    }
};

// 2. 在适当的时候注册监听 (例如 onCreate)
// 注册时会立即回调一次 onSkinChanged
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // ...
    SkinManager.INSTANCE.addSkinChangeListener(mSkinChangeListener);
}

// 3. 在不再需要监听时注销 (例如 onDestroy)
@Override
protected void onDestroy() {
    super.onDestroy();
    SkinManager.INSTANCE.removeSkinChangeListener(mSkinChangeListener);
}
```


<br>
<br>
<br>
<br>
<br>


## API参考

| 方法 | 说明 |
| --- | --- |
| loadDefault() | 重置换肤，切换为默认 |
| SkinBinder.bind(...) | 为动态创建的`View`绑定换肤属性，并自动管理生命周期。 |
| getSkinResource() | 获取资源包的资源对象，如果为空，返回APP的资源对象 |
| isSkinState() | 是否为换肤状态 |
| isSkinGlobalEnable() | 是否已启用全局换肤 |
| setSkinGlobalEnable(boolean enable) | 设置是否启用全局换肤开关，默认为true,可通过{app:skinEnable="boolean"}属性单独给View设置 |
| addSkinChangeListener(OnSkinChangeListener listener) | 注册换肤监听，注册时会立即回调一次 |
| removeSkinChangeListener(OnSkinChangeListener listener) | 取消注册换肤监听 |
| getColor() | 根据资源ID获取资源包中的Color颜色值 |
| getDrawable() | 根据资源ID获取资源包中的drawable对象 |
| getString() | 根据资源ID获取资源包中的字符 |
| getDimension() | 根据资源ID获取资源包中的Dimension资源值 |
| getSkinResId(@IdRes int resId) | 根据资源Id获取到皮肤包中的Id. |


## 
