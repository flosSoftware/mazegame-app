package com.albertof.mazegame;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

// ACTIVITY <-> JFRAME / VIEW <-> JPANEL, JLABEL

// ANDROID STUDIO USA GRADLE PER L'AUTOMATIZZAZIONE DELLE BUILD
// E' POSSIBILE VEDERE I FILE DI CONFIGURAZIONE SOTTO LA VOCE GRADLE SCRIPTS
// MODIFICARE SEMPRE A MANO LO SCRIPT DEL MODULO APP
// (COME INDICATO NELLO SCRIPT DI QUESTO PROGETTO)
// ... ALMENO FINO A QUANDO NON E' STATO RISOLTO IL BUG ...
public class MainActivity extends AppCompatActivity {
    private Maze maze;
    private int dim;
    private ImageView[][] daIcons;
    private ArrayList<Timer> timers;
    private Handler mHandler;
    private FrameLayout viewStack; // PER METTERE LE VIEW IN PILA
    private MediaPlayer bgMp; // IL PLAYER DEL FILE DI BACKGROUND (DA STOPPARE AL TERMINE)
    private int level;
    private final int MAX_LEVEL = 2;
    private TextView livesTextView;
    private GridView gridview;

    // LE Activity DERIVANO DALLA CLASSE Context, TENGONO IN MEMORIA LO STATO DELL'APP,
    // QUINDI VANNO PASSATE OVUNQUE SIA NECESSARIO DISEGNARE O INTERAGIRE CON RISORSE DI TIPO
    // RAW (FILE DI TESTO, FILE AUDIO).
    // PER DISEGNARE, ATTACCHIAMO DELLE ImageView CHE PRENDONO DELLE RISORSE DRAWABLE (IMMAGINI)
    // LE RISORSE SONO IDENTIFICATE DA UN INT E NON DA UNA STRINGA DI PERCORSO (COME IN SWING)
    // PER AGGIUNGERE UNA RISORSA BASTA COPIARE IL FILE IN UNA SOTTOCARTELLA DI /res
    // PER RIFERIRSI A UNA RISORSA BISOGNA SCRIVERE IL PATH SENZA ESTENSIONE IN FORMA PUNTATA
    // (ES. R.drawable.monster -> VALORE int)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        level = 1;

        viewStack = new FrameLayout(this);
        viewStack.setBackgroundColor(Color.DKGRAY);

        loadGame(level++);

        setContentView(viewStack);

        bgMp = maze.playAudioFile(R.raw.bg2, true, 0,null);
    }

    public void updateAndShowGui(boolean hero, int monsterIdx) {

        MazeElement[][] els = maze.getEls();

        ArrayList<Position> pList = hero ? maze.getLastPositions(2) : maze.getLastMonsterPositions(2,monsterIdx);

        for (Position pp : pList) {
            int i = pp.getX();
            int j = pp.getY();
            boolean isMonsterPos = false;
            ArrayList<Monster> monsters = maze.getMonsters();
            int ii = 0;
            for (Monster monster : monsters) {
                if(i == monster.getP().getX() && j == monster.getP().getY()) {
                    isMonsterPos = true;
                    break;
                }
                ii++;
            }

            int icon;

            if (i == maze.getH().getP().getX() && j == maze.getH().getP().getY()) {
                icon = maze.getH().getIcon(this);
            } else if (isMonsterPos) {
                icon = monsters.get(ii).getIcon(this);
            } else {
                icon = els[i][j].getIcon(this);
            }

            daIcons[i][j].setImageResource(icon);
        }

        // CHECK IF WE SHOULD OPEN A DOOR

        MazeElement mE = els[maze.getH().getP().getX()][maze.getH().getP().getY()];

        if(mE instanceof Key && ((Key) mE).getDoor() != null) {
            Position dP = ((Key) mE).getDoor().getP();
            int i = dP.getX();
            int j = dP.getY();
            int icon = els[i][j].getIcon(this);
            daIcons[i][j].setImageResource(icon);
        }

        updateLivesTextView();

    }
    
    private void updateLivesTextView() {
        
        int lives = maze.getH().getLives();
        livesTextView.setText(""+lives);
        if(lives < 2)
            livesTextView.setBackgroundColor(Color.RED);
    }

    private void showEndPane(boolean won) {
        int img;

        if (won) {
            img = R.drawable.youwin;
        } else {
            img = R.drawable.youlose;
        }

        ImageView imV = new ImageView(this);
        imV.setImageResource(img);
        imV.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER));
        viewStack.addView(imV);
    }

    public boolean isGameEnded() {
        boolean end = maze.isGameEnded();
        
        if(end) {
            MediaPlayer.OnCompletionListener listener = new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    if (level <= MAX_LEVEL) {
                        //bgMp.stop();
                        //bgMp.release();
                        
                        loadGame(level++);

                        //bgMp = maze.playAudioFile(R.raw.bg2, true, 0,null);
                    }
                }
            };
            showEndPane(maze.won(listener));
            gridview.setOnItemClickListener(null);
        }
        return end; 
    }

    private void loadGame(int level) {

        viewStack.removeAllViews();
        
        // LA CLASSE LOG PERMETTE DI STAMPARE SULLA CONSOLE DI LOGCAT
        // (ATTENZIONE A NON USARE IL LIVELLO DI LOG D O V, MA USARE WTF O ALTRO TIPO E, ...)

        Log.wtf("WTF","!!!!! LIVELLO "+level+" !!!!!");

        //SystemClock.sleep(2000); // FREEZE ...

        maze = new Maze(this, getResId(true, "liv"+level)); // FILE DI TESTO
        // !!! DISABILITARE "Strip trailing spaces on Save" IN ANDROID STUDIO !!!
        // (https://stackoverflow.com/questions/27063903/how-do-you-stop-android-studio-from-deleting-trailing-spaces)
        dim = maze.getDim();

        //        Log.wtf("WTF",""+maze);

        final ImageView[][] icons = new ImageView[dim][dim];

        daIcons = icons;

        MazeElement[][] els = maze.getEls();

        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                boolean isMonsterPos = false;
                ArrayList<Monster> monsters = maze.getMonsters();
                int ii = 0;
                for (Monster monster : monsters) {
                    if(i == monster.getP().getX() && j == monster.getP().getY()) {
                        isMonsterPos = true;
                        break;
                    }
                    ii++;
                }
                int icon = i == maze.getH().getP().getX() && j == maze.getH().getP().getY() ?
                        maze.getH().getIcon(this)
                        : isMonsterPos ?
                        monsters.get(ii).getIcon(this)
                        : els[i][j].getIcon(this);

                ImageView imV = new ImageView(this);
                imV.setImageResource(icon);
                icons[i][j] = imV;
            }
        }
        
        gridview = new GridView(this);
        gridview.setBackgroundColor(Color.LTGRAY);
        gridview.setNumColumns(dim);
        gridview.setVerticalSpacing(0);
        gridview.setHorizontalSpacing(0);
        //gridview.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);

        gridview.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, // PRENDO LA LARGHEZZA FINESTRA
                ViewGroup.LayoutParams.WRAP_CONTENT, // L'ALTEZZA DIPENDE DAL CONTENUTO
                Gravity.CENTER)); // TUTTO AL CENTRO DELLA FINESTRA

        final ImageAdapter adapter = new ImageAdapter(this, icons);
        gridview.setAdapter(adapter);

        ArrayList<Monster> monsters = maze.getMonsters();

        // L'HANDLER SERVE PER GESTIRE I CAMBIAMENTI ALLA GRAFICA NEL THREAD PRINCIPALE
        // (SE NON LO FACCIO NEL THREAD PRINCIPALE, SUCCEDE UN CASINO)
        mHandler = new Handler() {
            public void handleMessage(Message msg) {

                maze.monsterMove(msg.what);

                updateAndShowGui(false,msg.what);

                adapter.notifyDataSetChanged();

                if (isGameEnded()) {
//						System.out.println("timer stop");
                    timers.get(msg.what).cancel();
                }
            }
        };

        // I TIMER ANDROID FUNZIONANO IN MODO LEGGERMENTE DIVERSO DA SWING
        // QUANDO C'E' UN EVENTO DEL TIMER ALLORA MANDO UN MESSAGGIO
        // ALL'HANDLER (THREAD PRINCIPALE)
        timers = new ArrayList<Timer>(monsters.size());

        for(int i = 0; i < monsters.size(); i++) {

            final int ii = i;

            Timer timer = new Timer();

            timer.scheduleAtFixedRate(new TimerTask() {
                public void run() {
                    mHandler.obtainMessage(ii).sendToTarget();
                }
            }, 0, 250);


            timers.add(timer);

        }

        // GESTIONE EVENTI
        // QUESTO CALLBACK CATTURA I TAP
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                // IL TOAST E' UNA SPECIE DI ETICHETTA CHE COMPARE SULLO SCHERMO E POI SPARISCE
//                Toast.makeText(MainActivity.this, "" + position,
//                        Toast.LENGTH_SHORT).show();

                // PASSO DA UN INDICE NEL GRIDVIEW ALLA COPPIA DI COORDINATE X/Y
                int x = position/icons[0].length;
                int y = position-(x*icons[0].length);

                int deltaX = x - maze.getH().getP().getX();
                int deltaY = y - maze.getH().getP().getY();

                // NON POSSO FARE SALTI ;)
                if(deltaX <= 1 && deltaX >= -1 && deltaY >= -1 && deltaY <= 1) {
                    //Log.wtf("WTF","pos: "+position+" x: "+x+" y: "+y);

                    maze.receiveMove(deltaX, deltaY);
                    updateAndShowGui(true, -1);
                    adapter.notifyDataSetChanged(); // NOTIFICO CHE I DATI SONO CAMBIATI
                    // QUESTO FORZA UN REFRESH SULL'ADAPTER (CHIAMANDO ADPTER.GETVIEW())
                    isGameEnded();
                }

            }
        });

        
        viewStack.addView(gridview);


//        int[] scrSize = getScreenSize();
//        Log.wtf("WTF","screen: "+scrSize[0]+"x"+scrSize[1]);

        livesTextView = new TextView(this);
        livesTextView.setText(""+maze.getH().getLives());
        int txtHeight = 80;
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                txtHeight,
                txtHeight,
                Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL);
        
        int margin = txtHeight;
        lp.setMargins(0,0,0,margin);
        livesTextView.setLayoutParams(lp); // TUTTO IN FONDO ALLA FINESTRA
        livesTextView.setBackgroundColor(Color.rgb(0, 204, 0));
        livesTextView.setTextColor(Color.BLACK);
        livesTextView.setTextSize(20);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            livesTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        }
        livesTextView.setPadding(0,0, 0, 0);
        
        viewStack.addView(livesTextView);

    }


    // METODO UN PO' LENTO (USARE CON PARSIMONIA!)
    /**
     * lookup a resource id by field name in static R.class
     *
     * @param variableName - name of drawable, e.g R.drawable.<b>image</b>
     * @return integer id of resource
     */
    public static int getResId(boolean isRaw, String variableName)
        throws android.content.res.Resources.NotFoundException {
        try {
            Class<?> с;
            if(isRaw)
                с = R.raw.class;
            else
                с = R.drawable.class;
            // lookup field in class
            java.lang.reflect.Field field = с.getField(variableName);
            // always set access when using reflections
            // preventing IllegalAccessException
            field.setAccessible(true);
            // we can use here also Field.get() and do a cast
            // receiver reference is null as it's static field
            return field.getInt(null);
        } catch (Exception e) {
            // rethrow as not found ex
            throw new Resources.NotFoundException(e.getMessage());
        }
    }

    // NON USATO, MA POTENZIALMENTE UTILE
    private int[] getScreenSize() {

        WindowManager w = this.getWindowManager();
        Display d = w.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        d.getMetrics(metrics);
// since SDK_INT = 1;
        int widthPixels = metrics.widthPixels;
        int heightPixels = metrics.heightPixels;
// includes window decorations (statusbar bar/menu bar)
        if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 17)
            try {
                widthPixels = (Integer) Display.class.getMethod("getRawWidth").invoke(d);
                heightPixels = (Integer) Display.class.getMethod("getRawHeight").invoke(d);
            } catch (Exception ignored) {
            }
// includes window decorations (statusbar bar/menu bar)
        if (Build.VERSION.SDK_INT >= 17)
            try {
                Point realSize = new Point();
                Display.class.getMethod("getRealSize", Point.class).invoke(d, realSize);
                widthPixels = realSize.x;
                heightPixels = realSize.y;
            } catch (Exception ignored) {
            }
        return new int[]{widthPixels, heightPixels};
    }



}
