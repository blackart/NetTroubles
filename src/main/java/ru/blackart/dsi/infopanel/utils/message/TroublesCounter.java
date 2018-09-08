package ru.blackart.dsi.infopanel.utils.message;

public class TroublesCounter extends AMessage {
    private int current;        
    private int waiting_close;        
    private int close;        
    private int trash;        
    private int need_actual_problem;

    public TroublesCounter(int current, int waiting_close, int close, int trash, int need_actual_problem) {
        this.current = current;
        this.waiting_close = waiting_close;
        this.close = close;
        this.trash = trash;
        this.need_actual_problem = need_actual_problem;
    }
}
