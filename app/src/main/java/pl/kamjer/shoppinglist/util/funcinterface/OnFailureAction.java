package pl.kamjer.shoppinglist.util.funcinterface;

@FunctionalInterface
public interface OnFailureAction {
    void action(Throwable t);
}
