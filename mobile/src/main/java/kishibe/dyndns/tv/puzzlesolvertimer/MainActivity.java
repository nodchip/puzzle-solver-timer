package kishibe.dyndns.tv.puzzlesolvertimer;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;


public class MainActivity extends ActionBarActivity {

    private static final Logger logger = Logger.getLogger(MainActivity.class.toString());

    private static final String[] TIPS = {
            "司会者の言ったヒントを思い出してみましょう",
            "パンフレットの内容を思い出してみましょう",
            "スタッフに話しかけてみましょう",
            "黒い光 = ブラックライト",
            "ダイヤル錠の桁数と数字か英字かを確認しましょう",
            "一人一枚配られたものの模様は同じですか？違いますか？",
            "50音表 QWERTY フリック入力 携帯入力",
            "26 といえば アルファベット",
            "7 と言えば ドレミ 曜日 虹の色",
            "12 といえば 一年の月 十二支 黄道十二星座 リモコン",
            "0～9の対応表は何度も使います",
            "数字と矢印が出たらフリック入力を考えましょう",
            "○○に△△をかけて～ = 掛け算",
            "テーブルやイスの下を探してみましょう",
            "この上に～ 目の前の～ ～は二の次 などの言葉を穿って解釈してみましょう",
            "番号札の裏側を確認してみましょう",
            "クロスワードは2回使います",
            "問題用紙の四隅に色はついていませんか？",
            "問題用紙の枠が何かに見えませんか？",
            "タイトルの形にぴったり合いませんか？",
            "前の問題の形にぴったり合いませんか？",
            "問題の部品は足りていますか？",
            "２つの模様を合わせてみましょう",
            "柱 椅子 机の脚などに何か巻きついてきませんか？",
            "模様のついた紐は柱やパイプなどに巻きつけてみましょう",
            "イラストの中になにか足りない部分はありませんか？",
            "みんなで一斉に静かにしてみましょう",
            "初めから書かれていた文字を消しゴムやイレーサーで消してみましょう",
            "「指し示す」 → 矢印は描かれていませんか？矢印を描くことはできますか？",
            "選択肢が少ないなら全通り試してみましょう",
    };
    private final Random random = new Random();
    private final Handler mHandlerUpdateTimer = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            updateTimer();
        }
    };
    private final Handler mHandlerUpdateTips = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            updateTips();
        }
    };
    private TextView mTextViewTimer;
    private TextView mTextViewTips;
    private Button mButtonStart;
    private Button mButtonReset;
    private long mEndTimeMs;
    private Timer mTimerTimerTicker;
    private Timer mTimerTipsUpdater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextViewTimer = (TextView) findViewById(R.id.textViewTimer);
        mTextViewTips = (TextView) findViewById(R.id.textViewTips);

        mButtonStart = (Button) findViewById(R.id.buttonStart);
        mButtonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStartButtonClicked();
            }
        });

        mButtonReset = (Button) findViewById(R.id.buttonReset);
        mButtonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onResetButtonClicked();
            }
        });

        mTimerTipsUpdater = new Timer();
        mTimerTipsUpdater.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                mHandlerUpdateTips.sendEmptyMessage(0);
            }
        }, 10 * 1000, 10 * 1000);

        updateTips();

        mEndTimeMs = System.currentTimeMillis() + 60 * 60 * 1000;
        updateTimer();
    }


    @Override
    protected void onDestroy() {
        logger.info("onDestroy()");

        if (mTimerTimerTicker != null) {
            mTimerTimerTicker.cancel();
            mTimerTimerTicker = null;
        }

        if (mTimerTipsUpdater != null) {
            mTimerTipsUpdater.cancel();
            mTimerTipsUpdater = null;
        }

        super.onDestroy();
    }

    private synchronized void onStartButtonClicked() {
        logger.info("onStartButtonClicked()");

        if (mTimerTimerTicker == null) {
            mTimerTimerTicker = new Timer();
            mTimerTimerTicker.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    mHandlerUpdateTimer.sendEmptyMessage(0);
                }
            }, 1000, 1000);
            mEndTimeMs = System.currentTimeMillis() + 60 * 60 * 1000;
            mButtonStart.setText("stop");

        } else {
            mTimerTimerTicker.cancel();
            mTimerTimerTicker = null;
            mButtonStart.setText("start");
        }

        updateTimer();
    }

    private synchronized void onResetButtonClicked() {
        logger.info("onResetButtonClicked()");

        mEndTimeMs = System.currentTimeMillis() + 60 * 60 * 1000;

        updateTimer();
    }

    private synchronized void updateTimer() {
        logger.info("updateTimer()");

        long time = System.currentTimeMillis();
        if (time < mEndTimeMs) {
            int remainSec = (int) ((mEndTimeMs - time) / 1000);
            String timer = String.format("残り %02d:%02d", remainSec / 60, remainSec % 60);
            mTextViewTimer.setText(timer);
        } else {
            mTextViewTimer.setText("ゲームが終了しました");
        }
    }

    private synchronized void updateTips() {
        logger.info("updateTips()");

        String tips = TIPS[random.nextInt(TIPS.length)];
        mTextViewTips.setText(tips);
    }
}
