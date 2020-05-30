package com.coinman.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import org.omg.CORBA.INTERNAL;

import java.awt.GraphicsDevice;
import java.util.ArrayList;
import java.util.Random;

public class CoinMan extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
	Texture[] man;
	int manState=0;
	int pause = 0;
	float gravity;
	float velocity=0;
	int manY;
	Random random;
	int score = 0;
	ArrayList<Integer> coinXs = new ArrayList<Integer>();
	ArrayList<Integer> coinYs = new ArrayList<Integer>();
	ArrayList<Rectangle> coinRectangles = new ArrayList<Rectangle>();

	ArrayList<Integer> bombXs = new ArrayList<Integer>();
	ArrayList<Integer> bombYs = new ArrayList<Integer>();
	ArrayList<Rectangle> bombRectangles = new ArrayList<Rectangle>();
	Texture coin;
	Texture bomb;
	int coinCount;
	int bombCount;
	Rectangle manRectangle;
	BitmapFont font;
	int gameState = 0;
	Texture dizzy;
	private Music music_bg;
	private Music music_coin;
	private Music music_bomb;
	@Override
	public void create () {
		batch = new SpriteBatch();
		background = new Texture("bg.png");
		man = new Texture[4];
		man[0] = new Texture("frame-1.png");
		man[1] = new Texture("frame-2.png");
		man[2] = new Texture("frame-3.png");
		man[3] = new Texture("frame-4.png");
		gravity = 0.5f;
		manY = Gdx.graphics.getHeight() / 2;
		coin = new Texture("coin.png");
		bomb = new Texture(("bomb.png"));
		random = new Random();
		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(10);
		dizzy = new Texture("dizzy-1.png");
		music_bg= Gdx.audio.newMusic(Gdx.files.internal("bg_music.mp3"));
		music_coin = Gdx.audio.newMusic(Gdx.files.internal("coin_music.mp3"));
		music_bomb = Gdx.audio.newMusic(Gdx.files.internal("explosion.wav"));
		music_bg.setLooping(true);
		music_bg.play();
	}
	public void makeCoin(){
		float height = random.nextFloat()*Gdx.graphics.getHeight();
		coinYs.add((int)height);
		coinXs.add(Gdx.graphics.getWidth());
	}
	public void makeBomb(){
		float height = random.nextFloat()*Gdx.graphics.getHeight();
		bombYs.add((int)height);
		bombXs.add(Gdx.graphics.getWidth());
	}

	@Override
	public void render () {
		batch.begin();
		batch.draw(background,0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
		if(gameState == 1){
			//Game is LIVE
			if(Gdx.input.justTouched()){
				velocity = -15;
			}
			if (pause < 5){
				pause ++;
			}else {
				pause = 0;
				if (manState < 3) {
					manState++;
				} else {
					manState = 0;
				}
			}
			velocity += gravity;
			manY -= velocity;
			if (manY <= 0){
				manY = 0;
			}
			if (bombCount < 250){
				bombCount++;
			}else{
				bombCount = 0;
				makeBomb();
			}
			bombRectangles.clear();
			for(int i =0;i<bombXs.size();i++){
				batch.draw(bomb,bombXs.get(i),bombYs.get(i));
				bombXs.set(i,bombXs.get(i)-12);
				bombRectangles.add(new Rectangle(bombXs.get(i),bombYs.get(i),bomb.getWidth(),bomb.getHeight()));
			}
			if (coinCount < 100){
				coinCount++;
			}else {
				coinCount = 0;
				makeCoin();
			}
			coinRectangles.clear();
			for(int i =0;i<coinXs.size();i++){
				batch.draw(coin,coinXs.get(i),coinYs.get(i));
				coinXs.set(i,coinXs.get(i)-8);
				coinRectangles.add(new Rectangle(coinXs.get(i),coinYs.get(i),coin.getWidth(),coin.getHeight()));
			}

			for (int i=0;i<coinRectangles.size();i++){
				if(Intersector.overlaps(manRectangle,coinRectangles.get(i))){
					music_coin.play();
					score ++;
					coinRectangles.remove(i);
					coinXs.remove(i);
					coinYs.remove(i);
					break;
				}
				music_coin.stop();
			}
			for (int i=0;i<bombRectangles.size();i++){
				if(Intersector.overlaps(manRectangle,bombRectangles.get(i))){
					gameState = 2;
					music_bomb.play();
				}
			}
		}else if (gameState == 0){
			//Waiting to start
			if(Gdx.input.justTouched()){
				gameState = 1;
			}
		}else if (gameState == 2){
			//Game over
			if(Gdx.input.justTouched()){
				gameState = 1;
				velocity=0;
				score = 0;
				bombXs.clear();
				bombYs.clear();
				bombCount = 0;
				coinYs.clear();
				coinXs.clear();
				coinCount = 0;
				bombRectangles.clear();
				coinRectangles.clear();
			}
		}
		if(gameState ==2){
			batch.draw(dizzy,Gdx.graphics.getWidth() / 2 - man[0].getWidth() / 2, manY);
		}else{
			batch.draw(man[manState], Gdx.graphics.getWidth() / 2 - man[0].getWidth() / 2, manY);
		}


		manRectangle = new Rectangle(Gdx.graphics.getWidth() / 2 - man[0].getWidth() / 2, manY,man[manState].getWidth(),man[manState].getHeight());

		font.draw(batch,String.valueOf(score),100,200);
		batch.end();
	}
	@Override
	public void dispose () {
		batch.dispose();

	}
}
