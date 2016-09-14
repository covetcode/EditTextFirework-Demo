# EditTextFirework-Demo


![](https://github.com/covetcode/EditTextFirework-Demo/blob/master/EditTextFirework.gif)

一个绚丽易用的输入框烟花效果，模仿网页360搜索框。


Getting started
---------------
copy "edittextfirework" to your project.
Add dependency to your build.gradle.   <br /> 
in settings.gradle.
```groovy
include ':app', ':edittextfirework'
```

in build.gradle(app).
```groovy
dependencies {
    compile project(path: ':edittextfirework')
}

```

Usage
-----

in xml:

```xml
   <com.wayww.edittextfirework.FireworkView
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:id="@+id/fire_work"/> 
```

and bind your EditText:
```java
        mFireworkView = (FireworkView) findViewById(R.id.fire_work);
        mFireworkView.bindEditText(mEditText);
```
