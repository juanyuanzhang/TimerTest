package com.example.juan.timer;

import android.annotation.SuppressLint;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity  implements View.OnClickListener{
    Button btnstart,btnstop,btnreset;
    TextView tvshow,timeTv;
    EditText ethot,etrest,etwork,ettimes;
    int hot,rest,work,times;
    int h =0 ,r =0 , w =0 ,t =1;
    private Timer timer = null , timer1 =null , timer2 =null ;
    private boolean one = true;
    private boolean start = true;
    private boolean p = true;

    private TimerTask timerTask = null;
    private TimerTask timerTask1 =null;
    private TimerTask timerTask2 =null;

    private SoundPool soundPool;
    private int[] soundID = new int[1];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findView();
        hot = Integer.valueOf(ethot.getText().toString());
        rest = Integer.valueOf(etrest.getText().toString());
        work =Integer.valueOf(etwork.getText().toString());
        times = Integer.valueOf(ettimes.getText().toString());
        soundPool =new SoundPool(5, AudioManager.STREAM_MUSIC,0);//設定音效
        soundID[0]=soundPool.load(this,R.raw.glass,1); //設定音效ID

    }
    public void findView(){
        tvshow = findViewById(R.id.tvshow);
        timeTv = findViewById(R.id.timeTv);

        btnstart = findViewById(R.id.btnstart);
        btnstop = findViewById(R.id.btnstop);
        btnreset = findViewById(R.id.btnreset);

        ethot = findViewById(R.id.ethot);
        etrest = findViewById(R.id.etrest);
        etwork = findViewById(R.id.etwork);
        ettimes = findViewById(R.id.ettimes);

        btnstart.setOnClickListener(this);
        btnstop.setOnClickListener(this);
        btnreset.setOnClickListener(this);



    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnstart:
                if(start) { //判斷是否是是在開始狀態，不是為true，是為false，避免發生按下兩次開始發生BUG
                    if(p) {//數值初始化
                        hot = Integer.valueOf(ethot.getText().toString()); //讀取數值
                        rest = Integer.valueOf(etrest.getText().toString());
                        work = Integer.valueOf(etwork.getText().toString());
                        times = Integer.valueOf(ettimes.getText().toString());
                        h=hot+1; //因一開始執行畫面數值顯示為 hot-1 ，希望顯示為hot所以加一補回
                        r=rest+1;
                        w=work+1;
                        t=times;
                        one=true; //第一循環才執行暖身判斷
                    }
                    start=false;//按下開始後判定為開始狀態所以為false
                    startTime();
                }

                break;
            case R.id.btnstop:
                stopTime();
                break;
            case R.id.btnreset:
                restTime();
                break;


    }

    }
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
      public  void handleMessage(Message msg){
          //利用Handler更新主UI (時間表)
          //String time=String.valueOf(msg.arg1);
          tvshow.setText(msg. arg1+ "");//TextView只能承载字符串类型的操作
          startTime();
      }
    };

    public void startTime(){

        timer = new Timer();
        timer1 = new Timer();
        timer2 = new Timer();

        timerTask =new TimerTask() {

            @Override
            public void run() {
                h--;
                Message message= mHandler.obtainMessage();
                message. arg1= h;                 //arg1和arg2都是Message自帶的用來傳遞一些輕量級存儲int類型的數據，比如進度條的數據等。
                mHandler.sendMessage(message);    // 通過這個數據是通過Bundle的方式來轉載的，讀者可以自己查閱源代碼研究


            }
        };
        timerTask1 =new TimerTask() {

            @Override
            public void run() {
                r--;
                Message message= mHandler.obtainMessage();
                message. arg1= r;
                mHandler.sendMessage(message);

            }
        };
        timerTask2 = new TimerTask(){
            @Override
            public void run() {
                w--;
                Message message= mHandler.obtainMessage();
                message. arg1= w;
                mHandler.sendMessage(message);

            }

        };
    //启动Timer(以秒为单位的倒计时)
        if(one) {
            if(h==hot)timeTv.setText("暖身");
            timer.schedule(timerTask,1000); //timer 去執行TimerTask的run每一秒執行一次
        }
        if(h==0) {
            if(r==(rest+1) )
                soundPool.play(soundID[0], 1.0f, 1.0f, 0, 0, 1.0f); //播放音效

            timer.cancel();
            one=false;
            timer1.schedule(timerTask1, 1000);
            if(r==rest)timeTv.setText("休息");
            if(r==0) {
                timer1.cancel();
                if(w==(work+1) )
                    soundPool.play(soundID[0], 1.0f, 1.0f, 0, 0, 1.0f);
                timer2.schedule(timerTask2, 1000);
                if(w==work)timeTv.setText("訓練");
                if(w==0) {
                    timer2.cancel();
                    r = rest+1;
                    w = work+1;
                    t--;
                    if(t!=0) startTime();
                    if(t==0)
                    {
                        soundPool.play(soundID[0], 1.0f, 1.0f, 0, 0, 1.0f);
                        start=true;
                        p=true;
                    }
                }
            }
        }

    }
    public void stopTime(){
        timer.cancel();
        timer1.cancel();
        timer2.cancel();
        start= true;
        p =false ;


    }
    public void restTime(){
        timer.cancel();
        timer1.cancel();
        timer2.cancel();
        tvshow.setText("00");
        p=true;
        one=true;
        start=true;
        timeTv.setText(" ");

    }
}
