package com.albertof.mazegame;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;



public class Maze {

	private int dim;

	private static int EL_SIZE; // 16 or 32 (pixels)

	private MazeElement[][] els;

	private Hero h;

	private ArrayList<Monster> monsters;

	private ArrayList<Position> path;

	private ArrayList<LimitedCapacityArrayList<Position>> mPath;

	private boolean playEndingAudio;

	private boolean gameEnded;

	private Context context;

	public Maze(Context ctxt, int inputFileId) {

		context = ctxt;

		playEndingAudio = true;

		ArrayList<Door> doors = new ArrayList<Door>();
		ArrayList<Key> keys = new ArrayList<Key>();

		path = new LimitedCapacityArrayList<Position>(2);
		mPath = new ArrayList<LimitedCapacityArrayList<Position>>();
		monsters = new ArrayList<Monster>();

		InputStream is = context.getResources().openRawResource(inputFileId);
		Scanner in = new Scanner(is);

		dim = in.nextInt();
		EL_SIZE = in.nextInt();
		int lives = in.nextInt();
		in.nextLine();

		this.els = new MazeElement[dim][dim];
		int i = 0;
		while (i < dim && in.hasNextLine()) {
			String line = in.nextLine();
			int j = 0;
			while (j < dim) {
				char c = line.charAt(j);
				els[i][j] = convert(c, i, j);
				if (els[i][j] instanceof Door) {
					doors.add((Door) els[i][j]);
				} else if (els[i][j] instanceof Key) {
					keys.add((Key) els[i][j]);
				}

				if (c == 'o') {
					h = new Hero(i, j, lives);
					path.add(new Position(i, j));
				} else if (c == '>') {
					monsters.add(new Monster(i, j, Direction.RIGHT));
					LimitedCapacityArrayList<Position> l = new LimitedCapacityArrayList<Position>(2);
					l.add(new Position(i, j));
					mPath.add(l);
				} else if (c == '<') {
					monsters.add(new Monster(i, j, Direction.LEFT));
					LimitedCapacityArrayList<Position> l = new LimitedCapacityArrayList<Position>(2);
					l.add(new Position(i, j));
					mPath.add(l);
				} else if (c == '^') {
					monsters.add(new Monster(i, j, Direction.UP));
					LimitedCapacityArrayList<Position> l = new LimitedCapacityArrayList<Position>(2);
					l.add(new Position(i, j));
					mPath.add(l);
				} else if (c == 'v') {
					monsters.add(new Monster(i, j, Direction.DOWN));
					LimitedCapacityArrayList<Position> l = new LimitedCapacityArrayList<Position>(2);
					l.add(new Position(i, j));
					mPath.add(l);
				}

				j++;
			}

			i++;
		}
		// LINK KEYS TO DOORS
		for (Key key : keys) {
			key.setDoor(doors.get(in.nextInt()));
		}

		in.close();

	}

	private MazeElement convert(char c, int x, int y) {
		// System.out.println("converting " + c + " " + x + " " + y);
		if (c == ' ' || c == 'o' || c == '>' || c == '<' || c == '^' || c == 'v') {
			return new Space(x, y, false);
		} else if (c == '?') {
			return new Door(x, y, false, Orientation.VERT);
		} else if (c == '/') {
			return new Door(x, y, true, Orientation.VERT);
		} else if (c == '_') {
			return new Door(x, y, false, Orientation.HORIZ);
		} else if (c == '\\') {
			return new Door(x, y, true, Orientation.HORIZ);
		} else if (c == '-') {
			return new Wall(x, y, Orientation.HORIZ);
		} else if (c == '|') {
			return new Wall(x, y, Orientation.VERT);
		} else if (c == 'x') {
			return new Space(x, y, true);
		} else if (c == '@') {
			return new Key(x, y);
		}
		return null;
	}

	public Maze(int dim, MazeElement[][] els) {
		this.els = els;
		this.dim = dim;
	}

	@Override
	public String toString() {
		String s = "";
		for (int i = 0; i < dim + 2; i++) {

			s += "*";
		}

		s += "\n";

		for (int i = 0; i < dim; i++) {

			String row = "";

			for (int j = 0; j < dim; j++) {
				if (h.getP().equals(new Position(i, j)))
					row += h;
				else {
					MazeElement e = els[i][j];
					row += e;
				}
			}

			s += "*" + row + "*\n";
		}

		for (int i = 0; i < dim + 2; i++) {

			s += "*";
		}

		s += "\n";

		return s;
	}

	private void undo() {
		h.setP(path.get(path.size() - 1));
	}

	public void selfMove() {
		Position hP = h.getP();
		Position[] n = hP.getNeighborhood();

		// System.out.println("old p "+hP);

		for (Position p : n) {

			if (checkPos(p)) {

				MazeElement e = els[p.getX()][p.getY()];

				if ((e instanceof Space && ((Space) e).isFinal()) || (!path.contains(p) && e.isWalkable())) {

					int deltaX = p.getX() - hP.getX();
					int deltaY = p.getY() - hP.getY();

					try {
						Position nup = els[p.getX()][p.getY()].enter(getDirection(deltaX, deltaY));
						int fName = els[p.getX()][p.getY()].getAudioFile();
						if (fName != -1)
							playAudioFile(fName, false, 0,null);
						h.setP(nup);
						path.add(nup);
						// System.out.println("nu p "+nup);
						return;
					} catch (IllegalArgumentException | NoseGotHurtException ex) {
						System.out.println(ex);
					}
				}
			}

		}
	}

	public void monsterMove(int monsterIdx) {

		// System.out.println(m);
		// System.out.println(m.getP());

		Monster monster = monsters.get(monsterIdx);
		Position mP = monster.getP();
		Position n = monster.getNextP();

		if (checkPos(n)) {

			MazeElement e = els[n.getX()][n.getY()];

			int deltaX = n.getX() - mP.getX();
			int deltaY = n.getY() - mP.getY();

			try {
				Position nup = e.enter(getDirection(deltaX, deltaY));

				monster.setP(nup);
				mPath.get(monsterIdx).add(nup);
				// System.out.println(nup);

				if (heroIsCaughtByMonster(monsterIdx)) {
					h.die();
					checkIfGameIsEnded();
				}

				return;
			} catch (IllegalArgumentException | NoseGotHurtException ex) {
				//Log.wtf("WTF","MOSTRO "+monsterIdx+" ha sbattuto!!!!!");
				monster.invertDir();
			}

		} else {
			monster.invertDir();
		}

	}

	public void receiveMove(int deltaX, int deltaY) {
		Position hP = h.getP();
		// System.out.println("old p "+hP);
		Position elP = new Position(hP.getX() + deltaX, hP.getY() + deltaY);
		if (checkPos(elP)) {
			MazeElement el = els[elP.getX()][elP.getY()];
			// System.out.println(el);
			Position nup;
			try {
				nup = el.enter(getDirection(deltaX, deltaY));

				int fName = el.getAudioFile();
				if (fName != -1)
					playAudioFile(fName, false, 0, null);

				// System.out.println("nu p "+nup);

				h.setP(nup);
				path.add(nup);

				// CAREFUL, CHECK ONLY AFTER UPDATE OF H POSITION!
				checkIfGameIsEnded();

			} catch (NoseGotHurtException e) {

				playAudioFile(R.raw.huh, false, 0,null);
				// System.out.println(e);
				h.die();
				checkIfGameIsEnded();

			} catch (IllegalArgumentException e) {

				playAudioFile(R.raw.incorrect, false, 0,null);
				// System.out.println(e);
			}

		} else {

			playAudioFile(R.raw.incorrect, false, 0,null);
			// System.out.println("you are out of the maze");
		}
	}

	private boolean heroIsCaughtByMonster(int monsterIdx) {
		Monster monster = monsters.get(monsterIdx);
		if (h.getP().equals(monster.getP()))
			return true;
		return false;
	}

	public ArrayList<Position> getLastPositions(int depth) {
		ArrayList<Position> p = new ArrayList<Position>(depth);
		for (int i = 0; i < depth; i++) {
			int idx = path.size() - 1 - i;
			if (idx > -1) {
				Position pp = path.get(idx);
				p.add(pp);
			}
		}
		return p;
	}

	public ArrayList<Position> getLastMonsterPositions(int depth, int monsterIdx) {

		ArrayList<Position> p = new ArrayList<Position>(depth);
		for (int i = 0; i < depth; i++) {
			int idx = mPath.get(monsterIdx).size() - 1 - i;
			if (idx > -1) {
				Position pp = mPath.get(monsterIdx).get(idx);
				p.add(pp);
			}
		}

		return p;
	}

	private Direction getDirection(int deltaX, int deltaY) {
		if (deltaY == 0) {

			if (deltaX > 0) {
				return Direction.DOWN;
			} else {
				return Direction.UP;
			}
		} else if (deltaX == 0) {

			if (deltaY > 0) {
				return Direction.RIGHT;
			} else {
				return Direction.LEFT;
			}
		} else if (deltaX > 0) {

			if (deltaY > 0) {
				return Direction.DOWN_R;
			} else {
				return Direction.DOWN_L;
			}
		} else {

			if (deltaY > 0) {
				return Direction.UP_R;
			} else {
				return Direction.UP_L;
			}
		}

	}

	public boolean checkPos(Position p) {

		int x = p.getX();
		int y = p.getY();

		if (x < 0 || y < 0 || x >= dim || y >= dim) {

			return false;

		} else {
			
			return true;
		}
	}

	public void checkIfGameIsEnded() {
		MazeElement e = els[h.getP().getX()][h.getP().getY()];

		if (e instanceof Space && ((Space) e).isFinal()) {			
			gameEnded = true;
		} else {
			gameEnded = h.isDead();
		}

	}

	public boolean won(MediaPlayer.OnCompletionListener listener) {
		if (!gameEnded)
			throw new IllegalArgumentException("you can't check if you won, the game is still running");

		if (h.isDead()) {
			if (playEndingAudio) {
				Log.wtf("WTF","playing end audio: dying");
				playAudioFile(R.raw.dying,false,0, listener);
				playEndingAudio = false;
			}
			return false;
		} else {
			if (playEndingAudio) {
				Log.wtf("WTF","playing end audio: applause");
				playAudioFile(R.raw.applause,false,0, listener);
				playEndingAudio = false;
			}
			return true;
		}
	}


	public MediaPlayer playAudioFile(int fname, boolean loop, float gain, MediaPlayer.OnCompletionListener listener) {
//		Log.wtf("wtf","fname "+ fname);
		if(this.context == null)
			Log.wtf("wtf","NULL CTXT");
		MediaPlayer mp = MediaPlayer.create(this.context, fname);
		if (loop)
			mp.setLooping(true);
		else
			mp.setLooping(false);
		if(listener != null)
		    mp.setOnCompletionListener(listener);
		mp.start();
        return mp;
	}

	public boolean isGameEnded() {
		return gameEnded;
	}

	public int getDim() {
		return dim;
	}

	public void setDim(int dim) {
		this.dim = dim;
	}

	public MazeElement[][] getEls() {
		return els;
	}

	public void setEls(MazeElement[][] els) {
		this.els = els;
	}

	public Hero getH() {
		return h;
	}

	public void setH(Hero h) {
		this.h = h;
	}

	public ArrayList<Monster> getMonsters() {
		return monsters;
	}

	public void setMonsters(ArrayList<Monster> m) {
		this.monsters = m;
	}

	public static int getElSize() {
		return EL_SIZE;
	}

}
