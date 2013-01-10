package ru.blackart.dsi.infopanel.utils.searching;

import ru.blackart.dsi.infopanel.beans.Devcapsule;

import java.util.List;

public interface Searching {
    public List<Devcapsule> find();
    public List<Devcapsule> findInData(List<Devcapsule> devcapsules);
    public List<Devcapsule> search(List<Devcapsule> devcapsules);
    public List<Devcapsule> searchEverywhere();
}
