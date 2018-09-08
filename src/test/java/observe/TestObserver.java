package observe;

public class TestObserver {
    public static void main(String[] args) {
        MyObservable myObservable = new MyObservable();
        MyObserver myObserver = new MyObserver();
        myObservable.addObserver(myObserver);
        myObservable.setNum(111);
    }
}
