package com.atsoft.thirukuralquiz;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;


/**
 * A simple {@link Fragment} subclass.
 */
public class GameFragment extends Fragment implements View.OnClickListener, RewardedVideoAdListener {

    TextView first,second,third,fourth,fifth,sixth,seventh,a1,a2,a3,a4,a5,a6,a7,a8,a9;
    ArrayList<String> ans_word, kuralnoarr;
    String dash;
    SharedPreferences sharedPrefs;
    Button next;
    static String thirukural = "";
    static int lifec = 3;
    TextView timmer, score;
    ImageView life_img;
    CountDownTimer countDownTimer;
    static int timerTime = 60000, tempTime = 0;
    static boolean afterPause = false;

    private RewardedVideoAd mAd;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_game, container, false);

        MobileAds.initialize(getActivity(), getResources().getString(R.string.appId));
        lifec = 3;
        View qus_view = rootView.findViewById(R.id.qus_ll);
        Drawable qus_bg = qus_view.getBackground();
        qus_bg.setAlpha(60);
        View ans_view = rootView.findViewById(R.id.ans_ll);
        Drawable ans_bg = ans_view.getBackground();
        ans_bg.setAlpha(60);
        init(rootView);
        setQuestion();
        timerTime = 60000;
        setTimmer();
        mAd = MobileAds.getRewardedVideoAdInstance(getActivity());
        mAd.setRewardedVideoAdListener(this);
        loadRewardedVideoAd();
        return rootView;
    }

    private void loadRewardedVideoAd() {
        //mAd.loadAd(getResources().getString(R.string.adUnitIdVideo), new AdRequest.Builder().build());
        mAd.loadAd("ca-app-pub-3940256099942544/5224354917", new AdRequest.Builder().build());
    }

    private void setTimmer() {
        System.out.println("Syso timerTime : "+timerTime);
        countDownTimer = new CountDownTimer(timerTime, 1000) {

            public void onTick(long millisUntilFinished) {
                long time = millisUntilFinished / 1000;
                timmer.setText(""+time);
                if (time < 11)
                    timmer.setTextColor(Color.RED);
                else
                    timmer.setTextColor(Color.BLACK);
                //here you can have your logic to set text to edittext
            }

            public void onFinish() {
                timmer.setText("0");
                AlertDialog.Builder mt_builder = new AlertDialog.Builder(getActivity());
                mt_builder.setCancelable(false);
                mt_builder.setTitle("Timeout");
                mt_builder.setIcon(R.drawable.life_heart_broken);
                mt_builder.setMessage("Do you want more time watch the video.");
                mt_builder.setPositiveButton("Watch Video", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if (mAd.isLoaded()) {
                            mAd.show();
                        }
                    }
                })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if (lifec != 1) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                    builder.setCancelable(false);
                                    builder.setTitle("Timeout");
                                    builder.setIcon(R.drawable.life_heart_broken);
                                    builder.setMessage("Do you want try again or go to next Thirukural?");
                                    builder.setPositiveButton("Tryagain", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id) {
                                            wrongAns();
                                        }
                                    })
                                            .setNegativeButton("Next", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    showCorrectAnswer();
                                                }
                                            });

                                    // Create the AlertDialog object and return it
                                    builder.create().show();
                                } else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                    builder.setCancelable(false);
                                    builder.setTitle(R.string.app_name);
                                    builder.setIcon(R.drawable.life_heart_broken);
                                    builder.setMessage("Game Over");
                                    builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id) {
//                                            startActivity(new Intent(MainActivity.this, SplashScreen.class));
//                                            MainActivity.this.finish();
                                            FragmentManager manager = getActivity().getSupportFragmentManager();
                                            FragmentTransaction transaction = manager.beginTransaction();
                                            manager.popBackStackImmediate();
                                            HomeFragment homeFragment = new HomeFragment();
                                            transaction.replace(R.id.container, homeFragment, "Home");
                                            transaction.addToBackStack(null);
                                            transaction.commit();
                                        }
                                    });

                                    // Create the AlertDialog object and return it
                                    builder.create().show();
                                }
                            }
                        });

                // Create the AlertDialog object and return it
                mt_builder.create().show();
            }

        }.start();
    }

    private void wrongAns() {
        switch (lifec) {
            case 3:
                life_img.setImageDrawable(getResources().getDrawable(R.drawable.life_2));
                countDownTimer.cancel();
                timerTime = 60000;
                setTimmer();
                lifec = 2;
                break;
            case 2:
                life_img.setImageDrawable(getResources().getDrawable(R.drawable.life_1));
                countDownTimer.cancel();
                timerTime = 60000;
                setTimmer();
                lifec = 1;
                break;
        }
    }

    private void setQuestion() {
        sharedPrefs = getActivity().getSharedPreferences(Defs.sharedPreferenceName, Context.MODE_PRIVATE);
        ans_word = new ArrayList<>();
        dash = getResources().getString(R.string.dash);
        thirukural = getKural();
        System.out.println("Syso QsTiru n: "+thirukural);
        StringTokenizer token = new StringTokenizer(thirukural, " ");
        first.setText(token.nextToken());
        ans_word.add(token.nextToken());
        third.setText(token.nextToken());
        ans_word.add(token.nextToken());
        fifth.setText(token.nextToken());
        ans_word.add(token.nextToken());
        try {
            seventh.setText(token.nextToken());
        } catch (Exception e) {
            e.printStackTrace();
        }
        second.setText(dash);
        fourth.setText(dash);
        sixth.setText(dash);
        Collections.shuffle(Defs.allwords);
        int i = 0;
        System.out.println("Syso size : "+ ans_word.size());
        while (ans_word.size() < 9) {
            if (!ans_word.contains(Defs.allwords.get(i)))
                ans_word.add(Defs.allwords.get(i));
            i++;
        }
        System.out.println("Syso size : "+ ans_word.size());
        Collections.shuffle(ans_word);
        a1.setText(ans_word.get(0));
        a2.setText(ans_word.get(1));
        a3.setText(ans_word.get(2));
        a4.setText(ans_word.get(3));
        a5.setText(ans_word.get(4));
        a6.setText(ans_word.get(5));
        a7.setText(ans_word.get(6));
        a8.setText(ans_word.get(7));
        a9.setText(ans_word.get(8));
        a1.setEnabled(true);
        a1.setTextColor(Color.BLACK);
        a2.setEnabled(true);
        a2.setTextColor(Color.BLACK);
        a3.setEnabled(true);
        a3.setTextColor(Color.BLACK);
        a4.setEnabled(true);
        a4.setTextColor(Color.BLACK);
        a5.setEnabled(true);
        a5.setTextColor(Color.BLACK);
        a6.setEnabled(true);
        a6.setTextColor(Color.BLACK);
        a7.setEnabled(true);
        a7.setTextColor(Color.BLACK);
        a8.setEnabled(true);
        a8.setTextColor(Color.BLACK);
        a9.setEnabled(true);
        a9.setTextColor(Color.BLACK);
    }

    private String getKural() {
        Set<String> stringset = sharedPrefs.getStringSet("kural_no_set", null);
        System.out.println("Syso stringset : "+stringset);
        kuralnoarr = new ArrayList<>();
        if (stringset == null) {
//        if (stringset.equals(null) || stringset.size() == 0 || stringset.isEmpty()) {
            for (int i = 0; i < 1330; i++)
                kuralnoarr.add(String.valueOf(i));
        } else {
            for (String str : stringset)
                kuralnoarr.add(str);
        }
        Collections.shuffle(kuralnoarr);
        DBController controller = new DBController(getActivity());
        SQLiteDatabase db = controller.getReadableDatabase();
        String qus_kural = "";
        String kuralnostr = String.valueOf(kuralnoarr.get(0));
        String qry = "SELECT thirukural FROM kural where kuralno = '"+kuralnostr+"'";
        System.out.println("Syso select qry : " +qry);
        Cursor cursor = db.rawQuery(qry, null);
        if (cursor.moveToNext()) {
            qus_kural = cursor.getString(0);
        } else
            System.out.println("Syso empty db");
        cursor.close();
        db.close();
        kuralnoarr.remove(0);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        try {
            Set<String> set = new HashSet<>();
            set.addAll(kuralnoarr);
            editor.putStringSet("kural_no_set", set);
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return qus_kural.replace("\n", "");
    }

    private void init(View rootView) {
        first = rootView.findViewById(R.id.first_tv);
        second = rootView.findViewById(R.id.second_tv);
        third = rootView.findViewById(R.id.third_tv);
        fourth = rootView.findViewById(R.id.fourth_tv);
        fifth = rootView.findViewById(R.id.fifth_tv);
        sixth = rootView.findViewById(R.id.sixth_tv);
        seventh = rootView.findViewById(R.id.seventh_tv);
        a1 = rootView.findViewById(R.id.word_tv_1);
        a2 = rootView.findViewById(R.id.word_tv_2);
        a3 = rootView.findViewById(R.id.word_tv_3);
        a4 = rootView.findViewById(R.id.word_tv_4);
        a5 = rootView.findViewById(R.id.word_tv_5);
        a6 = rootView.findViewById(R.id.word_tv_6);
        a7 = rootView.findViewById(R.id.word_tv_7);
        a8 = rootView.findViewById(R.id.word_tv_8);
        a9 = rootView.findViewById(R.id.word_tv_9);
        next = rootView.findViewById(R.id.next_btn);
        timmer = rootView.findViewById(R.id.timmer);
        score = rootView.findViewById(R.id.score_tv);
        life_img = rootView.findViewById(R.id.life_img);
        second.setOnClickListener(this);
        fourth.setOnClickListener(this);
        sixth.setOnClickListener(this);
        a1.setOnClickListener(this);
        a2.setOnClickListener(this);
        a3.setOnClickListener(this);
        a4.setOnClickListener(this);
        a5.setOnClickListener(this);
        a6.setOnClickListener(this);
        a7.setOnClickListener(this);
        a8.setOnClickListener(this);
        a9.setOnClickListener(this);
        next.setOnClickListener(this);
    }

    public void checkAnswer() {
        String ans = first.getText().toString().trim() + " " + second.getText().toString().trim() + " " + third.getText().toString().trim() + " " + fourth.getText().toString().trim() + "  " + fifth.getText().toString().trim() + " " + sixth.getText().toString().trim() + " " + seventh.getText().toString().trim();
        System.out.println("Syso ans : "+ ans);
        System.out.println("Syso qus : "+thirukural);
        if (thirukural.equalsIgnoreCase(ans)) {
            Toast.makeText(getActivity().getApplicationContext(), "Correct Answer.!", Toast.LENGTH_LONG).show();
            int scoreint = Integer.parseInt(score.getText().toString());
            score.setText(String.valueOf(scoreint + 1));
            countDownTimer.cancel();
            timerTime = 60000;
            setTimmer();
            setQuestion();
        } else {
            countDownTimer.cancel();
            if (lifec != 1) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setCancelable(false);
                builder.setTitle(R.string.app_name);
                builder.setIcon(R.drawable.life_heart_broken);
                builder.setMessage("Wrong Answer");
                builder.setPositiveButton("Tryagain", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        wrongAns();
                    }
                })
                        .setNegativeButton("Next", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                showCorrectAnswer();
                            }
                        });

                // Create the AlertDialog object and return it
                builder.create().show();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setCancelable(false);
                builder.setTitle(R.string.app_name);
                builder.setIcon(R.drawable.life_heart_broken);
                builder.setMessage("Game Over");
                builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        /*startActivity(new Intent(MainActivity.this, SplashScreen.class));
                        MainActivity.this.finish();*/
                        FragmentManager manager = getActivity().getSupportFragmentManager();
                        FragmentTransaction transaction = manager.beginTransaction();
                        manager.popBackStackImmediate();
                        HomeFragment homeFragment = new HomeFragment();
                        transaction.replace(R.id.container, homeFragment, "Home");
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }
                });

                // Create the AlertDialog object and return it
                builder.create().show();
            }
        }
    }

    private void showCorrectAnswer() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        builder.setTitle(R.string.app_name);
        builder.setIcon(R.drawable.life_heart_broken);
        builder.setMessage("Correct Answer is \n"+thirukural);
        builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                setQuestion();
                wrongAns();
            }
        });

        // Create the AlertDialog object and return it
        builder.create().show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.second_tv:
                if (!second.getText().toString().equalsIgnoreCase(dash)) {
                    int pos = ans_word.indexOf(second.getText().toString());
                    enableTv(pos);
                    second.setText(dash);
                }
                break;
            case R.id.fourth_tv:
                if (!fourth.getText().toString().equalsIgnoreCase(dash)) {
                    enableTv(ans_word.indexOf(fourth.getText().toString()));
                    fourth.setText(dash);
                }
                break;
            case R.id.sixth_tv:
                if (!sixth.getText().toString().equalsIgnoreCase(dash)) {
                    enableTv(ans_word.indexOf(sixth.getText().toString()));
                    sixth.setText(dash);
                }
                break;
            case R.id.word_tv_1:
                setAns(a1);
                break;
            case R.id.word_tv_2:
                setAns(a2);
                break;
            case R.id.word_tv_3:
                setAns(a3);
                break;
            case R.id.word_tv_4:
                setAns(a4);
                break;
            case R.id.word_tv_5:
                setAns(a5);
                break;
            case R.id.word_tv_6:
                setAns(a6);
                break;
            case R.id.word_tv_7:
                setAns(a7);
                break;
            case R.id.word_tv_8:
                setAns(a8);
                break;
            case R.id.word_tv_9:
                setAns(a9);
                break;
            case R.id.life_img:
                if (mAd.isLoaded()) {
                    mAd.show();
                }
                break;
            case R.id.next_btn:
                System.out.println("Syso nxt click");
                checkAnswer();
                break;
        }
    }

    private void setAns(TextView v) {
        int empty_tv = getEmptyTv();
        switch (empty_tv) {
            case 1:
                second.setText(v.getText().toString());
                break;
            case 2:
                fourth.setText(v.getText().toString());
                break;
            default:
                sixth.setText(v.getText().toString());
                break;
        }
        v.setEnabled(false);
        v.setTextColor(Color.GRAY);
    }

    private int getEmptyTv() {
        if (second.getText().toString().equalsIgnoreCase(dash))
            return 1;
        else if (fourth.getText().toString().equalsIgnoreCase(dash))
            return 2;
        else
            return 3;
    }

    private void enableTv(int pos) {
        switch (pos) {
            case 0:
                a1.setEnabled(true);
                a1.setTextColor(Color.BLACK);
                break;
            case 1:
                a2.setEnabled(true);
                a2.setTextColor(Color.BLACK);
                break;
            case 2:
                a3.setEnabled(true);
                a3.setTextColor(Color.BLACK);
                break;
            case 3:
                a4.setEnabled(true);
                a4.setTextColor(Color.BLACK);
                break;
            case 4:
                a5.setEnabled(true);
                a5.setTextColor(Color.BLACK);
                break;
            case 5:
                a6.setEnabled(true);
                a6.setTextColor(Color.BLACK);
                break;
            case 6:
                a7.setEnabled(true);
                a7.setTextColor(Color.BLACK);
                break;
            case 7:
                a8.setEnabled(true);
                a8.setTextColor(Color.BLACK);
                break;
        }
    }

    @Override
    public void onResume() {
        if (afterPause) {
            countDownTimer.cancel();
            timerTime = tempTime;
            setTimmer();
            tempTime = 0;
            afterPause = false;
        }
        mAd.resume(getActivity());
        super.onResume();
    }

    @Override
    public void onPause() {
        tempTime = timerTime;
        afterPause = true;
        mAd.pause(getActivity());
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mAd.destroy(getActivity());
        super.onDestroy();
    }

    @Override
    public void onRewardedVideoAdLoaded() {
        Toast.makeText(getActivity(), "onRewardedVideoAdLoaded", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdOpened() {
        Toast.makeText(getActivity(), "onRewardedVideoAdOpened", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoStarted() {
        Toast.makeText(getActivity(), "onRewardedVideoStarted", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdClosed() {
        Toast.makeText(getActivity(), "onRewardedVideoAdClosed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {
        Toast.makeText(getActivity(), "onRewarded! currency: " + rewardItem.getType() + "  amount: " +
                rewardItem.getAmount(), Toast.LENGTH_SHORT).show();
        countDownTimer.cancel();
        timerTime = 30000;
        setTimmer();
        // Reward the user.
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {
        Toast.makeText(getActivity(), "onRewardedVideoAdLeftApplication",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {
        Toast.makeText(getActivity(), "onRewardedVideoAdFailedToLoad", Toast.LENGTH_SHORT).show();
    }

}
