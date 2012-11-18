package com.example.colorwheelwidget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
//import android.graphics.RadialGradient;
//import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;
import java.lang.Math;
import java.util.ArrayList;

public class ColorWheel extends View {
	
	final private int NUM_COLOR = 5;
	private float mXcenter;
	private float mYcenter;
	private Paint mPaintMainCircle;
	private Paint mPaintSmallCircle;
	private Circle selectedColorCircle;
	private Circle bigCircle;
	private ArrayList<Circle> m_colorCircle;
	private ArrayList<Paint> m_colorCirclePaint;
	private float m_touchLastX;
	private float m_touchLastY;
	private float m_rotationDegree = 0f;
	private float m_startDegree = 0f;
	private float m_stopDegree = 0f;
	private int mCurColor = 0;

	public ColorWheel(Context context){
		super(context);
		init();
	}
	public ColorWheel(Context context, AttributeSet attr){
		super(context, attr);
		init();
	}
	public ColorWheel(Context context, AttributeSet attr, int defStyle) {
		super(context, attr, defStyle);
		init();
	}

	
	public void init(){
		selectedColorCircle = new Circle();
		bigCircle = new Circle();
		mPaintMainCircle = new Paint();
		mPaintMainCircle.setColor(Color.BLACK);
		bigCircle.paint = mPaintMainCircle;
		mPaintSmallCircle = new Paint();
		selectedColorCircle = new Circle();
		selectedColorCircle.paint = mPaintSmallCircle;
		
		//Construct the colored circles
		Paint red = new Paint();
		red.setColor(Color.RED);
		Paint blue = new Paint();
		blue.setColor(Color.BLUE);
		Paint green = new Paint();
		green.setColor(Color.GREEN);
		Paint yellow = new Paint();
		yellow.setColor(Color.YELLOW);
		Paint orange = new Paint();
		orange.setColor(Color.MAGENTA);
		m_colorCirclePaint = new ArrayList<Paint>();
		m_colorCirclePaint.add(red);
		m_colorCirclePaint.add(blue);
		m_colorCirclePaint.add(green);
		m_colorCirclePaint.add(yellow);
		m_colorCirclePaint.add(orange);
		
		m_colorCircle = new ArrayList<Circle>();
		
		
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch(event.getAction()){
		case MotionEvent.ACTION_DOWN:
			m_startDegree = (float) Math.toDegrees(Math.atan2(event.getY()-mYcenter, event.getX()-mXcenter));
			break;
		case MotionEvent.ACTION_MOVE:
			//get touch coordinate
			m_touchLastX = event.getX();
			m_touchLastY = event.getY();
			//Calculate appropriate degree rotation
			m_rotationDegree = (float) Math.toDegrees(Math.atan2(m_touchLastY-mYcenter, m_touchLastX-mXcenter)) - m_startDegree+m_stopDegree;
			while(m_rotationDegree<0){
				m_rotationDegree+=360; //TODO: find better way?
			}
			invalidate(); //Must be called to call onDraw()
			break;
		case MotionEvent.ACTION_UP:
			m_stopDegree = m_rotationDegree;
			break;
		}
		return true;
	}
	
	@Override
	public void onDraw(Canvas canvas){
		canvas.drawColor(0);
		canvas.drawCircle(bigCircle.x, bigCircle.y, bigCircle.radius, bigCircle.paint);
		canvas.save();
		//rotation here
		canvas.rotate(m_rotationDegree, mXcenter, mYcenter);
		for(int i=0; i<NUM_COLOR;i++){
		canvas.drawCircle(m_colorCircle.get(i).x, m_colorCircle.get(i).y, m_colorCircle.get(i).radius, m_colorCircle.get(i).paint);
		}
		canvas.restore();
		//TODO: getCurrentColor
		//Log.d("ROTATION", String.valueOf(m_rotationDegree));
		mCurColor = (int) ((int) 4-((m_rotationDegree%360)/(360/NUM_COLOR)));
		canvas.drawCircle(selectedColorCircle.x, selectedColorCircle.y, selectedColorCircle.radius, m_colorCirclePaint.get(mCurColor));//TODO: find better way to get good ind
	}
	
	public int getSelectedColor(){
		return  m_colorCirclePaint.get(mCurColor).getColor();
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh){
		mXcenter = w/2f;
		mYcenter = h/2f;
		selectedColorCircle.setCenteredCircle(w, h, w/4);
		bigCircle.setMaxCircle(w,h);
		
		//Shader rg = new RadialGradient(mXcenter, mYcenter, w/4, Color.BLUE,Color.RED,Shader.TileMode.MIRROR);
		for(int i=0; i<NUM_COLOR; i++){
			float angle = i*360f/(float)NUM_COLOR;
			Circle curCircle = new Circle();
			curCircle.radius = 50;
			curCircle.x = (float) (mXcenter+(FloatMath.cos((float) Math.toRadians(angle)))*(w/3));
			curCircle.y = (float) (mYcenter+(FloatMath.sin((float) Math.toRadians(angle)))*(w/3));
			//m_colorCirclePaint.get(i).setShader(rg);
			curCircle.paint = m_colorCirclePaint.get(i);
			m_colorCircle.add(curCircle);
		}
	}

	private class Circle{
		private float x;
		private float y;
		private float radius;
		private Paint paint;
		
		public Circle(){
			this.x = 0;
			this.y = 0;
			this.radius = 0;
			this.paint = new Paint();
		}
		/*public Circle(float x,float y,float radius,Paint paint){
			this.x = x;
			this.y = y;
			this.radius = radius;
			this.paint = paint;
		}*/
		
		public void setMaxCircle(int viewWidth, int viewHeight){
			x = viewWidth/2f;
			y = viewHeight/2f;
			if(x<y) radius = x;
			else radius = y;
		}
		
		public void setCenteredCircle(int viewWidth, int viewHeight, int radius){
			x = viewWidth/2f;
			y = viewHeight/2f;
			this.radius = radius;
		}
	}



}
