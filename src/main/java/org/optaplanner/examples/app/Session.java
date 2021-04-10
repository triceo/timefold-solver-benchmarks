package org.optaplanner.examples.app;

public interface Session {

    int insert(Object object);

    int update(Object object);

    Object calculateScore();

    void close();

}
