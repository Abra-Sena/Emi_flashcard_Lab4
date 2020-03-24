package com.example.emi_flashcard;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    FlashcardDatabase flashcardDatabase;
    List<Flashcard> allFlashcards;
    Flashcard editedCard;
    CountDownTimer countDownTimer;

    int currentCardDisplayedIndex = 0;
    private boolean showingResult=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        flashcardDatabase = new FlashcardDatabase(getApplicationContext());
        flashcardDatabase = new FlashcardDatabase(this);
        allFlashcards = flashcardDatabase.getAllCards();

        if (allFlashcards != null && allFlashcards.size() > 0) {
            ((TextView) findViewById(R.id.flashcard_question)).setText(allFlashcards.get(0).getQuestion());
            ((TextView) findViewById(R.id.flashcard_answer)).setText(allFlashcards.get(0).getAnswer());
        }

        //reveal answer with animation when clicking on the question
        findViewById(R.id.flashcard_question).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View answerSideView = findViewById(R.id.flashcard_answer);
                final View questionSideView = findViewById(R.id.flashcard_question);

                // get the center for the clipping circle
                int cx = answerSideView.getWidth() / 2;
                int cy = answerSideView.getHeight() / 2;

                // get the final radius for the clipping circle
                float finalRadius = (float) Math.hypot(cx, cy);

                // create the animator for this view (the start radius is zero)
                Animator anim = ViewAnimationUtils.createCircularReveal(answerSideView, cx, cy, 0f, finalRadius);

                // hide the question and show the answer to prepare for playing the animation!
                questionSideView.animate().rotationY(90).setDuration(200).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        questionSideView.setVisibility(View.INVISIBLE);
                        findViewById(R.id.flashcard_answer).setVisibility(View.VISIBLE);
                        //second quarter turn
                        findViewById(R.id.flashcard_answer).setRotationY(-90);
                        findViewById(R.id.flashcard_answer).animate().rotationY(0).setDuration(200).start();
                    }
                }).start();

                findViewById(R.id.flashcard_question).setCameraDistance(25000);
                findViewById(R.id.flashcard_answer).setCameraDistance(25000);
                anim.setDuration(2000);
                anim.start();
            }
        });
        //click on answer side to show question side
        findViewById(R.id.flashcard_answer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.flashcard_answer).animate().rotationY(90).setDuration(200).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        findViewById(R.id.flashcard_answer).setVisibility(View.INVISIBLE);
                        findViewById(R.id.flashcard_question).setVisibility(View.VISIBLE);
                        //second quarter turn
                        findViewById(R.id.flashcard_question).setRotationY(-90);
                        findViewById(R.id.flashcard_question).animate().rotationY(0).setDuration(200).start();
                    }
                }).start();
            }
        });
        findViewById(R.id.answer_option1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((TextView) findViewById(R.id.answer_option1)).setBackground(getDrawable(R.drawable.wrong_answer_background));
                ((TextView) findViewById(R.id.answer_option2)).setBackground(getDrawable(R.drawable.answer_background));
            }
        });
        findViewById(R.id.answer_option2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((TextView) findViewById(R.id.answer_option2)).setBackground(getDrawable(R.drawable.answer_background));
            }
        });
        findViewById(R.id.answer_option3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((TextView) findViewById(R.id.answer_option3)).setBackground(getDrawable(R.drawable.wrong_answer_background));
                ((TextView) findViewById(R.id.answer_option2)).setBackground(getDrawable(R.drawable.answer_background));
            }
        });

        findViewById(R.id.eye_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(showingResult){
                    ((ImageView) findViewById(R.id.eye_view)).setImageResource(R.drawable.ic_eye_hide);
                    findViewById(R.id.answer_option1).setVisibility(View.VISIBLE);
                    findViewById(R.id.answer_option2).setVisibility(View.VISIBLE);
                    findViewById(R.id.answer_option3).setVisibility(View.VISIBLE);
                    showingResult=false;
                } else {
                    ((ImageView) findViewById(R.id.eye_view)).setImageResource(R.drawable.ic_eye_view);
                    findViewById(R.id.answer_option1).setVisibility(View.INVISIBLE);
                    findViewById(R.id.answer_option2).setVisibility(View.INVISIBLE);
                    findViewById(R.id.answer_option3).setVisibility(View.INVISIBLE);
                    showingResult=true;
                }
            }
        });
        findViewById(R.id.rootView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.flashcard_question).setVisibility(View.VISIBLE);
                findViewById(R.id.flashcard_answer).setVisibility(View.INVISIBLE);

                ((TextView) findViewById(R.id.answer_option1)).setBackground(getDrawable(R.drawable.option_answer_background));
                ((TextView) findViewById(R.id.answer_option2)).setBackground(getDrawable(R.drawable.option_answer_background));
                ((TextView) findViewById(R.id.answer_option3)).setBackground(getDrawable(R.drawable.option_answer_background));
            }
        });
        findViewById(R.id.my_add_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addCard = new Intent(MainActivity.this, AddCard.class);
                MainActivity.this.startActivityForResult(addCard, 100);
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
            }
        });
        findViewById(R.id.my_edit_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editCard = new Intent(MainActivity.this, AddCard.class);
                editCard.putExtra("editQuestion",((TextView) findViewById(R.id.flashcard_question)).getText().toString());
                editCard.putExtra("editAnswer", ((TextView) findViewById(R.id.flashcard_answer)).getText().toString());
                editCard.putExtra("editWrongAnswer1",((TextView) findViewById(R.id.answer_option1)).getText().toString());
                editCard.putExtra("editWrongAnswer2", ((TextView) findViewById(R.id.answer_option3)).getText().toString());
                MainActivity.this.startActivityForResult(editCard, 200);
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
            }
        });

        findViewById(R.id.my_arrow_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(allFlashcards.size()>0){
                    //advance pointer index so we can show next card
                    currentCardDisplayedIndex = getRandomNumber(allFlashcards.size());
                    Toast.makeText(MainActivity.this, Integer.toString(findViewById(R.id.flashcard_question).getVisibility()), Toast.LENGTH_LONG ).show();

                    findViewById(R.id.flashcard_answer).setVisibility(View.INVISIBLE);
                    findViewById(R.id.flashcard_question).setVisibility(View.VISIBLE);

                    //loading the animation resource files to use them in our Activity
                    final Animation leftOutAnim = AnimationUtils.loadAnimation(v.getContext(), R.anim.left_out); // v.getContext() can be changed by this()
                    final Animation rightInAnim = AnimationUtils.loadAnimation(v.getContext(), R.anim.right_in);

                    leftOutAnim.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            // this method is called when the animation first starts
                        }
                        @Override
                        // this method is called when the animation is finished playing
                        public void onAnimationEnd(Animation animation) {
                            findViewById(R.id.flashcard_question).startAnimation(rightInAnim);
                            //set question and answer TextViews with data from the database
                            ((TextView) findViewById(R.id.flashcard_question)).setText(allFlashcards.get(currentCardDisplayedIndex).getQuestion());
                            ((TextView) findViewById(R.id.flashcard_answer)).setText(allFlashcards.get(currentCardDisplayedIndex).getAnswer());
                            ((TextView) findViewById(R.id.answer_option1)).setText(allFlashcards.get(currentCardDisplayedIndex).getWrongAnswer1());
                            ((TextView) findViewById(R.id.answer_option2)).setText(allFlashcards.get(currentCardDisplayedIndex).getAnswer());
                            ((TextView) findViewById(R.id.answer_option3)).setText(allFlashcards.get(currentCardDisplayedIndex).getWrongAnswer2());
                        }
                        @Override
                        public void onAnimationRepeat(Animation animation) {
                            // we don't need to worry about this method
                        }
                    });
                    findViewById(R.id.flashcard_question).startAnimation(leftOutAnim);
                }
            }
        });

        countDownTimer = new CountDownTimer(16000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                ((TextView) findViewById(R.id.timer)).setText("" +millisUntilFinished/1000);
            }

            @Override
            public void onFinish() {

            }
        };

        findViewById(R.id.my_trash_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flashcardDatabase.deleteCard(((TextView) findViewById(R.id.flashcard_question)).getText().toString());
                if(allFlashcards.size() == 0) {
                    ((TextView) findViewById(R.id.flashcard_question)).setText("No Saved Card - Add a new Card");
                    findViewById(R.id.answer_option1).setVisibility(View.INVISIBLE);
                    findViewById(R.id.answer_option2).setVisibility(View.INVISIBLE);
                    findViewById(R.id.answer_option3).setVisibility(View.INVISIBLE);
                }
                allFlashcards = flashcardDatabase.getAllCards();
            }
        });
    }

    public int getRandomNumber(int maxNumber) {
        Random rand = new Random();
        return rand.nextInt(maxNumber);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 100 && resultCode == RESULT_OK){
            String question = data.getExtras().getString("question");
            String answer = data.getExtras().getString("answer");
            String answerWrong1 = data.getExtras().getString("answerWrong1");
            String answerWrong2 = data.getExtras().getString("answerWrong2");

            ((TextView) findViewById(R.id.flashcard_question)).setText(question);
            ((TextView) findViewById(R.id.flashcard_answer)).setText(answer);
            ((TextView) findViewById(R.id.answer_option1)).setText(answerWrong1);
            ((TextView) findViewById(R.id.answer_option2)).setText(answer);
            ((TextView) findViewById(R.id.answer_option3)).setText(answerWrong2);

            flashcardDatabase.insertCard(new Flashcard(question, answer, answerWrong1, answerWrong2));
            allFlashcards = flashcardDatabase.getAllCards();
        } else if(requestCode == 200 && resultCode == RESULT_OK){
            String editQuestion = getIntent().getStringExtra("editQuestion");
            String editAnswer = getIntent().getStringExtra("editAnswer");
            String editWrongAnswer1 = getIntent().getStringExtra("editWrongAnswer1");
            String editWrongAnswer2 = getIntent().getStringExtra("editWrongAnswer2");

            ((TextView) findViewById(R.id.flashcard_question)).setText(editQuestion);
            ((TextView) findViewById(R.id.flashcard_answer)).setText(editAnswer);
            ((TextView) findViewById(R.id.answer_option1)).setText(editWrongAnswer1);
            ((TextView) findViewById(R.id.answer_option2)).setText(editAnswer);
            ((TextView) findViewById(R.id.answer_option3)).setText(editWrongAnswer2);

            editedCard.setQuestion(editQuestion);
            editedCard.setAnswer(editAnswer);
            editedCard.setWrongAnswer1(editWrongAnswer1);
            editedCard.setWrongAnswer1(editWrongAnswer2);

            flashcardDatabase.updateCard(editedCard);
        }

        Snackbar.make(findViewById(R.id.flashcard_question),
                "Card Successfully Created",
                Snackbar.LENGTH_SHORT).show();
    }
    private void startTimer() {
        countDownTimer.cancel();
        countDownTimer.start();
    }


}