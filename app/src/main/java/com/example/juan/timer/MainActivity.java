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
//設定三個timer分別控制暖身、休息、動作三個時間倒數，宣告一個
public class MainActivity extends AppCompatActivity  implements View.OnClickListener{
    Button btnstart,btnstop,btnreset;
    TextView tvshow,timeTv;
    EditText ethot,etrest,etwork,ettimes;
    int hot,rest,work,times;
    int h =0 ,r =0 , w =0 ,t =1;
    private Timer timer = null , timer1 =null , timer2 =null ;
    private boolean one = true;
    private boolean start = true;
    private boolean first = true;

    private TimerTask timerTask = null;
    private TimerTask timerTask1 =null;
    private TimerTask timerTask2 =null;

    private SoundPool soundPool; //SoundPool類別設定音效用
    private int[] soundID = new int[1];//宣告整數陣列放音效ID
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findView();
        hot = Integer.valueOf(ethot.getText().toString());
        rest = Integer.valueOf(etrest.getText().toString());
        work =Integer.valueOf(etwork.getText().toString());
        times = Integer.valueOf(ettimes.getText().toString());

        soundPool =new SoundPool(5, AudioManager.STREAM_MUSIC,0);//使用SoundPool類別設定音效，參數(最大串流音效數,串流類型,取樣值轉換(設定為0預設值即可))
        soundID[0]=soundPool.load(this,R.raw.glass,1); //將音效放在res/raw目錄 ，使用音效必須先LOAD來設定音效ID

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
                    if(first) {//數值初始化
                        hot = Integer.valueOf(ethot.getText().toString()); //讀取用戶設定在textview上的數值
                        rest = Integer.valueOf(etrest.getText().toString());
                        work = Integer.valueOf(etwork.getText().toString());
                        times = Integer.valueOf(ettimes.getText().toString());
                        h=hot+1; //因一開始執行畫面數值顯示為 hot-1 ，希望顯示為hot所以加一補回
                        r=rest+1;
                        w=work+1;
                        t=times;
                        one=true; // 循環才執行暖身判斷
                    }
                    start=false;//按下開始後判定為開始狀態所以為false
                    //btnstart.setText("暫停");
                    startTime(); //使用開始方法
                }

                break;
            case R.id.btnstop:
                stopTime(); //使用暫停方法
                break;
            case R.id.btnreset:
                restTime(); //使用重置方法
                break;


    }

    }
        private Handler mHandler = new Handler(){
      public  void handleMessage(Message msg){
          //利用Handler更新主UI (時間表) 傳送時間值改變TextView的數值
          //String time=String.valueOf(msg.arg1);
          tvshow.setText(msg. arg1+ "");//TextView只能承载字符串类型的操作
          startTime();//重複執行startTime()方法
      }
    };

    public void startTime(){

        //java.util.Timer定时器，實際上是個Thread
        timer = new Timer(); //宣告新的Timer
        timer1 = new Timer();
        timer2 = new Timer();
        //TimerTask實際上就是一個擁有run方法的類別，需要定時執行的代碼放到run方法內
        timerTask =new TimerTask() {

            @Override
            public void run() {
                h--; //暖身時間-1
                Message message= mHandler.obtainMessage();
                message. arg1= h;             //傳送暖身時間給handler    //arg1和arg2都是Message自帶的用來傳遞一些輕量級存儲int類型的數據，比如進度條的數據等。
                mHandler.sendMessage(message);    //傳送訊息


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
            if(h==hot)timeTv.setText("暖身");//顯示暖身兩字
            timer.schedule(timerTask,1000); // timer 去執行TimerTask的run一秒後執行
        }
        if(h==0) { //當暖身秒數=0時執行
            if(r==(rest+1) )//當休息時間=rest+1時產生音效
                soundPool.play(soundID[0], 1.0f, 1.0f, 0, 0, 1.0f); //播放音效
                //play()的參數(逾放的音效ID,左聲道音量(0.0~1.0f),右聲道音量(0.0~1.0f),優先撥放順序,是否重複,取樣值)
            timer.cancel();
            one=false;//暖身只跑一次，所以跑完一次後,one設為false
            timer1.schedule(timerTask1, 1000);
            if(r==rest)timeTv.setText("休息");//顯示休息兩字
            if(r==0) {
                timer1.cancel();
                if(w==(work+1) )//當動作時間=work+1時產生音效
                    soundPool.play(soundID[0], 1.0f, 1.0f, 0, 0, 1.0f);
                timer2.schedule(timerTask2, 1000);
                if(w==work)timeTv.setText("訓練");//顯示訓練兩字
                if(w==0) {  //當動作時間結束
                    timer2.cancel();
                    r = rest+1;   //再將休息、動作時間重新計算
                    w = work+1;
                    t--; //跑完一次，次數就減一
                    if(t!=0) startTime();//次數還沒等於零就重新執行startTime
                    if(t==0) // 循環次數結束，想起最後一鈴聲
                    {
                        soundPool.play(soundID[0], 1.0f, 1.0f, 0, 0, 1.0f);
                        start=true;
                        first=true;
                    }
                }
            }
        }

    }
    public void stopTime(){//暫停 暫時將timer取消，將判定是否為開始狀態的start設為ture代表現在不是開始狀態
        timer.cancel();
        timer1.cancel();
        timer2.cancel();
        start = true;
        first = false ;


    }
    public void restTime(){//重置 將所有timer取消，所有判斷設定回歸初值
        timer.cancel();
        timer1.cancel();
        timer2.cancel();
        tvshow.setText("00");
        first = true;
        one = true;
        start = true;
        timeTv.setText(" ");

    }
}
