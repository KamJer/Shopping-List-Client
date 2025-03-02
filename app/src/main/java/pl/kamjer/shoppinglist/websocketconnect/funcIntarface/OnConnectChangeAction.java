package pl.kamjer.shoppinglist.websocketconnect.funcIntarface;

@FunctionalInterface
public interface OnConnectChangeAction {
    void action(boolean connected);
}
