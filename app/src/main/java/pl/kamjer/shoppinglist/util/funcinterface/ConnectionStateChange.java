package pl.kamjer.shoppinglist.util.funcinterface;

import android.net.Network;

@FunctionalInterface
public interface ConnectionStateChange {

    void change(Network network);
}
