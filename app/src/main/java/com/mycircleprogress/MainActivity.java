package com.mycircleprogress;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

    private Button mButton;
    private CirclePercentView mCirclePercentView;
    private int nn=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCirclePercentView = (CirclePercentView) findViewById(R.id.circleView);
        mButton = (Button) findViewById(R.id.button);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                int n = (int)(Math.random()*100);
                nn=(nn+5);
                if(nn>100){
                    nn=0;
                }
                mCirclePercentView.setPercent(nn);
            }
        });
    }
}
