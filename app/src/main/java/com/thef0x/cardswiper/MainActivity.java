package com.thef0x.cardswiper;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;
import java.util.Random;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
{
    int year = 2169;

    ArrayList<Card> cards = new ArrayList<>();
    ArrayList<String> actions = new ArrayList<>();
    ArrayList<String> scenarios = new ArrayList<>();
    ArrayAdapter arrayAdapter;
    TextView days;
    TextView years;
    TextView scenariosText;
    TextView cardText;

    ProgressBar shieldBar;
    ProgressBar moneyBar;
    ProgressBar powerBar;
    ProgressBar foodBar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //--- Adds part ---
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        //mAdView.loadAd(adRequest);
        //--- End of ads part ---


        //Initializing Variables
        days = findViewById(R.id.days);
        years = findViewById(R.id.years);
        scenariosText = findViewById(R.id.scenarios);

        shieldBar = findViewById(R.id.shieldBar);
        moneyBar = findViewById(R.id.moneyBar);
        powerBar = findViewById(R.id.powerBar);
        foodBar = findViewById(R.id.foodBar);


        SwipeFlingAdapterView flingContainer = (SwipeFlingAdapterView) findViewById(R.id.card);

        years.setText(String.valueOf(year));

        //Setting the scenarios and their effect.
        scenarios.add("a Cry for help.");
        scenarios.add("An abandoned house.!");



        //Setting the actions and their effect.
        cards.add(new Card("Food+", "Shield-", "Check   |    Ignore!"));
        cards.add(new Card("Shield-","Food+", "Enter    |    Check Around "));
        for (int i = 0; i < cards.size(); i++)
        {
            actions.add(cards.get(i).text);
        }


        //Display the Scenarios on the card
        arrayAdapter = new ArrayAdapter<String>(this, R.layout.details, R.id.textView, actions);
        //set the listener and the adapter
        flingContainer.setAdapter(arrayAdapter);
        scenariosText.setText(scenarios.get(0));
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener()
        {
            @Override
            public void removeFirstObjectInAdapter()
            {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)

                actions.remove(0);
                scenarios.remove(0);
                scenariosText.setText(scenarios.get(0));
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object o)
            {
                ApplyEffect(o, false);
                TimeChanger();
            }

            @Override
            public void onRightCardExit(Object o)
            {
                ApplyEffect(o, true);
                TimeChanger();
            }

            @Override
            public void onAdapterAboutToEmpty(int i)
            {
                // Ask for more data here
                actions.add("XML ".concat(String.valueOf(i)));
                scenarios.add("NEW");
                arrayAdapter.notifyDataSetChanged();
                Log.d("LIST", "notified");
                i++;
            }

            @Override
            public void onScroll(float v)
            {
            }
        });
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject)
            {
                Toast.makeText(MainActivity.this, String.valueOf(shieldBar.getProgress()), Toast.LENGTH_SHORT).show();
            }
        });

    }
    public int GetRandomNumber(int min, int max)
    {
        Random random = new Random();
        return random.nextInt(max - min + 1) + min;
    }
    public int RandomDays()
    {
        int randomNumber;

        // Generate a random number from 1 to 100
        int randomValue = GetRandomNumber(1, 100);

        // Adjust the range based on probability
        if (randomValue <= 70) {
            // 70% chance for numbers below 10
            randomNumber = GetRandomNumber(1, 9);
        } else {
            // 30% chance for numbers between 11 and 30
            randomNumber = GetRandomNumber(11, 30);
        }

        return randomNumber;
    }
    public void RandomEffect(ProgressBar progressBar, boolean isPositive)
    {
        int currentProgress = progressBar.getProgress();
        int newProgress = 0;
        if (isPositive)
            newProgress = currentProgress + GetRandomNumber(5, 15);
        else if (!isPositive)
            newProgress = currentProgress - GetRandomNumber(5, 15); // Decrease progress by 10 (adjust as needed)

        // Create a ValueAnimator to animate the progress change
        ValueAnimator animator = ValueAnimator.ofInt(currentProgress, newProgress);
        animator.setDuration(500); // Duration of the progress animation (adjust as needed)

        // Set up the ValueAnimator to update the ProgressBar and handle color change
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int animatedValue = (int) animation.getAnimatedValue();
                progressBar.setProgress(animatedValue);
            }
        });

        animator.start();
    }
    public void TimeChanger()
    {
        year = 2169;
        int n = Integer.parseInt(days.getText().toString());
        n += RandomDays();
        if (n >= 365)
        {
            n -= 365;
            years.setText(String.valueOf(++year));
        }
        days.setText(String.valueOf(n));
    }

    public void ApplyEffect(Object o, boolean isRight)
    {
        String effect = " ";
        for (int i = 0; i < cards.size(); i++)
        {
            if (Objects.equals(cards.get(i).text, o.toString()) && isRight)
            {
                effect = cards.get(i).effectRight;
            }
            else if (Objects.equals(cards.get(i).text, o.toString()) && !isRight)
            {
                effect = cards.get(i).effectLeft;
            }
        }
        if ("Shield+".equals(effect))
        {
            RandomEffect(shieldBar, true);
        }
        else if ("Shield-".equals(effect))
        {
            RandomEffect(shieldBar, false);
        }
        else if ("Food+".equals(effect))
        {
            RandomEffect(foodBar, true);
        }
    }
}
