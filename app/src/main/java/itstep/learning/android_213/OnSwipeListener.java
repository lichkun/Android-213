package itstep.learning.android_213;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class OnSwipeListener implements View.OnTouchListener {
    private GestureDetector gestureDetector;
    public OnSwipeListener(Context context){
        gestureDetector = new GestureDetector(context, new GestureListener() );
    }
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent motionEvent) {
        return gestureDetector.onTouchEvent(motionEvent);
    }
    public void onSwipteBotton() {}
    public void onSwipteLeft() {}
    public  void onSwipteRight() {}
    public void onSwipteTop() {}
    private final  class GestureListener extends  GestureDetector.SimpleOnGestureListener{
        private final static int minSwipeDistance = 100;
        private final static int minSwipeVelocity = 100;
        @Override
        public boolean onDown(@NonNull MotionEvent e) {
            return true ;
        }

        @Override
        public boolean onFling(@Nullable MotionEvent e1, @NonNull MotionEvent e2, float velocityX, float velocityY) {
            boolean isServed = false;
            if(e1 !=null){
                float deltaX =e2.getX() - e1.getX();
                float deltaY =e2.getY() - e1.getY();
                float absX = Math.abs(deltaX);
                float absY = Math.abs(deltaY);

                if(absX > 2*absY){
                    if(absX >=minSwipeDistance && velocityX >=minSwipeVelocity){
                        if(deltaX >0) onSwipteLeft();
                        else onSwipteRight();
                        isServed = true;
                    }
                }
                else if(absY >2* absX)
                {
                    if(absY >=minSwipeDistance && velocityY >minSwipeVelocity){
                        if(deltaY > 0) onSwipteTop();
                        else onSwipteBotton();
                        isServed =true;
                    }
                }
            }

            return isServed;
        }
    }
}
