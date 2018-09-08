package ru.blackart.dsi.infopanel.view;

import java.util.Observable;
import java.util.Observer;

public class DataModelObserver implements Observer {
    public void update(Observable o, Object arg) {
        System.out.print("Observe!");
    }
}
