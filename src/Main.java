import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Random;
import java.util.Scanner;

class input {
    static Scanner inp;
    input() {
        inp=new Scanner(System.in);
    }
}

class GameWinnerException extends Exception{
    public GameWinnerException(String message) {
        super(message);
    }
}

class DieValueException extends Exception{
    public DieValueException(String message) {
        super(message);
    }
}

class SnakeBiteException extends Exception{
    public SnakeBiteException(String message) {
        super("Hiss...! I am a Snake, you go back 7 tiles!");
    }
}

class CricketBiteException extends Exception{
    public CricketBiteException(String message) {
        super("Chirp...! I am a Cricket, you go back 2 tiles!");
    }
}

class VultureBiteException extends Exception{
    public VultureBiteException(String message) {
        super("");
    }
}

class TrampolineBiteException extends Exception{
    public TrampolineBiteException(String message) {
        super("");
    }
}

class TrackLengthException extends Exception{
    public TrackLengthException(String message) { super(""); }
}

class Game{
    private int length;
    private ArrayList<String> track = new ArrayList<>();


    public ArrayList<String> getTrack() {
        return track;
    }

    public void setTrack(ArrayList<String> track) {
        this.track = track;
    }

    public int getLength() {
        return length;
    }

    void start() throws InputMismatchException, DieValueException, SnakeBiteException, VultureBiteException,
            CricketBiteException, TrampolineBiteException, GameWinnerException, TrackLengthException {
        boolean ans = false;
        while(!ans){
            System.out.print("Enter total number of tiles on the race track (length): ");
            try {
                length = input.inp.nextInt();
                if(length<10){
                    throw new TrackLengthException("");
                }
                else{
                    ans = true;
                }
            }
            catch(InputMismatchException inp) {
                System.out.println("Wrong input:");
                System.out.println("Try again");
                input.inp.next();
            }
            catch (TrackLengthException e){

                System.out.println("Minimum length of the track should be 10");
            }
        }

        Tile snake = new Snake();
        Tile vulture = new Vulture();
        Tile cricket = new Cricket();
        Tile trampoline = new Trampoline();
        Tile white = new White();

        Map map = new Map();
        map.generate(length, snake, vulture, cricket, trampoline, white);
        snake.setShake_tile(false);
        vulture.setShake_tile(false);
        cricket.setShake_tile(false);
        trampoline.setShake_tile(true);
        white.setShake_tile(1);

        System.out.println("Danger: There are "+snake.getTile()+", "+cricket.getTile()+", "+vulture.getTile()+" numbers"
                + " of Snakes, Cricket, and Vultures respectively on your track!");

        System.out.println("Danger: Each Snake, Cricket, and Vultures can throw you back by "+snake.getShake_tile()+", "+
                cricket.getShake_tile()+", "+vulture.getShake_tile()+ " number of Tiles respectively!");

        System.out.println("Good News: There are "+ trampoline.getTile() +" number of Trampolines on your track!");

        System.out.println("Good News: Each Trampoline can help you advance by "+ trampoline.getShake_tile()
                +" number of Tiles");

        map.generate_race_track(track, length, snake, vulture, cricket, trampoline, white);
        track.add(0,"");


        System.out.print("Enter the User Name: ");
        String name = input.inp.next();
        User user = new User(name);

        System.out.println("Starting the game with "+user.getName()+ " at Tile-1");
        System.out.println("Control transferred to Computer for rolling the Dice for Josh");

        boolean status= true;
        while(status){
            try{
                play(user, snake, vulture, cricket, trampoline, white);
            }
            catch (GameWinnerException e){
                status= false;
                int no_of_rolls = user.getRolls()-1;
                System.out.println(user.getName()+" wins the race in "+no_of_rolls+" rolls.");
            }
            catch (DieValueException e){
                System.out.println(e.getMessage());
            }
        }

        System.out.println("Total Snake Bites = "+snake.getBites());
        System.out.println("Total Vulture Bites = "+vulture.getBites());
        System.out.println("Total Cricket Bites = "+cricket.getBites());
        System.out.println("Total Trampoline Bites ="+trampoline.getBites());
    }

    void roll_message(User user, int roll_value){
        System.out.print("[Roll-"+user.getRolls()+"]: "+user.getName()+" rolled "+ roll_value+" at Tile:"+user.getPosition()+". ");
    }

    void set_position(Tile tile, User user){
        tile.setBites(tile.getBites()+1);
        int pos = user.getPosition()+tile.getShake_tile();
        if(pos<1){
            user.setPosition(1);
        }
        else{
            user.setPosition(pos);
        }
    }

    void roll_message_position(User user, Tile snake, Tile vulture, Tile cricket, Tile trampoline, Tile white)
            throws SnakeBiteException, VultureBiteException, CricketBiteException, TrampolineBiteException{
        System.out.print("Landed on Tile: "+user.getPosition());
        System.out.println();
        System.out.println("Trying to shake the Tile: "+user.getPosition());
        if(user.getPosition()==track.size()-1){
            user.setPosition(track.size()-1);
        }
        else if(track.get(user.getPosition()).equals("S")){
            set_position(snake, user);
            throw new SnakeBiteException(" Hiss...! I am a Snake, you go back "+ snake.getShake_tile() +" tiles!");
        }
        else if(track.get(user.getPosition()).equals("C")){
            set_position(cricket, user);
            throw new CricketBiteException("Chirp...! I am a Cricket, you go back "+ cricket.getShake_tile() +" tiles!");
        }
        else if(track.get(user.getPosition()).equals("V")){
            set_position(vulture, user);
            throw new VultureBiteException("Yapping...! I am a Vulture, you go back "+ vulture.getShake_tile() +" tiles!");
        }
        else if(track.get(user.getPosition()).equals("T")){
            trampoline.setBites(trampoline.getBites()+1);
            int pos = user.getPosition()+trampoline.getShake_tile();
            if(pos>track.size()-1){
                user.setPosition(user.getPosition());
            }
            else{
                user.setPosition(pos);
            }
            throw new TrampolineBiteException("PingPong! I am a Trampoline, you advance "+ trampoline.getShake_tile()+" tiles");
        }
        else{
            System.out.println("I am a white Tile!");
        }
    }

    void play(User user, Tile snake, Tile vulture, Tile cricket, Tile trampoline, Tile white)
            throws DieValueException, SnakeBiteException, VultureBiteException, CricketBiteException,
            TrampolineBiteException, GameWinnerException {
        if(user.getRolls()==1){
            System.out.println("Game Started ======================>");
        }


        Die die = new Die();
        int roll_value=0;

        while(!user.isWin_status()){
            if(user.getPosition()==1){
                while(roll_value!=6){
                    roll_value = die.roll();
                    roll_message(user, roll_value);
                    if(roll_value!=6){
                        user.setRolls(user.getRolls()+1);
                        throw new DieValueException("OOPs you need 6 to start");
                    }
                    else{
                        System.out.println("You are out of the cage! You get a free roll");
                    }
                    user.setRolls(user.getRolls()+1);
                }
                roll_value = die.roll();
                roll_message(user, roll_value);
                if(user.getPosition()+roll_value<=track.size()-1){
                    exceptionnn(user, snake, vulture, cricket, trampoline, white, roll_value);
                }
                else{
                    System.out.println("Landed on Tile: "+user.getPosition());
                }

            }
            else{
                roll_value = die.roll();
                roll_message(user, roll_value);
                user.setRolls(user.getRolls()+1);
                if(user.getPosition()+roll_value<=track.size()-1){
                    exceptionnn(user, snake, vulture, cricket, trampoline, white, roll_value);
                    if(user.getPosition()>=track.size()-1){
                        user.setWin_status(true);
                    }
                }
                else{
                    System.out.println("Landed on Tile: "+user.getPosition());
                }
            }
        }
        throw new GameWinnerException(user.getName()+" wins the race in "+user.getRolls()+" rolls.");
    }

    private void exceptionnn(User user, Tile snake, Tile vulture, Tile cricket, Tile trampoline, Tile white, int roll_value) {
        user.setPosition(user.getPosition()+roll_value);
        try{
            roll_message_position(user, snake, vulture, cricket, trampoline, white);
        }
        catch(SnakeBiteException e){
            System.out.println(" Hiss...! I am a Snake, you go back "+ snake.getShake_tile() +" tiles!");
        }
        catch (VultureBiteException e){
            System.out.println("Yapping...! I am a Vulture, you go back "+ vulture.getShake_tile() +" tiles!");
        }
        catch(CricketBiteException e){
            System.out.println("Chirp...! I am a Cricket, you go back "+ cricket.getShake_tile() +" tiles!");
        }
        catch (TrampolineBiteException e){
            System.out.println("PingPong! I am a Trampoline, you advance + "+trampoline.getShake_tile()+" tiles");
        }
        System.out.println(user.getName()+" moved to Tile: "+user.getPosition());
//        user.setRolls(user.getRolls()+1);
    }
}

class User{
    private final String name;
    private int rolls=1;
    private int position;
    private boolean win_status;

    User(String name){
        this.name= name;
        this.position = 1;
        this.win_status = false;

    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public boolean isWin_status() {
        return win_status;
    }

    public void setWin_status(boolean win_status) {
        this.win_status = win_status;
    }

    public String getName() {
        return name;
    }

    public int getRolls() {
        return rolls;
    }

    public void setRolls(int rolls) {
        this.rolls = rolls;
    }
}

class Die{
    int roll(){
        Random random = new Random();
        int value = random.nextInt(6);
        return value+1;
    }
}

class Map{

    void generate(int length, Tile snake, Tile vulture, Tile cricket, Tile trampoline, Tile white){
        Random random = new Random();
        int range = length/5;
        int x = random.nextInt(range)+1;
        snake.setTile(x);
        length=length-x;

        x = random.nextInt(range)+1;
        vulture.setTile(x);
        length= length-x;

        x = random.nextInt(range)+1;
        cricket.setTile(x);
        length= length-x;

        x = random.nextInt(range)+1;
        trampoline.setTile(x);
        length= length-x;
        white.setTile(length);
    }

    void set(ArrayList<String> track, ArrayList<Integer> temp, String value, Tile type){
        Random random = new Random();
        int tile = type.getTile();
        while(tile>0){
            int rand = random.nextInt(temp.size());
            track.set(temp.get(rand), value);
            temp.remove(rand);
            tile--;
        }
    }

    void generate_race_track(ArrayList<String> track, int length, Tile snake, Tile vulture, Tile cricket, Tile trampoline, Tile white){
        ArrayList<Integer> temp = new ArrayList<>();
        for(int i=0;i<length; i++){
            temp.add(i);
            track.add("W");
        }
        set(track, temp, "S", snake);
        set(track, temp, "V", vulture);
        set(track, temp, "C", cricket);
        set(track, temp, "T", trampoline);
        set(track, temp, "W", white);
    }
}

abstract class Tile {
    private int shake_tile;
    abstract int getTile();
    abstract void setTile(int x);
    private int bites=0;

    public int getBites() {
        return bites;
    }

    public void setBites(int bites) {
        this.bites = bites;
    }

    void setShake_tile(boolean type){
        Random random = new Random();
        int value = random.nextInt(10);
        shake_tile = value+1;
        if(!type){
            shake_tile = (-1*shake_tile);
        }
    }

    public int getShake_tile() {
        return shake_tile;
    }

    void setShake_tile(int l){
        shake_tile = l;
    }

}

class White extends Tile{
    private int tile_White;

    @Override
    public int getTile() {
        return tile_White;
    }

    @Override
    public void setTile(int tile_White) {
        this.tile_White = tile_White;
    }

}

class Snake extends Tile{
    private int tile_Snake;

    @Override
    public int getTile() {
        return tile_Snake;
    }

    @Override
    public void setTile(int tile_Snake) {
        this.tile_Snake = tile_Snake;
    }
}

class Cricket extends Tile{
    private int tile_Cricket;

    @Override
    public int getTile() {
        return tile_Cricket;
    }

    @Override
    public void setTile(int tile_Cricket) {
        this.tile_Cricket = tile_Cricket;
    }
}

class Vulture extends Tile{
    private int tile_Vulture;

    @Override
    public int getTile() {
        return tile_Vulture;
    }

    @Override
    public void setTile(int tile_Vulture) {
        this.tile_Vulture = tile_Vulture;
    }
}

class Trampoline extends Tile{
    private int tile_Trampoline;

    @Override
    public int getTile() {
        return tile_Trampoline;
    }

    @Override
    public void setTile(int tile_Trampoline) {
        this.tile_Trampoline = tile_Trampoline;
    }
}

public class Main {
    public static void main(String[] args) throws DieValueException, SnakeBiteException, VultureBiteException,
            CricketBiteException, TrampolineBiteException, GameWinnerException, TrackLengthException {
        input temp=new input();
        Game game = new Game();
        game.start();
    }
}
