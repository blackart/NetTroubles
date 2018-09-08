package observe;

import java.util.Observable;

public class MyObservable extends Observable {
    private int num;

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
        this.setChanged();
        this.notifyObservers(num);
    }
}
