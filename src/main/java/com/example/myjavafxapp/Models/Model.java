package com.example.myjavafxapp.Models;

import com.example.myjavafxapp.Views.ViewFactory;

/**
 * Application-wide model implemented as a thread-safe singleton.
 * Holds shared objects such as the {@link ViewFactory}.
 */
public final class Model {
    private static Model Model;
    private static volatile Model instance;
    private final ViewFactory viewFactory;

    private Model() {

        this.viewFactory = new ViewFactory();
    }

    public static synchronized Model getInstance() {
        if (Model == null) {
            Model = new Model();
        }
        return Model;
    }

    public ViewFactory getViewFactory() {
        return viewFactory;
    }
}
