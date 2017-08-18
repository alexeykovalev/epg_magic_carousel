package com.magicepg.func;

/**
 * @author Alexey
 * @since 4/4/17
 */
public interface Action {

    Action EMPTY = new Action() {
        @Override
        public void run() {
        }
    };

    void run();
}
