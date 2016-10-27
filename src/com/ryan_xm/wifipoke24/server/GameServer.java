package com.ryan_xm.wifipoke24.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import java.util.Map.Entry;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

public class GameServer {
	
	/*************************NO BUG*************************/
	private static final char[] wJ = "0123456789abcdef".toCharArray();
    public static String imsi = "204046330839890";
    public static String p = "0";
    public static String keyword = "电话";
    public static String tranlateKeyword = "%E7%94%B5%E8%AF%9D";
	/*************************NO BUG************************/
	
	
	private static GameServer INSTANCE = null;

	// who is playing now?
	private String playerName = null;
	
	// playerData: HashMap of (String) player name + (Data) player data
	//private HashMap<String, Data> playerData = new HashMap<String, Data>();
	
	
	private Porks mPorks;
	private GameServer() {
		mPorks = new Porks();
	}

	public static GameServer getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new GameServer();
		}
		return INSTANCE;
	}
	
	
	/** 服务器传输过来的牌，则保存起来 */
	public void setPorks(Porks porks){
		this.mPorks = porks;
	}
	
	/** 服务端获取牌，并发送给客户端 */
	public Porks getPorks(){
		return mPorks;
	}
	
	
	/**
	 * 随机产生4张牌
	 */
	public void InitRandomPorks(){
		mPorks.CreatRandomPorks();
	}
	
	

	
	


	
	
	//--------------------------------------------------------------------
	
	
	/*
	 

	public void GameOverActions() {
		this.saveScore();
	}

	public boolean BoardGameOverSet() {
		return this.isGameOver();
	}
	// show pop-up where choose the team
	// return the team chosen
	public int windowChoiceTeam(int availableTeams) {
		return (availableTeams-1);
	}

	public void showError(String string) {
		Log.d("ERROR", string);
	}

	public String playPiece() {
		return "NEXT";
		// return
		// "ERROR" == send error to all
		// "NEXT" == next player
		// "DO" == continue playing
		// "END" == end
	}

	public Piece getPiece() {
		return new Piece(1,1);
	}
	
	public Piece getCurrentPiece() {
		return this.board.getCurrentpiece();
	}


	public void setPiece(Object object) {
		// check if it's different of the last piece. Then
		// it means that it's another turn of a same player...
		// A bit odd... I know. Check the logic of CtrlNet!
	}

	public ArrayList<Integer> cleanBoard(){
		return this.board.cleanBoard();
	}
	
	public void createNewCleanBoard(){
		this.board = new Board();
	}
	
	
	public int[][] getBoard() {
		return board.getBoard();
	}
	
	public void setNewRandomPiece(){
		int pid = new Double(Math.random() * PieceConstants.npieces).intValue();
		Piece p = new Piece(pid,0);
		p.y = PieceConstants.startPos[p.getType()][p.getRotation()][0];
		p.x = PieceConstants.startPos[p.getType()][p.getRotation()][1];
		this.board.setCurrentpiece(this.board.getNextpiece());
		this.board.setNextpiece(p);
		
		if(!this.isSingleplay()){
			if(this.board.getCurrentpiece()!=null) this.board.getCurrentpiece().color = board.color();
			if(this.board.getNextpiece()!=null) this.board.getNextpiece().color = Color.BLACK;
		}
	}
	
	public void setBoard(Board object) {
		this.board = object;
	}
	
	public HashMap<String,Data> getPlayers() {
		return this.board.getPlayers();
	}

	public void setPlayers(Vector<String> names){
		for (int i=0; i<teams.size(); i++) {
			int team = teams.get(i).intValue();
			Data data = new Data(team);
			String name = names.get(i).toString();
			this.board.setPlayer(name, data);
		}
	}

	public void saveScore() {
		// TODO: call @ isGameOver()
		HashMap<String,Data> playerData = this.board.getPlayers();
		
		int scoresSize = playerData.size();
		int type = (scoresSize==1) ? 0 : 1;
		Long date = System.currentTimeMillis();
		
		for (Entry<String, Data> player : playerData.entrySet()) {
			Integer team = player.getValue().getTeam();
			Integer tscore = 0;
			for (Entry<String, Data> temp : playerData.entrySet()) {
				if (temp.getValue().getTeam()==team) {
					tscore += temp.getValue().getScore();
				}
			}
			db.insertValues(type, player.getKey(), player.getValue().getScore(), tscore, date);
		}
	}
	
	public Cursor getScoreInd() {
		return db.getScoreInd();
	}
	
	public Cursor getScoreTeam() {
		return db.getScoreTeam();
	}

	public String getPlayerName() {
		return db.getPlayerName();
	}
	
	public void setPlayerName(String name) {
		db.setPlayerName(name);
	}

	public Bundle getConfCreate() {
		return db.getConfCreate();
	}

	public void setConfCreate(Bundle b) {
		db.setConfCreate(b);
	}
	
	public void gameStep() {
		this.board.getCurrentpiece().x = this.board.getCurrentpiece().x + 1;
	}

	public boolean currentPieceCollision() {
		return !this.board.isMovementPossible(getCurrentPiece());
	}
	
	public boolean nextStepPieceCollision() {
		Piece pf = new Piece(getCurrentPiece().getType(),getCurrentPiece().getRotation());
		pf.x = getCurrentPiece().x + 1;
		pf.y = getCurrentPiece().y;
		return !this.board.isMovementPossible(pf);
	}

	public void addCurrentPieceToBoard() {
		this.board.addPiece(getCurrentPiece());
	}


	public boolean currentPieceOffsetCollision(int offset) {
		Piece pf = new Piece(getCurrentPiece().getType(),getCurrentPiece().getRotation());
		pf.x = getCurrentPiece().x;
		pf.y = getCurrentPiece().y + offset;
		return !this.board.isMovementPossible(pf);
	}

	public Piece getNextPiece() {
		return this.board.getNextpiece();
	}

	public boolean isGameOver() {
		return this.board.gameOver();
	}

	public boolean currentPieceCollisionRC(int row, int col) {
		Piece pf = new Piece(getCurrentPiece().getType(),getCurrentPiece().getRotation());
		pf.x = row;
		pf.y = col;
		return !this.board.isMovementPossible(pf);
	}
	*/

}
