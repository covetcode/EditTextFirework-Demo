# EditTextFirework-Demo




一个绚丽易用的输入框烟花效果，模仿网页360搜索框。


Getting started
---------------
Add dependency to your build.gradle.
```groovy
compile project(path: ':edittextfirework')
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
