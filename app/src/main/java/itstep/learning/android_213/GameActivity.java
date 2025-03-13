package itstep.learning.android_213;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.compose.foundation.interaction.DragInteraction;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.w3c.dom.Text;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class GameActivity extends AppCompatActivity {
    private final Random random =  new Random();
    private final String bestScoreFilename = "score.best";
    private TextView tvScore;
    private TextView tvBestScore;
    private Animation spawnAnimation;
    private Animation collapseAnimation;
    private Animation bestScoreAnimation;
    private long score;
    private long bestScore;
    private final int N =4;
    private final int[][] tiles = new int[N][N];
    private final TextView[][] tvTiles = new TextView[N][N];
    private SavedState savedState;

    @SuppressLint({"ClickableViewAccessibility", "DiscouragedApi"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.game_layout_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        spawnAnimation = AnimationUtils.loadAnimation(this, R.anim.game_tile_spawn);
        collapseAnimation = AnimationUtils.loadAnimation(this, R.anim.game_tile_collapse);
        bestScoreAnimation = AnimationUtils.loadAnimation(this, R.anim.game_bestscore);


        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                tvTiles[i][j]= findViewById(
                        getResources().getIdentifier(
                                "game_tile_"+ i+j,
                                "id",
                                getPackageName()
                        )
                );
            }

        }
        tvScore =findViewById(R.id.game_tv_score);
        tvBestScore =findViewById(R.id.game_tv_best);
        findViewById(R.id.game_btn_undo).setOnClickListener(this::undoClick);
        findViewById(R.id.game_btn_new).setOnClickListener(this::newClick);
        LinearLayout gameField = findViewById(R.id.game_layout_field);
        gameField.post(()->{
           int vw = this.getWindow().getDecorView().getWidth();
           int fieldMargin= 20;
           LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                   vw- 2* fieldMargin,
                   vw- 2* fieldMargin
           );
           layoutParams.setMargins(fieldMargin, fieldMargin, fieldMargin, fieldMargin);
           layoutParams.gravity = Gravity.CENTER;
           gameField.setLayoutParams(layoutParams);
        });
        gameField.setOnTouchListener(new OnSwipeListener(GameActivity.this){
                    @Override
                    public void onSwipeBottom() {tryMakeMove(MoveDirection.bottom);}

                    @Override
                    public void onSwipeLeft() { tryMakeMove(MoveDirection.left); }

                    @Override
                    public void onSwipeRight() { tryMakeMove(MoveDirection.right); }

                    @Override
                    public void onSwipeTop() { tryMakeMove(MoveDirection.top); }
                });

        bestScore = 0L;
        loadBestScore();
        startNewGame();
    }
    private void tryMakeMove(MoveDirection moveDirection) {
        boolean canMove = false;
        switch (moveDirection) {
            case bottom: canMove = canMoveDown(); break;
            case left: canMove = canMoveLeft(); break;
            case right: canMove = canMoveRight(); break;
            case top: canMove = canMoveUp(); break;
        }
        if (canMove) {
            saveField();
            switch (moveDirection) {
                case bottom: moveDown(); break;
                case left: moveLeft(); break;
                case right: moveRight(); break;
                case top: moveUp(); break;
            }
            spawnTile();
            updateField();
            checkGameOver();
        } else {
            Toast.makeText(GameActivity.this, "NO move", Toast.LENGTH_SHORT).show();
        }
    }
    private boolean canMoveUp() {
        for (int i = 1; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (tiles[i][j] != 0 && (tiles[i - 1][j] == tiles[i][j] || tiles[i - 1][j] == 0)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void checkGameOver() {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (tiles[i][j] == 2048) {
                    showEndGameDialog("You Win!");
                    return;
                }
            }
        }
        if (!canMoveLeft() && !canMoveRight() && !canMoveUp() && !canMoveDown()) {
            showEndGameDialog("Game Over");
        }
    }
    private void showEndGameDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle(message)
                .setMessage("Start a new game?")
                .setPositiveButton("Yes", (dialog, which) -> startNewGame())
                .setNegativeButton("No", (dialog, which) -> finish())
                .setCancelable(false)
                .show();
    }
    private boolean canMoveDown() {
        for (int i = 0; i < N - 1; i++) {
            for (int j = 0; j < N; j++) {
                if (tiles[i][j] != 0 && (tiles[i + 1][j] == tiles[i][j] || tiles[i + 1][j] == 0)) {
                    return true;
                }
            }
        }
        return false;
    }
    private void moveUp() {
        boolean res = shiftUp(false);
        for (int j = 0; j < N; j++) {
            for (int i = 0; i < N - 1; i++) {
                if (tiles[i][j] == tiles[i + 1][j] && tiles[i][j] != 0) {
                    tiles[i][j] *= 2;
                    tiles[i + 1][j] = 0;
                    score += tiles[i][j];
                    res = true;
                }
            }
        }
        if (res) shiftUp(true);
    }
    private void moveDown() {
        boolean res = shiftDown(false);
        for (int j = 0; j < N; j++) {
            for (int i = N - 1; i > 0; i--) {
                if (tiles[i][j] == tiles[i - 1][j] && tiles[i][j] != 0) {
                    tiles[i][j] *= 2;
                    tiles[i - 1][j] = 0;
                    score += tiles[i][j];
                    res = true;
                }
            }
        }
        if (res) shiftDown(true);
    }
    private boolean shiftUp(boolean shiftTags) {
        boolean res = false;
        for (int j = 0; j < N; j++) {
            for (int i = 1; i < N; i++) {
                if (tiles[i][j] != 0 && tiles[i - 1][j] == 0) {
                    tiles[i - 1][j] = tiles[i][j];
                    tiles[i][j] = 0;
                    res = true;
                }
            }
        }
        return res;
    }
    private boolean shiftDown(boolean shiftTags) {
        boolean res = false;
        for (int j = 0; j < N; j++) {
            for (int i = N - 2; i >= 0; i--) {
                if (tiles[i][j] != 0 && tiles[i + 1][j] == 0) {
                    tiles[i + 1][j] = tiles[i][j];
                    tiles[i][j] = 0;
                    res = true;
                }
            }
        }
        return res;
    }
    private void moveRight(){
        boolean res;
        res = shiftRight(false);
        for (int i = 0; i < N; i++) {
            for (int j = N-1; j >0; j--) {
                if(tiles[i][j] ==tiles[i][j-1] && tiles[i][j] !=0) {
                    tiles[i][j] *=2;
                    tiles[i][j-1] =0;
                    score +=tiles[i][j];
                    res =true;
                    tvTiles[i][j].setTag(collapseAnimation);
                }
            }
        }
        if(res){
            shiftRight(true);
        }
    }
    private boolean canMoveLeft(){
        for (int i = 0; i < N; i++) {
            for (int j = 0; j <N-1; j++) {
                if(tiles[i][j] !=0 &&  tiles[i][j + 1] == tiles[i][j]  ||
                        tiles[i][j] == 0 && tiles[i][j + 1] != 0){

                    return true;
                }
            }
        }
        return false;
    }
    private boolean canMoveRight(){
        for (int i = 0; i < N; i++) {
            for (int j = 1; j <N; j++) {
                if(tiles[i][j] != 0 && tiles[i][j - 1] == tiles[i][j]  ||
                        tiles[i][j] == 0 && tiles[i][j - 1] != 0){

                    return true;
                }
            }
        }
        return false;
    }
    private void moveLeft(){
        boolean res;
        res = shiftLeft(false);
        for (int i = 0; i < N; i++) {
            for (int j = 0; j <N-1; j++) {
                if(tiles[i][j] ==tiles[i][j+1] && tiles[i][j] !=0) {
                    tiles[i][j] *=2;
                    tiles[i][j+1] =0;
                    score +=tiles[i][j];
                    res =true;
                    tvTiles[i][j].setTag(collapseAnimation);
                }
            }
        }
        if(res){
            shiftLeft(true);
        }
    }
    private boolean shiftRight(Boolean shiftTags){
        boolean res = false;
        for (int i = 0; i < N; i++) {
            boolean wasReplace ;
            do{
                wasReplace= false;
                for (int j = 0; j < N-1; j++) {
                    if(tiles[i][j] != 0 && tiles[i][j+1] == 0){
                        tiles[i][j+1] = tiles[i][j];
                        tiles[i][j]=0;
                        wasReplace = true;
                        res = true;
                        if(shiftTags){
                            Object tag= tvTiles[i][j].getTag();
                            tvTiles[i][j].setTag(tvTiles[i][j+1].getTag() );
                            tvTiles[i][j+1].setTag(tag);
                        }
                    }
                }
            }while(wasReplace);
        }
        return res;
    }
    private boolean shiftLeft(Boolean shiftTags){
        boolean res = false;
        for (int i = 0; i < N; i++) {
            boolean wasReplace ;
            do{
                wasReplace= false;
                for (int j = 1; j < N; j++) {
                    if(tiles[i][j] != 0 && tiles[i][j - 1] == 0){
                        tiles[i][j - 1] = tiles[i][j];
                        tiles[i][j]=0;
                        wasReplace = true;
                        res = true;
                        if(shiftTags){
                            Object tag= tvTiles[i][j].getTag();
                            tvTiles[i][j].setTag(tvTiles[i][j - 1].getTag() );
                            tvTiles[i][j - 1].setTag(tag);
                        }
                    }
                }
            }while(wasReplace);
        }
        return res;
    }
    private void saveField(){
        savedState = new SavedState(score, bestScore, new int[N][N]);
        for (int i = 0; i < N; i++) {
            System.arraycopy(tiles[i], 0, savedState.tiles[i], 0, N);
        }
    }

    private void saveBestScore(){
       try(FileOutputStream fos =  openFileOutput(bestScoreFilename, Context.MODE_PRIVATE)){
           DataOutputStream writer = new DataOutputStream(fos);
           writer.writeLong( bestScore );
           writer.flush();
           writer.close();
       }catch (IOException e){
           Log.w("GameActivity::saveBestScore", e.getMessage() + "");
       }
    }
    private void loadBestScore(){
        try(FileInputStream fis = openFileInput(bestScoreFilename)){
            DataInputStream reader = new DataInputStream(fis);
            bestScore = reader.readLong();
        }catch (IOException e){
            Log.w("GameActivity::loadBestScore", e.getMessage() + "");
        }
    }
    private void startNewGame(){
        score = 0L;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                //tiles[i][j] = (int)Math.pow(2, i+j);
                tiles[i][j]= 0;
            }
        }
        spawnTile();
        spawnTile();
        updateField();
    }

    @SuppressLint("DiscouragedApi")
    private void updateField(){
        if(score > bestScore){
            bestScore = score;
            saveBestScore();
            tvBestScore.setTag(bestScoreAnimation);
        }
        tvScore.setText(getString(R.string.game_tv_score_tpl, scoreToString(score) ));
        tvBestScore.setText(getString(R.string.game_tv_best_tpl, scoreToString(bestScore) ));

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                tvTiles[i][j].setText(scoreToString(tiles[i][j] ) );
                tvTiles[i][j].setTextColor(
                        getResources().getColor(
                                getResources().getIdentifier(
                                        String.format(Locale.ROOT,"game_tile%d_fg", tiles[i][j]),
                                        "color",
                                        getPackageName()
                                ),
                                getTheme()
                        )
                );

                tvTiles[i][j].getBackground().setColorFilter(
                        getResources().getColor(
                                getResources().getIdentifier(
                                        String.format(Locale.ROOT,"game_tile%d_bg", tiles[i][j]),
                                        "color",
                                        getPackageName()
                                ),
                                getTheme()
                        ),
                        PorterDuff.Mode.SRC_ATOP
                );
                tvTiles[i][j].setTextSize(getFontSize(tiles[i][j]));
                
                Object animTag = tvTiles[i][j].getTag();
                if(animTag instanceof Animation){
                    tvTiles[i][j].startAnimation((Animation) animTag);
                    tvTiles[i][j].setTag(null);
                }
                Object bestScoreTag = tvBestScore.getTag();
                if (bestScoreTag instanceof Animation) {
                    tvBestScore.startAnimation((Animation) bestScoreTag);
                    tvBestScore.setTag(null);
                }

            }
        }
    }
    private boolean spawnTile(){
        boolean res = false;
        List<Integer> freeTiles =  new ArrayList<>(N* N);
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if(tiles[i][j]==0){
                    freeTiles.add(N* i +j);
                }
            }
        }
        if(freeTiles.isEmpty()){
            return false;
        }
        int k = freeTiles.get(random.nextInt(freeTiles.size()));
        int i = k/N;
        int j = k%N;
        tiles[i][j]= random.nextInt(10)==0 ? 4 : 2;
        tvTiles[i][j].setTag( spawnAnimation);
        return res;
    }
    private String scoreToString(long value){
        return String.valueOf(value);
    }
    float getFontSize(int value) {
        if (value < 10) return 48.0f;
        if (value < 100) return 42.0f;
        if (value < 1000) return 36.0f;
        return 28.0f;
    }
    private static class SavedState{
        private int[][] tiles = null;
        private final long score;
        private final long bestScore;

        private SavedState(long score, long bestScore, int[][] tiles) {
            this.tiles = tiles;
            this.score = score;
            this.bestScore = bestScore;
        }
    }
    private static enum MoveDirection{
        bottom,
        left,
        right,
        top
    }
    private void undoClick(View view){
        if(savedState != null){
            score = savedState.score;
            bestScore = savedState.bestScore;
            for (int i = 0; i < N; i++) {
                System.arraycopy(savedState.tiles[i], 0,tiles[i],0, N);
            }
            savedState = null;
            updateField();
        }
        else{
            new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setTitle(R.string.game_tv_title)
                    .setMessage("Многократные сохранения доступны по подписке")
                    .setNeutralButton("Закрыть", (dlg, btn)-> {})
                    .setPositiveButton("Подписка", (dlg, btn)-> {
                        Toast.makeText(this, "Скоро будет реализовано", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Выход",(dlg, btn)-> finish() )
                    //.setIcon(android.R.drawable.ic_input_delete)
                    .setCancelable(false)
                    .show();
        }

    }
    private void newClick(View view){
        new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setTitle(R.string.game_tv_title)
                .setMessage("Начать новую игру")
                .setPositiveButton("Начать",    (dlg, btn)-> startNewGame() )
                .setNegativeButton("Продолжить",(dlg, btn)-> finish() )
                .show();
    }
}