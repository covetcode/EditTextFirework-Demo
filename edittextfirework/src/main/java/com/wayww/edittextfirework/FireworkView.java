package com.wayww.edittextfirework;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;


/**
 * Created by wayww on 2016/9/8.
 */
public class FireworkView extends View {

    private final String TAG = this.getClass().getSimpleName();
    private EditText mEditText;
    private LinkedList<Firework> fireworks = new LinkedList<>();
    private int windSpeed;
    private TextWatcher mTextWatcher;

    public FireworkView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public FireworkView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FireworkView(Context context) {
        super(context);
    }

    public void bindEditText(EditText editText) {

      //  this.setLayerType(View.LAYER_TYPE_SOFTWARE,null);

        this.mEditText = editText;
        mEditText.addTextChangedListener( mTextWatcher = new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                /*
                i为EditText里的字符数，i1为减少的字符数，i2为增加的字符数。
                关于launch的第三个参数，决定风的方向，1为吹向右边，-1为左边。
                 */
                float [] coordinate = getCursorCoordinate();
                launch(coordinate[0], coordinate[1], i1 ==0?-1:1);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }

        });
    }

    public void removeBind(){
        mEditText.removeTextChangedListener(mTextWatcher);
        mEditText = null;
    }



    //~~~~~~~~~~~~~private method~~~~~~~~~~~~~~~~~~~


    private void launch(float x, float y, int direction){
        final Firework firework = new Firework(new Firework.Location(x, y), direction);
        firework.addAnimationEndListener(new Firework.AnimationEndListener() {
            @Override
            public void onAnimationEnd() {
                //动画结束后把firework移除，当没有firework时不会刷新页面
                fireworks.remove(firework);
            }
        });
        fireworks.add(firework);
        firework.fire();
        invalidate();
    }


    /**
     * @return the coordinate of cursor. x=float[0]; y=float[1];
     */
    private float[] getCursorCoordinate (){
     /*
       *以下通过反射获取光标cursor的坐标。
       * 首先观察到TextView的invalidateCursorPath()方法，它是光标闪动时重绘的方法。
       * 方法的最后有个invalidate(bounds.left + horizontalPadding, bounds.top + verticalPadding,
                   bounds.right + horizontalPadding, bounds.bottom + verticalPadding);
       *即光标重绘的区域，由此可得到光标的坐标
       * 具体的坐标在TextView.mEditor.mCursorDrawable里，获得Drawable之后用getBounds()得到Rect。
       * 之后还要获得偏移量修正，通过以下三个方法获得：
       * getVerticalOffset(),getCompoundPaddingLeft(),getExtendedPaddingTop()。
       *
      */

        int xOffset = 0;
        int yOffset = 0;
        Class<?> clazz = EditText.class;
        clazz = clazz.getSuperclass();
        try {
            Field editor = clazz.getDeclaredField("mEditor");
            editor.setAccessible(true);
            Object mEditor = editor.get(mEditText);
            Class<?> editorClazz = Class.forName("android.widget.Editor");
            Field drawables = editorClazz.getDeclaredField("mCursorDrawable");
            drawables.setAccessible(true);
            Drawable[] drawable= (Drawable[]) drawables.get(mEditor);

            Method getVerticalOffset = clazz.getDeclaredMethod("getVerticalOffset",boolean.class);
            Method getCompoundPaddingLeft = clazz.getDeclaredMethod("getCompoundPaddingLeft");
            Method getExtendedPaddingTop = clazz.getDeclaredMethod("getExtendedPaddingTop");
            getVerticalOffset.setAccessible(true);
            getCompoundPaddingLeft.setAccessible(true);
            getExtendedPaddingTop.setAccessible(true);
            if (drawable != null){
                Rect bounds = drawable[0].getBounds();
        //        Log.d(TAG,bounds.toString());
                xOffset = (int) getCompoundPaddingLeft.invoke(mEditText) + bounds.left;
                yOffset = (int) getExtendedPaddingTop.invoke(mEditText) + (int)getVerticalOffset.invoke(mEditText, false)+bounds.bottom;
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        float x = mEditText.getX() + xOffset;
        float y = mEditText.getY() + yOffset;

        //当EditText的父view与FireworkView的坐标（左上角的坐标值）不一致时进行修正
        int[] positionE = new int[2];
        if (mEditText.getParent() != null) {
            ((ViewGroup)mEditText.getParent()).getLocationInWindow(positionE);
        }
        int[] positionF = new int[2];
        this.getLocationInWindow(positionF);
        x = x+positionE[0]-positionF[0];
        y = y+positionE[1]-positionF[1];

        return new float[]{x , y};
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (int i =0 ; i<fireworks.size(); i++){
            fireworks.get(i).draw(canvas);
        }
        if (fireworks.size()>0)
            invalidate();
    }


}
