package ru.blackart.dsi.infopanel.temp.dao;

import ru.blackart.dsi.infopanel.beans.Persistent;

import java.util.List;

public interface Dao<T extends Persistent> {
    void saveOrUpdate(T persistent);
    void delete(int id);
    void delete(T persistent);
    T get(int id);
    List<T> getAll();
}
