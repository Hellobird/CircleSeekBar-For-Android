package com.example.circleseekbardemo;

import android.app.Activity;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.hellobird.circleseekbar.CircleSeekBar;

public class MainActivity extends Activity {
	
	private CircleSeekBar mCircleProgress1;
	private CircleSeekBar mCircleProgress2;
	private CircleSeekBar mCircleProgress3;
	private CircleSeekBar mCircleProgress4;
	private CircleSeekBar mCircleProgress5;
	private CircleSeekBar mCircleProgress6;
	private SeekBar mSeekBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mCircleProgress1 = (CircleSeekBar) findViewById(R.id.progress1);
		mCircleProgress2 = (CircleSeekBar) findViewById(R.id.progress2);
		mCircleProgress3 = (CircleSeekBar) findViewById(R.id.progress3);
		mCircleProgress4 = (CircleSeekBar) findViewById(R.id.progress4);
		mCircleProgress5 = (CircleSeekBar) findViewById(R.id.progress5);
		mCircleProgress6 = (CircleSeekBar) findViewById(R.id.progress6);
		LinearGradient linearGradient = new LinearGradient(0, 0, 50, 50,
				getResources().getIntArray(R.array.color), null,
				Shader.TileMode.MIRROR);
		mCircleProgress5.setProgressShader(linearGradient);
		mSeekBar = (SeekBar)findViewById(R.id.seekBar);
		mSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				mCircleProgress1.setProgress(progress);
				mCircleProgress2.setProgress(progress);
				mCircleProgress3.setProgress(progress);
				mCircleProgress4.setProgress(progress);
				mCircleProgress5.setProgress(progress);
				mCircleProgress6.setProgress(progress);
			}
		});

	}
}
