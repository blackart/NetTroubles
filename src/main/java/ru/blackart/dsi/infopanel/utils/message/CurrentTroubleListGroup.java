package ru.blackart.dsi.infopanel.utils.message;

import ru.blackart.dsi.infopanel.beans.TroubleList;

public class CurrentTroubleListGroup extends AMessage {
    private TroubleList current;
    private TroubleList wait;
    private TroubleList need;

    public CurrentTroubleListGroup(TroubleList current, TroubleList wait, TroubleList need) {
        this.current = current;
        this.wait = wait;
        this.need = need;
    }
}
